package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hamzakhalid.integration.R
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "Screen7Activity"
class Screen7Activity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: MentorAdapter
    private lateinit var database: DatabaseReference

    private lateinit var recyclerView2: RecyclerView
    private lateinit var mentorAdapter2: MentorAdapter
    private lateinit var database2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen7)
        // Retrieve userId from intent extras
        val userId = intent.getIntExtra("USER_ID", -1)
        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        // Assuming you have a reference to the TextView in your code
        val nameTextView = findViewById<TextView>(R.id.Name)

// Check if currentUserId is not null before using it
        currentUserId?.let { userId ->
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Retrieve the full name from the dataSnapshot
                    val fullName = dataSnapshot.child("name").getValue(String::class.java)

                    // Extract the first name from the full name
                    val firstName = fullName?.split(" ")?.get(0)

                    // Update the TextView with the first name
                    firstName?.let { nameTextView.text = it }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
      //  val relativeView = findViewById<RelativeLayout>(R.id.JohnCooperBox)  // Replace with your relative view ID
       // relativeView.setOnClickListener {
            // Handle click event here
           // openScreen10Activity()
        //}
        recyclerView = findViewById(R.id.my_recycler_view1)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mentorAdapter = MentorAdapter(mutableListOf())
        recyclerView.adapter = mentorAdapter
        // Set item click listener for mentorAdapter
        mentorAdapter.setOnItemClickListener(object : MentorAdapter.OnItemClickListener {
            override fun onItemClick(mentor: Mentor) {
                onMentorItemClick(mentor)
            }
        })

        database = FirebaseDatabase.getInstance().reference.child("mentors")
        fetchMentors()
        recyclerView2 = findViewById(R.id.my_recycler_view2)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mentorAdapter2 = MentorAdapter(mutableListOf())
        recyclerView2.adapter = mentorAdapter2

        mentorAdapter2.setOnItemClickListener(object : MentorAdapter.OnItemClickListener {
            override fun onItemClick(mentor: Mentor) {
                onMentorItemClick(mentor)
            }
        })


        database2 = FirebaseDatabase.getInstance().reference.child("mentors")
        fetchMentors2()

        val NotificationsIcon = findViewById<ImageView>(R.id.NotificationsIcon)
        NotificationsIcon.setOnClickListener{
            startActivity(Intent(this, Screen24Activity::class.java))
        }

        bottomNavigationView=findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.action_search -> {
                    replaceFragment(Screen8())
                    true
                }
                R.id.action_chat -> {
                    val fragment = Screen14()
                    val bundle = Bundle()
                    bundle.putInt("USER_ID", userId)
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                    true
                }
                R.id.action_upload-> {
                    replaceFragment(Screen12())
                    true
                }
                R.id.action_profile -> {
                    val fragment = Screen21()
                    val bundle = Bundle()
                    bundle.putInt("USER_ID", userId)
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                    true
                }

                else -> {false}
            }
        }
    }

    private fun fetchMentors() {
       /*
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentors = mutableListOf<Mentor>()
                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)
                    mentor?.let { mentors.add(it) }
                }
                mentorAdapter.updateMentors(mentors)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Database error: ${databaseError.message}")
            }
        })*/
        val url = "http://192.168.1.11/A3_fetchAllMentors.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val mentors = mutableListOf<Mentor>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val mentor = Mentor(
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            jsonObject.getString("status"),
                            jsonObject.getString("rate") ,
                            jsonObject.getString("imageurl")
                        )
                        mentors.add(mentor)
                    }
                    mentorAdapter.updateMentors(mentors)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON exception here
                    Toast.makeText(this, "Error occurred while parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.e("API Error", "Error occurred: ${error.message}")
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                // No search query needed to fetch all mentors
                val params = HashMap<String, String>()
                return params
            }
        }

// Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
    private fun fetchMentors2() {
        /*
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentors = mutableListOf<Mentor>()
                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)
                    mentor?.let { mentors.add(it) }
                }
                mentorAdapter2.updateMentors(mentors)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Database error: ${databaseError.message}")
            }
        })*/
        val url = "http://192.168.1.11/A3_fetchAllMentors.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val mentors = mutableListOf<Mentor>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val mentor = Mentor(
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            jsonObject.getString("status"),
                            jsonObject.getString("rate"),
                            jsonObject.getString("imageurl")
                        )
                        mentors.add(mentor)
                    }
                    mentorAdapter2.updateMentors(mentors)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON exception here
                    Toast.makeText(this, "Error occurred while parsing data", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.e("API Error", "Error occurred: ${error.message}")
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                // No search query needed to fetch all mentors
                val params = HashMap<String, String>()
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commitAllowingStateLoss()
    }
    private fun openScreen10Activity() {
        val intent = Intent(this, Screen10Activity::class.java)
        startActivity(intent)
    }
    private fun onMentorItemClick(mentor: Mentor) {
        val intent = Intent(this, Screen10Activity::class.java)
        intent.putExtra("mentorName", mentor.name)
        intent.putExtra("mentorDescription", mentor.description)
        intent.putExtra("mentorRate",mentor.rate)
        Log.d(TAG, "Clicked mentor: ${mentor.name}")
        startActivity(intent)
    }
}



