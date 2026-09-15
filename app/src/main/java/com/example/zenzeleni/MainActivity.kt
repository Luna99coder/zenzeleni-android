package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openSettingsButton = findViewById<Button>(R.id.openSettingsButton)
        val challengesListText = findViewById<TextView>(R.id.challengesListText)

        openSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        db.collection("challenges")
            .get()
            .addOnSuccessListener { result ->
                val builder = StringBuilder()
                for (document in result) {
                    val title = document.getString("title") ?: "Untitled"
                    val points = document.getLong("pointReward") ?: 0
                    builder.append("• $title — $points points\n\n")
                    Log.d("Zenzeleni", "${document.id} => ${document.data}")
                }
                challengesListText.text = builder.toString()
            }
            .addOnFailureListener { exception ->
                Log.e("Zenzeleni", "Error fetching challenges", exception)
                challengesListText.text = "Failed to load challenges"
            }
    }
}