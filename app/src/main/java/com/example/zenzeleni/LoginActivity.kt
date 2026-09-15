package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = "Please fill in all fields"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    errorText.text = "Login successful!"
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { exception ->
                    errorText.text = exception.message ?: "Login failed"
                }
        }

        goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}