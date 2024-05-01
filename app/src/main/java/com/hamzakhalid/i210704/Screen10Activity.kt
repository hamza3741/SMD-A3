package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hamzakhalid.integration.R
import com.squareup.picasso.Picasso

class Screen10Activity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen10)

        // Get the Intent that launched this activity
        val intent = intent

        // Extract the mentor name and description from the Intent
        val mentorName = intent.getStringExtra("mentorName")
        val imageurl = intent.getStringExtra("imageUrl")
        Log.d("Mentor Image URL", imageurl ?: "No image URL")

        val mentorDescription = intent.getStringExtra("mentorDescription")
        val mentorRate=intent.getStringExtra("mentorRate")

        // Update the name and description views
        updateMentorInfo(mentorName, mentorDescription)
        val contactImage1 = findViewById<ImageView>(R.id.contactImage1)

        // Append the folder path to the image URL
        val imageUrlWithFolder = "http://192.168.1.11/A3_MentorImages/$imageurl"

// Load the image using Picasso
        Picasso.get().load("http://192.168.1.11/A3_MentorImages/$imageurl").into(contactImage1)

        var btn1 = findViewById<Button>(R.id.ReviewBtn)
        btn1.setOnClickListener{
            val reviewIntent = Intent(this, Screen11Activity::class.java)
            reviewIntent.putExtra("mentorName", mentorName) // Add mentor name to the intent
            startActivity(reviewIntent)
        }
        var btn2 = findViewById<Button>(R.id.BookSessBtn)
        btn2.setOnClickListener{
            val reviewIntent = Intent(this, Screen13Activity::class.java)
            reviewIntent.putExtra("mentorName", mentorName) // Add mentor name to the intent
            reviewIntent.putExtra("mentorRate", mentorRate)
            reviewIntent.putExtra("mentorDescription",mentorDescription)
            startActivity(reviewIntent)
        }
        var btn3 = findViewById<Button>(R.id.JoinCommunityBtn)
        btn3.setOnClickListener{
            setContentView(R.layout.fragment_screen16) // Use screen16.xml layout directly
            // Perform fragment transaction to replace frame_container with Screen16 fragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, Screen16())
            fragmentTransaction.commit()
        }
        val arrowBack = findViewById<ImageView>(R.id.arrow_back10)
        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            startActivity(Intent(this, Screen7Activity::class.java))
        }
    }
    private fun updateMentorInfo(name: String?, description: String?) {
        nameTextView = findViewById(R.id.Name)
        descriptionTextView = findViewById(R.id.Description)

        // Concatenate the greeting with the mentor name
        val greeting = "Hi, I'm $name"
        nameTextView.text = greeting
        descriptionTextView.text = description
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
