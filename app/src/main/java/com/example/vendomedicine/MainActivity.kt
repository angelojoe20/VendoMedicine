package com.example.vendomedicine

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val maxQuantity = 4
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SelectedItemsAdapter
    private val selectedItems = mutableListOf<SelectedItem>()

    private val productPrices = mapOf(
        "Ibuprofen" to 17.0,
        "Paracetamol" to 6.0,
        "Loperamide" to 10.0,
        "Cetirizine" to 15.0
    )

    private lateinit var ibuprofenButton: Button
    private lateinit var paracetamolButton: Button
    private lateinit var loperamideButton: Button
    private lateinit var cetirizineButton: Button
    private lateinit var imageView: ImageView

    private lateinit var ibuprofenQuantityTextView: TextView
    private lateinit var paracetamolQuantityTextView: TextView
    private lateinit var loperamideQuantityTextView: TextView
    private lateinit var cetirizineQuantityTextView: TextView

    private val defaultButtonColor = Color.parseColor("#e4b3e1")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        ibuprofenButton.setOnClickListener { handleButtonClick(ibuprofenButton, "Ibuprofen", ibuprofenQuantityTextView) }
        paracetamolButton.setOnClickListener { handleButtonClick(paracetamolButton, "Paracetamol", paracetamolQuantityTextView) }
        loperamideButton.setOnClickListener { handleButtonClick(loperamideButton, "Loperamide", loperamideQuantityTextView) }
        cetirizineButton.setOnClickListener { handleButtonClick(cetirizineButton, "Cetirizine", cetirizineQuantityTextView) }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SelectedItemsAdapter(selectedItems)
        recyclerView.adapter = adapter
    }

    private fun handleButtonClick(button: Button, itemName: String, quantityTextView: TextView) {
        val existingItem = selectedItems.find { it.name == itemName }
        val newQuantity = maxQuantity // Set quantity directly to max

        if (existingItem == null) {
            selectedItems.add(SelectedItem(itemName, newQuantity)) // Add item with max quantity
        } else {
            existingItem.quantity = newQuantity // Set existing item to max quantity
        }

        // Update the quantity TextView
        quantityTextView.text = newQuantity.toString()

        button.setBackgroundColor(Color.GREEN)
        when (itemName) {
            "Ibuprofen" -> imageView.setImageResource(R.drawable.iprubofen)
            "Paracetamol" -> imageView.setImageResource(R.drawable.paracetamol)
            "Loperamide" -> imageView.setImageResource(R.drawable.loperamide)
            "Cetirizine" -> imageView.setImageResource(R.drawable.cetirizine)
        }

        button.postDelayed({
            button.setBackgroundColor(defaultButtonColor)
        }, 100)

        adapter.notifyDataSetChanged()
    }

    private fun resetQuantities() {
        selectedItems.clear()
        ibuprofenQuantityTextView.text = "0"
        paracetamolQuantityTextView.text = "0"
        loperamideQuantityTextView.text = "0"
        cetirizineQuantityTextView.text = "0"
        adapter.notifyDataSetChanged()
    }

    private fun resetButtonColors() {
        ibuprofenButton.setBackgroundColor(defaultButtonColor)
        paracetamolButton.setBackgroundColor(defaultButtonColor)
        loperamideButton.setBackgroundColor(defaultButtonColor)
        cetirizineButton.setBackgroundColor(defaultButtonColor)
    }

    private fun showNoOrderNotification() {
        Toast.makeText(this, "No orders selected!", Toast.LENGTH_SHORT).show()
    }
}