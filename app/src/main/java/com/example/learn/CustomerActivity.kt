package com.example.learn

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomerActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.registerButton)

        dbHelper = DatabaseHelper(this)

        registerButton.setOnClickListener {
            validateAndRegister()
        }
    }


    private fun validateAndRegister() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
            nameInput.error = "Name should only contain alphabets"
            nameInput.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email address"
            emailInput.requestFocus()
            return
        }
        if (!phone.matches(Regex("^[0-9]{10}\$"))) {
            phoneInput.error = "Enter a valid 10-digit phone number"
            phoneInput.requestFocus()
            return
        }
        if (password.length < 8) {
            passwordInput.error = "Password must be at least 8 characters"
            passwordInput.requestFocus()
            return
        }

        val isInserted = dbHelper.insertCustomer(name, email, phone, password)

        if (isInserted) {
            // Fetch the customer from DB
            val customer = dbHelper.getCustomerByEmail(email)
            if (customer != null) {
                // Save session
                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("customer_id", customer.id)
                editor.putString("customer_name", customer.name)
                editor.apply()
            }

            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CustomerScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Email already exists.", Toast.LENGTH_SHORT).show()
        }
    }
}