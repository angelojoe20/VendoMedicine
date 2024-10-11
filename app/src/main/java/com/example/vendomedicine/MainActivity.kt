package com.example.vendomedicine

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

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
    private val REQUEST_LOCATION_PERMISSION = 1
    private val REQUEST_ENABLE_BT = 2
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectedDevice: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null

    // Define UUIDs
    private val SERVICE_UUID = UUID.fromString("1bf2a612-29c3-4a82-9b3d-b9abc9e81daa")
    private val CHARACTERISTIC_UUID = UUID.fromString("45088d05-aa3b-42da-aa75-bf85d5046829")

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
            //sendDataToESP32("1")
        }
        paracetamolButton.setOnClickListener {
            handleButtonClick(paracetamolButton, "Paracetamol", paracetamolQuantityTextView)
            //sendDataToESP32("2")
        }
        loperamideButton.setOnClickListener {
            handleButtonClick(loperamideButton, "Loperamide", loperamideQuantityTextView)
            //sendDataToESP32("3")
        }
        cetirizineButton.setOnClickListener {
            handleButtonClick(cetirizineButton, "Cetirizine", cetirizineQuantityTextView)
            //sendDataToESP32("4")
        }

        // Check for Bluetooth permissions and connect to the device
        //checkBluetoothPermissions() // Ensure this is called here after definition
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

 ///////////////////////////////////BLUETOOTH INITIALIZATION/////////////////////////////////////////////////////

    /*private fun initializeBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            // Bluetooth is enabled, show connection prompt
            showBluetoothConnectionPrompt()
        }
    }

    private fun showBluetoothConnectionPrompt() {
        val pairedDevices = bluetoothAdapter?.bondedDevices
        val deviceNames = pairedDevices?.map { it.name }?.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Connect to ESP32")
            .setItems(deviceNames) { dialog: DialogInterface, which: Int ->
                val device = pairedDevices?.elementAt(which)
                device?.let {
                    connectToDevice(it)
                }
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("Bluetooth", "Connected to GATT server.")
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("Bluetooth", "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic = gatt.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID)
            }
        }
    }

    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), REQUEST_LOCATION_PERMISSION)
        } else {
            initializeBluetooth()
        }
    }

    private fun sendDataToESP32(data: String) {
        characteristic?.let {
            it.value = data.toByteArray()
            val success = bluetoothGatt?.writeCharacteristic(it) ?: false
            if (success) {
                Log.i("Bluetooth", "Data sent: $data")
                Toast.makeText(this, "Data sent: $data", Toast.LENGTH_SHORT).show() // Debug message
            } else {
                Log.e("Bluetooth", "Failed to send data: $data")
                Toast.makeText(this, "Failed to send data: $data", Toast.LENGTH_SHORT).show() // Error message
            }
        } ?: run {
            Log.e("Bluetooth", "Characteristic is null, cannot send data.")
            Toast.makeText(this, "Characteristic is null, cannot send data.", Toast.LENGTH_SHORT).show() // Error message
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permissions are required to proceed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
*/