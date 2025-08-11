package com.example.learn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuArtist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_artist)

        val profileButton: Button = findViewById(R.id.profileButton)

        profileButton.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val artistId = sharedPref.getInt("artist_id", -1)

            if (artistId != -1) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Artist not logged in. Please log in first.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}