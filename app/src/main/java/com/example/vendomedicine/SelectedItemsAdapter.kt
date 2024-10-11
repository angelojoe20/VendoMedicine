package com.example.vendomedicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for selected items
class SelectedItemsAdapter(
    private val items: List<SelectedItem>, // No need to redefine SelectedItem
    private val itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder>() {

    // ViewHolder class for holding item views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemQuantity: TextView = view.findViewById(R.id.itemQuantity)
    }

    // Interface for handling item clicks
    interface OnItemClickListener {
        fun onItemClick(selectedItem: SelectedItem)
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_order, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemQuantity.text = "Quantity: ${item.quantity}"

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    // Return the size of the items list
    override fun getItemCount() = items.size
}