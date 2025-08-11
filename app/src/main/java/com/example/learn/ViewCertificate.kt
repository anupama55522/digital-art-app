package com.example.learn

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import java.io.File
import android.util.Log
import com.bumptech.glide.Glide

class ViewCertificate : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_certificate)
        imageView = findViewById(R.id.ivCertificate)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val certPath = intent.getStringExtra("CERT_PATH")
        if (certPath != null) {
            displayImage(certPath)
        }

        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun displayImage(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            Glide.with(this).load(file).into(imageView)
        } else {
            Log.e("ViewCertificate", "File not found: $filePath")
        }
    }
}