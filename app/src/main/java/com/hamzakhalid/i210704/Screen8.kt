package com.hamzakhalid.i210704

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import com.hamzakhalid.integration.R

class Screen8 : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen8, container, false)
        val searchView = view.findViewById<SearchView>(R.id.SearchResults)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // Pass the search query to Screen9
                    navigateToScreen9(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text change here
                return false
            }
        })
        // Find the arrow_back9 icon by its ID
        val arrowBack = view.findViewById<ImageView>(R.id.arrow_back8)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            // Create an intent to start Screen18 activity
            val intent = Intent(activity, Screen7Activity::class.java)
            startActivity(intent)
        }
        // Step 1: Create a list of strings
        val mentors = arrayOf("Mentor1", "Mentor2", "Mentor3")

        // Step 2: Create an ArrayAdapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mentors)

        // Step 3: Set the adapter to the ListView
        val listView: ListView = view.findViewById(R.id.list)
        listView.adapter = adapter

        return view
    }

    private fun navigateToScreen9(searchQuery: String) {
        val fragment = Screen9()
        val bundle = Bundle()
        bundle.putString("searchQuery", searchQuery)
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}