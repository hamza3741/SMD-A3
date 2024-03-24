package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.hamzakhalid.integration.R

class Screen24Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen24)
        val arrow_back24 = findViewById<ImageView>(R.id.arrow_back24)
        arrow_back24.setOnClickListener{
            startActivity(Intent(this, Screen7Activity::class.java))
        }
    }
}
