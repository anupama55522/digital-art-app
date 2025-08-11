package com.example.learn

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryCustomer : AppCompatActivity() {

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history_customer)

        orderRecyclerView = findViewById(R.id.orderHistoryRecyclerView)
        emptyTextView = findViewById(R.id.emptyOrderText)
        db = DatabaseHelper(this)

        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val customerId = sharedPref.getInt("customer_id", -1)
        Log.d("OrderHistory", "Customer ID: $customerId")

        if (customerId != -1) {
            val orders = db.getOrdersByCustomerId(customerId)
            Log.d("OrderHistory", "Orders found: ${orders.size}")

            if (orders.isNotEmpty()) {
                orderRecyclerView.layoutManager = LinearLayoutManager(this)
                orderRecyclerView.adapter = OrderAdapter(orders)
                orderRecyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            } else {
                orderRecyclerView.visibility = View.GONE
                emptyTextView.text = "No orders found."
                emptyTextView.visibility = View.VISIBLE
            }
        } else {
            orderRecyclerView.visibility = View.GONE
            emptyTextView.text = "User not logged in."
            emptyTextView.visibility = View.VISIBLE
        }
    }
}