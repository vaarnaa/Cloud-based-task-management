package com.example.taskapplication

import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

// Should 'open' be changed to 'abstract'?
open class BaseActivity : AppCompatActivity() {
    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    /* Copy this to other classes and modify it as needed to read from Firebase Realtime Database. */
    private fun readFromDatabase(dbPath: DatabaseReference,
                                 TAG: String,
                                 onComplete: () -> Unit,
                                 onFailure: (() -> Unit)? = {}) {
        // Create a listener to be able to read from the database.
        val listener = object : ValueEventListener {
            // Called (1) when this listener is attached for the first time, and
            // (2) when the data is updated for the first time.
            // Afterwards this listener is removed.
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                Log.d(TAG, "readFromDatabase:onDataChange " +
                        "dataSnapShot: $dataSnapShot exists: ${dataSnapShot.exists()}")
                if (dataSnapShot.exists()) {
                    // A key-value pair was found at the given database path.
                    // Must call a function instead of returning/modifying a value here.
                    onComplete()
                } else {
                    // A key-value pair was not found at the given database path.
                    if (onFailure != null) onFailure()
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
}