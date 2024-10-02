package com.example.vendomedicine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var quantity1 = 0
    private var quantity2 = 0
    private var quantity3 = 0
    private var quantity4 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cancelButton: Button = findViewById(R.id.button)
        val proceedButton: Button = findViewById(R.id.button2)
        val floatingActionButton1: FloatingActionButton = findViewById(R.id.floatingActionButton)
        val floatingActionButton2: FloatingActionButton = findViewById(R.id.floatingActionButton2)
        val floatingActionButton3: FloatingActionButton = findViewById(R.id.floatingActionButton3)
        val floatingActionButton4: FloatingActionButton = findViewById(R.id.floatingActionButton4)
        val quantityTextView1: TextView = findViewById(R.id.quantityTextView1)
        val quantityTextView2: TextView = findViewById(R.id.quantityTextView2)
        val quantityTextView3: TextView = findViewById(R.id.quantityTextView3)
        val quantityTextView4: TextView = findViewById(R.id.quantityTextView4)

        cancelButton.setOnClickListener {
            // Handle cancel button click
        }

        proceedButton.setOnClickListener {
            val intent = Intent(this, ProcessPayment::class.java)
            startActivity(intent)
        }

        floatingActionButton1.setOnClickListener {
            updateQuantity(quantityTextView1, 1)
        }

        floatingActionButton2.setOnClickListener {
            updateQuantity(quantityTextView2, 1)
        }

        floatingActionButton3.setOnClickListener {
            updateQuantity(quantityTextView3, 1)
        }

        floatingActionButton4.setOnClickListener {
            updateQuantity(quantityTextView4, 1)
        }
    }

    private fun updateQuantity(textView: TextView, change: Int) {
        var quantity = textView.text.toString().toInt()
        quantity += change
        if (quantity < 0) quantity = 0
        textView.text = quantity.toString()
    }
}