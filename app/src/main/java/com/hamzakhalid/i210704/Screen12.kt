package com.hamzakhalid.i210704

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.hamzakhalid.integration.R
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
private const val TAG="Screen12"
private var selectedImageUri: Uri? = null

private const val GALLERY_REQUEST_CODE = 100

class Screen12 : Fragment() {
    private lateinit var spinnerStatus: Spinner
    private lateinit var nameEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var sessionRateEditText: EditText
    private lateinit var uploadMentorButton: Button

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "mentor_channel"
        private const val NOTIFICATION_ID = 124
    }
    // Reference to Firebase Database
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen12, container, false)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        nameEditText = view.findViewById(R.id.editTextName)
        descriptionEditText = view.findViewById(R.id.editTextDescription)
        sessionRateEditText = view.findViewById(R.id.editTextSessionRate)
        uploadMentorButton = view.findViewById(R.id.UploadBtn1)
        imageView = view.findViewById(R.id.profileIconImageView)

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference.child("mentors")

        val arrowBack = view.findViewById<ImageView>(R.id.arrow_back12)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            // Create an intent to start Screen18 activity
            val intent = Intent(activity, Screen7Activity::class.java)
            startActivity(intent)
        }
        // Find the Spinner by ID
        val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)

        // Define options
        val statusOptions = arrayOf("Available", "Not Available")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerStatus.adapter = adapter

        val uploadPhotoButton: Button = view.findViewById(R.id.btnUploadPhoto1)
        // Set click listener for the button
        uploadPhotoButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        uploadMentorButton.setOnClickListener {
            /*
            uploadMentor()
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM token: $token")
                    // Use 'token' to send push notifications
                    sendPushNotification("New Mentor Added", "", token)
                } else {
                    Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                }
            }
            sendNotification("New Mentor Added", "")
            // Send push notification when a screenshot is captured
            // sendPushNotification("ScreenShot Detected", "ScreenShot Captured")

             */

            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val status = spinnerStatus.selectedItem.toString()
            val rate = sessionRateEditText.text.toString()

            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
            val url = "http://192.168.1.11/A3_insertMentor.php"
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
                            sendPushNotification("New Mentor Added", "", token)
                        } else {
                            Log.e(TAG, "Failed to get FCM token: ${task.exception}")
                        }
                    }
                    sendNotification("New Mentor Added", "")
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Log.e("API Error", "Error occurred: ${error.message}")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["name"] = name
                    params["description"] = description
                    params["rate"] = rate
                    params["status"] = status
                    if (selectedImageUri != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            selectedImageUri
                        )
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        val imageBytes = byteArrayOutputStream.toByteArray()
                        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                        params["image"] = encodedImage
                    }
                    return params
                }
            }
            // Add the request to the RequestQueue
            Volley.newRequestQueue(requireContext()).add(stringRequest)
            }
            else{
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.addMentor(name, description, rate, status, "")
            }
        }

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            // Do something with the selected image URI, for example:
            selectedImageUri?.let {
                imageView.setImageURI(it)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the button by ID
        val uploadPhotoButton: Button = view.findViewById(R.id.btnUploadPhoto1)

        // Set click listener for the button
        /*
        uploadPhotoButton.setOnClickListener {
            // Create an intent to start Screen17 activity
            val intent = Intent(activity, Screen17Activity::class.java)
            startActivity(intent)
        }*/
        val uploadVideoButton: Button = view.findViewById(R.id.btnUploadVideo1)
        uploadVideoButton.setOnClickListener {
            // Create an intent to start Screen18 activity
            val intent = Intent(activity, Screen18Activity::class.java)
            startActivity(intent)
        }
    }
    private fun uploadMentor() {
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val status = spinnerStatus.selectedItem.toString()
        val rate = sessionRateEditText.text.toString()

        // Check if any field is empty
        if (name.isEmpty() || description.isEmpty() || rate.isEmpty()) {
            // Show error message if any field is empty
            showToast("Please fill in all fields.")
            return
        }

        // Create a mentor object
        val mentor = Mentor(name, description, status, rate)

        // Push mentor object to the database
        val mentorId = databaseReference.push().key // Get unique key for mentor
        mentorId?.let {
            databaseReference.child(it).setValue(mentor).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Show success message if mentor is uploaded successfully
                    showToast("Mentor uploaded successfully!")
                } else {
                    // Show failure message if mentor upload fails
                    showToast("Failed to upload mentor: ${task.exception?.message}")
                }
            }
        }
    }
    // Helper function to show Toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)

            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Screen12.NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(Screen12.NOTIFICATION_ID, notificationBuilder.build())
    }
}