package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

        currentEmailText.text = "Logged in as: ${auth.currentUser?.email ?: "Unknown"}"

        val languages = arrayOf("English", "isiZulu")
        languageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()
            if (newPassword.isEmpty()) {
                statusText.text = "Please enter a new password"
                return@setOnClickListener
            }
            auth.currentUser?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    statusText.text = "Password updated successfully"
                }
                ?.addOnFailureListener { exception ->
                    statusText.text = exception.message ?: "Failed to update password"
                }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}