package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var rfidInput: EditText
    private lateinit var confirmButton: Button
    private val validRFIDs = listOf("0004278059", "0004713969", "0004300970", "0004709554", "0004726985")
    private var scannedRFID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        rfidInput = findViewById(R.id.rfidInput)
        confirmButton = findViewById(R.id.confirmButton)

        rfidInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                rfidInput.setText("") // Clear the placeholder text
                rfidInput.setTextColor(resources.getColor(android.R.color.black))
            }
        }

        confirmButton.setOnClickListener {
            processRFID()
        }
    }

    private fun processRFID() {
        try {
            val inputRFID = rfidInput.text.toString().trim()
            Log.d("SplashActivity", "Scanned RFID: '$inputRFID', Length: ${inputRFID.length}")

            if (inputRFID.length == 10) {
                if (validRFIDs.contains(inputRFID)) {
                    if (scannedRFID == null) {
                        scannedRFID = inputRFID
                        rfidInput.setText(scannedRFID)
                        showConfirmationDialog()
                    } else {
                        Toast.makeText(this, "RFID already scanned: '$scannedRFID'.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("SplashActivity", "Invalid RFID detected: '$inputRFID'")
                    Toast.makeText(this, "Invalid RFID, try again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid 10-digit RFID", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error processing RFID", e)
            Toast.makeText(this, "An error occurred while scanning. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Proceed to Main Activity")
        builder.setMessage("Do you want to proceed to the Main Activity?")
        builder.setPositiveButton("Yes") { _, _ ->
            Log.d("SplashActivity", "Proceeding to MainActivity...")

            // Use a try-catch to handle the transition to MainActivity
            try {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("SplashActivity", "Error starting MainActivity", e)
                Toast.makeText(this, "Error starting MainActivity.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            Log.d("SplashActivity", "User chose not to proceed to MainActivity.")
        }
        builder.create().show()
    }
}
