package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_image_quality.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class ImageQualityActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_quality)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonChangeImgQSubmit.setOnClickListener(this)
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

    private fun changeImageQuality() {
        val userId = auth.currentUser!!.uid
        // Save to /users/<uid>/imageQuality in the database.
        val dbPath = database.child("users").child(userId).child("imageQuality")
        val imgQuality = spinnerImgQ.selectedItem.toString()
            .toLowerCase(Locale.ENGLISH).split(" ")[0]
        dbPath.setValue(imgQuality).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                Log.d(TAG, "Image quality setting updated.")
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
            imageQualityButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            imageQualityButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonChangeImgQSubmit -> changeImageQuality()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ImageQualityActivity"
    }
}