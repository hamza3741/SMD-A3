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

private const val TAG = "Screen21"
class Screen21 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: MentorAdapter
    private lateinit var database: DatabaseReference

    private lateinit var ReviewRecyclerView: RecyclerView
    private lateinit var ReviewAdapter: MentorReviewAdapter
    private lateinit var ReviewDatabase: FirebaseDatabase

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
        fetchMentors()
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
        })
    }
    private fun fetchMentorReviews() {
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
        })
    }
}