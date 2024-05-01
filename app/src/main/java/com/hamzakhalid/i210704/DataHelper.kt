package com.hamzakhalid.i210704
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase"
        private const val TABLE_USERS = "users"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
        private const val KEY_COUNTRY = "country"
        private const val KEY_CITY = "city"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_MESSAGE_ID = "message_id"
        private const val KEY_MESSAGE_TYPE = "message_type"
        private const val KEY_MESSAGE_TEXT = "message_text"
        private const val KEY_MEDIA_URL = "media_url"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MENTOR_NAME = "mentor_name"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val TABLE_MESSAGES = "messages"
        // Columns for the mentors table
        private const val TABLE_MENTORS = "mentors"
        private const val KEY_MENTOR_ID = "mentor_id"
        private const val KEY_MENTOR_DESCRIPTION = "description"
        private const val KEY_MENTOR_RATE = "rate"
        private const val KEY_MENTOR_STATUS = "status"
        private const val KEY_MENTOR_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableQuery = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT" + ")")
        db.execSQL(createUserTableQuery)
        val createMessageTableQuery = ("CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY,"
                + KEY_MESSAGE_TYPE + " TEXT,"
                + KEY_MESSAGE_TEXT + " TEXT,"
                + KEY_MEDIA_URL + " TEXT,"
                + KEY_USER_ID + " TEXT,"
                + KEY_MENTOR_NAME + " TEXT,"
                + KEY_TIMESTAMP + " INTEGER" + ")")
        db.execSQL(createMessageTableQuery)
        val createMentorTableQuery = ("CREATE TABLE " + TABLE_MENTORS + "("
                + KEY_MENTOR_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_MENTOR_DESCRIPTION + " TEXT,"
                + KEY_MENTOR_RATE + " TEXT,"
                + KEY_MENTOR_STATUS + " TEXT,"
                + KEY_MENTOR_IMAGE + " TEXT" + ")")
        db.execSQL(createMentorTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MENTORS")
        onCreate(db)
    }
    fun addMessage(
        messageType: String,
        messageText: String,
        mediaUrl: String,
        userId: String,
        mentorName: String,
        timestamp: Long
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_MESSAGE_TYPE, messageType)
        values.put(KEY_MESSAGE_TEXT, messageText)
        values.put(KEY_MEDIA_URL, mediaUrl)
        values.put(KEY_USER_ID, userId)
        values.put(KEY_MENTOR_NAME, mentorName)
        values.put(KEY_TIMESTAMP, timestamp)
        val id = db.insert(TABLE_MESSAGES, null, values)
        db.close()
        return id
    }
    @SuppressLint("Range")
    fun getAllMentors(): ArrayList<Mentor> {
        val mentorList = ArrayList<Mentor>()
        val selectQuery = "SELECT * FROM $TABLE_MENTORS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val mentorId = cursor.getLong(cursor.getColumnIndex(KEY_MENTOR_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val description = cursor.getString(cursor.getColumnIndex(KEY_MENTOR_DESCRIPTION))
                val rate = cursor.getString(cursor.getColumnIndex(KEY_MENTOR_RATE))
                val status = cursor.getString(cursor.getColumnIndex(KEY_MENTOR_STATUS))
                val image = cursor.getString(cursor.getColumnIndex(KEY_MENTOR_IMAGE))

                val mentor = Mentor(mentorId.toString(), name, description, rate, status)
                mentorList.add(mentor)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return mentorList
    }
    fun addMentor(name: String, description: String, rate: String, status: String, image: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_MENTOR_DESCRIPTION, description)
        values.put(KEY_MENTOR_RATE, rate)
        values.put(KEY_MENTOR_STATUS, status)
        values.put(KEY_MENTOR_IMAGE, image)
        val id = db.insert(TABLE_MENTORS, null, values)
        db.close()
        return id
    }
    fun addUser(name: String, phone: String, country: String, city: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_PHONE, phone)
        values.put(KEY_COUNTRY, country)
        values.put(KEY_CITY, city)
        values.put(KEY_EMAIL, email)
        values.put(KEY_PASSWORD, password)
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $KEY_EMAIL=? AND $KEY_PASSWORD=?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email, password))
        val count = cursor.count
        cursor.close()
        db.close()
        return count>0
        }
    @SuppressLint("Range")
    fun getAllMessages(): ArrayList<Message> {
        val messageList = ArrayList<Message>()
        val selectQuery = "SELECT * FROM $TABLE_MESSAGES"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val messageId = cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_ID))
                val messageTypeString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TYPE))
                val messageType = when (messageTypeString) {
                    MessageType.TEXT.name -> MessageType.TEXT
                    MessageType.VIDEO.name -> MessageType.VIDEO
                    MessageType.IMAGE.name -> MessageType.IMAGE
                    MessageType.VOICE_NOTE.name -> MessageType.VOICE_NOTE
                    MessageType.FILE.name -> MessageType.FILE
                    else -> null // Handle invalid message type
                }
                val messageText = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT))
                val mediaUrl = cursor.getString(cursor.getColumnIndex(KEY_MEDIA_URL))
                val userId = cursor.getString(cursor.getColumnIndex(KEY_USER_ID))
                val mentorName = cursor.getString(cursor.getColumnIndex(KEY_MENTOR_NAME))
                val timestamp = cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP))

                val message = Message(
                    messageId.toString(),
                    messageType,
                    messageText,
                    mediaUrl,
                    userId,
                    mentorName,
                    timestamp
                )
                messageList.add(message)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return messageList
    }
}