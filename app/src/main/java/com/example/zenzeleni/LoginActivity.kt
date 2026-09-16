package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// Handles existing user sign-in using Firebase Authentication.
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorText = findViewById<TextView>(R.id.loginErrorText)
        val goToRegisterText = findViewById<TextView>(R.id.goToRegisterText)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Basic local validation before hitting the network
            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = "Please fill in all fields"
                Log.w(TAG, "Login attempted with missing fields")
                return@setOnClickListener
            }

            Log.d(TAG, "Attempting login for: $email")

            // Verify credentials against Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "Login successful for: $email")
                    errorText.text = "Login successful!"
                    // Move to the app's Home/Dashboard screen
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { exception ->
                    // Common causes: wrong password, account doesn't exist, no internet
                    Log.e(TAG, "Login failed for: $email", exception)
                    errorText.text = exception.message ?: "Login failed"
                }
        }

        // Lets a new user jump to the Register screen
        goToRegisterText.setOnClickListener {
            Log.d(TAG, "User navigating to Register screen")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}