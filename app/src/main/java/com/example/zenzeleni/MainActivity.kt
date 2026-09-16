package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// The app's Home/Dashboard screen, shown after a successful login.
// Loads data from two different sources to demonstrate both required
// connection types:
//   1. Cloud Firestore (via the Firebase SDK) — the app's own database
//   2. A RESTful API (via Retrofit) — an external live currency exchange rate service,
//      giving users a sense of what their rewards/points might be worth in Rands
class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openSettingsButton = findViewById<Button>(R.id.openSettingsButton)
        val challengesListText = findViewById<TextView>(R.id.challengesListText)
        val postsListText = findViewById<TextView>(R.id.postsListText)

        openSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        loadChallenges(challengesListText)
        loadExchangeRates(postsListText)
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

    // Fetches live currency exchange rates (base: ZAR) from an external RESTful API
    // using Retrofit, and displays a few common currencies.
    private fun loadExchangeRates(postsListText: TextView) {
        Log.d(tag, "Fetching exchange rates from external REST API")

        RetrofitClient.api.getExchangeRates().enqueue(object : Callback<ExchangeRateResponse> {
            override fun onResponse(
                call: Call<ExchangeRateResponse>,
                response: Response<ExchangeRateResponse>
            ) {
                if (response.isSuccessful) {
                    val rates = response.body()?.rates
                    if (rates != null) {
                        val builder = StringBuilder()
                        val currenciesToShow = listOf("USD", "EUR", "GBP")
                        for (currency in currenciesToShow) {
                            val rate = rates[currency]
                            if (rate != null) {
                                builder.append("1 ZAR = %.4f $currency\n\n".format(rate))
                            }
                        }
                        postsListText.text = builder.toString()
                        Log.d(tag, "Successfully loaded exchange rates")
                    } else {
                        postsListText.text = "No rate data available"
                    }
                } else {
                    Log.e(tag, "API call failed with code: ${response.code()}")
                    postsListText.text = "Failed to load exchange rates"
                }
            }

            override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                Log.e(tag, "API call error", t)
                postsListText.text = "Failed to load exchange rates (no connection?)"
            }
        })
    }
}