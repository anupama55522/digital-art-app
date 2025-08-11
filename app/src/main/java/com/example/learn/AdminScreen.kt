package com.example.learn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_screen)
        val artistCard: CardView = findViewById(R.id.artist_card)
        artistCard.setOnClickListener {
            val intent = Intent(this, ManageArtist::class.java)
            startActivity(intent)
        }

        val customerCard: CardView = findViewById(R.id.customer_card)
        customerCard.setOnClickListener {
            val intent = Intent(this, ManageCustomer::class.java)
            startActivity(intent)
        }

        val homeCard: CardView = findViewById(R.id.home_admin)
        homeCard.setOnClickListener {
            refreshActivity()
        }

        val logoutCard: CardView = findViewById(R.id.logout_admin)
        logoutCard.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logoutUser() {

        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun refreshActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}