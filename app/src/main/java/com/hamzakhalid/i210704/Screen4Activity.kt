package com.hamzakhalid.i210704

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.hamzakhalid.integration.R

class Screen4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen4)
        var VerifyBtn1 = findViewById<Button>(R.id.VerifyBtn)
        VerifyBtn1.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
        val arrow_back4 = findViewById<ImageView>(R.id.arrow_back4)
        arrow_back4.setOnClickListener{
            startActivity(Intent(this, Screen3Activity::class.java))
        }

    }
}