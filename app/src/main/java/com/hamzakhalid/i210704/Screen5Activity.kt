package com.hamzakhalid.i210704

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.hamzakhalid.integration.R

class Screen5Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen5)
        var SendBtn = findViewById<Button>(R.id.SendBtn)
       SendBtn.setOnClickListener{
            startActivity(Intent(this, Screen6Activity::class.java))
        }
        var TextViewLogin = findViewById<TextView>(R.id.TextViewLogin)
        TextViewLogin.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
        val arrow_back5 = findViewById<ImageView>(R.id.arrow_back5)
        arrow_back5.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
    }
}