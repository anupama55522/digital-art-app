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

class ArtworkList(
    private val context: Context,
    private val artworkList: List<Artwork>
) : RecyclerView.Adapter<ArtworkList.ArtworkViewHolder>() {

    inner class ArtworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artworkImage: ImageView = itemView.findViewById(R.id.artworkImage)
        val artworkTitle: TextView = itemView.findViewById(R.id.artworkTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_artwork, parent, false)
        return ArtworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        val artwork = artworkList[position]
        holder.artworkTitle.text = artwork.title

        Glide.with(context)
            .load(artwork.imageUri)
            .placeholder(R.drawable.gallery)
            .into(holder.artworkImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArtworkDetails::class.java)
            intent.putExtra("artwork", artwork)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = artworkList.size
}