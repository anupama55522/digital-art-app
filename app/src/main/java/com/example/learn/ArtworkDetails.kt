package com.example.learn

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ArtworkDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artwork_details)

        val artwork: Artwork? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("artwork", Artwork::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("artwork") as? Artwork
        }

        if (artwork != null) {
            val artworkImage: ImageView = findViewById(R.id.detailArtworkImage)
            val artworkTitle: TextView = findViewById(R.id.detailArtworkTitle)
            val artworkCategory: TextView = findViewById(R.id.detailArtworkCategory)
            val artworkPrice: TextView = findViewById(R.id.detailArtworkPrice)
            val artworkAvailability: TextView = findViewById(R.id.detailArtworkAvailability)
            val artworkDescription: TextView = findViewById(R.id.detailArtworkDescription)
            val artworkCustomize: TextView = findViewById(R.id.detailArtworkCustomize)
            val artworkStock: TextView = findViewById(R.id.detailArtworkStock)
            val artworkDimension: TextView = findViewById(R.id.detailArtworkDimension)

            Glide.with(this)
                .load(artwork.imageUri)
                .placeholder(R.drawable.gallery)
                .into(artworkImage)

            artworkTitle.text = artwork.title
            artworkCategory.text = "Category: ${artwork.category}"
            artworkPrice.text = "Price: ₹${artwork.price}"
            artworkAvailability.text = "Available: ${artwork.availability}"
            artworkDescription.text = "Description:\n${artwork.description}"
            artworkCustomize.text = "Customizable: ${artwork.customizable}"
            artworkStock.text = "Stock: ${artwork.stock}"
            artworkDimension.text = "Dimension: ${artwork.dimension}"

            // Buttons
            val updateButton = findViewById<Button>(R.id.updateArtworkButton)
            val deleteButton = findViewById<Button>(R.id.deleteArtworkButton)

            updateButton.setOnClickListener {
                val intent = Intent(this, UpdateArtworkActivity::class.java)
                intent.putExtra("artwork", artwork)
                startActivity(intent)
            }

            deleteButton.setOnClickListener {
                val dbHelper = DatabaseHelper(this)
                val deleted = dbHelper.deleteArtworkById(artwork.id)
                if (deleted) {
                    Toast.makeText(this, "Artwork deleted", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}