package com.example.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerList(private val context: Context, private val customerList: List<Customer>) :
    RecyclerView.Adapter<CustomerList.CustomerViewHolder>() {

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvCustomerName)
        val emailTextView: TextView = itemView.findViewById(R.id.tvCustomerEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customerList[position]
        holder.nameTextView.text = customer.name
        holder.emailTextView.text = customer.email
    }

    override fun getItemCount(): Int = customerList.size
}