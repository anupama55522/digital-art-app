package com.example.learn

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class OrderFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)

        val artwork: Artwork? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("artwork", Artwork::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("artwork") as? Artwork
        }

        val imageView = findViewById<ImageView>(R.id.orderArtworkImage)
        val titleView = findViewById<TextView>(R.id.orderArtworkTitle)
        val addressInput = findViewById<EditText>(R.id.shippingAddress)
        val customizeInput = findViewById<EditText>(R.id.customRequest)
        val submitButton = findViewById<Button>(R.id.submitOrderBtn)

        artwork?.let { artworkItem ->
            Glide.with(this).load(artworkItem.imageUri).into(imageView)
            titleView.text = artworkItem.title

            if (artworkItem.customizable == "yes") {
                customizeInput.visibility = EditText.VISIBLE
            } else {
                customizeInput.visibility = EditText.GONE
            }

            submitButton.setOnClickListener {
                val address = addressInput.text.toString().trim()
                val customText = customizeInput.text.toString().trim().ifBlank { null }

                if (address.isEmpty()) {
                    Toast.makeText(this, "Please enter shipping address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val customerId = getCurrentCustomerId()
                if (customerId == -1) {
                    Toast.makeText(this, "Customer not logged in", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val order = Order(
                    cid = customerId,
                    artId = artworkItem.id,
                    address = address,
                    customize = customText
                )

                val dbHelper = DatabaseHelper(this)
                val success = dbHelper.insertOrder(order)

                if (success) {
                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentCustomerId(): Int {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        return sharedPref.getInt("customer_id", -1)
    }
}