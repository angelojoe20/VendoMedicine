package com.example.vendomedicine

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun setupRecyclerView(recyclerView: RecyclerView, context: Context, selectedItems: List<SelectedItem>) {
    recyclerView.layoutManager = LinearLayoutManager(context)
    val adapter = SelectedItemsAdapter(selectedItems)
    recyclerView.adapter = adapter
}
