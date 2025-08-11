package com.example.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ArtistScreen : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_artist_screen)

        dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val artistId = sharedPref.getInt("artist_id", -1)
        val artistName = sharedPref.getString("artist_name", null) ?: "Artist"

        val artworkButton = findViewById<CardView>(R.id.artworkButton)
        val artistHiTextView = findViewById<TextView>(R.id.artist_hi)
        val menuIcon: ImageView = findViewById(R.id.menuIcon)
        artistHiTextView.text = "Hi, $artistName"
        val ordersCard = findViewById<CardView>(R.id.ordersCard)
        ordersCard.setOnClickListener {
            if (artistId != -1) {
                val intent = Intent(this, ArtistOrdersActivity::class.java)
                intent.putExtra("artist_id", artistId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Artist not found!", Toast.LENGTH_SHORT).show()
            }
        }

        artworkButton.setOnClickListener {
            if (artistId != -1) {
                val isVerified = dbHelper.isArtistVerified(artistId)
                if (isVerified) {
                    startActivity(Intent(this, ArtworksActivity::class.java))
                } else {
                    Toast.makeText(this, "Verification pending. You cannot add artworks yet.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Error: Artist not found!", Toast.LENGTH_SHORT).show()
            }
        }

        val artistLogout = findViewById<LinearLayout>(R.id.artistLogout)
        artistLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        menuIcon.setOnClickListener {
            startActivity(Intent(this, MenuArtist::class.java))
        }
    }
}