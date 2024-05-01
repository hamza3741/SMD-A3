package com.hamzakhalid.i210704

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hamzakhalid.integration.R
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "Screen14"
class Screen14 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: ChatMessageAdapter
    private lateinit var database: DatabaseReference
    private var userId: Int = -1
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
        // Retrieve userId from arguments
        userId = arguments?.getInt("USER_ID") ?: -1
        // Retrieve the search query from arguments
        //val searchQuery = arguments?.getString("searchQuery") //retrieve search query
        fetchChats()
    }
    private fun fetchChats() {
        /*
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
        })*/
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
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
        else{
            val dbHelper = DatabaseHelper(requireContext())
            mentorAdapter.updateMentors(dbHelper.getAllMentors())
        }
    }
    private fun openScreen15WithMentorName(mentorName: String) {
        val fragment = Screen15()
        val args = Bundle()
        args.putString("mentorName", mentorName)
        args.putInt("userID",userId)
        fragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}