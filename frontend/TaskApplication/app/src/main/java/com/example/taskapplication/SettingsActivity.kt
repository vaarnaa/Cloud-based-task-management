package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonChangePassword.setOnClickListener(this)
        buttonChangeProfImg.setOnClickListener(this)
        buttonChangeImgQ.setOnClickListener(this)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
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

    private fun changeImageQuality() {
        val intent = Intent(this, ImageQualityActivity::class.java)
        startActivity(intent)
    }

    private fun readFromDatabase(dbPath: DatabaseReference, action: String) {
        // Create a listener to be able to read from the database.
        val listener = object : ValueEventListener {
            // Called (1) when this listener is attached for the first time, and
            // (2) when the data is updated for the first time.
            // Afterwards this listener is removed.
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                Log.d(TAG, "readFromDatabase:onDataChange dataSnapShot: $dataSnapShot")
                if (dataSnapShot.exists()) {
                    // A key-value pair was found at the given database path.
                    when (action) {
                        "updateImageQuality" -> updateImageQuality(dataSnapShot.value.toString())
                    }
                } else {
                    // A key-value pair was not found at the given database path.
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // An error occurred (probably due to incorrect Firebase Database rules).
                Log.d(TAG, "readFromDatabase:onCancelled: $databaseError")
                Toast.makeText(baseContext, "Something went wrong when reading from database",
                    Toast.LENGTH_SHORT).show()
            }
        }
        // Attach the listener to the given database path.
        dbPath.addListenerForSingleValueEvent(listener)
    }

    private fun updateImageQuality(imgQuality: String) {
        imageQualitySetting.text = getString(R.string.image_quality_setting, imgQuality)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            val userPath = database.child("users").child(user.uid)
            val imgQuality = userPath.child("imageQuality")
            // The user is signed in.
            status.text = getString(R.string.emailpassword_status_fmt, user.email)
            userId.text = getString(R.string.firebase_status_fmt, user.uid)
            displayName.text = getString(R.string.firebase_display_name_fmt, user.displayName)
            profilePhotoUrl.text = getString(R.string.firebase_profile_photo_fmt, user.photoUrl)
            readFromDatabase(imgQuality, "updateImageQuality")
            profileButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            status.setText(R.string.signed_out)
            userId.text = null
            displayName.text = null
            profilePhotoUrl.text = null
            imageQualitySetting.text = null
            profileButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonChangePassword -> changePassword()
            R.id.buttonChangeProfImg -> changeProfileImage()
            R.id.buttonChangeImgQ -> changeImageQuality()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "SettingsActivity"
    }
}