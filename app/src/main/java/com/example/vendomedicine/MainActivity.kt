package com.example.vendomedicine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
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

    // Bluetooth handler
    private lateinit var bluetoothHandler: BluetoothHandler

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize BluetoothHandler
        enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                bluetoothHandler.showBluetoothConnectionPrompt()
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
            }
        }

        checkBluetoothPermissions()

        // Initialize buttons
        val cancelButton: Button = findViewById(R.id.button)
        val proceedButton: Button = findViewById(R.id.button2)
        ibuprofenButton = findViewById(R.id.imageButton)
        paracetamolButton = findViewById(R.id.imageButton2)
        loperamideButton = findViewById(R.id.imageButton3)
        cetirizineButton = findViewById(R.id.imageButton4)

        // Initialize quantity TextViews
        ibuprofenQuantityTextView = findViewById(R.id.quantityTextView1)
        paracetamolQuantityTextView = findViewById(R.id.quantityTextView2)
        loperamideQuantityTextView = findViewById(R.id.quantityTextView3)
        cetirizineQuantityTextView = findViewById(R.id.quantityTextView4)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()

        // Set button click listeners
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

        // Set up medication buttons with data sending
        ibuprofenButton.setOnClickListener {
            handleButtonClick(ibuprofenButton, "Ibuprofen", ibuprofenQuantityTextView)
            bluetoothHandler.sendDataToESP32("1") // Send command for Ibuprofen
        }
        paracetamolButton.setOnClickListener {
            handleButtonClick(paracetamolButton, "Paracetamol", paracetamolQuantityTextView)
            bluetoothHandler.sendDataToESP32("2") // Send command for Paracetamol
        }
        loperamideButton.setOnClickListener {
            handleButtonClick(loperamideButton, "Loperamide", loperamideQuantityTextView)
            bluetoothHandler.sendDataToESP32("3") // Send command for Loperamide
        }
        cetirizineButton.setOnClickListener {
            handleButtonClick(cetirizineButton, "Cetirizine", cetirizineQuantityTextView)
            bluetoothHandler.sendDataToESP32("4") // Send command for Cetirizine
        }
    }

    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
        } else {
            initializeBluetoothHandler()
        }
    }

    private fun initializeBluetoothHandler() {
        bluetoothHandler = BluetoothHandler(this, enableBluetoothLauncher)
        bluetoothHandler.checkBluetoothPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeBluetoothHandler()
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }
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
        }, 1000)
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
        AlertDialog.Builder(this)
            .setTitle("No items selected")
            .setMessage("Please select at least one item to proceed with your order.")
            .setPositiveButton("OK", null)
            .show()
    }
}