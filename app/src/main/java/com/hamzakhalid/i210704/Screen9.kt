package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hamzakhalid.integration.R

private const val TAG = "Screen9"
class Screen9 : Fragment(), SearchMentorAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: SearchMentorAdapter
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_screen9, container, false)
        // Find the arrow_back9 icon by its ID
        val arrowBack = view.findViewById<ImageView>(R.id.arrow_back9)

        // Retrieve the search query from fragment arguments
        //val searchQuery = arguments?.getString("searchQuery")

        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            // Replace the current fragment with the Screen8 fragment
            replaceFragment(Screen8())
        }
        recyclerView = view.findViewById(R.id.search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mentorAdapter = SearchMentorAdapter(mutableListOf(), this)
       // mentorAdapter = SearchMentorAdapter(mutableListOf())
        recyclerView.adapter = mentorAdapter

        database = FirebaseDatabase.getInstance().reference.child("mentors")
        return view
    }
    private fun replaceFragment(fragment: Fragment){
        // Perform fragment transaction to replace the current fragment with the Screen8 fragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null) // Add to back stack to enable back navigation
        transaction.commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the search query from arguments
        //val searchQuery = arguments?.getString("searchQuery") //retrieve search query
        fetchMentors()
    }
    private fun fetchMentors() {
        val searchQuery = arguments?.getString("searchQuery")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentors = mutableListOf<Mentor>()

                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)
                    mentor?.let {
                        // Filter mentors whose name matches the search query
                        if (searchQuery.isNullOrEmpty() || it.name.contains(searchQuery, ignoreCase = true)) {
                            mentors.add(it)
                        }
                    }
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

    override fun onItemClick(mentor: Mentor) {
        val intent = Intent(activity, Screen10Activity::class.java)
        intent.putExtra("mentorName", mentor.name)
        intent.putExtra("mentorDescription", mentor.description)
        intent.putExtra("mentorRate",mentor.rate)
        Log.d(TAG, "Clicked mentor: ${mentor.name}")
        startActivity(intent)
    }
}