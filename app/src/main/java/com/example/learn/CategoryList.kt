package com.example.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryList(
    private val context: Context,
    private val categories: List<String>,
    private val onCategoryClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<CategoryList.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryText.text = category


        holder.itemView.setOnClickListener {
            onCategoryClick?.invoke(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}