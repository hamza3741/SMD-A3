package com.hamzakhalid.i210704

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.hamzakhalid.integration.R

//data class MentorReview(val mentorName: String, val feedback: String)
private const val TAG="Screen11Activity"
class Screen11Activity : AppCompatActivity() {
    private lateinit var nameTextView: TextView

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009
        const val NOTIFICATION_CHANNEL_ID = "review_channel"
        private const val NOTIFICATION_ID = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen11)
        val arrow_back11 = findViewById<ImageView>(R.id.arrow_back11)
        arrow_back11.setOnClickListener{
            startActivity(Intent(this, Screen10Activity::class.java))
        }
        // Get the Intent that launched this activity
        val intent = intent

        // Extract the mentor name and description from the Intent
        val mentorName = intent.getStringExtra("mentorName")
        val mentorDescription = intent.getStringExtra("mentorDescription")

        // Update the name and description views
        updateMentorInfo(mentorName, mentorDescription)
        var SubmitFeedback = findViewById<Button>(R.id.SubmitFeedback)
        // Initialize the editTextExperience variable with the reference to the EditText
        val editTextExperience = findViewById<EditText>(R.id.editTextExperience)
        SubmitFeedback.setOnClickListener {
            /*
            val feedback = editTextExperience.text.toString().trim()

            if (feedback.isNotEmpty()) {
                // Create MentorReview object
                val mentorReview = MentorReview(mentorName ?: "", feedback)

                // Insert into the database
                insertMentorReview(mentorReview)

                // Clear the editText
                editTextExperience.setText("")

                // Show toast message
                Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please provide feedback", Toast.LENGTH_SHORT).show()
            }
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM token: $token")
                    // Use 'token' to send push notifications
                    sendPushNotification("Feedback Added", "", token)
                } else {
                    Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                }
            }
            sendNotification("Feedback Added", "")*/
            val feedback = editTextExperience.text.toString().trim()
            val url = "http://192.168.1.11/A3_insertMentorReviews.php"
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // Handle successful response
                    Log.d("API Response", response)
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            Log.d(TAG, "FCM token: $token")
                            // Use 'token' to send push notifications
                            sendPushNotification("Feedback Added", "", token)
                        } else {
                            Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                        }
                    }
                    sendNotification("Feedback Added", "")
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Log.e("API Error", "Error occurred: ${error.message}")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["MentorName"] = mentorName ?: ""
                    params["feedback"] = feedback
                    return params
                }
            }

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(stringRequest)
        }
    }
    private fun insertMentorReview(mentorReview: MentorReview) {
        // Firebase Realtime Database reference
        val database = FirebaseDatabase.getInstance()
        val mentorReviewsRef = database.getReference("mentor_reviews")

        // Push the MentorReview object to the database
        val reviewId = mentorReviewsRef.push().key
        reviewId?.let {
            mentorReviewsRef.child(it).setValue(mentorReview)
        }
    }
    private fun sendPushNotification(title: String, message: String, deviceToken: String) {
        val data = mapOf(
            "title" to title,
            "body" to message
        )

        val remoteMessage = RemoteMessage.Builder(deviceToken + "@fcm.googleapis.com")
            .setMessageId(nextMsgId().toString())
            .setData(data)
            .build()

        FirebaseMessaging.getInstance().send(remoteMessage)
    }


    // Function to generate unique message ID
    private fun nextMsgId(): Int {
        return (Math.random() * 1000).toInt()
    }
    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,
            Screen23Activity.NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Screen23Activity.NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(Screen11Activity.NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun updateMentorInfo(name: String?, description: String?) {
        nameTextView = findViewById(R.id.Name1)
        // Concatenate the greeting with the mentor name
        val greeting = "Hi, I'm $name"
        nameTextView.text = greeting
    }
}
