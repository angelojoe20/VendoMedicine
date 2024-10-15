package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProcessPayment : AppCompatActivity() {
     lateinit var recyclerView: RecyclerView
     lateinit var selectedItemsAdapter: SelectedItemsAdapter
     lateinit var selectedItems: List<SelectedItem>

    // Bluetooth variables
     lateinit var bluetoothHandler: BluetoothHandler
     lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.process_payment)

        // Initialize Bluetooth
        initBluetooth()

        // Retrieve data from Intent
        selectedItems = intent.getParcelableArrayListExtra<SelectedItem>("SELECTED_ITEMS") ?: emptyList()
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT") ?: "0.00"

        // Find TextView in process_payment.xml
        val amountTextView: TextView = findViewById(R.id.textView3)

        // Set the retrieved values into the TextView for total amount
        amountTextView.text = "AMOUNT: ₱$totalAmount" // Format to show peso sign

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView2)
        setupRecyclerView(recyclerView, selectedItems)

        // Back button functionality
        val backButton: Button = findViewById(R.id.button3)
        backButton.setOnClickListener {
            finish()
        }

        // Payment button functionality
        val paymentButton: Button = findViewById(R.id.button4)
        paymentButton.setOnClickListener {
            if (bluetoothHandler.isBluetoothConnected) {
                sendMedicineData() // Send data when Bluetooth is connected
                showReceiptDialog()
            } else {
                bluetoothHandler.showBluetoothConnectionPrompt() // Show connection prompt
            }
        }
    }

     fun initBluetooth() {
        enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                bluetoothHandler.initializeBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permission required to proceed", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize Bluetooth handler
        bluetoothHandler = BluetoothHandler(this, enableBluetoothLauncher)
        bluetoothHandler.checkBluetoothPermissions()
    }

     fun setupRecyclerView(recyclerView: RecyclerView, selectedItems: List<SelectedItem>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        selectedItemsAdapter = SelectedItemsAdapter(selectedItems)
        recyclerView.adapter = selectedItemsAdapter
    }

    // Function to send medicine data based on selected items
     fun sendMedicineData() {
        selectedItems.forEach { item ->
            when (item.name) {
                "Ibuprofen" -> bluetoothHandler.sendDataToESP32('1')
                "Paracetamol" -> bluetoothHandler.sendDataToESP32('2')
                "Loperamide" -> bluetoothHandler.sendDataToESP32('3')
                "Cetirizine" -> bluetoothHandler.sendDataToESP32('4')
            }
        }
    }

    // Function to show the dialog for receipt
     fun showReceiptDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Receipt")
        builder.setMessage("Do you want to receive a receipt for your purchase?")

        // If the user chooses "Yes"
        builder.setPositiveButton("Yes") { _, _ ->
            // Handle receipt logic here (e.g., generate and send receipt)
            showNotification("Receipt", "Your receipt will be sent shortly.")
            clearOrderHistoryAndNavigate()
        }

        // If the user chooses "No"
        builder.setNegativeButton("No") { _, _ ->
            showNotification("No Receipt", "No receipt will be provided.")
            clearOrderHistoryAndNavigate() // Still navigate back even if no receipt is chosen
        }

        // Show the dialog
        builder.create().show()
    }

    // Function to clear order history and navigate back to main activity
     fun clearOrderHistoryAndNavigate() {
        clearOrderHistory() // Call to your clearing method

        // Navigate back to activity_main.xml
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
        startActivity(intent)
        finish() // Finish current activity
    }

    // Placeholder for clearing order history
     fun clearOrderHistory() {
        val sharedPreferences = getSharedPreferences("VendoMed", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("order_history") // Change this key to whatever you're using
        editor.apply()
    }

    // Function to display a notification (placeholder for actual notification logic)
     fun showNotification(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show() // Show the notification dialog
    }
}
