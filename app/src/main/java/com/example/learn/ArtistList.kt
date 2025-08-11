package com.example.learn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ArtistList(
    private val context: Context,
    private val artistList: MutableList<Artist>,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<ArtistList.ArtistViewHolder>() {

    class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvArtistName)
        val emailTextView: TextView = itemView.findViewById(R.id.tvArtistEmail)
        val viewCertButton: Button = itemView.findViewById(R.id.btnViewCertificate)
        val acceptButton: Button = itemView.findViewById(R.id.btnAccept)
        val rejectButton: Button = itemView.findViewById(R.id.btnReject)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artistList[position]
        holder.nameTextView.text = artist.name
        holder.emailTextView.text = artist.email

        if (artist.verify == "accepted") {
            holder.acceptButton.text = "Accepted"
            holder.acceptButton.isEnabled = false
            holder.rejectButton.visibility = View.GONE
        } else if (artist.verify == "rejected") {
            holder.rejectButton.text = "Rejected"
            holder.rejectButton.isEnabled = false
            holder.acceptButton.visibility = View.GONE
        } else {
            holder.acceptButton.text = "Accept"
            holder.acceptButton.isEnabled = true
            holder.acceptButton.visibility = View.VISIBLE
            holder.rejectButton.text = "Reject"
            holder.rejectButton.isEnabled = true
            holder.rejectButton.visibility = View.VISIBLE
        }

        holder.viewCertButton.setOnClickListener {
            if (!artist.certPath.isNullOrEmpty()) {
                val intent = Intent(context, ViewCertificate::class.java).apply {
                    putExtra("CERT_PATH", artist.certPath)
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Certificate not available", Toast.LENGTH_SHORT).show()
            }
        }

        holder.acceptButton.setOnClickListener {
            if (databaseHelper.updateArtistVerification(artist.id, "accepted")) {
                artistList[position] = artist.copy(verify = "accepted")
                notifyItemChanged(position) // Refresh UI
                Toast.makeText(context, "${artist.name} accepted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to accept ${artist.name}", Toast.LENGTH_SHORT).show()
            }
        }

        holder.rejectButton.setOnClickListener {
            if (databaseHelper.updateArtistVerification(artist.id, "rejected")) {
                artistList[position] = artist.copy(verify = "rejected")  // Update list
                notifyItemChanged(position)
                Toast.makeText(context, "${artist.name} rejected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to reject ${artist.name}", Toast.LENGTH_SHORT).show()
            }
        }

        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Artist")
            builder.setMessage("Are you sure you want to delete ${artist.name}?")

            builder.setPositiveButton("Yes") { _, _ ->
                val success = databaseHelper.deleteArtist(artist.id)
                if (success) {
                    artistList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, artistList.size)
                    Toast.makeText(context, "Artist deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete artist", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    override fun getItemCount(): Int = artistList.size
}