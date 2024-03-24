package com.hamzakhalid.i210704

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hamzakhalid.integration.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen1)
        var btn1 = findViewById<Button>(R.id.invisibleBtn)
        btn1.setOnClickListener{
            startActivity(Intent(this, Screen2Activity::class.java))
        }
    }
}