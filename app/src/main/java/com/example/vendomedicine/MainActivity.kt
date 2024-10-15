package com.example.vendomedicine

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
     val maxQuantity = 4
     lateinit var recyclerView: RecyclerView
     lateinit var adapter: SelectedItemsAdapter
     val selectedItems = mutableListOf<SelectedItem>()

     val productPrices = mapOf(
        "Ibuprofen" to 17.0,
        "Paracetamol" to 6.0,
        "Loperamide" to 10.0,
        "Cetirizine" to 15.0
    )

     lateinit var ibuprofenButton: Button
     lateinit var paracetamolButton: Button
     lateinit var loperamideButton: Button
     lateinit var cetirizineButton: Button

     lateinit var imageView: ImageView

     lateinit var ibuprofenQuantityTextView: TextView
     lateinit var paracetamolQuantityTextView: TextView
     lateinit var loperamideQuantityTextView: TextView
     lateinit var cetirizineQuantityTextView: TextView

     val defaultButtonColor = Color.parseColor("#e4b3e1")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize buttons
        val cancelButton: Button = findViewById(R.id.button)
        val proceedButton: Button = findViewById(R.id.button2)
        ibuprofenButton = findViewById(R.id.button5)
        paracetamolButton = findViewById(R.id.button8)
        loperamideButton = findViewById(R.id.button7)
        cetirizineButton = findViewById(R.id.button6)

        imageView = findViewById(R.id.imageView)

        // Initialize quantity TextViews
        ibuprofenQuantityTextView = findViewById(R.id.quantityTextView1)
        paracetamolQuantityTextView = findViewById(R.id.quantityTextView2)
        loperamideQuantityTextView = findViewById(R.id.quantityTextView3)
        cetirizineQuantityTextView = findViewById(R.id.quantityTextView4)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()

        cancelButton.setOnClickListener {
            resetQuantities()
            resetButtonColors()
            imageView.setImageResource(0)
        }

        proceedButton.setOnClickListener {
            if (selectedItems.isEmpty()) {
                showNoOrderNotification()
                return@setOnClickListener
            }

            var totalAmount = 0.0
            for (item in selectedItems) {
                val pricePerUnit = productPrices[item.name] ?: 0.0
                totalAmount += item.quantity * pricePerUnit
            }

            val intent = Intent(this, ProcessPayment::class.java)
            intent.putParcelableArrayListExtra("SELECTED_ITEMS", ArrayList(selectedItems))
            intent.putExtra("TOTAL_AMOUNT", totalAmount.toString())
            startActivity(intent)
        }

        // Set up medication buttons
        ibuprofenButton.setOnClickListener {
            handleButtonClick(ibuprofenButton, "Ibuprofen", ibuprofenQuantityTextView)
        }
        paracetamolButton.setOnClickListener {
            handleButtonClick(paracetamolButton, "Paracetamol", paracetamolQuantityTextView)
        }
        loperamideButton.setOnClickListener {
            handleButtonClick(loperamideButton, "Loperamide", loperamideQuantityTextView)
        }
        cetirizineButton.setOnClickListener {
            handleButtonClick(cetirizineButton, "Cetirizine", cetirizineQuantityTextView)
        }
    }

     fun resetButtonColors() {
        ibuprofenButton.setBackgroundColor(defaultButtonColor)
        paracetamolButton.setBackgroundColor(defaultButtonColor)
        loperamideButton.setBackgroundColor(defaultButtonColor)
        cetirizineButton.setBackgroundColor(defaultButtonColor)
    }

     fun handleButtonClick(button: Button, itemName: String, quantityTextView: TextView) {
        var quantity = quantityTextView.text.toString().toIntOrNull() ?: 0

        if (quantity < maxQuantity) {
            quantity++
            quantityTextView.text = quantity.toString()
            button.setBackgroundColor(Color.parseColor("#FFD700")) // Change color to indicate selection
            selectedItems.add(SelectedItem(itemName, quantity))
        } else {
            Toast.makeText(this, "Maximum quantity reached for $itemName", Toast.LENGTH_SHORT).show()
        }
    }

     fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SelectedItemsAdapter(selectedItems)
        recyclerView.adapter = adapter
    }

     fun showNoOrderNotification() {
        AlertDialog.Builder(this)
            .setTitle("No Order")
            .setMessage("You haven't selected any medicines yet.")
            .setPositiveButton("OK", null)
            .show()
    }

     fun resetQuantities() {
        ibuprofenQuantityTextView.text = "0"
        paracetamolQuantityTextView.text = "0"
        loperamideQuantityTextView.text = "0"
        cetirizineQuantityTextView.text = "0"
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }
}
