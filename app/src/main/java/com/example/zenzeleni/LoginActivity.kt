package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// Handles existing user sign-in, either with email/password
// or with Google Sign-In (Single Sign-On).
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val tag = "LoginActivity"

    // Handles the result that comes back after the Google account picker closes
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(tag, "Google sign-in succeeded for: ${account.email}")
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            Log.e(tag, "Google sign-in failed", e)
            findViewById<TextView>(R.id.loginErrorText).text = "Google sign-in failed"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In to request the user's ID token,
        // which Firebase needs to verify their identity
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("507975204619-q8960i795oe0v1ap4vtej270jk6rrl57.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton)
        val errorText = findViewById<TextView>(R.id.loginErrorText)
        val goToRegisterText = findViewById<TextView>(R.id.goToRegisterText)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = "Please fill in all fields"
                Log.w(tag, "Login attempted with missing fields")
                return@setOnClickListener
            }

            Log.d(tag, "Attempting email/password login for: $email")

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(tag, "Login successful for: $email")
                    errorText.text = "Login successful!"
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e(tag, "Login failed for: $email", exception)
                    errorText.text = exception.message ?: "Login failed"
                }
        }

        // Launches Google's account picker when the SSO button is tapped
        googleSignInButton.setOnClickListener {
            Log.d(tag, "Launching Google sign-in")
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        goToRegisterText.setOnClickListener {
            Log.d(tag, "User navigating to Register screen")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Takes the Google ID token and uses it to sign in to Firebase,
    // creating a Firebase account automatically if one doesn't already exist
    // for this Google email
    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken == null) {
            Log.e(tag, "Google ID token was null")
            return
        }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Log.d(tag, "Firebase auth with Google successful")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Firebase auth with Google failed", exception)
                findViewById<TextView>(R.id.loginErrorText).text = "Google sign-in failed"
            }
    }
}