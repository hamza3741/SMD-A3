package com.hamzakhalid.i210704
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hamzakhalid.integration.R
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "Screen21"
class Screen21 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: MentorAdapter
    private lateinit var database: DatabaseReference

    private lateinit var ReviewRecyclerView: RecyclerView
    private lateinit var ReviewAdapter: MentorReviewAdapter
    private lateinit var ReviewDatabase: FirebaseDatabase
    private var userId: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ReviewDatabase=FirebaseDatabase.getInstance()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen21, container, false)

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        // Assuming you have a reference to the TextView in your code
        val nameTextView = view.findViewById<TextView>(R.id.text3)

// Check if currentUserId is not null before using it
        currentUserId?.let { userId ->
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Retrieve the name from the dataSnapshot
                    val name = dataSnapshot.child("name").getValue(String::class.java)

                    // Update the TextView with the new name
                    name?.let { nameTextView.text = it }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }

/*
        val EditIcon1 = view.findViewById<ImageView>(R.id.EditIcon1)
        EditIcon1.setOnClickListener {
            val intent = Intent(context, Screen22Activity::class.java)
            startActivity(intent)
        }
        val EditIcon2 = view.findViewById<ImageView>(R.id.EditIcon2)
        EditIcon2.setOnClickListener {
            val intent = Intent(context, Screen22Activity::class.java)
            startActivity(intent)
        }

 */
        val arrowBack = view.findViewById<ImageView>(R.id.arrow_back21)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            // Create an intent to start Screen18 activity
            val intent = Intent(activity, Screen7Activity::class.java)
            startActivity(intent)
        }
        recyclerView = view.findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mentorAdapter = MentorAdapter(mutableListOf())
        recyclerView.adapter = mentorAdapter

        database = FirebaseDatabase.getInstance().reference.child("mentors")

        ReviewRecyclerView = view.findViewById(R.id.review_recycler_view)
        ReviewRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        ReviewAdapter = MentorReviewAdapter(mutableListOf())
        ReviewRecyclerView.adapter =ReviewAdapter

        //ReviewDatabase = FirebaseDatabase.getInstance().reference.child("mentor_reviews")

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve userId from arguments
        userId = arguments?.getInt("USER_ID") ?: -1
        fetchMentors()
        val EditIcon1 = view.findViewById<ImageView>(R.id.EditIcon1)
        EditIcon1.setOnClickListener {
            val intent = Intent(context, Screen22Activity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        val EditIcon2 = view.findViewById<ImageView>(R.id.EditIcon2)
        EditIcon2.setOnClickListener {
            val intent = Intent(context, Screen22Activity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // Find the button by ID
        val BookedSessionsBtn: Button = view.findViewById(R.id.BookedSessionsBtn)

        // Set click listener for the button
        BookedSessionsBtn.setOnClickListener {
            // Create an intent to start Screen17 activity
            val intent = Intent(activity, Screen23Activity::class.java)
            startActivity(intent)
        }
        fetchMentorReviews()
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
                // Handle database error here
                // This method will be triggered in case of a database error.
                // You can handle the error appropriately, for example, show an error message.
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
                            jsonObject.getString("rate")
                        )
                        mentors.add(mentor)
                    }
                    mentorAdapter.updateMentors(mentors)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON exception here
                    Toast.makeText(requireContext(), "Error occurred while parsing data", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.e("API Error", "Error occurred: ${error.message}")
                Toast.makeText(requireContext(), "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                // No search query needed to fetch all mentors
                val params = HashMap<String, String>()
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
    private fun fetchMentorReviews() {
        /*
        //val currentUserId = mAuth.currentUser?.uid ?: return // Get current user ID
        val ReviewDatabase = ReviewDatabase.reference.child("mentor_reviews")

        ReviewDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ReviewsList = mutableListOf<MentorReview>()
                for (reviewSnapshot in snapshot.children) {
                    val reviewSnapshot = reviewSnapshot.getValue(MentorReview::class.java)
                    reviewSnapshot?.let {
                        ReviewsList.add(it)
                    }
                }
                ReviewAdapter = MentorReviewAdapter(ReviewsList)
                ReviewRecyclerView.adapter = ReviewAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch appointments: $error")
                //Toast.makeText(applicationContext, "Failed to fetch appointments", Toast.LENGTH_SHORT).show()
            }
        })*/
        val url = "http://192.168.1.11/A3_fetchMentorReviews.php"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val reviewsList = mutableListOf<MentorReview>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val mentorName = jsonObject.getString("MentorName")
                        val feedback = jsonObject.getString("feedback")
                        val mentorReview = MentorReview(mentorName, feedback)
                        reviewsList.add(mentorReview)
                    }

                    ReviewAdapter = MentorReviewAdapter(reviewsList)
                    ReviewRecyclerView.adapter = ReviewAdapter
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.e(TAG, "Error occurred: ${error.message}")
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}