package com.example.learn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageCustomer : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerList
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_customer)

        recyclerView = findViewById(R.id.rvCustomer)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        val customerList = databaseHelper.getAllCustomers()

        adapter = CustomerList(this, customerList)
        recyclerView.adapter = adapter
    }
}