package com.example.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuCustomer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_customer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val customerId = sharedPref.getInt("customer_id", -1)

        val historyButton = findViewById<Button>(R.id.HistoryButton)
        historyButton.setOnClickListener {
            if (customerId != -1) {
                val intent = Intent(this, OrderHistoryCustomer::class.java)
                intent.putExtra("customer_id", customerId)
                startActivity(intent)
            } else {
                // Optional: show a toast if ID is not found
            }
        }

        val logoutBtn = findViewById<Button>(R.id.logoutCustomer)
        logoutBtn.setOnClickListener {
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}