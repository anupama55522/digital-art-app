package com.example.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class ArtistActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var uploadCertificateButton: Button
    private lateinit var submitButton: Button

    private var selectedFileUri: Uri? = null
    private var certificatePath: String? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedFileUri = uri
                certificatePath = getFilePath(uri)
                if (certificatePath != null) {
                    Toast.makeText(this, "Certificate uploaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error processing certificate", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist)

        dbHelper = DatabaseHelper(this)

        nameInput = findViewById(R.id.artistName)
        emailInput = findViewById(R.id.artistEmail)
        phoneInput = findViewById(R.id.artistPhone)
        passwordInput = findViewById(R.id.artistPassword)
        uploadCertificateButton = findViewById(R.id.uploadCertificateButton)
        submitButton = findViewById(R.id.submitArtistButton)

        uploadCertificateButton.setOnClickListener {
            openFilePicker()
        }

        submitButton.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch("image/*")
    }

    private fun getFilePath(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileDir = File(filesDir, "certificates")
            if (!fileDir.exists()) fileDir.mkdirs()

            val fileName = getFileName(uri) ?: return null
            val destinationFile = File(fileDir, fileName)

            FileOutputStream(destinationFile).use { output ->
                inputStream.copyTo(output)
            }

            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    fileName = cursor.getString(index)
                }
            }
        }
        return fileName
    }

    private fun validateAndSubmit() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
            nameInput.error = "Name should only contain alphabets"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email address"
            return
        }
        if (!phone.matches(Regex("^[0-9]{10}\$"))) {
            phoneInput.error = "Enter a valid 10-digit phone number"
            return
        }
        if (password.length < 8) {
            passwordInput.error = "Password must be at least 8 characters"
            return
        }
        if (certificatePath == null) {
            Toast.makeText(this, "Please upload a valid certificate (JPG/PNG)", Toast.LENGTH_SHORT).show()
            return
        }
        val artistId = dbHelper.insertArtist(name, email, phone, certificatePath!!, password)
        if (artistId != -1) {
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt("artist_id", artistId)
                putString("artist_name", name)
                apply()
            }

            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ArtistScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
        }
    }
}