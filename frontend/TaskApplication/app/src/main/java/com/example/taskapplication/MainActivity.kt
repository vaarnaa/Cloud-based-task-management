package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonLogin.setOnClickListener(this)
        buttonSignup.setOnClickListener(this)
        buttonLogout.setOnClickListener(this) // TODO: Move this to UserActivity.
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        // TODO: Redirect to UserActivity if signed in.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signUp() {
        // Redirect the user to the sign up activity.
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information.
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Logging in failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    status.setText(R.string.auth_failed)
                }
                hideProgressDialog()
            }
    }

    // TODO: Move this to UserActivity.
    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun validateForm(): Boolean {
        var valid = true
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

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            status.text = getString(R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified)
            detail.text = getString(R.string.firebase_status_fmt, user.uid)
            loginButtons.visibility = View.GONE
            loginFields.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE
        } else {
            status.setText(R.string.signed_out)
            detail.text = null
            loginButtons.visibility = View.VISIBLE
            loginFields.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonLogin -> signIn(et_email.text.toString(), et_password.text.toString())
            R.id.buttonSignup -> signUp()
            R.id.buttonLogout -> signOut() // TODO: Move this to UserActivity.
        }
    }

    companion object {
        // Used for debugging purposes.
        private const val TAG = "MainActivity"
    }
}