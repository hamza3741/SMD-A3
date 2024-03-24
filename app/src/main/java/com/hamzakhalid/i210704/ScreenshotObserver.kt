import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import android.os.FileObserver

/*
class ScreenshotObserver(path: String) : FileObserver(path, FileObserver.CREATE) {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    override fun onEvent(event: Int, path: String?) {
        if (event == FileObserver.CREATE && path != null) {
            val filePath = "$path/$path"
            // Trigger push notification when a new screenshot is detected
            sendPushNotification("New Screenshot Detected", "A new screenshot was taken: $filePath")
        }
    }

    private fun sendPushNotification(title: String, message: String) {
        val notification = RemoteMessage.Builder("612228380278")
            .setMessageId(java.lang.Integer.toString(java.util.concurrent.atomic.AtomicInteger().incrementAndGet()))
            .addData("title", title)
            .addData("body", message)
            .build()

        val task = firebaseMessaging.send(notification)
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Push notification sent successfully
            } else {
                // Failed to send push notification
            }
        }
    }

}
*/

