package com.example.taskapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit) as Button
        val etUsername = findViewById<EditText>(R.id.et_email) as EditText
        val etPassword = findViewById<EditText>(R.id.et_password) as EditText
        val etEmail = findViewById<EditText>(R.id.et_email) as EditText

        buttonSubmit.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text
            val email = etEmail.text

            // code to resolve sign up
            // afterwards redirect user to some other activity
        }
    }
}