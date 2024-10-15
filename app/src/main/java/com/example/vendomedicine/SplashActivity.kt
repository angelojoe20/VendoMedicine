package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class SplashActivity : AppCompatActivity() {

    private lateinit var rfidInput: EditText
    private val validRFIDs = listOf(
        "0004315586",
        "0004713969",
        "0004278059",
        "0004300970",
        "0004726985",
        "0004709548",
        "0004674353",
        "0006127120",
        "0004666016",
        "0004709554"
    )
    private var scannedRFID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        rfidInput = findViewById(R.id.rfidInput)

        // Clear placeholder text when EditText gains focus
        rfidInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                rfidInput.setText("") // Clear the placeholder text
                rfidInput.setTextColor(resources.getColor(android.R.color.black))
            }
        }

        // Listen to input changes and process RFID when 10 characters are entered
        rfidInput.addTextChangedListener {
            val inputRFID = rfidInput.text.toString().trim()
            if (inputRFID.length == 10) {
                processRFID(inputRFID)
            }
        }
    }

    private fun processRFID(inputRFID: String) {
        Log.d("SplashActivity", "Scanned RFID: '$inputRFID', Length: ${inputRFID.length}")

        if (validRFIDs.contains(inputRFID)) {
            if (scannedRFID == null) {
                scannedRFID = inputRFID
                rfidInput.setText(scannedRFID)

                // Automatically proceed to MainActivity
                Log.d("SplashActivity", "Proceeding to MainActivity...")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "RFID already scanned: '$scannedRFID'.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("SplashActivity", "Invalid RFID detected: '$inputRFID'")
            Toast.makeText(this, "Invalid RFID, try again", Toast.LENGTH_SHORT).show()
        }
    }
}
