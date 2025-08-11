package com.example.learn

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ArtistOrdersActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var ordersListView: ListView
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_orders)

        ordersListView = findViewById(R.id.artistOrdersListView)
        dbHelper = DatabaseHelper(this)

        val artistId = intent.getIntExtra("artist_id", -1)
        if (artistId == -1) {
            Toast.makeText(this, "Artist not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val orders = dbHelper.getOrdersByArtistId(artistId)
        val adapter = ArtistOrderList(this, orders)
        ordersListView.adapter = adapter
    }
}