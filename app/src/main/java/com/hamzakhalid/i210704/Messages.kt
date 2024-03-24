package com.hamzakhalid.i210704


import java.util.UUID

data class Message(
    val messageId: String = UUID.randomUUID().toString(), // Unique identifier for the message
    val messageType: MessageType?,
    val messageContent: String?, // For text message
    val mediaUrl: String?, // For video, image, or voice note
    val userId: String?, // User ID who sent the message
    val mentorName: String?, // Name of the mentor to whom the message is sent
    val timestamp: Long = System.currentTimeMillis() // Time when the message was sent (in milliseconds)
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", null, null, null, null, null, 0L)
}

enum class MessageType {
    TEXT,
    VIDEO,
    IMAGE,
    VOICE_NOTE,
    FILE // New message type for files
}

/*
data class Message(
    val messageId: String = UUID.randomUUID().toString(), // Unique identifier for the message
    val messageType: MessageType?,
    val messageContent: String?, // For text message
    val mediaUrl: String?, // For video, image, or voice note
    val userId: String?, // User ID who sent the message
    val mentorName: String?, // Name of the mentor to whom the message is sent
    val timestamp: Long = System.currentTimeMillis() // Time when the message was sent
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", null, null, null, null, null, 0L)
}

enum class MessageType {
    TEXT,
    VIDEO,
    IMAGE,
    VOICE_NOTE
}*/