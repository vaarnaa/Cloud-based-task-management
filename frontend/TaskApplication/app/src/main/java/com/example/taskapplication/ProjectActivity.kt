package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_project.*

class ProjectActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Handle clicks in `onClick`.
        fab.setOnClickListener(this)
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

    // actions on click menu items
    /*override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }*/

    private fun createTask() {
        val intent = Intent(this, CreateTaskActivity::class.java)
        startActivity(intent)
    }

    private fun populateTaskList(projects: Map<String, String>) {
        // TODO: Render the retrieved tasks into a ListView with the necessary attributes.
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
                        // "populateTaskList" -> populateTaskList(dataSnapShot.value.toString())
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

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The projectId of this project must be passed as
            // an extra from an Activity calling this activity.
            val projectId = intent.extras?.getString("projectId")
            val projectPath = database.child("projects").child(projectId!!)
            val tasksPath = projectPath.child("tasks")
            // The user is signed in.
            // TODO: Fetch all project tasks here, sorted by creation date.
            //       For each, display a checkbox next to it.
            //       Database URL: /projects/<projectId>/tasks
            readFromDatabase(tasksPath, "populateTaskList")
        } else {
            // The user is signed out, so redirect to the login page.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> createTask()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ProjectActivity"
    }
}
