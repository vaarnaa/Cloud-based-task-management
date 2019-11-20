package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonProjects.setOnClickListener(this)
        buttonSettings.setOnClickListener(this)
        buttonLogout.setOnClickListener(this)
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun showProjects() {
        val intent = Intent(this, ProjectsActivity::class.java)
        startActivity(intent)
    }

    private fun showSettings() {
        // val intent = Intent(this, SettingsActivity::class.java)
        // startActivity(intent)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The user is signed in.
            status.text = getString(R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified)
            detail.text = getString(R.string.firebase_status_fmt, user.uid)
            userButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            status.setText(R.string.signed_out)
            detail.text = null
            userButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonProjects -> showProjects()
            R.id.buttonSettings -> showSettings()
            R.id.buttonLogout -> signOut()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "UserActivity"
    }
}