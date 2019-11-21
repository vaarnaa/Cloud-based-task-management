package com.example.taskapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_profile_image.*

class ProfileImageActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_image)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonChangeProfImgSubmit.setOnClickListener(this)
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun changeProfileImage() {
        // Update the profile image of the user.
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(et_image_url.text.toString()))
            .build()
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    Log.d(TAG, "User profile image updated.")
                    // Redirect back to the profile settings page if updated successfully.
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The user is signed in.
            profileButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            profileButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonChangeProfImgSubmit -> changeProfileImage()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ProfileImageActivity"
    }
}