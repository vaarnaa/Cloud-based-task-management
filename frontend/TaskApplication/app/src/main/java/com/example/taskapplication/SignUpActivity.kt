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
        buttonUsernameTest.setOnClickListener(this)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
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

    private fun usernameTest(username: String) {
        return
    }

    private fun validateUsername(username: String) {
        if (!validateForm()) return
        showProgressDialog()
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                Log.d(TAG, "validateUsername:onDataChange")
                val user = dataSnapShot.getValue(String::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // The given username exists already.
                Log.d(TAG, "validateUsername:onCancelled", databaseError.toException())
                // ...
            }
        }
        val d = database.child("usernames").child(username)
        d.addValueEventListener(listener)
        Log.d(TAG, "d: $d")
        d.setValue("uid")
            /*.addOnSuccessListener {
                succeeded(username)
                // createAccount(et_email.text.toString(), et_password.text.toString())
            }
            .addOnFailureListener {
                failed()
            }*/
        // Log.d(TAG, "set the value")
        hideProgressDialog()

        // Checking the username for uniqueness.
        /*Log.d(TAG, "CALLED validateUsername")
        val validUN: Boolean = validateUsername(username)
        Log.d(TAG, "RETURNED validateUsername")
        if (!validUN) {
            Toast.makeText(baseContext, "validateUsername FALSE",
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(baseContext, "validateUsername TRUE",
                Toast.LENGTH_SHORT).show()
        }*/


        // showProgressDialog()
        // Save usernames into /usernames/<username>.
        // var valid: Boolean? = null
        /* val th = object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                Log.d(TAG, mutableData.toString())
                if (mutableData.getValue(String::class.java) == null) {
                    // The 'username' key does not exist, so it is available.
                    // The value does not matter here.
                    mutableData.value = "uid"
                    return Transaction.success(mutableData)
                }
                // Username taken, abort the transaction.
                return Transaction.abort()
            }
            override fun onComplete(
                // null if no errors occurred, otherwise it contains a description of the error.
                error: DatabaseError?,
                // True if the transaction successfully completed,
                // false if it was aborted or an error occurred.
                committed: Boolean,
                // The current data at the location or null if an error occurred.
                currentData: DataSnapshot?
            ) {
                /*if (committed) {
                    // Username was available and saved successfully.
                    et_username.error = null
                    Toast.makeText(baseContext, "Username saved successfully!",
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "SUCCESS data: $committed")
                    // valid = true
                } else {
                    // Username was taken, so inform of failure.
                    val arr = generateUsernames(username)
                    var text = "Username unavailable.\nYou can try these:\n"
                    for (v in arr) text += "$v\n"
                    et_username.error = text
                    Toast.makeText(baseContext, "Username unavailable.",
                        Toast.LENGTH_SHORT).show()
                    // valid = false
                }
                // hideProgressDialog()*/
            }
        }
        database.child("usernames").child(username)
            .runTransaction(th)
        return th
        while (true) {
            if (valid != null) return valid
        } */
    }

    private fun generateUsernames(username: String): Array<String> {
        val ns = Array(3) { nextInt(100, 200) }
        val ss = arrayOf("", "", "")
        for (i in ns.indices) ss[i] = "$username${ns[i]}"
        return ss
    }

    private fun failed() {
        Log.d(TAG, "FAILED")
    }

    private fun succeeded(username: String) {
        Log.d(TAG, "SUCCEEDED: $username")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignupSubmit -> validateUsername(et_username.text.toString())
            R.id.buttonUsernameTest -> usernameTest(et_username.text.toString())
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "SignUpActivity"
    }
}