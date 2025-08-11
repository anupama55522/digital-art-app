package com.example.learn

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import at.favre.lib.crypto.bcrypt.BCrypt

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val appContext = context.applicationContext

    override fun onCreate(db: SQLiteDatabase) {
        val createAdminTable = """
            CREATE TABLE IF NOT EXISTS admin (
                admin_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                admin_email TEXT UNIQUE NOT NULL, 
                admin_password TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createAdminTable)

        val createArtistTable = """
            CREATE TABLE IF NOT EXISTS artist (
                aid INTEGER PRIMARY KEY AUTOINCREMENT, 
                a_name TEXT NOT NULL, 
                a_email TEXT UNIQUE NOT NULL, 
                a_phone TEXT NOT NULL, 
                cert_path TEXT NOT NULL,  
                a_password TEXT NOT NULL,
                verify TEXT NOT NULL DEFAULT 'pending' CHECK(verify IN('pending','accepted','rejected'))
            );
        """.trimIndent()
        db.execSQL(createArtistTable)

        val createCustomerTable = """
            CREATE TABLE IF NOT EXISTS customer (
                cid INTEGER PRIMARY KEY AUTOINCREMENT, 
                c_name TEXT NOT NULL, 
                c_email TEXT UNIQUE NOT NULL, 
                c_phone TEXT NOT NULL, 
                c_password TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createCustomerTable)
        val createArtworkTable = """
    CREATE TABLE IF NOT EXISTS artwork (
        art_id INTEGER PRIMARY KEY AUTOINCREMENT,
        aid INTEGER NOT NULL,
        title TEXT NOT NULL,
        category TEXT NOT NULL,
        image TEXT NOT NULL,
        price REAL NOT NULL,
        avail TEXT NOT NULL CHECK(avail IN ('yes', 'no')),
        description TEXT,
        customize_op TEXT NOT NULL CHECK(customize_op IN ('yes', 'no')),
        stock INTEGER NOT NULL DEFAULT 1,
        dimension TEXT,
        FOREIGN KEY (aid) REFERENCES artist(aid) ON DELETE CASCADE
    );
""".trimIndent()
        db.execSQL(createArtworkTable)
        val createOrderTable = """
    CREATE TABLE IF NOT EXISTS orders (
        pid INTEGER PRIMARY KEY AUTOINCREMENT,
        cid INTEGER NOT NULL,
        art_id INTEGER NOT NULL,
        address TEXT NOT NULL,
        status TEXT NOT NULL DEFAULT 'processing',
        customize TEXT,
        custom_status TEXT DEFAULT 'pending',
        date TEXT DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (cid) REFERENCES customer(cid),
        FOREIGN KEY (art_id) REFERENCES artwork(art_id)
    );
""".trimIndent()
        db.execSQL(createOrderTable)

        val insertAdminQuery = """
            INSERT INTO admin (admin_email, admin_password) 
            VALUES ('admin2025@gmail.com', '${hashPassword("9447")}');
        """.trimIndent()
        db.execSQL(insertAdminQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE artist ADD COLUMN new_column TEXT DEFAULT ''")
        }
    }

    private fun saveCertificateToInternalStorage(sourcePath: String): String? {
        return try {
            val dir = File(appContext.filesDir, "certificates")
            if (!dir.exists()) dir.mkdirs()
            val newFile = File(dir, "certificate_${System.currentTimeMillis()}.png")
            val inputStream: InputStream = File(sourcePath).inputStream()
            val outputStream = FileOutputStream(newFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            newFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    private fun saveArtworkImageToInternalStorage(sourcePath: String): String? {
        return try {
            val dir = File(appContext.filesDir, "artwork_images")
            if (!dir.exists()) dir.mkdirs()
            val extension = sourcePath.substringAfterLast('.', "jpg") // fallback to jpg
            val newFile = File(dir, "artwork_${System.currentTimeMillis()}.$extension")
            val inputStream: InputStream = File(sourcePath).inputStream()
            val outputStream = FileOutputStream(newFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            newFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun insertArtist(name: String, email: String, phone: String, certPath: String, password: String): Int {
        val db = this.writableDatabase
        return try {
            val storedPath = saveCertificateToInternalStorage(certPath) ?: return -1  // Return -1 if saving fails
            val hashedPassword = hashPassword(password)

            val values = ContentValues().apply {
                put("a_name", name)
                put("a_email", email)
                put("a_phone", phone)
                put("cert_path", storedPath)
                put("a_password", hashedPassword)
                put("verify", "pending")
            }

            val artistId = db.insert("artist", null, values)
            db.close()

            if (artistId == -1L) -1 else artistId.toInt()  // Return artist ID if successful, -1 if failed
        } catch (e: Exception) {
            e.printStackTrace()
            -1  // Return -1 on exception
        }
    }


    fun insertCustomer(name: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        return try {
            val hashedPassword = hashPassword(password)

            val values = ContentValues().apply {
                put("c_name", name)
                put("c_email", email)
                put("c_phone", phone)
                put("c_password", hashedPassword)
            }
            val result = db.insert("customer", null, values)
            result != -1L
        } finally {
            db.close()
        }
    }
    fun insertOrder(order: Order, db: SQLiteDatabase = writableDatabase): Boolean {
        val istTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
        }.format(java.util.Date())

        val values = ContentValues().apply {
            put("cid", order.cid)
            put("art_id", order.artId)
            put("address", order.address)
            put("status", order.status)
            put("customize", order.customize)
            put("custom_status", order.customStatus)
            put("date", istTime) // Add the Indian time here
        }

        val result = db.insert("orders", null, values)
        return result != -1L
    }
    fun deleteArtworkById(artId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("artwork", "art_id = ?", arrayOf(artId.toString()))
        db.close()
        return result > 0
    }
    fun getOrdersByCustomer(cid: Int): List<Order> {
        val db = readableDatabase
        val orders = mutableListOf<Order>()
        val query = """
        SELECT * FROM orders 
        WHERE cid = ? 
        ORDER BY date DESC
    """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(cid.toString()))

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    pid = cursor.getInt(cursor.getColumnIndexOrThrow("pid")),
                    cid = cid,
                    artId = cursor.getInt(cursor.getColumnIndexOrThrow("art_id")),
                    artTitle = "", // not fetched from DB anymore
                    address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    customize = cursor.getString(cursor.getColumnIndexOrThrow("customize")),
                    customStatus = cursor.getString(cursor.getColumnIndexOrThrow("custom_status")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun getOrdersByCustomerId(customerId: Int): List<Order> {
        val orderList = mutableListOf<Order>()
        val db = this.readableDatabase

        val query = """
        SELECT o.pid, o.cid, o.art_id, a.title, o.address, o.status, o.customize, o.custom_status, o.date
        FROM orders o
        JOIN artwork a ON o.art_id = a.art_id
        WHERE o.cid = ?
        ORDER BY o.date DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(customerId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    pid = cursor.getInt(cursor.getColumnIndexOrThrow("pid")),
                    cid = cursor.getInt(cursor.getColumnIndexOrThrow("cid")),
                    artId = cursor.getInt(cursor.getColumnIndexOrThrow("art_id")),
                    artTitle = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    customize = cursor.getString(cursor.getColumnIndexOrThrow("customize")),
                    customStatus = cursor.getString(cursor.getColumnIndexOrThrow("custom_status")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                )
                orderList.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return orderList
    }


    fun updateArtwork(
        id: Int,
        title: String,
        description: String,
        price: Double,
        category: String,
        availability: String,
        customizable: String,
        stock: Int,
        dimension: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("price", price)
            put("category", category)
            put("avail", availability) // Corrected column name
            put("customize_op", customizable)
            put("stock", stock)
            put("dimension", dimension)
        }

        val result = db.update("artwork", values, "art_id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun validateCustomer(email: String, password: String): Boolean {
        val db = this.readableDatabase
        return try {
            val query = "SELECT c_password FROM customer WHERE c_email = ?"
            val cursor = db.rawQuery(query, arrayOf(email))

            var storedHashedPassword: String? = null
            if (cursor.moveToFirst()) {
                storedHashedPassword = cursor.getString(0)
            }
            cursor.close()

            return storedHashedPassword != null &&
                    BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword).verified
        } finally {
            db.close()
        }
    }

    fun updateArtistVerification(artistId: Int, status: String): Boolean {
        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("verify", status)
            }
            val result = db.update("artist", values, "aid = ?", arrayOf(artistId.toString()))
            result > 0
        } finally {
            db.close()
        }
    }

    fun getAllArtists(): List<Artist> {
        val artistList = mutableListOf<Artist>()
        val db = this.readableDatabase
        return try {
            val query = "SELECT aid, a_name, a_email, cert_path, verify FROM artist"
            val cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val artist = Artist(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                    )
                    artistList.add(artist)
                } while (cursor.moveToNext())
            }
            cursor.close()
            artistList
        } finally {
            db.close()
        }
    }

    fun getAllCustomers(): List<Customer> {
        val customerList = mutableListOf<Customer>()
        val db = this.readableDatabase
        return try {
            val query = "SELECT cid, c_name, c_email FROM customer"
            val cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val customer = Customer(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                    customerList.add(customer)
                } while (cursor.moveToNext())
            }
            cursor.close()
            customerList
        } finally {
            db.close()
        }
    }
    fun getCustomerByEmail(email: String): Customer? {
        val db = this.readableDatabase
        return try {
            val cursor = db.rawQuery("SELECT cid, c_name FROM customer WHERE c_email = ?", arrayOf(email))
            if (cursor.moveToFirst()) {
                val customer = Customer(
                    id = cursor.getInt(0),
                    name = cursor.getString(1),
                    email = email
                )
                cursor.close()
                customer
            } else {
                cursor.close()
                null
            }
        } finally {
            db.close()
        }
    }
    fun getArtworksByArtist(artistId: Int): List<Artwork> {
        val artworks = mutableListOf<Artwork>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM artwork WHERE aid = ?", arrayOf(artistId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val artwork = Artwork(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("art_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    availability = cursor.getString(cursor.getColumnIndexOrThrow("avail")),
                    customizable = cursor.getString(cursor.getColumnIndexOrThrow("customize_op")),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                    dimension = cursor.getString(cursor.getColumnIndexOrThrow("dimension"))
                )
                artworks.add(artwork)
            } while (cursor.moveToNext())
        }


        cursor.close()
        return artworks
    }
    fun getAllArtworksForCustomer(): List<Artwork> {
        val artworkList = mutableListOf<Artwork>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM artwork", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("art_id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val avail = cursor.getString(cursor.getColumnIndexOrThrow("avail"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description")) ?: ""
                val customizeOp = cursor.getString(cursor.getColumnIndexOrThrow("customize_op"))
                val stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
                val dimension = cursor.getString(cursor.getColumnIndexOrThrow("dimension")) ?: ""

                val artwork = Artwork(
                    id = id,
                    title = title,
                    imageUri = imagePath,
                    description = description,
                    price = price,
                    category = category,
                    availability = avail,
                    customizable = customizeOp,
                    stock = stock,
                    dimension = dimension
                )

                artworkList.add(artwork)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return artworkList
    }
    fun getArtistByEmail(email: String): Artist? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM artist WHERE a_email = ?", arrayOf(email))

        var artist: Artist? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("aid"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("a_name"))
            val emailVal = cursor.getString(cursor.getColumnIndexOrThrow("a_email"))
            val certPath = cursor.getString(cursor.getColumnIndexOrThrow("cert_path"))
            val verify = cursor.getString(cursor.getColumnIndexOrThrow("verify"))
            val isAccepted = verify.equals("accepted", ignoreCase = true)

            artist = Artist(id, name, emailVal, certPath, verify, isAccepted)
        }
        cursor.close()
        return artist
    }



    fun deleteArtist(artistId: Int): Boolean {
        val db = this.writableDatabase
        return try {
            val result = db.delete("artist", "aid = ?", arrayOf(artistId.toString()))
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
    fun deleteCustomer(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("customer", "id=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun isArtistVerified(artistId: Int): Boolean {
        val db = this.readableDatabase
        return try {
            val query = "SELECT verify FROM artist WHERE aid = ?"
            val cursor = db.rawQuery(query, arrayOf(artistId.toString()))

            var isVerified = false
            if (cursor.moveToFirst()) {
                isVerified = cursor.getString(0) == "accepted"
            }
            cursor.close()
            isVerified
        } finally {
            db.close()
        }
    }
    fun getArtistById(aid: Int): Artist? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM artist WHERE aid = ?", arrayOf(aid.toString()))
        var artist: Artist? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("a_name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("a_email"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("a_phone"))
            val certPath = cursor.getString(cursor.getColumnIndexOrThrow("cert_path"))
            val verify = cursor.getString(cursor.getColumnIndexOrThrow("verify"))

            artist = Artist(aid, name, email, certPath, verify)
        }
        cursor.close()
        db.close()
        return artist
    }
    fun getArtistPhoneById(aid: Int): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT a_phone FROM artist WHERE aid = ?", arrayOf(aid.toString()))
        var phone = ""
        if (cursor.moveToFirst()) {
            phone = cursor.getString(cursor.getColumnIndexOrThrow("a_phone"))
        }
        cursor.close()
        db.close()
        return phone
    }

    fun validateArtist(email: String, password: String): Boolean {
        val db = this.readableDatabase
        return try {
            val query = "SELECT aid, a_name, a_password FROM artist WHERE a_email = ?"
            val cursor = db.rawQuery(query, arrayOf(email))

            var artistId: Int? = null
            var artistName: String? = null
            var storedHashedPassword: String? = null

            if (cursor.moveToFirst()) {
                artistId = cursor.getInt(0)
                artistName = cursor.getString(1)
                storedHashedPassword = cursor.getString(2)
            }
            cursor.close()

            val isVerified = storedHashedPassword != null &&
                    BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword).verified

            if (isVerified && artistName != null) {
                val sharedPref = appContext.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("artist_id", artistId ?: -1)
                    putString("artist_name", artistName)
                    apply()
                }
            }

            isVerified
        } finally {
            db.close()
        }
    }
    fun getOrdersByArtistId(artistId: Int): List<Order> {
        val db = readableDatabase
        val orders = mutableListOf<Order>()
        val query = """
        SELECT o.pid, o.cid, o.art_id, a.title, o.address, o.status, 
               o.customize, o.custom_status, o.date
        FROM orders o
        INNER JOIN artwork a ON o.art_id = a.art_id
        WHERE a.aid = ?
        ORDER BY o.date DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(artistId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    pid = cursor.getInt(0),
                    cid = cursor.getInt(1),
                    artId = cursor.getInt(2),
                    artTitle = cursor.getString(3),
                    address = cursor.getString(4),
                    status = cursor.getString(5),
                    customize = cursor.getString(6),
                    customStatus = cursor.getString(7),
                    date = cursor.getString(8)
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun insertArtwork(
        aid: Int,
        title: String,
        category: String,
        imagePath: String,
        price: Double,
        avail: String,
        description: String,
        customizeOp: String,
        stock: Int,
        dimension: String
    ): Boolean {
        val db = this.writableDatabase
        return try {
            val storedImagePath = saveArtworkImageToInternalStorage(imagePath) ?: return false

            val values = ContentValues().apply {
                put("aid", aid)
                put("title", title)
                put("category", category)
                put("image", storedImagePath)
                put("price", price)
                put("avail", avail)
                put("description", description)
                put("customize_op", customizeOp)
                put("stock", stock)
                put("dimension", dimension)
            }

            val result = db.insert("artwork", null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    companion object {
        private const val DATABASE_NAME = "GalleryDB.db"
        private const val DATABASE_VERSION = 3
    }
}
