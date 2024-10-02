package com.example.vendomedicine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val maxQuantity = 4  // Set the maximum quantity here

    @SuppressLint("MissingInflatedId")
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
            resetQuantities(quantityTextView1, quantityTextView2, quantityTextView3, quantityTextView4)
        }

        proceedButton.setOnClickListener {
            val intent = Intent(this, ProcessPayment::class.java)
            startActivity(intent)
        }

        floatingActionButton1.setOnClickListener {
            setQuantityToMax(quantityTextView1)
        }

        floatingActionButton2.setOnClickListener {
            setQuantityToMax(quantityTextView2)
        }

        floatingActionButton3.setOnClickListener {
            setQuantityToMax(quantityTextView3)
        }

        floatingActionButton4.setOnClickListener {
            setQuantityToMax(quantityTextView4)
        }
    }

    private fun setQuantityToMax(textView: TextView) {
        val currentQuantity = textView.text.toString().toInt()
        if (currentQuantity < maxQuantity) {
            textView.text = maxQuantity.toString()
        }
    }

    private fun resetQuantities(vararg textViews: TextView) {
        for (textView in textViews) {
            textView.text = "0"
        }
    }
}
