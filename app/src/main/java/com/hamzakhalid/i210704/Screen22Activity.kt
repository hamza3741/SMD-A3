package com.hamzakhalid.i210704

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hamzakhalid.integration.R

class Screen22Activity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // Request code for image selection
    private val PICK_IMAGE_REQUEST = 1

    // Declare profileIconImageView at the class level
    private lateinit var profileIconImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen22)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val arrow_back22 = findViewById<ImageView>(R.id.arrow_back22)
        arrow_back22.setOnClickListener{
            setContentView(R.layout.fragment_screen21) // Use screen16.xml layout directly
            // Perform fragment transaction to replace frame_container with Screen16 fragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, Screen21())
            fragmentTransaction.commit()
        }
        val spinnerCountry: Spinner =findViewById(R.id.spinnerCountry)
        // Step 1: Define the list of countries
        val countries = arrayOf("Pakistan", "United Arab Emirates","Saudi Arabia", "USA", "UK")

        // Step 2: Create an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)

        // Step 3: Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Step 4: Set the adapter to the Spinner
        spinnerCountry.adapter = adapter

        val spinnerCity: Spinner =findViewById(R.id.spinnerCity)
        // Step 1: Define the list of countries
        val cities = arrayOf("Islamabad", "Karachi","Lahore", "Peshawar", "Quetta")

        // Step 2: Create an ArrayAdapter
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item,cities)

        // Step 3: Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Step 4: Set the adapter to the Spinner
        spinnerCity.adapter = adapter2

        val Name=findViewById<EditText>(R.id.editTextName)
        val email=findViewById<EditText>(R.id.editTextEmail)
        val contact=findViewById<EditText>(R.id.editContact)
        val UpdateProfile1: Button = findViewById(R.id.UpdateProfile)

        // Initialize profileIconImageView
        profileIconImageView = findViewById(R.id.profileIconImageView)

        UpdateProfile1.setOnClickListener {
            // Get current user's UID
            val currentUser = mAuth.currentUser
            val uid = currentUser?.uid

            // Get updated information
            val updatedName = Name.text.toString()
            val updatedEmail = email.text.toString()
            val updatedContact = contact.text.toString()
            val selectedCountry = spinnerCountry.selectedItem.toString()
            val selectedCity = spinnerCity.selectedItem.toString()

            // Update user information in the database
            uid?.let {
                val userRef = database.getReference("users").child(it)
                userRef.child("name").setValue(updatedName)
                userRef.child("email").setValue(updatedEmail)
                userRef.child("contact").setValue(updatedContact)
                userRef.child("country").setValue(selectedCountry)
                userRef.child("city").setValue(selectedCity)
                    .addOnSuccessListener {
                        // Show success message
                        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // Show failure message
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        // Set OnClickListener to profileIconImageView
        profileIconImageView.setOnClickListener {
            // Create an Intent to pick an image from the gallery
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }
    }
    // Initialize the ActivityResultLauncher
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // Handle the result here
            handleGalleryResult(data)
        }
    }
    // Handle the result of image selection
    private fun handleGalleryResult(data: Intent?) {
        val selectedImage = data?.data
        if (selectedImage != null) {
            profileIconImageView.setImageURI(selectedImage)
            Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
