package com.example.learn

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomerScreen : AppCompatActivity() {

    private lateinit var allArtworks: List<Artwork>
    private lateinit var artworkAdapter: ArtworkCustomerAdapter
    private var currentCategory: String? = null
    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_screen)

        val sharedPref: SharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val customerId = sharedPref.getInt("customer_id", -1)

        val menuIcon = findViewById<ImageView>(R.id.menuCustomer)
        menuIcon.setOnClickListener {
            val intent = Intent(this, MenuCustomer::class.java)
            startActivity(intent)
        }


        val dbHelper = DatabaseHelper(this)
        allArtworks = dbHelper.getAllArtworksForCustomer()

        val artworkRecycler = findViewById<RecyclerView>(R.id.artworkRecyclerCustomer)
        artworkRecycler.layoutManager = LinearLayoutManager(this)
        artworkAdapter = ArtworkCustomerAdapter(this, allArtworks)
        artworkRecycler.adapter = artworkAdapter


        val categoryRecycler = findViewById<RecyclerView>(R.id.categoryRecycler)
        categoryRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categoryList = resources.getStringArray(R.array.art_categories).toList()
        val categoryAdapter = CategoryList(this, categoryList) { selectedCategory ->
            currentCategory = selectedCategory
            applyFilters()
        }
        categoryRecycler.adapter = categoryAdapter


        val searchInput = findViewById<EditText>(R.id.searchProduct)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim().lowercase()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applyFilters() {
        val filteredList = allArtworks.filter { artwork ->
            val matchesTitle = artwork.title.lowercase().contains(currentSearchQuery)
            val matchesCategory = currentCategory?.let {
                artwork.category.equals(it, ignoreCase = true)
            } ?: true
            matchesTitle && matchesCategory
        }
        artworkAdapter.updateData(filteredList)
    }
}