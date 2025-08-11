package com.example.learn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val artistButton = findViewById<Button>(R.id.artistButton)
        val customerButton = findViewById<Button>(R.id.customerButton)

        artistButton.setOnClickListener {
            val intent = Intent(this, ArtistActivity::class.java)
            startActivity(intent)
        }
        customerButton.setOnClickListener {
            val intent = Intent(this, CustomerActivity::class.java)
            startActivity(intent)
        }
    }
}