package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonChangePassword.setOnClickListener(this)
        buttonChangeProfImg.setOnClickListener(this)
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun changePassword() {
        val intent = Intent(this, PasswordActivity::class.java)
        startActivity(intent)
    }

    private fun changeProfileImage() {
        val intent = Intent(this, ProfileImageActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The user is signed in.
            status.text = getString(R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified)
            userId.text = getString(R.string.firebase_status_fmt, user.uid)
            displayName.text = getString(R.string.firebase_display_name_fmt,
                user.displayName)
            profilePhotoUrl.text = getString(R.string.firebase_profile_photo_fmt,
                user.photoUrl)
            profileButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            status.setText(R.string.signed_out)
            userId.text = null
            displayName.text = null
            profilePhotoUrl.text = null
            profileButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonChangePassword -> changePassword()
            R.id.buttonChangeProfImg -> changeProfileImage()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "SettingsActivity"
    }
}