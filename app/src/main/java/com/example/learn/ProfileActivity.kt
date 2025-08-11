package com.example.learn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val nameInput: EditText = findViewById(R.id.nameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val phoneInput: EditText = findViewById(R.id.phoneInput)
        val saveButton: Button = findViewById(R.id.saveButton)
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val artistId = sharedPref.getInt("artist_id", -1)

        if (artistId != -1) {
            val dbHelper = DatabaseHelper(this)
            val artist = dbHelper.getArtistById(artistId)

            artist?.let {
                nameInput.setText(it.name)
                emailInput.setText(it.email)
                val phone = dbHelper.getArtistPhoneById(artistId)
                phoneInput.setText(phone)
            }
        }
        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}