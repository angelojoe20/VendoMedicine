package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val proceedButton: Button = findViewById(R.id.button2)
        proceedButton.setOnClickListener {
            val intent = Intent(this, ProcessPayment::class.java)
            startActivity(intent)
        }
    }
}