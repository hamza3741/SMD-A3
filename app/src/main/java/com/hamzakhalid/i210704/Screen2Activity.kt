package com.hamzakhalid.i210704
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.hamzakhalid.integration.R

class Screen2Activity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen2)
        var textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener{
            startActivity(Intent(this, Screen5Activity::class.java))
        }
        var textViewSignUp = findViewById<TextView>(R.id.textViewSignUp)
        textViewSignUp.setOnClickListener{
            startActivity(Intent(this, Screen3Activity::class.java))
        }
        val email=findViewById<EditText>(R.id.editTextEmail)
        val pass=findViewById<EditText>(R.id.editTextPassword)
        var mAuth= FirebaseAuth.getInstance()

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        var btn1: Button = findViewById(R.id.LoginBtn);
        if (networkInfo != null && networkInfo.isConnected) {
        btn1.setOnClickListener {
            /*
            mAuth.signInWithEmailAndPassword(
                email.text.toString(),
                pass.text.toString()
            ).addOnSuccessListener {
                startActivity(Intent(this,Screen7Activity::class.java))
                finish()
            }
                .addOnFailureListener {
                    Log.e("Signin_Error",it.message.toString())
                    Toast.makeText(this,"Login Credentials do not match.",Toast.LENGTH_LONG).show()
                }*/
            /*
            val url = "http://192.168.1.9/A3_validateUser.php"
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener <String> { response ->
                    if (response == "success") {
                        startActivity(Intent(this, Screen7Activity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login Credentials do not match.", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("LoginError", "Error occurred: ${error.message}")
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["userEmail"] = email.text.toString()
                    params["pass"] = pass.text.toString()
                    return params
                }
            }

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(stringRequest)*/
            val url = "http://192.168.1.11/A3_validateUser.php"
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener<String> { response ->
                    if (response != "-1") {
                        val userId = response.toInt() // Assuming ID is an integer
                        // Use the userId as needed
                        // For example:
                        Log.e("UserID", "User ID: $userId")
                        val intent = Intent(this, Screen7Activity::class.java).apply {
                            putExtra("USER_ID", userId)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Credentials do not match.", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("LoginError", "Error occurred: ${error.message}")
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["userEmail"] = email.text.toString()
                    params["pass"] = pass.text.toString()
                    return params
                }
            }

// Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(stringRequest)
         }

        }else {
            val dbHelper = DatabaseHelper(this)
            // Assuming you have a button to handle login
            btn1.setOnClickListener {
                val email1 = findViewById<EditText>(R.id.editTextEmail).text.toString()
                val password1 = findViewById<EditText>(R.id.editTextPassword).text.toString()

                val isAuthenticated = dbHelper.checkUser(email1, password1)
                if (isAuthenticated) {
                    startActivity(Intent(this, Screen7Activity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login Credentials do not match.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
