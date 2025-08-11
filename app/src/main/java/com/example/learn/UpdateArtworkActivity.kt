package com.example.learn

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UpdateArtworkActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var availabilityEditText: EditText
    private lateinit var customizableEditText: EditText
    private lateinit var stockEditText: EditText
    private lateinit var dimensionEditText: EditText
    private lateinit var saveButton: Button

    private var artworkId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_artwork)

        titleEditText = findViewById(R.id.editTitle)
        descriptionEditText = findViewById(R.id.editDescription)
        priceEditText = findViewById(R.id.editPrice)
        categoryEditText = findViewById(R.id.editCategory)
        availabilityEditText = findViewById(R.id.editAvailability)
        customizableEditText = findViewById(R.id.editCustomizable)
        stockEditText = findViewById(R.id.editStock)
        dimensionEditText = findViewById(R.id.editDimension)
        saveButton = findViewById(R.id.saveArtworkButton)

        val artwork = intent.getSerializableExtra("artwork") as? Artwork
        if (artwork != null) {
            artworkId = artwork.id
            titleEditText.setText(artwork.title)
            descriptionEditText.setText(artwork.description)
            priceEditText.setText(artwork.price.toString())
            categoryEditText.setText(artwork.category)
            availabilityEditText.setText(artwork.availability) // this still maps to avail in DB
            customizableEditText.setText(artwork.customizable)
            stockEditText.setText(artwork.stock.toString())
            dimensionEditText.setText(artwork.dimension)
        }

        saveButton.setOnClickListener {
            updateArtwork()
        }
    }

    private fun updateArtwork() {
        val db = DatabaseHelper(this)

        val updated = db.updateArtwork(
            artworkId,
            titleEditText.text.toString(),
            descriptionEditText.text.toString(),
            priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
            categoryEditText.text.toString(),
            availabilityEditText.text.toString(), // still using 'availability' here,
            customizableEditText.text.toString(),
            stockEditText.text.toString().toIntOrNull() ?: 0,
            dimensionEditText.text.toString()
        )

        if (updated) {
            Toast.makeText(this, "Artwork updated successfully", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Failed to update artwork", Toast.LENGTH_SHORT).show()
        }
    }
}