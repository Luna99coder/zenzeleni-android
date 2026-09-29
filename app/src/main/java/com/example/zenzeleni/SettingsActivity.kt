package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

// Lets the logged-in user view their account, change their password,
// pick a language, and log out.
class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: com.google.firebase.auth.FirebaseAuth
    private val tag = "SettingsActivity"

    // Tracks whether the spinner selection came from the user tapping it,
    // or from us setting it programmatically on screen load (to avoid
    // re-triggering a language change every time the screen opens)
    private var isSpinnerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = com.google.firebase.auth.FirebaseAuth.getInstance()

        val currentEmailText = findViewById<TextView>(R.id.currentEmailText)
        val newPasswordInput = findViewById<EditText>(R.id.newPasswordInput)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val statusText = findViewById<TextView>(R.id.settingsStatusText)

        val currentEmail = auth.currentUser?.email ?: "Unknown"
        currentEmailText.text = "Logged in as: $currentEmail"
        Log.d(tag, "Settings opened for user: $currentEmail")

        // The two supported languages. Index 0 = English, Index 1 = isiZulu.
        val languages = arrayOf("English", "isiZulu")
        val languageCodes = arrayOf("en", "zu")
        languageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)

        // Pre-select whichever language is currently active, so the dropdown
        // reflects reality when the user reopens Settings
        val currentLocale = AppCompatDelegate.getApplicationLocales()
        if (!currentLocale.isEmpty && currentLocale[0]?.language == "zu") {
            languageSpinner.setSelection(1)
        } else {
            languageSpinner.setSelection(0)
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (!isSpinnerInitialized) {
                    // Skip the very first automatic selection event
                    isSpinnerInitialized = true
                    return
                }
                val selectedCode = languageCodes[position]
                Log.d(tag, "User selected language: $selectedCode")
                val appLocale = LocaleListCompat.forLanguageTags(selectedCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()

            if (newPassword.isEmpty()) {
                statusText.text = "Please enter a new password"
                Log.w(tag, "Password change attempted with empty input")
                return@setOnClickListener
            }

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
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}