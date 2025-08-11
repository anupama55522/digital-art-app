package com.example.learn

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.favre.lib.crypto.bcrypt.BCrypt

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        dbHelper = DatabaseHelper(this)

        val emailField = findViewById<EditText>(R.id.adminLoginEmail)
        val passwordField = findViewById<EditText>(R.id.adminLoginPassword)
        val loginButton = findViewById<Button>(R.id.adminLoginButton)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                if (validateAdmin(email, password)) {
                    val intent = Intent(this, AdminScreen::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateAdmin(email: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        var storedHashedPassword: String? = null

        val cursor: Cursor = db.rawQuery(
            "SELECT admin_password FROM admin WHERE admin_email = ?",
            arrayOf(email)
        )

        if (cursor.moveToFirst()) {
            storedHashedPassword = cursor.getString(0)
        }

        cursor.close()
        db.close()

        return storedHashedPassword != null &&
                BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword).verified
    }
}