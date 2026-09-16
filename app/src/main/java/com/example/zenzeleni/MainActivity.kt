package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

// The app's Home/Dashboard screen, shown after a successful login.
// Fetches and displays a live list of challenges from Cloud Firestore.
class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openSettingsButton = findViewById<Button>(R.id.openSettingsButton)
        val challengesListText = findViewById<TextView>(R.id.challengesListText)

        openSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        loadChallenges(challengesListText)
    }

    // Fetches all documents in the "challenges" collection from Firestore
    // and displays them as a simple list of title + point reward.
    private fun loadChallenges(challengesListText: TextView) {
        Log.d(tag, "Fetching challenges from Firestore")

        db.collection("challenges")
            .get()
            .addOnSuccessListener { result ->
                val builder = StringBuilder()
                for (document in result) {
                    val title = document.getString("title") ?: "Untitled"
                    val points = document.getLong("pointReward") ?: 0
                    builder.append("• $title — $points points\n\n")
                    Log.d(tag, "Loaded challenge: ${document.id} => ${document.data}")
                }
                challengesListText.text = builder.toString()
                Log.d(tag, "Successfully loaded ${result.size()} challenges")
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Error fetching challenges", exception)
                challengesListText.text = "Failed to load challenges"
            }
    }
}