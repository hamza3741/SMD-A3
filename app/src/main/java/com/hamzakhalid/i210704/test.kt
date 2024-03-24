package com.example.myapplication

/*
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MessageAdapter.MessageViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException


class Screen15Activity : AppCompatActivity() {

    private var mentorId: String = "-NtN-1muzKPb-J7fBThA"
    private lateinit var chatMessagesRecyclerView: RecyclerView
    private val messagesList = mutableListOf<Message>()
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var audioFilePath: String

    // Define permission request code
    private val PERMISSION_CODE = 101

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Here you can show explanation to the user asynchronously
        }

        // Request the permissions
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions were granted
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permissions were denied
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_15)


        fetchMentorName(mentorId)




        findViewById<ImageButton>(R.id.send_button).setOnClickListener {
            val messageText =
                findViewById<EditText>(R.id.chat_input_edittext).text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessageToMentor(mentorId, messageText)
                // Clear the input field after sending
                findViewById<EditText>(R.id.chat_input_edittext).text.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<ImageButton>(R.id.mic_button).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions if not granted
                requestPermissions()
            } else {
                // Start recording

                setupRecordButton()
            }
        }


        chatMessagesRecyclerView = findViewById(R.id.chatMessagesRecyclerView)
        chatMessagesRecyclerView.layoutManager = LinearLayoutManager(this)
        chatMessagesRecyclerView.adapter = MessageAdapter(messagesList)

        fetchMessages()

        //call btnnn
        val callButton = findViewById<ImageButton>(R.id.call_btn_msg)

        callButton.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            startActivity(intent)
        }

        //video call btnnn
        val videocallButton = findViewById<ImageButton>(R.id.video_btn_msg)

        videocallButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            startActivity(intent)
        }

        //Profile btnnn
        val pButton = findViewById<ImageView>(R.id.profile_btn15)

        pButton.setOnClickListener {
            val intent = Intent(this, Screen21Activity::class.java)
            startActivity(intent)
        }

        //Search btnnn
        val sButton = findViewById<ImageView>(R.id.search_btn15)

        sButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


        //Add btnnn
        val adButton = findViewById<TextView>(R.id.add_btn15)

        adButton.setOnClickListener {
            val intent = Intent(this, AddMentorActivity::class.java)
            startActivity(intent)
        }


        //home btnnn
        val homeButton = findViewById<ImageView>(R.id.home_btn15)

        homeButton.setOnClickListener {
            val intent = Intent(this, Screen7Activity::class.java)
            startActivity(intent)
        }
    }


    fun sendMessageToMentor(mentorId: String, messageText: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("Chats")

        // Generate a unique key for the new message
        val messageId = databaseRef.push().key ?: return

        // Create a message object
        val message = Message(
            senderId = senderId,
            receiverId = mentorId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        // Path to store message: /Chats/[mentorId]/Messages/[messageId]
        databaseRef.child(mentorId).child("Messages").child(messageId).setValue(message)
            .addOnSuccessListener {
                // Message sent successfully
                Log.d("sendMessageToMentor", "Message sent successfully")
            }
            .addOnFailureListener {
                // Failed to send message
                Log.e("sendMessageToMentor", "Failed to send message", it)
            }
    }

    private fun fetchMessages() {
        // Correct the Firebase path if necessary. This is just an example.
        val dbRef = FirebaseDatabase.getInstance().getReference("Chats/${mentorId}/Messages")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    message?.let { messagesList.add(it) }
                }
                chatMessagesRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Could not fetch messages", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun fetchMentorName(mentorId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("mentors").child(mentorId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentorName = snapshot.child("name").value.toString()
                findViewById<TextView>(R.id.name_mentor_msg).text =
                    mentorName // Ensure you have a TextView with id mentorNameTextView
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Could not fetch mentor name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
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
    }

    private fun stopRecording(recorder: MediaRecorder) {
        recorder.apply {
            stop()
            release()
        }
    }

    private fun stopRecordingAndUploadAudio() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        // Upload audio to Firebase Storage
        uploadAudioToFirebaseStorage(audioFilePath, onSuccess = { audioUrl ->
            // Send voice message automatically after successful upload
            sendVoiceMessage(audioUrl)
            runOnUiThread {
                Toast.makeText(this, "Voice message sent.", Toast.LENGTH_SHORT).show()
            }
        }, onFailure = { exception ->
            Log.e("UploadAudio", "Failed to upload audio", exception)
            runOnUiThread {
                Toast.makeText(this, "Failed to upload audio: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun sendvMessageToMentor(message: Message) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Chats/${mentorId}/Messages")
        val messageId = databaseRef.push().key ?: return

        databaseRef.child(messageId).setValue(message).addOnSuccessListener {
            Log.d("sendMessageToMentor", "Voice message sent successfully")
        }.addOnFailureListener {
            Log.e("sendMessageToMentor", "Failed to send voice message", it)
        }
    }

    private fun sendVoiceMessage(audioUrl: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val message = Message(
            senderId = senderId,
            receiverId = mentorId,
            voiceUrl = audioUrl,
            timestamp = System.currentTimeMillis()
        )

        // Send the voice message to Firebase
        sendvMessageToMentor(message)
    }



    private fun setupRecordButton() {
        val recordButton = findViewById<ImageButton>(R.id.mic_button)
        recordButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRecording()
                    Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    stopRecordingAndUploadAudio()
                    Toast.makeText(this, "Recording stopped...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }




    fun uploadAudioToFirebaseStorage(audioFilePath: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val file = Uri.fromFile(File(audioFilePath))
        val storageRef = FirebaseStorage.getInstance().reference.child("voiceMessages/${file.lastPathSegment}")
        val uploadTask = storageRef.putFile(file)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                val audioUrl = uri.toString()
                onSuccess(audioUrl) // Invoke callback with the URL of the uploaded voice message
            }
        }.addOnFailureListener { exception ->
            onFailure(exception) // Invoke callback with the exception
        }
    }

    fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val (_, _, _, message1, _, voiceUrl) = messagesList[position]
        holder.messageText.text = message1

        if (voiceUrl != null && !voiceUrl.isEmpty()) {
            holder.playButton.visibility = View.VISIBLE
            holder.playButton.setOnClickListener { v: View? ->
                playAudio(
                    voiceUrl
                )
            }
        } else {
            holder.playButton.visibility = View.GONE
        }
    }

    private fun playAudio(audioUrl: String?) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepare() // might take long! (for buffering, etc)
            mediaPlayer.start()
        } catch (e: IOException) {
            Log.e("AudioPlayback", "Error playing audio", e)
        }

        // Release the MediaPlayer resources once the playback is completed
        mediaPlayer.setOnCompletionListener { obj: MediaPlayer -> obj.release() }
            }


}*/