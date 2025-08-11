package com.example.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ArtworksActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtworkList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artworks)

        dbHelper = DatabaseHelper(this)
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val artistId = sharedPref.getInt("artist_id", -1)

        recyclerView = findViewById(R.id.artworkRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this,2)

        val artworks = dbHelper.getArtworksByArtist(artistId)
        adapter = ArtworkList(this, artworks)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.addArtworkButton).setOnClickListener {
            startActivity(Intent(this, CreateArtwork::class.java))
        }
    }
}