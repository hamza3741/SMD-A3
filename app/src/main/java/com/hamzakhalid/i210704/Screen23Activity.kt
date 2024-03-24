package com.hamzakhalid.i210704

//import ScreenshotObserver
//import com.google.android.gms.common.api.Response
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.hamzakhalid.integration.R

private const val SERVER_KEY = "AAAAjountnY:APA91bH9wlNPn0bnH5DjK0Yt8EhwBhs-bYQGa1f2uMgwStjfcoultSwJWrY6eOIKzadKf5L8Qbp2piskpC9uyaXyGzb6lpK8X9PPgpphxM_dEFrKsbPr8s3I1CHzqdajSiaUwISxRQJg"
private const val SENDER_ID = "612228380278"
private const val TAG="Screen23Activity"
private lateinit var firebaseDatabase: FirebaseDatabase // Reference to Firebase Database
private lateinit var mAuth: FirebaseAuth
class Screen23Activity : AppCompatActivity(), ScreenshotDetectionDelegate.ScreenshotDetectionListener {
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009
            const val NOTIFICATION_CHANNEL_ID = "screenshot_channel"
            private const val NOTIFICATION_ID = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.screen23)

        firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Initialize RecyclerView
        appointmentRecyclerView = findViewById(R.id.appointmentrecycler)
        appointmentRecyclerView.layoutManager = LinearLayoutManager(this)

        // Assuming you have a list of appointments, you need to pass it to the adapter
        val appointmentsList = ArrayList<Appointment>() // Replace it with your list of appointments
        appointmentAdapter = AppointmentAdapter(appointmentsList)
        appointmentRecyclerView.adapter = appointmentAdapter
        // Start observing the screenshot directory
        checkReadExternalStoragePermission()

        // Call fetchAppointments to fetch appointments from the database
        fetchAppointments()

        val arrow_back23 = findViewById<ImageView>(R.id.arrow_back23)
        arrow_back23.setOnClickListener {
            setContentView(R.layout.fragment_screen21) // Use screen16.xml layout directly
            // Perform fragment transaction to replace frame_container with Screen16 fragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, Screen21())
            fragmentTransaction.commit()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestReadExternalStoragePermission()
        }
    }
    private fun fetchAppointments() {
        val currentUserId = mAuth.currentUser?.uid ?: return // Get current user ID
        val appointmentsRef = firebaseDatabase.reference.child("appointments").orderByChild("userId").equalTo(currentUserId)

        appointmentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointmentsList = mutableListOf<Appointment>()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let {
                        appointmentsList.add(it)
                    }
                }
                appointmentAdapter = AppointmentAdapter(appointmentsList)
                appointmentRecyclerView.adapter = appointmentAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch appointments: $error")
                Toast.makeText(applicationContext, "Failed to fetch appointments", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    private fun showReadExternalStoragePermissionDeniedMessage() {
        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
    }

    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    override fun onScreenCaptured(path: String) {
        Log.d(TAG, "On Screen Captured: Take action when screen was captued -> $path")
        showToast("Toast Message => Screenshot captured: $path")


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM token: $token")
                // Use 'token' to send push notifications
                sendPushNotification("ScreenShot Detected", "ScreenShot Captured", token)
            } else {
                Log.e(TAG, "Failed to get FCM token: ${task.exception}")
            }
        }
        sendNotification("Screenshot Captured", "Screenshot captured")
        // Send push notification when a screenshot is captured
        // sendPushNotification("ScreenShot Detected", "ScreenShot Captured")
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

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }



    override fun onScreenCapturedWithDeniedPermission() {
        Log.d(TAG, "onScreenCapturedWithDeniedPermission: Permission to read external storage")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    /*
    private fun sendPushNotification(title: String, message: String, deviceToken: String) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val serverKey = "AAAAjountnY:APA91bH9wlNPn0bnH5DjK0Yt8EhwBhs-bYQGa1f2uMgwStjfcoultSwJWrY6eOIKzadKf5L8Qbp2piskpC9uyaXyGzb6lpK8X9PPgpphxM_dEFrKsbPr8s3I1CHzqdajSiaUwISxRQJg"
        val contentType = "application/json"

        val jsonBody = JSONObject()
        jsonBody.put("to", deviceToken)
        jsonBody.put("priority", "high")

        val data = JSONObject()
        data.put("title", title)
        data.put("body", message)

        jsonBody.put("data", data)

        val requestBody = jsonBody.toString()

        val request = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response ->
                // Handle successful response
                Log.d(TAG, "Push notification sent successfully")
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.e(TAG, "Failed to send push notification: ${error.message}")
            }) {
            override fun getBodyContentType(): String {
                return contentType
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=$serverKey"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }*/

    /*
    private fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "key=$SERVER_KEY"
        return headers
    }

    private fun sendPushNotification(title: String, message: String) {
        val data = mapOf(
            "title" to title,
            "body" to message
        )

        val remoteMessage = RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
            .setMessageId(nextMsgId().toString())
            .setData(data)
            .build()

        Firebase.messaging.send(remoteMessage)
    }


    // Function to generate unique message ID
    private fun nextMsgId(): Int {
        return (Math.random() * 1000).toInt()
    }*/
}