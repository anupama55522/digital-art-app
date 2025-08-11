package com.example.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.orderArtTitle)
        val address: TextView = itemView.findViewById(R.id.orderAddress)
        val status: TextView = itemView.findViewById(R.id.orderStatus)
        val customize: TextView = itemView.findViewById(R.id.orderCustomize)
        val customStatus: TextView = itemView.findViewById(R.id.orderCustomStatus)
        val date: TextView = itemView.findViewById(R.id.orderDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.title.text = "Title: ${order.artTitle}"
        holder.address.text = "Shipping Address: ${order.address}"
        holder.status.text = "Order Status: ${order.status}"
        holder.customize.text = "Customization: ${order.customize ?: "None"}"
        holder.customStatus.text = "Customization Status: ${order.customStatus}"
        holder.date.text = "Date: ${order.date}"
    }

    override fun getItemCount(): Int = orders.size
}