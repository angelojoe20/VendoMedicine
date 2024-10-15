package com.example.vendomedicine

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class BluetoothHandler(private val context: Context, private val enableBluetoothLauncher: ActivityResultLauncher<Intent>) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectedDevice: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null

    private val SERVICE_UUID = UUID.fromString("1bf2a612-29c3-4a82-9b3d-b9abc9e81daa")
    private val CHARACTERISTIC_UUID = UUID.fromString("45088d05-aa3b-42da-aa75-bf85d5046829")

    private val REQUEST_LOCATION_PERMISSION = 1

    fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                (context as MainActivity), arrayOf(
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
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            showBluetoothConnectionPrompt()
        }
    }

   fun showBluetoothConnectionPrompt() {
        val pairedDevices = bluetoothAdapter?.bondedDevices
        val deviceNames = pairedDevices?.map { it.name }?.toTypedArray()

        if (deviceNames != null) {
            AlertDialog.Builder(context)
                .setTitle("Connect to ESP32")
                .setItems(deviceNames) { dialog: DialogInterface, which: Int ->
                    val selectedDevice = pairedDevices?.elementAt(which)
                    selectedDevice?.let {
                        connectToDevice(it)
                    }
                }
                .show()
        } else {
            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        connectedDevice = device
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt?.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(SERVICE_UUID)
                characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
            }
        }
    }

    fun sendDataToESP32(data: String) {
        characteristic?.let {
            it.value = data.toByteArray()
            bluetoothGatt?.writeCharacteristic(it)
        }
    }
}
