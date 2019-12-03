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
import kotlin.random.Random.Default.nextInt

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
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    private fun createAccount(username: String, email: String, password: String) {
        showProgressDialog()
        Log.d(TAG, "createAccount:$email")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation success.
                    Log.d(TAG, "createUserWithEmail:success")
                    // Set the display name and profile image of the user.
                    val user = auth.currentUser!!
                    // Register the display name in the database.
                    database.child("usernames").child(username).setValue(user.uid)
                    // Register other user profile info in the database.
                    val userProfilePath = database.child("users").child(user.uid)
                    userProfilePath.child("username").setValue(username)
                    userProfilePath.child("imageQuality").setValue("high")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(Uri.parse(et_prof_img.text.toString()))
                        .build()
                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { t ->
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
            et_username.error = null
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

    private fun validateUsername(username: String) {
        if (!validateForm()) return
        showProgressDialog()
        // Create a listener to be able to read from the Firebase Database.
        val listener = object : ValueEventListener {
            // Called (1) when this listener is attached for the first time, and
            // (2) when the data is updated for the first time.
            // Afterwards this listener is removed (here it makes no difference since
            // we only use this for case (1)).
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                Log.d(TAG, "validateUsername:onDataChange " +
                        "dataSnapShot: $dataSnapShot " + "exists: ${dataSnapShot.exists()}")
                // `dataSnapShot.exists == false` means that no username exists in the database.
                if (!dataSnapShot.exists()) {
                    // Username is available.
                    Log.d(TAG, "validateUsername:valid")
                    et_username.error = null
                    // Proceed to creating the Firebase Auth account.
                    createAccount(
                        et_username.text.toString(),
                        et_email.text.toString(),
                        et_password.text.toString())
                } else {
                    // Username is not available, suggest three alternatives.
                    Log.d(TAG, "validateUsername:invalid")
                    val arr = generateUsernames(username)
                    var text = "Username unavailable.\nYou can try these:\n"
                    for (v in arr) text += "$v\n"
                    et_username.error = text
                    Toast.makeText(baseContext, "Username unavailable.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // An error occurred (probably due to incorrect Firebase Database rules).
                Log.d(TAG, "validateUsername:onCancelled: $databaseError")
                Toast.makeText(baseContext, "Something went wrong...",
                    Toast.LENGTH_SHORT).show()
            }
        }
        // Attach the listener to the path of the desired username.
        database.child("usernames").child(username)
            .addListenerForSingleValueEvent(listener)
        hideProgressDialog()
    }

    private fun generateUsernames(username: String): Array<String> {
        val ns = Array(3) { nextInt(100, 200) }
        val ss = arrayOf("", "", "")
        for (i in ns.indices) ss[i] = "$username${ns[i]}"
        return ss
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignupSubmit -> validateUsername(et_username.text.toString())
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "SignUpActivity"
    }
}