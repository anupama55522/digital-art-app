package com.example.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ArtistOrderList(
    private val context: Context,
    private val orders: List<Order>
) : BaseAdapter() {

    override fun getCount(): Int {
        return orders.size
    }

    override fun getItem(position: Int): Any {
        return orders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_artist_order, parent, false)

        val order = orders[position]

        val orderTitle: TextView = view.findViewById(R.id.orderTitle)
        val orderAddress: TextView = view.findViewById(R.id.orderAddress)
        val orderRequest: TextView = view.findViewById(R.id.orderRequest)
        val orderStatus: TextView = view.findViewById(R.id.orderStatus)

        orderTitle.text = order.artTitle
        orderAddress.text = "Address: ${order.address}"
        orderRequest.text = "Customization: ${order.customize}"
        orderStatus.text = "Status: ${order.status}"

        return view
    }
}