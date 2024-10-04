package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProcessPayment : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectedItemsAdapter: SelectedItemsAdapter
    private lateinit var selectedItems: List<SelectedItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.process_payment)

        // Retrieve data from Intent
        selectedItems = intent.getParcelableArrayListExtra<SelectedItem>("SELECTED_ITEMS") ?: emptyList()
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT") ?: "0.00"

        // Find TextView in process_payment.xml
        val amountTextView: TextView = findViewById(R.id.textView3)


        amountTextView.text = "AMOUNT: ₱$totalAmount"

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
            showReceiptDialog()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, selectedItems: List<SelectedItem>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        selectedItemsAdapter = SelectedItemsAdapter(selectedItems)
        recyclerView.adapter = selectedItemsAdapter
    }

    // Function to show the dialog for receipt
    private fun showReceiptDialog() {
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
    private fun clearOrderHistoryAndNavigate() {
        // Clear order history (you may want to use SharedPreferences or another method)
        clearOrderHistory() // Call to your clearing method

        // Navigate back to activity_main.xml
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
        startActivity(intent)
        finish() // Finish current activity
    }

    // Placeholder for clearing order history
    private fun clearOrderHistory() {
        // Implement your logic here to clear the order history
        // For example, if you are using SharedPreferences:
        val sharedPreferences = getSharedPreferences("VendoMed", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("order_history") // Change this key to whatever you're using
        editor.apply()
    }

    // Function to display a notification (placeholder for actual notification logic)
    private fun showNotification(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show() // Show the notification dialog
    }
}
