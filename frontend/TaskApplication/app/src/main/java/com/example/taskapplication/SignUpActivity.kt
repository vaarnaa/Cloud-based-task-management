package com.example.taskapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SignUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit) as Button
        val etUsername = findViewById<EditText>(R.id.et_username) as EditText
        val etPassword = findViewById<EditText>(R.id.et_password) as EditText
        val etEmail = findViewById<EditText>(R.id.et_email) as EditText

        buttonSubmit.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text
            val email = etEmail.text

            // code to resolve sign up
            // afterwards redirect user to useractivity if successful
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }
}