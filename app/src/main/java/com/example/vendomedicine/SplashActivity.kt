// SPLASH

package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    // Bluetooth handler instance
    // private lateinit var bluetoothHandler: BluetoothHandler
    // private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

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

        // Commenting out Bluetooth registration as it's no longer needed
        // enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        //     if (result.resultCode == RESULT_OK) {
        //         bluetoothHandler.initializeBluetooth()
        //     } else {
        //         Toast.makeText(this, "Bluetooth permission required to proceed", Toast.LENGTH_SHORT).show()
        //     }
        // }

        // Initialize Bluetooth handler
        // bluetoothHandler = BluetoothHandler(this, enableBluetoothLauncher)
        // bluetoothHandler.checkBluetoothPermissions()
    }

    private fun initializeRFIDReader() {
        // Implement your RFID reader initialization logic here
    }

    private fun disableRFIDReader() {
        // Implement logic to disable RFID reader
        Log.d("SplashActivity", "RFID reader disabled.")
    }

    private fun processRFID(inputRFID: String) {
        Log.d("SplashActivity", "Scanned RFID: '$inputRFID', Length: ${inputRFID.length}")

        if (validRFIDs.contains(inputRFID)) {
            if (scannedRFID == null) {
                scannedRFID = inputRFID // Save the scanned RFID to prevent rescan
                Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show()
                disableRFIDReader() // Disable RFID reader

                // Proceed to MainActivity
                val intent = Intent(this, MainActivity::class.java).apply {
                    // Commenting out Bluetooth connection passing as it's no longer needed
                    // putExtra("BLUETOOTH_CONNECTED", bluetoothHandler.isBluetoothConnected)
                }
                startActivity(intent)
                finish() // Close SplashActivity
            } else {
                Toast.makeText(this, "RFID already scanned", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid RFID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disableRFIDReader() // Ensure the RFID reader is disabled when the activity is destroyed
    }
}
