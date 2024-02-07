package com.example.mapmarkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val buttonStart: Button = findViewById(R.id.button_first)
        buttonStart.setOnClickListener {
            finish()
        }
    }
}