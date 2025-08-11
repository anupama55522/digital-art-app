package com.example.learn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageArtist : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtistList
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var artistList: MutableList<Artist>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_artist)

        recyclerView = findViewById(R.id.rvArtists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        artistList = databaseHelper.getAllArtists().toMutableList()

        adapter = ArtistList(this, artistList, databaseHelper)
        recyclerView.adapter = adapter
    }

    fun refreshArtistList() {
        artistList.clear()
        artistList.addAll(databaseHelper.getAllArtists())
        adapter.notifyDataSetChanged()
    }
}