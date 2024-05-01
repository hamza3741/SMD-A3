package com.hamzakhalid.i210704

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hamzakhalid.integration.R

class Screen3Activity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen3)
        var textViewLogIn2 = findViewById<TextView>(R.id.textViewLogIn2)
        textViewLogIn2.setOnClickListener {
            startActivity(Intent(this, Screen2Activity::class.java))
        }
        var mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val email = findViewById<EditText>(R.id.editTextEmail)
        val pass = findViewById<EditText>(R.id.editTextPassword)
        val btn1: Button = findViewById(R.id.SignUpBtn);

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        /*
        btn1.setOnClickListener {
            mAuth.createUserWithEmailAndPassword(
                email.text.toString(),
                pass.text.toString()
            ).addOnSuccessListener {
                startActivity(Intent(this,Screen7Activity::class.java))
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed To Signup",Toast.LENGTH_LONG).show()
                }
            }*/
        val spinnerCountry: Spinner = findViewById(R.id.spinnerCountry)
        // Step 1: Define the list of countries
        val countries = arrayOf("Pakistan", "United Arab Emirates", "Saudi Arabia", "USA", "UK")

        // Step 2: Create an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)

        // Step 3: Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Step 4: Set the adapter to the Spinner
        spinnerCountry.adapter = adapter

        val spinnerCity: Spinner = findViewById(R.id.spinnerCity)
        // Step 1: Define the list of countries
        val cities = arrayOf("Islamabad", "Karachi", "Lahore", "Peshawar", "Quetta")

        // Step 2: Create an ArrayAdapter
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)

        // Step 3: Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Step 4: Set the adapter to the Spinner
        spinnerCity.adapter = adapter2

        val Name = findViewById<EditText>(R.id.editTextName)
        val contact = findViewById<EditText>(R.id.editContact)


        if (networkInfo != null && networkInfo.isConnected) {
        btn1.setOnClickListener {
          /*
            mAuth.createUserWithEmailAndPassword(
                email.text.toString(),
                pass.text.toString()
            ).addOnSuccessListener { authResult ->
                val user = authResult.user
                val uid = user?.uid ?: ""
                val userEmail = email.text.toString()
                val userName = Name.text.toString()
                val userContact = contact.text.toString()
                val country = spinnerCountry.selectedItem.toString()
                val city = spinnerCity.selectedItem.toString()

                // Save user information to Firebase Realtime Database
                saveUserToDatabase(uid, userName, userEmail, userContact, country, city)

                // Navigate to next screen
                startActivity(Intent(this, Screen7Activity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed To Signup", Toast.LENGTH_LONG).show()
            }*/
                val name = Name.text.toString()
                val phone = contact.text.toString()
                val country = spinnerCountry.selectedItem.toString()
                val city = spinnerCity.selectedItem.toString()
                val email = email.text.toString()
                val pass = pass.text.toString()

                val url = "http://192.168.1.11/A3_insertUser.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        Log.d("API Response", response)
                        Toast.makeText(this, "User Added Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Screen7Activity::class.java))
                        finish()
                    },
                    Response.ErrorListener { error ->
                        // Handle error
                        Log.e("API Error", "Error occurred: ${error.message}")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["userName"] = name
                        Log.d("Stored name", "Done")
                        params["userEmail"] = email
                        Log.d("Stored email", "Done")
                        params["userContact"] = phone
                        Log.d("Stored contact", "Done")
                        params["country"] = country
                        Log.d("Stored country", "Done")
                        params["city"] = city
                        Log.d("Stored city", "Done")
                        params["pass"] = pass
                        Log.d("Stored password", "Done")
                        return params
                    }
                }
// Add the request to the RequestQueue
                Log.d("Storing data in db", "in process")
                // Add the request to the RequestQueue
                Volley.newRequestQueue(this).add(stringRequest)
                Log.d("Data in Queue", "Done")
            }
    }else {
            val dbHelper = DatabaseHelper(this)
            btn1.setOnClickListener {

                val name = Name.text.toString()
                val phone = contact.text.toString()
                val country = spinnerCountry.selectedItem.toString()
                val city = spinnerCity.selectedItem.toString()
                val email = email.text.toString()
                val password = pass.text.toString()

                val id = dbHelper.addUser(name, phone, country, city, email, password)
                if (id > 0) {
                    // User added successfully
                    Toast.makeText(this, "User added successfully.", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this, Screen7Activity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error in signing up user.", Toast.LENGTH_LONG)
                        .show()
                    }
                }
            }
    }
    private fun getLoggedInUserId(): String {
        // Get current user's UID
        val currentUser = mAuth.currentUser
        val uid = currentUser?.uid

        // Check if uid is null, throw an exception, or return a default value
        if (uid == null) {
            throw IllegalStateException("No logged in user found!")  // Or return an empty string or handle differently
        }
        return uid
    }

    private fun saveUserToDatabase(uid: String, name: String, email: String, contact: String, country: String, city: String) {
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(uid)
        userRef.child("name").setValue(name)
        userRef.child("email").setValue(email)
        userRef.child("contact").setValue(contact)
        userRef.child("country").setValue(country)
        userRef.child("city").setValue(city)
    }
    }