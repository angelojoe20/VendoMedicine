package com.example.vendomedicine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val maxQuantity = 4
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SelectedItemsAdapter
    private val selectedItems = mutableListOf<SelectedItem>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cancelButton: Button = findViewById(R.id.button)
        val proceedButton: Button = findViewById(R.id.button2)
        val ibuprofenButton: Button = findViewById(R.id.button5)
        val paracetamolButton: Button = findViewById(R.id.button8)
        val loperamideButton: Button = findViewById(R.id.button7)
        val cetirizineButton: Button = findViewById(R.id.button6)
        val quantityTextView1: TextView = findViewById(R.id.quantityTextView1)
        val quantityTextView2: TextView = findViewById(R.id.quantityTextView2)
        val quantityTextView3: TextView = findViewById(R.id.quantityTextView3)
        val quantityTextView4: TextView = findViewById(R.id.quantityTextView4)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = SelectedItemsAdapter(selectedItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        cancelButton.setOnClickListener {
            resetQuantities(quantityTextView1, quantityTextView2, quantityTextView3, quantityTextView4)
        }

        proceedButton.setOnClickListener {
            val intent = Intent(this, ProcessPayment::class.java)
            startActivity(intent)
        }

        ibuprofenButton.setOnClickListener {
            setQuantityToMax(quantityTextView1, "Ibuprofen")
        }

        paracetamolButton.setOnClickListener {
            setQuantityToMax(quantityTextView2, "Paracetamol")
        }

        loperamideButton.setOnClickListener {
            setQuantityToMax(quantityTextView3, "Loperamide")
        }

        cetirizineButton.setOnClickListener {
            setQuantityToMax(quantityTextView4, "Cetirizine")
        }
    }

    private fun setQuantityToMax(textView: TextView, itemName: String) {
        textView.text = maxQuantity.toString()
        selectedItems.add(SelectedItem(itemName, maxQuantity))
        adapter.notifyDataSetChanged()
    }

    private fun resetQuantities(vararg textViews: TextView) {
        for (textView in textViews) {
            textView.text = "0"
        }
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }
}