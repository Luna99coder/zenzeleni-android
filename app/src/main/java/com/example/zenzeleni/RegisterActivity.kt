package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// Handles new user sign-up using Firebase Authentication.
// Firebase automatically encrypts and securely stores the password —
// this app never sees or stores the raw password itself.
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Get the shared Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Link the code to the views defined in activity_register.xml
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val errorText = findViewById<TextView>(R.id.errorText)
        val goToLoginText = findViewById<TextView>(R.id.goToLoginText)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validate input locally before making a network call,
            // so the user gets instant feedback and we avoid wasted requests
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorText.text = "Please fill in all fields"
                Log.w(TAG, "Registration attempted with one or more empty fields")
                return@setOnClickListener
            }

            Log.d(TAG, "Attempting to register new user: $email")

            // Ask Firebase to create the account.
            // Password hashing/encryption is handled entirely on Firebase's servers.
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "Registration successful for: $email")
                    errorText.text = "Account created successfully!"
                    // Send the user to the Login screen to sign in with their new account
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener { exception ->
                    // Common causes: email already in use, weak password, no internet
                    Log.e(TAG, "Registration failed for: $email", exception)
                    errorText.text = exception.message ?: "Registration failed"
                }
        }

        // Lets an existing user jump straight to the Login screen
        goToLoginText.setOnClickListener {
            Log.d(TAG, "User navigating to Login screen")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}