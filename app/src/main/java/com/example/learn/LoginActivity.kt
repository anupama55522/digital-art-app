package com.example.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerRedirectTextView: TextView
    private lateinit var adminRedirectTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        emailInput = findViewById(R.id.loginEmail)
        passwordInput = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        registerRedirectTextView = findViewById(R.id.registerRedirect)
        adminRedirectTextView = findViewById(R.id.adminRedirect)

        loginButton.setOnClickListener {
            validateLogin()
        }
        registerRedirectTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        adminRedirectTextView.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty()) {
            emailInput.error = "Enter your email"
            emailInput.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Enter your password"
            passwordInput.requestFocus()
            return
        }
        if (dbHelper.validateCustomer(email, password)) {
            val customer = dbHelper.getCustomerByEmail(email)
            if (customer != null) {
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("customer_id", customer.id)
                editor.putString("customer_name", customer.name)
                editor.apply()
            }

            Toast.makeText(this, "Customer Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CustomerScreen::class.java)
            startActivity(intent)
            finish()
            return
        }
        if (dbHelper.validateArtist(email, password)) {
            val artist = dbHelper.getArtistByEmail(email)
            if (artist != null) {
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("artist_id", artist.id)
                editor.putString("artist_name", artist.name)
                editor.apply()
            }

            Toast.makeText(this, "Artist Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ArtistScreen::class.java)
            startActivity(intent)
            finish()
            return
        }
        Toast.makeText(this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show()
    }
}