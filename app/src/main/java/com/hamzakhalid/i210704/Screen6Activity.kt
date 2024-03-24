package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hamzakhalid.integration.R

class Screen6Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen6)
        var ResetBtn = findViewById<Button>(R.id.ResetBtn)
        ResetBtn.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
        var TextViewLogin = findViewById<TextView>(R.id.TextViewLogin)
        TextViewLogin.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
        val arrow_back6 = findViewById<ImageView>(R.id.arrow_back6)
        arrow_back6.setOnClickListener{
            startActivity(Intent(this, Screen5Activity::class.java))
        }
    }
}
