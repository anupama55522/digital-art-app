package com.example.learn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ArtworkDetailsCustomer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artwork_details_customer)

        val artwork = intent.getSerializableExtra("artwork") as? Artwork

        val imageView = findViewById<ImageView>(R.id.artworkImage)
        val titleView = findViewById<TextView>(R.id.artworkTitle)
        val descriptionView = findViewById<TextView>(R.id.artworkDescription)
        val priceView = findViewById<TextView>(R.id.artworkPrice)
        val buyButton = findViewById<Button>(R.id.buyButton)
        val customizeButton = findViewById<Button>(R.id.customizeButton)
        val orderFormLayout = findViewById<View>(R.id.orderFormLayout) // Layout that holds form fields

        // Initially hide both
        customizeButton.visibility = View.GONE
        orderFormLayout.visibility = View.GONE

        artwork?.let {
            Glide.with(this).load(it.imageUri).into(imageView)
            titleView.text = it.title
            descriptionView.text = it.description
            priceView.text = "Price: ₹${it.price}"
            buyButton.setOnClickListener {
                val intent = Intent(this, OrderFormActivity::class.java)
                intent.putExtra("artwork", artwork)
                startActivity(intent)
            }

        }
    }
}