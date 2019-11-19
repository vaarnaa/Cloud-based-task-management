package com.example.taskapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : Activity() {
    // Declare an instance of Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin) as Button
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp) as Button
        val etUsername = findViewById<EditText>(R.id.et_username) as EditText
        val etPassword = findViewById<EditText>(R.id.et_password) as EditText

        buttonLogin.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text

            // code to resolve login
            // afterwards redirect user to some other activity
        }

        buttonSignUp.setOnClickListener {
            // redirect user to sign up activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        // TODO: updateUI(currentUser)
    }
}












/*
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // all views
    var myButton = findViewById(R.id.my_button)
    var etUsername: EditText = findViewById<EditText>(R.id.et_username) as EditText
    var etPassword: EditText = findViewById<EditText>(R.id.et_password) as EditText
    val buttonLogin : Button = findViewById<Button>(R.id.buttonLogin) as Button
    val buttonSignUp: Button = findViewById<Button>(R.id.buttonSignUp) as Button

    buttonLogin.setOnClickListener(object : View.OnClickListener{
        override fun onClick(v: View?) {
            //Your code here
        }})

    val clickListener = View.OnClickListener { view ->

        when (view.getId()) {
            R.id.buttonLogin -> resolveLogin()
            R.id.buttonSignUp -> resolveSignUp()
        }
    }

    fun resolveLogin() {
        val username = et_username.text
        val password = et_password.text

        // code for resolving login
        println("login")
    }

    fun resolveSignUp() {
        // user directed to signUp view

        println("signUp")
    }
    /*
    // set on-click listener
    buttonLogin.setOnClickListener {
        val user_name = et_username.text;
        val password = et_password.text;

        // your code to validate the user_name and password combination
        // and verify the same

    }
    */

*/




