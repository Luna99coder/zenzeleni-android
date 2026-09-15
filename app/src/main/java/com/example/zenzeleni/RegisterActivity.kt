package com.example.zenzeleni

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

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

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorText.text = "Please fill in all fields"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    errorText.text = "Account created successfully!"
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener { exception ->
                    errorText.text = exception.message ?: "Registration failed"
                }
        }

        goToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}