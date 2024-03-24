package com.hamzakhalid.i210704
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hamzakhalid.integration.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Screen15 : Fragment(), MessageAdapter.MessageEditListener {
    private var mentorName: String? = null
    private lateinit var editTextMessage: EditText // Reference to the EditText where the user enters the message
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: MutableList<Message>
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 123 // Use any integer value
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mediaPlayer: MediaPlayer // Declare mediaPlayer here

    private var isRecording = false
    private lateinit var mediaRecorder: MediaRecorder
    private var audioFilePath: String? = null

    private lateinit var firebaseDatabase: FirebaseDatabase // Reference to Firebase Database
    private lateinit var mAuth: FirebaseAuth
    private lateinit var callIcon: ImageView
    private lateinit var VideoIcon: ImageView

    private val PERMISSION_CODE = 101
    private fun requestPermissions() {
        val fragmentContext = requireContext()

        if (fragmentContext.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
            } || fragmentContext.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }) {
            // Here you can show explanation to the user asynchronously
        }

        // Request the permissions
        requestPermissions(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions were granted
                    Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    // Permissions were denied
                    Toast.makeText(requireContext(), "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initiate the call again
                    callIcon.performClick()
                } else {
                    // Permission denied, inform the user
                    Toast.makeText(requireContext(), "Permission denied to make a phone call", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_VIDEO_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initiate the video call again
                    VideoIcon.performClick()
                } else {
                    // Permission denied, inform the user
                    Toast.makeText(requireContext(), "Permission denied to make a video call", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // Handle other request codes if needed
            }
        }
    }

    // Constants
    companion object {
        private const val PERMISSION_CODE_VIDEO_CALL = 122 // Define the permission code for video call
        private val REQUEST_CODE_PICK_FILE = 126 // Use any integer value
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 124
        private const val REQUEST_CODE_PICK_MEDIA = 125
        private const val PERMISSION_CODE_CALL_PHONE = 123 // Use any integer value
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen15, container, false)

        // Extract mentor name from arguments
        mentorName = arguments?.getString("mentorName")

        // Set mentor name to the TextView
        val nameTextView = view.findViewById<TextView>(R.id.Name)
        nameTextView.text = mentorName

        editTextMessage = view.findViewById(R.id.editTextMessage)
        recyclerView = view.findViewById(R.id.messages_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        messageList = mutableListOf()
        messageAdapter = MessageAdapter(messageList, this)
        recyclerView.adapter = messageAdapter

        audioFilePath = "${requireContext().getExternalFilesDir(null)?.absolutePath}/audio_recording.3gp"
        //audioFilePath = "${requireContext().externalCacheDir?.absolutePath}/audiorecordtest.3gp"
      // audioFilePath = "/Music/audio_recording.mp3"
        mediaRecorder = MediaRecorder()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            )
        }

         callIcon = view.findViewById<ImageView>(R.id.CallIcon)
        callIcon.setOnClickListener {
           // val intent = Intent(context, Screen20Activity::class.java)
           // startActivity(intent)
            // Check if the device supports telephony features
            if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                // Device supports telephony, proceed with making the call
                val phoneNumber = "03333010648" // Replace with actual phone number
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")

                // Check for CALL_PHONE permission
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start the call
                    startActivity(callIntent)
                } else {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_CODE_CALL_PHONE)
                }
            } else {
                // Device does not support telephony, inform the user
                Toast.makeText(requireContext(), "This device does not support phone calls.", Toast.LENGTH_SHORT).show()
            }
        }


         VideoIcon = view.findViewById<ImageView>(R.id.VideoIcon)
        VideoIcon.setOnClickListener {
           // val intent = Intent(context, Screen19Activity::class.java)
           // startActivity(intent)
            initiateVideoCall()
        }
        val arrowBack15 = view.findViewById<ImageView>(R.id.arrow_back15)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack15.setOnClickListener {
            // Replace the current fragment with the Screen8 fragment
            replaceFragment(Screen14())
        }
        val sendButton = view.findViewById<ImageView>(R.id.SendButton)
        // Set OnClickListener to the SendButton icon
        sendButton.setOnClickListener {
            // Call insertMessage function when SendButton is clicked
            onSendButtonClick(view)
        }
        val CameraButton = view.findViewById<ImageView>(R.id.CameraButton)
        // Set OnClickListener to the SendButton icon
        CameraButton.setOnClickListener {
            // Create an intent to open the camera application
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Check if there's a camera activity available to handle the intent
            if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
                // Start the camera activity
                startActivity(cameraIntent)
            } else {
                // Handle the case where there's no camera activity available
                Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
            }
        }


        val GalleryButton = view.findViewById<ImageView>(R.id.GalleryButton)

        GalleryButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission to read external storage if not granted
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
                )
            } else {
                openGallery()
            }
        }

        val AttachmentButton = view.findViewById<ImageView>(R.id.AttachmentButton)
        AttachmentButton.setOnClickListener{
            openFilePicker()
        }


        val micButton = view.findViewById<ImageView>(R.id.micButton)
        micButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRecording()
                }
                MotionEvent.ACTION_UP -> {
                    stopRecording()
                    onSendVoiceNote()
                }
            }
            true
        }

        micButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permissions if not granted
                requestPermissions()
            } else {
                micButton.performClick()
            }
        }
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("messages")
        // Listen for changes in the database and update the RecyclerView
        // Listen for changes in the database and update the RecyclerView
        // Listen for changes in the database and update the RecyclerView
        databaseReference.addValueEventListener(object : ValueEventListener {
          /*  override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                for (snapshot in dataSnapshot.children) {
                    val messageId = snapshot.child("messageId").getValue(String::class.java)
                    val messageText = snapshot.child("messageText").getValue(String::class.java)
                    val messageType = snapshot.child("messageType").getValue(String::class.java)
                    val timestampString = snapshot.child("timestamp").getValue(String::class.java)
                    val mediaUrl = snapshot.child("audioFilePath").getValue(String::class.java) // Retrieve audioFilePath as mediaUrl

                    // Parse the timestamp string into milliseconds since epoch
                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestampString)?.time ?: 0L

                    val message = Message(
                        messageId ?: "",
                        messageType?.let { MessageType.valueOf(it) },
                        messageText,
                        mediaUrl,
                        null,
                        null,
                        timestamp
                    )

                    message?.let { messageList.add(it) }
                }
                messageAdapter.notifyDataSetChanged()
            }*/
          override fun onDataChange(dataSnapshot: DataSnapshot) {
              messageList.clear()
              for (snapshot in dataSnapshot.children) {
                  val messageId = snapshot.child("messageId").getValue(String::class.java)
                  val messageText = snapshot.child("messageText").getValue(String::class.java)
                  val messageType = snapshot.child("messageType").getValue(String::class.java)
                  val timestampString = snapshot.child("timestamp").getValue(String::class.java)
                  val mediaUrl = snapshot.child("mediaUrl").getValue(String::class.java) // Retrieve mediaUrl

                  // Parse the timestamp string into milliseconds since epoch
                  val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestampString)?.time ?: 0L

                  val message = Message(
                      messageId ?: "",
                      messageType?.let { MessageType.valueOf(it) },
                      messageText,
                      mediaUrl, // Pass mediaUrl to the Message constructor
                      null,
                      null,
                      timestamp
                  )

                  message?.let { messageList.add(it) }
              }
              messageAdapter.notifyDataSetChanged()
          }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        return view

    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Allow any file type to be selected
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }
    private fun initiateVideoCall() {
        val phoneNumber = "03333010648" // Replace with the actual phone number
        val videoCallIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:$phoneNumber"))

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(videoCallIntent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_CODE_CALL_PHONE
            )
        }
    }

    private fun saveFileMessageToDatabase(fileUri: String) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val messageId = databaseReference.push().key
        val messageData = mapOf(
            "messageId" to messageId,
            "messageType" to MessageType.FILE.name, // Assuming it's a file
            "mediaUrl" to fileUri, // Update the key to mediaUrl
            "userID" to userID,
            "mentorName" to mentorName,
            "timestamp" to timeStamp
        )

        messageId?.let {
            databaseReference.child(it).setValue(messageData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Message saved successfully
                    } else {
                        // Error handling
                        Toast.makeText(requireContext(), "Failed to save file", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_PICK_FILE -> {
                // Handle file pick result
                if (resultCode == Activity.RESULT_OK) {
                    val selectedMediaUri = data?.data // Change the variable name
                    if (selectedMediaUri != null) {
                        saveFileMessageToDatabase(selectedMediaUri.toString()) // Update the function call
                    }
                }
            }
            REQUEST_CODE_PICK_MEDIA -> {
                // Handle media pick result
                if (resultCode == Activity.RESULT_OK) {
                    val selectedMediaUri = data?.data
                    if (selectedMediaUri != null) {
                        saveMediaMessageToDatabase(selectedMediaUri.toString())
                    }
                }
            }
            // Handle other request codes if needed
            else -> {
                // Handle unknown request codes
            }
        }
    }


    // Function to open the gallery
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/* video/*"
        startActivityForResult(galleryIntent, REQUEST_CODE_PICK_MEDIA)
    }

    private fun saveMediaMessageToDatabase(mediaUrl: String) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val messageId = databaseReference.push().key
        val messageData = mapOf(
            "messageId" to messageId,
            "messageType" to MessageType.IMAGE.name, // Assuming it's an image
            "mediaUrl" to mediaUrl,
            "userID" to userID,
            "mentorName" to mentorName,
            "timestamp" to timeStamp
        )
        // Save message data to the Firebase Realtime Database
        messageId?.let {
            databaseReference.child(it).setValue(messageData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Message saved successfully
                    } else {
                        // Error handling
                        Log.e("SaveMessage", "Error saving message to database")
                    }
                }
        }
    }
    override fun onEditMessage(messageId: String, initialMessageText: String) {
        showEditDialog(messageId, initialMessageText)
    }
    private fun showEditDialog(messageId: String, initialMessageText: String) {
        val editText = EditText(requireContext())
        editText.setText(initialMessageText)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Message")
            .setView(editText)
            .setPositiveButton("Update") { dialog, _ ->
                val updatedMessageText = editText.text.toString()
                updateMessage(messageId, updatedMessageText)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun updateMessage(messageId: String, newMessageContent: String) {
        databaseReference.child(messageId).child("messageText").setValue(newMessageContent)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
    /*
    private fun startRecording() {
        audioFilePath = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("AudioRecord", "prepare() failed", e)
            }

        }
    }*/
    private fun startRecording() {

        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
                isRecording = true
            }
        } catch (e: IOException) {
            Log.e("AudioRecording", "Error starting recording", e)
        } catch (e: IllegalStateException) {
            Log.e("AudioRecording", "Illegal state exception while starting recording", e)
        } catch (e: Exception) {
            Log.e("AudioRecording", "Unexpected error starting recording", e)
        }
    }

    private fun playVoiceNote(mediaUrl: String?) {
        if (mediaUrl.isNullOrEmpty()) {
            // Handle empty or null media URL
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(mediaUrl)
                prepare()
                start()
                setOnCompletionListener { player -> player.release() }
            }
        } catch (e: IOException) {
            // Handle IOException
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun stopRecording() {
        try {
            mediaRecorder.apply {
                stop()
                release()
                isRecording = false
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }
    private fun onSendVoiceNote() {
        if (audioFilePath != null) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val timeStamp =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val messageId = databaseReference.push().key
            val messageData = mapOf(
                "messageId" to messageId,
                "messageType" to MessageType.VOICE_NOTE.name,
                "audioFilePath" to audioFilePath,
                "userID" to userID,
                "mentorName" to mentorName,
                "timestamp" to timeStamp
            )
            messageId?.let {
                databaseReference.child(it).setValue(messageData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Voice note sent successfully
                            // You might want to handle this case, like clearing the audio file path
                            audioFilePath = null
                        } else {
                            // Error handling
                            // You can handle error cases here
                        }
                    }
            }
        }
    }
    fun onSendButtonClick(view: View) {
        // Get the message text
        val messageText = editTextMessage.text.toString().trim()

        // Check if the message is not empty
        if (messageText.isNotEmpty()) {
            // Get current user ID
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            // Get current timestamp (String format)
            val timeStamp =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // Store message data in the database
            val databaseReference = FirebaseDatabase.getInstance().reference
            val messageId = databaseReference.push().key
            val messageData = mapOf(
                "messageId" to messageId,
                "messageText" to messageText,
                "userID" to userID,
                "mentorName" to mentorName,
                "timestamp" to timeStamp
            )
            messageId?.let {
                databaseReference.child("messages").child(it).setValue(messageData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Message sent successfully
                            editTextMessage.text.clear()
                        } else {
                            // Error handling
                            // You can handle error cases here
                        }
                    }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        // Perform fragment transaction to replace the current fragment with the Screen8 fragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null) // Add to back stack to enable back navigation
        transaction.commit()
    }
}