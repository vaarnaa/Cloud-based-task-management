package com.example.taskapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonSignupSubmit.setOnClickListener(this)
        buttonUsernameTest.setOnClickListener(this)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation success.
                    Log.d(TAG, "createUserWithEmail:success")
                    // Set the display name and profile image of the user.
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(et_username.text.toString())
                        .setPhotoUri(Uri.parse(et_prof_img.text.toString()))
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { t ->
                            if (t.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    // Redirect the user to the user activity.
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                } else {
                    // If account creation fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Signing up failed.",
                        Toast.LENGTH_SHORT).show()
                }
                hideProgressDialog()
            }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val username = et_username.text.toString()
        if (TextUtils.isEmpty(username)) {
            et_username.error = "Required."
            valid = false
        } else {
            val v = validateUsername(username)
            if (v.isEmpty()) {
                et_username.error = null
            } else {
                et_username.error = v
                valid = false
            }
        }
        val email = et_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            et_email.error = "Required."
            valid = false
        } else {
            et_email.error = null
        }
        val password = et_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            et_password.error = "Required."
            valid = false
        } else {
            et_password.error = null
        }
        return valid
    }

    private fun validateUsername(username: String): String {
        // TODO: Check from Firebase Database.
        val transactionSuccess = true
        if (transactionSuccess) {
            // Valid username, return an empty error string.
            return ""
        } else {
            // TODO: Username has been taken, suggest three alternatives.
            return "Username taken!"
        }
    }

    private fun usernameTest(username: String) {
        database.child("usernames").child(username)
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    Log.d(TAG, mutableData.toString())
                    if (mutableData.getValue(String::class.java) == null) {
                        // The 'username' key does not exist, so it is available.
                        // The value does not matter here.
                        mutableData.value = "asd"
                        return Transaction.success(mutableData)
                    }
                    // Username taken, abort the transaction.
                    return Transaction.abort()
                }
                override fun onComplete(
                    databaseError: DatabaseError?,
                    commited: Boolean,
                    dataSnapshot: DataSnapshot?
                ) {
                    // Transaction completed.
                    if (commited) {
                        // Username saved.
                        et_username.error = null
                        Toast.makeText(baseContext, "Username saved successfully!",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        // The username was taken, so inform the user of failure.
                        et_username.error = "Unavailable username."
                        Toast.makeText(baseContext, "Unavailable username.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignupSubmit -> createAccount(
                et_email.text.toString(), et_password.text.toString())
            R.id.buttonUsernameTest -> usernameTest(et_username.text.toString())
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "SignUpActivity"
    }
}