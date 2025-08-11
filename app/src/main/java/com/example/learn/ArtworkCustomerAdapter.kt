package com.example.learn

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ArtworkCustomerAdapter(
    private val context: Context,
    private var artworks: List<Artwork> // Renamed to 'artworks' and made mutable
) : RecyclerView.Adapter<ArtworkCustomerAdapter.ArtworkViewHolder>() {

    class ArtworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.artworkImageCustomer)
        val title: TextView = itemView.findViewById(R.id.artworkTitleCustomer)
    }

    fun updateData(newList: List<Artwork>) {
        artworks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_artwork_customer, parent, false)
        return ArtworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        val artwork = artworks[position]
        holder.title.text = artwork.title

        holder.image.setOnClickListener {
            val intent = Intent(context, ArtworkDetailsCustomer::class.java)
            intent.putExtra("artwork", artwork)
            context.startActivity(intent)
        }

        if (!artwork.imageUri.isNullOrEmpty()) {
            Glide.with(context)
                .load(artwork.imageUri)
                .placeholder(R.drawable.gallery)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.gallery)
        }
    }

    override fun getItemCount(): Int {
        return artworks.size
    }
}