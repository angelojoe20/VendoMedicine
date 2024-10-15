package com.example.vendomedicine

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothHandler(private val context: Context, private val enableBluetoothLauncher: ActivityResultLauncher<Intent>) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectedDevice: BluetoothDevice? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    private val REQUEST_LOCATION_PERMISSION = 1

    // New flag to indicate Bluetooth connection state
    var isBluetoothConnected = false

    fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as AppCompatActivity), arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                ), REQUEST_LOCATION_PERMISSION
            )
        } else {
            initializeBluetooth()
        }
    }

    fun initializeBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(
                context,
                "Bluetooth is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else if (!isBluetoothConnected) {
            showBluetoothConnectionPrompt()
        }
    }

    fun showBluetoothConnectionPrompt() {
        val pairedDevices = bluetoothAdapter?.bondedDevices
        val deviceNames = pairedDevices?.map { it.name }?.toTypedArray()

        if (deviceNames != null) {
            AlertDialog.Builder(context)
                .setTitle("Connect to ESP32")
                .setItems(deviceNames) { _, which: Int ->
                    val selectedDevice = pairedDevices.elementAt(which)
                    connectToDevice(selectedDevice)
                }
                .show()
        } else {
            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        connectedDevice = device
        try {
            val socket = device.createRfcommSocketToServiceRecord(
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            )
            socket.connect()
            outputStream = socket.outputStream
            inputStream = socket.inputStream
            Toast.makeText(context, "Connected to ${device.name}", Toast.LENGTH_SHORT).show()
            isBluetoothConnected = true
            (context as ProcessPayment).sendMedicineData() // Automatically send data
        } catch (e: IOException) {
            Log.e("Bluetooth", "Connection failed: ${e.message}")
            Toast.makeText(context, "Connection failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendDataToESP32(command: Char) {
        if (isBluetoothConnected) {
            try {
                val byteArray = byteArrayOf(command.toByte())
                outputStream?.write(byteArray)
                Log.d("Bluetooth", "Data sent: $command")
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error sending data: ${e.message}")
            }
        } else {
            Toast.makeText(context, "Bluetooth not connected", Toast.LENGTH_SHORT).show()
        }
    }
}
