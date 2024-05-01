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
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.hamzakhalid.integration.R
import java.util.Calendar
private const val TAG="Screen13Activity"
class Screen13Activity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var rateTextView: TextView
    private lateinit var calendarView: CalendarView
    private var selectedSlot: String? = null // Variable to store the selected time slot
    private var UserName: String? = null // Variable to store the selected time slot
    private var formattedDate: String? = null  // Local variable to store formatted date
    private lateinit var firebaseDatabase: FirebaseDatabase // Reference to Firebase Database
    private lateinit var mAuth: FirebaseAuth
    /*
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
     */
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "booking_channel"
        private const val NOTIFICATION_ID = 124
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen13)
        // Get Firebase Database instance
        firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val arrow_back13 = findViewById<ImageView>(R.id.arrow_back13)
        arrow_back13.setOnClickListener{
            startActivity(Intent(this, Screen10Activity::class.java))
        }
        // Get the Intent that launched this activity
        val intent = intent

        // Extract the mentor name and description from the Intent
        val mentorName = intent.getStringExtra("mentorName")
        val mentorRate = intent.getStringExtra("mentorRate")
        val mentorDescription=intent.getStringExtra("mentorDescription")
        // Update the name and description views
        updateMentorInfo(mentorName, mentorRate)

        calendarView = findViewById(R.id.calendarView2)

        // Set onDateChangeListener for the CalendarView
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Extract the date components
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val day = selectedDate.get(Calendar.DAY_OF_MONTH)
            val month = selectedDate.get(Calendar.MONTH)  // Months are 0-indexed
            val year = selectedDate.get(Calendar.YEAR)

            // Format the date string in the desired format
             formattedDate = String.format("%dth %s %d", day, getMonthName(month), year)

            // Do something with the formatted date
            Log.d("SELECTED_DATE", "Formatted Date: $formattedDate")
            // You can update UI elements or store the formatted date in a variable.
        }
        // Find buttons by their IDs
        val slotBtn9 = findViewById<Button>(R.id.SlotBtn9)
        val slotBtn10 = findViewById<Button>(R.id.SlotBtn10)
        val slotBtn11 = findViewById<Button>(R.id.SlotBtn11)
        val slotBtn12 = findViewById<Button>(R.id.SlotBtn12)
        val slotBtn13 = findViewById<Button>(R.id.SlotBtn13)
        val slotBtn14 = findViewById<Button>(R.id.SlotBtn14)
        val slotBtn15 = findViewById<Button>(R.id.SlotBtn15)
        val slotBtn16 = findViewById<Button>(R.id.SlotBtn16)
        val slotBtn17 = findViewById<Button>(R.id.SlotBtn17)
        slotBtn9.setOnClickListener {
            selectedSlot = "9:00 AM"
        }
        slotBtn10.setOnClickListener {
            selectedSlot = "10:00 AM"
        }
        slotBtn11.setOnClickListener {
            selectedSlot = "11:00 AM"
        }
        slotBtn12.setOnClickListener {
            selectedSlot = "12:00 PM"
        }
        slotBtn13.setOnClickListener {
            selectedSlot = "1:00 PM"
        }
        slotBtn14.setOnClickListener {
            selectedSlot = "2:00 PM"
        }
        slotBtn15.setOnClickListener {
            selectedSlot = "3:00 PM"
        }
        slotBtn16.setOnClickListener {
            selectedSlot = "4:00 PM"
        }
        slotBtn17.setOnClickListener {
            selectedSlot = "5:00 PM"
        }
        val BookAppointmentBtn = findViewById<Button>(R.id.AppointmentBtn1)
        BookAppointmentBtn.setOnClickListener {
            // Check if a slot is selected
            /*
            if (selectedSlot == null) {
                Log.d("BOOKING", "Please select a time slot!")
                // You can display a toast message to indicate no slot is selected
                return@setOnClickListener
            }

            // Get current user ID (assuming you have a method to retrieve it)
            val currentUserId = getLoggedInUserId() // Replace with your user ID retrieval method

            getUserName(
                userId = currentUserId,
                onSuccess = { retrievedUsername ->
                    // Username retrieved successfully
                    Log.d("USERNAME", "Username assigned: $retrievedUsername")
                    UserName = retrievedUsername // Assuming UserName is a variable where you want to store the username
                    // Proceed with booking logic or any other operations that require the username
                    // For example:
                    // performBookingLogic(retrievedUsername)
                },
                onFailure = { exception ->
                    // Handle username retrieval error
                    Log.e("USERNAME", "Error retrieving username", exception)
                }
            )
            // Check if formatted date and mentor info are available (should be from previous steps)
            if (formattedDate == null || mentorName == null || mentorDescription == null) {
                Log.e("BOOKING", "Missing appointment details!")
                // Handle missing data scenario (e.g., show error message)
                return@setOnClickListener
            }

            val userName = UserName

            // Create a new appointment data object
            val appointment = Appointment(
                currentUserId,
                userName.toString(),
                formattedDate!!,
                selectedSlot!!,
                mentorName!!,
                mentorDescription!!
            )

            // Get a reference to the appointments node in your database
            val appointmentsRef = firebaseDatabase.getReference("appointments")

            // Push the appointment data as a new child node with a unique key
            appointmentsRef.push().setValue(appointment)
                .addOnSuccessListener {
                    Log.d("BOOKING", "Appointment booked successfully!")
                    // You can display a success message or navigate to another screen
                }
                .addOnFailureListener { exception ->
                    Log.e("BOOKING", "Appointment booking failed!", exception)
                    // Handle booking failure (e.g., show error message)
                }

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM token: $token")
                    // Use 'token' to send push notifications
                    sendPushNotification("New Booking Added", "New Booking Added", token)
                } else {
                    Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                }
            }
            sendNotification("New Booking Added", "New Booking Added")*/
            // Check if a slot is selected
            if (selectedSlot == null) {
                Log.d("BOOKING", "Please select a time slot!")
                // You can display a toast message to indicate no slot is selected
                return@setOnClickListener
            }

            // Get current user ID (assuming you have a method to retrieve it)
            val currentUserId = getLoggedInUserId() // Replace with your user ID retrieval method

            getUserName(
                userId = currentUserId,
                onSuccess = { retrievedUsername ->
                    // Username retrieved successfully
                    Log.d("USERNAME", "Username assigned: $retrievedUsername")
                    // Proceed with booking logic or any other operations that require the username
                    // For example:
                    // performBookingLogic(retrievedUsername)

                    // Check if formatted date and mentor info are available (should be from previous steps)
                    if (formattedDate == null || mentorName == null || mentorDescription == null) {
                        Log.e("BOOKING", "Missing appointment details!")
                        // Handle missing data scenario (e.g., show error message)
                        return@getUserName
                    }

                    val url = "http://192.168.1.11/A3_insertAppointments.php"
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
                                    sendPushNotification("New Booking Added", "New Booking Added", token)
                                } else {
                                    Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                                }
                            }
                            sendNotification("New Booking Added", "New Booking Added")
                        },
                        Response.ErrorListener { error ->
                            // Handle error
                            Log.e("API Error", "Error occurred: ${error.message}")
                        }) {
                        override fun getParams(): MutableMap<String, String> {
                            val params = HashMap<String, String>()
                            params["UserID"] = currentUserId
                           params["UserName"] = retrievedUsername
                            params["name"] = mentorName
                            params["description"] = mentorDescription
                            params["date"] = formattedDate!!
                            params["timeslot"] = selectedSlot!!
                            return params
                        }
                    }

                    // Add the request to the RequestQueue
                    Volley.newRequestQueue(this).add(stringRequest)

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            Log.d(TAG, "FCM token: $token")
                            // Use 'token' to send push notifications
                            sendPushNotification("New Booking Added", "New Booking Added", token)
                        } else {
                            Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                        }
                    }
                    sendNotification("New Booking Added", "New Booking Added")
                },
                onFailure = { exception ->
                    // Handle username retrieval error
                    Log.e("USERNAME", "Error retrieving username", exception)
                }
            )
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
        notificationManager.notify(Screen13Activity.NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun getMonthName(month: Int): String {
        val monthNames = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return monthNames[month]
    }
    private fun getUserName(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val usersRef = firebaseDatabase.getReference("users")
        val userRef = usersRef.child(userId)

        userRef.child("name").get().addOnSuccessListener { nameSnapshot ->
            val username = nameSnapshot.value as? String
            if (username != null) {
                onSuccess(username)
            } else {
                onFailure(Exception("Username not found for userID: $userId"))
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
    private fun updateMentorInfo(name: String?, rate: String?) {
        nameTextView = findViewById(R.id.Name2)
        // Concatenate the greeting with the mentor name
        nameTextView.text = name
        rateTextView=findViewById(R.id.textfee)
        // Concatenate the greeting with the mentor name
        val charges = "$$rate/Session"
        rateTextView.text = charges
    }
    // Function to retrieve current user ID (replace with your implementation)
    private fun getLoggedInUserId(): String {
        // Get current user's UID
        val currentUser = mAuth.currentUser
        val uid = currentUser?.uid

        // Check if uid is null, throw an exception, or return a default value
        if (uid == null) {
            throw IllegalStateException("No logged in user found!")  // Or return an empty string or handle differently
        }
        return uid
    }

    // Data class to store appointment details
    data class Appointment(
        val userId: String,
        val userName: String,
        val date: String,
        val timeSlot: String,
        val mentorName: String,
        val mentorDescription: String
    )
}
