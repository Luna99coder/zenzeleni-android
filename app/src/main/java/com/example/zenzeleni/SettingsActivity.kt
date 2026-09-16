package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// Lets the logged-in user view their account, change their password,
// pick a language, and log out.
class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val tag = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()

        val currentEmailText = findViewById<TextView>(R.id.currentEmailText)
        val newPasswordInput = findViewById<EditText>(R.id.newPasswordInput)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val statusText = findViewById<TextView>(R.id.settingsStatusText)

        // Show which account is currently signed in
        val currentEmail = auth.currentUser?.email ?: "Unknown"
        currentEmailText.text = "Logged in as: $currentEmail"
        Log.d(tag, "Settings opened for user: $currentEmail")

        // Populate the language dropdown.
        // Full translation of app text is a planned feature for the final PoE submission.
        val languages = arrayOf("English", "isiZulu")
        languageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()

            if (newPassword.isEmpty()) {
                statusText.text = "Please enter a new password"
                Log.w(tag, "Password change attempted with empty input")
                return@setOnClickListener
            }

            // Ask Firebase to update the password for the currently signed-in user.
            // Firebase handles the encryption/storage of the new password.
            auth.currentUser?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    Log.d(tag, "Password updated successfully for: $currentEmail")
                    statusText.text = "Password updated successfully"
                }
                ?.addOnFailureListener { exception ->
                    Log.e(tag, "Password update failed for: $currentEmail", exception)
                    statusText.text = exception.message ?: "Failed to update password"
                }
        }

        logoutButton.setOnClickListener {
            Log.d(tag, "User logging out: $currentEmail")
            auth.signOut()
            // Return to Login and clear this screen from the back stack
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}