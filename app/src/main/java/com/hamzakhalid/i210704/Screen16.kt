package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hamzakhalid.integration.R


class Screen16 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen16, container, false)

        val callIcon = view.findViewById<ImageView>(R.id.CallIcon1)
        callIcon.setOnClickListener {
            val intent = Intent(context, Screen20Activity::class.java)
            startActivity(intent)
        }
        val VideoIcon = view.findViewById<ImageView>(R.id.VideoIcon1)
        VideoIcon.setOnClickListener {
            val intent = Intent(context, Screen19Activity::class.java)
            startActivity(intent)
        }

        val arrowBack16 = view.findViewById<ImageView>(R.id.arrow_back16)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack16.setOnClickListener {
            // Replace the current fragment with the Screen8 fragment
            replaceFragment(Screen14())
        }
        return view

    }
    fun onSendButtonClick(view: View) {
        // Your code to handle the button click goes here
    }
    private fun replaceFragment(fragment: Fragment){
        // Perform fragment transaction to replace the current fragment with the Screen8 fragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null) // Add to back stack to enable back navigation
        transaction.commit()
    }
}