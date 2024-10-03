package com.example.vendomedicine

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProcessPayment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.process_payment)

        val backButton: Button = findViewById(R.id.button3)
        backButton.setOnClickListener {
            finish()
        }
    }
}