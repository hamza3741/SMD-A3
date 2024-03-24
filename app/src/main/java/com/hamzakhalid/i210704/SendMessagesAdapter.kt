package com.hamzakhalid.i210704

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hamzakhalid.integration.R
import com.squareup.picasso.Picasso
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



private lateinit var databaseReference: DatabaseReference
class MessageAdapter(
    private val messages: List<Message>,
    private val messageEditListener: MessageEditListener? = null
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    interface MessageEditListener {
        fun onEditMessage(messageId: String, initialMessageText: String)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val timeTextView: TextView = itemView.findViewById(R.id.timestamp)
        val fileInfoTextView: TextView = itemView.findViewById(R.id.fileInfoTextView)
      //  val senderTextView: TextView = itemView.findViewById(R.id.senderTextView)
      val playButton: ImageView = itemView.findViewById(R.id.PlayVoiceNote) // Reference to play button
        val DeleteButton: ImageView = itemView.findViewById(R.id.DeleteMsg) // Reference to play button
        val EditButton: ImageView = itemView.findViewById(R.id.EditMsg)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        init {
            playButton.setOnClickListener {
                val message = messages[adapterPosition]
                if (message.messageType == MessageType.VOICE_NOTE) {
                    Log.d("MessageAdapter", "Play button clicked for voice note")
                    playVoiceNote(message.mediaUrl)
                }
            }
            EditButton.setOnClickListener {
                val message = messages[adapterPosition]
                messageEditListener?.onEditMessage(message.messageId, message.messageContent ?: "")
            }
            DeleteButton.setOnClickListener {
                databaseReference = FirebaseDatabase.getInstance().getReference("messages")
                val message = messages[adapterPosition]
                val messageId = message.messageId // Get message ID
                if (messageId != null) {
                    // Remove the message from the database
                    databaseReference.child(messageId).removeValue()
                        .addOnSuccessListener {
                            Log.d("MessageAdapter", "Message deleted successfully from database")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MessageAdapter", "Error deleting message from database: ${e.message}")
                        }
                }
            }
        }
        private fun updateMessage(messageId: String, newMessageContent: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("messages")
            databaseReference.child(messageId).child("messageContent").setValue(newMessageContent)
                .addOnSuccessListener {
                    Log.d("MessageAdapter", "Message updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("MessageAdapter", "Error updating message: ${e.message}")
                }
        }
        private fun playVoiceNote(mediaUrl: String?) {
            Log.d("MessageAdapter", "VN MediaUrl: $mediaUrl")
            mediaUrl?.let {
                try {
                    Log.d("MessageAdapter", "Playing voice note from: $mediaUrl")
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(it)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        mediaPlayer.start()
                        Log.d("MessageAdapter", "Voice note playback started")
                    }
                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.release()
                        Log.d("MessageAdapter", "Voice note playback completed")
                    }
                } catch (e: IOException) {
                    Log.e("MessageAdapter", "Error playing voice note: ${e.message}")
                    e.printStackTrace()
                } catch (e: Exception) {
                    Log.e("MessageAdapter", "Error playing voice note: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
/*
        holder.messageTextView.text = when (message.messageType) {
            MessageType.TEXT -> message.messageContent
            else -> message.messageType.toString() // You can customize this based on your UI
        }*/
        holder.messageTextView.text=message.messageContent
        /*
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(message.timestamp.toString())
        holder.timeTextView.text = SimpleDateFormat("hh:mm a").format(date)  // Use date directly
        */
        // Convert timestamp to Date object
        val date = Date(message.timestamp)

        // Format the date as per your requirement
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)

        holder.timeTextView.text = formattedTime
        // Set visibility of play button based on message type
        if (message.messageType == MessageType.IMAGE) {
            Log.d("Picasso", "Loading image from URL: ${message.mediaUrl}")
            holder.imageView.visibility = View.VISIBLE
            // Load image using Picasso/Glide
            Picasso.get().load(message.mediaUrl).into(holder.imageView)
            // Or using Glide:
            // Glide.with(holder.itemView.context).load(message.mediaUrl).into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }
        // Check message type
        /*
        if (message.messageType == MessageType.FILE) {
            // Display file information
            holder.imageView.visibility = View.VISIBLE
            holder.fileInfoTextView.visibility = View.VISIBLE
            holder.fileInfoTextView.text = getFileNameFromUrl(message.mediaUrl)
            holder.imageView.setOnClickListener {
                // Handle opening the file here
                // You can open the file using the mediaUrl
                openFile(message.mediaUrl, holder)
            }
        } else {
            // Hide ImageView and file info TextView for non-file messages
            holder.imageView.visibility = View.GONE
            holder.fileInfoTextView.visibility = View.GONE
        }*/

        if (message.messageType == MessageType.VOICE_NOTE) {
            holder.playButton.visibility = View.VISIBLE
        } else {
            holder.playButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun getFileNameFromUrl(url: String?): String {
        if (url.isNullOrEmpty()) return ""

        val uri = Uri.parse(url)
        return uri.lastPathSegment ?: ""
    }

    private fun openFile(mediaUrl: String?, holder: MessageViewHolder) {
        mediaUrl?.let { url ->
            val context = holder.itemView.context
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            val mimeType = context.contentResolver.getType(Uri.parse(url))
            intent.type = mimeType
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No application found to open the file", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
    }
    private fun convertTimestampToTime(timestamp: Long): String {
        // Convert timestamp to readable time format (you can customize this based on your requirements)
        // For example, using SimpleDateFormat
        return SimpleDateFormat("hh:mm a").format(Date(timestamp))
    }
}
