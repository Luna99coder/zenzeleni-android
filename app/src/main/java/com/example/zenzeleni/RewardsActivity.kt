package com.example.zenzeleni

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Shows the rewards stored in Cloud Firestore and lets the signed-in user
// redeem them with their points balance. Each user's balance lives in
// the "users" collection; every redemption is saved in "redemptions".
class RewardsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tag = "RewardsActivity"

    // New users start with this many points so there is something to redeem
    private val startingPoints = 100L

    private lateinit var balanceText: TextView
    private lateinit var statusText: TextView
    private lateinit var rewardsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        balanceText = findViewById(R.id.balanceText)
        statusText = findViewById(R.id.rewardsStatusText)
        rewardsContainer = findViewById(R.id.rewardsContainer)

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e(tag, "No signed-in user, cannot load rewards")
            showStatus(getString(R.string.rw_load_failed), true)
            return
        }

        loadBalanceThenRewards(uid)
    }

    // Reads the user's balance. If this is their first visit, creates their
    // user document with the starting points. Then loads the rewards list.
    private fun loadBalanceThenRewards(uid: String) {
        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val balance = doc.getLong("pointsBalance") ?: 0L
                    Log.d(tag, "Loaded points balance: $balance")
                    showBalance(balance)
                    loadRewards(uid)
                } else {
                    Log.d(tag, "First visit, creating user document with $startingPoints points")
                    val newUser = mapOf(
                        "pointsBalance" to startingPoints,
                        "email" to (auth.currentUser?.email ?: "")
                    )
                    userRef.set(newUser)
                        .addOnSuccessListener {
                            showBalance(startingPoints)
                            loadRewards(uid)
                        }
                        .addOnFailureListener { e ->
                            Log.e(tag, "Could not create user document", e)
                            showStatus(getString(R.string.rw_load_failed), true)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Could not load user balance", e)
                showStatus(getString(R.string.rw_load_failed), true)
            }
    }

    // Fetches every document in the "rewards" collection and builds one row for each
    private fun loadRewards(uid: String) {
        Log.d(tag, "Fetching rewards from Firestore")

        db.collection("rewards")
            .get()
            .addOnSuccessListener { result ->
                rewardsContainer.removeAllViews()

                if (result.isEmpty) {
                    showStatus(getString(R.string.rw_none), true)
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val title = document.getString("title") ?: "Untitled"
                    val cost = document.getLong("pointCost") ?: 0L
                    Log.d(tag, "Loaded reward: ${document.id} => $title ($cost points)")

                    // Build one row from item_reward.xml
                    val row = layoutInflater.inflate(R.layout.item_reward, rewardsContainer, false)
                    row.findViewById<TextView>(R.id.rewardTitleText).text = title
                    row.findViewById<TextView>(R.id.rewardCostText).text =
                        getString(R.string.rw_cost, cost.toInt())

                    val redeemButton = row.findViewById<Button>(R.id.redeemButton)
                    redeemButton.setOnClickListener {
                        redeem(uid, document.id, title, cost, redeemButton)
                    }

                    rewardsContainer.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error fetching rewards", e)
                showStatus(getString(R.string.rw_load_failed), true)
            }
    }

    // Spends points on a reward. A Firestore transaction is used so the balance
    // check and the deduction happen together, which stops the balance from
    // going negative even if the button is tapped twice quickly.
    private fun redeem(uid: String, rewardId: String, title: String, cost: Long, button: Button) {
        Log.d(tag, "Attempting to redeem '$title' for $cost points")
        button.isEnabled = false

        val userRef = db.collection("users").document(uid)

        db.runTransaction { transaction ->
            // All reads must come before any writes in a transaction
            val snapshot = transaction.get(userRef)
            val balance = snapshot.getLong("pointsBalance") ?: 0L

            if (balance < cost) {
                // -1 is our signal for "not enough points"
                -1L
            } else {
                val newBalance = balance - cost
                transaction.update(userRef, "pointsBalance", newBalance)

                // Save a record of this redemption
                val redemptionRef = db.collection("redemptions").document()
                transaction.set(
                    redemptionRef,
                    mapOf(
                        "userId" to uid,
                        "rewardId" to rewardId,
                        "rewardTitle" to title,
                        "pointsUsed" to cost,
                        "redeemedAt" to FieldValue.serverTimestamp()
                    )
                )
                newBalance
            }
        }
            .addOnSuccessListener { result ->
                button.isEnabled = true
                if (result < 0) {
                    Log.w(tag, "Redeem blocked: not enough points for '$title'")
                    showStatus(getString(R.string.rw_not_enough), true)
                } else {
                    Log.d(tag, "Redeemed '$title'. New balance: $result")
                    showBalance(result)
                    showStatus(getString(R.string.rw_redeemed, title), false)
                }
            }
            .addOnFailureListener { e ->
                button.isEnabled = true
                Log.e(tag, "Redeem failed for '$title'", e)
                showStatus(getString(R.string.rw_redeem_failed), true)
            }
    }

    private fun showBalance(balance: Long) {
        balanceText.text = getString(R.string.rw_balance, balance.toInt())
    }

    // Red for errors, green for success
    private fun showStatus(message: String, isError: Boolean) {
        statusText.setTextColor(Color.parseColor(if (isError) "#FF0000" else "#2E7D32"))
        statusText.text = message
    }
}