package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hamzakhalid.integration.R

private const val TAG = "Screen14"
class Screen14 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: ChatMessageAdapter
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen14, container, false)

        // Find the TextView
        //val textJohnCooper1 = view.findViewById<TextView>(R.id.textJohnCooper1)

        // Set OnClickListener
        //textJohnCooper1.setOnClickListener {
            // Replace current fragment with Screen15
        //    replaceFragment(Screen15())
        //}

        val image_profile1Icon = view.findViewById<ImageView>(R.id.image_profile1)
        image_profile1Icon.setOnClickListener {
            // Replace current fragment with Screen16
            replaceFragment(Screen16())
        }
        val arrowBack = view.findViewById<ImageView>(R.id.arrow_back14)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            // Create an intent to start Screen18 activity
            val intent = Intent(activity, Screen7Activity::class.java)
            startActivity(intent)
        }
        recyclerView = view.findViewById(R.id.chats_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Initialize the adapter with the onItemClick listener
        mentorAdapter = ChatMessageAdapter(mutableListOf()) { mentorName ->
            openScreen15WithMentorName(mentorName)
        }
        recyclerView.adapter = mentorAdapter

        database = FirebaseDatabase.getInstance().reference.child("mentors")
        return view
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the search query from arguments
        //val searchQuery = arguments?.getString("searchQuery") //retrieve search query
        fetchChats()
    }
    private fun fetchChats() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentors = mutableListOf<Mentor>()

                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)
                    mentor?.let {
                        // Add all mentors without filtering
                        mentors.add(it)
                    }
                }
                mentorAdapter.updateMentors(mentors)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error here
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }
    private fun openScreen15WithMentorName(mentorName: String) {
        val fragment = Screen15()
        val args = Bundle()
        args.putString("mentorName", mentorName)
        fragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}