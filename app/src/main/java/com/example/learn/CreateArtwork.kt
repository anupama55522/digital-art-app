package com.example.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class CreateArtwork : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadImageButton: Button
    private var imageUri: Uri? = null
    private lateinit var spinnerCategory: Spinner


    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_artwork)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerCategory = findViewById(R.id.spinnerCategory)
        imageView = findViewById(R.id.artworkImage)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        val submitButton: Button = findViewById(R.id.submitArtworkButton)


        val categories = resources.getStringArray(R.array.art_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter


        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                imageView.setImageURI(imageUri)
            }
        }


        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }


        val dbHelper = DatabaseHelper(this)


        submitButton.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val aid = sharedPref.getInt("artist_id", -1)
            val title = findViewById<EditText>(R.id.artworkTitle).text.toString()
            val category = spinnerCategory.selectedItem.toString()

            val imagePath = imageUri?.let {
                saveImageToInternalStorage(it)
            } ?: ""

            val price = findViewById<EditText>(R.id.artworkPrice).text.toString().toDoubleOrNull() ?: 0.0
            val avail = if (findViewById<CheckBox>(R.id.artworkAvailability).isChecked) "yes" else "no"
            val description = findViewById<EditText>(R.id.artworkDescription).text.toString()
            val customizeOp = when {
                findViewById<RadioButton>(R.id.customizableYes).isChecked -> "yes"
                findViewById<RadioButton>(R.id.customizableNo).isChecked -> "no"
                else -> "no"
            }
            val stock = findViewById<EditText>(R.id.artworkstock).text.toString().toIntOrNull() ?: 1
            val dimension = findViewById<EditText>(R.id.artworkDimension).text.toString()

            val success = dbHelper.insertArtwork(
                aid, title, category, imagePath, price, avail,
                description, customizeOp, stock, dimension
            )

            if (success) {
                Toast.makeText(this, "Artwork submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to submit artwork.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val extension = contentResolver.getType(uri)?.substringAfterLast("/") ?: "jpg"
            val fileName = "artwork_${System.currentTimeMillis()}.$extension"
            val dir = File(filesDir, "artwork_images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}