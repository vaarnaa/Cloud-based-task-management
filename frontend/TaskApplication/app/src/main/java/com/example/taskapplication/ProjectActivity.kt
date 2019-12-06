package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ListView
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
    // Declare an instance of ListView to display the list of tasks.
    private lateinit var listView: ListView
    private lateinit var taskAdapter: TasksCustomAdapter
    /*private var taskEntries = arrayListOf(mapOf(
        "a" to "av",
        "b" to "bv"
    ))*/
    private val taskEntries = arrayListOf<Map<String, String>>()
    private var updatingTaskList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        // setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Handle clicks in `onClick`.
        fab.setOnClickListener(this)
        // Initialize the task list and the adapter used to populate it.
        listView = projectTasksView
        taskAdapter = TasksCustomAdapter(applicationContext, taskEntries)
        listView.adapter = taskAdapter
        // TODO: listView.onItemClickListener to toggle checkbox by clicking text?
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

    fun onCheckboxClicked(v: View) {
        // Set these to false in order to send the click events to the parent Activity.
        // v.isClickable = false
        // v.isFocusable = false
        if (v is CheckBox) {
            if (v.isChecked) {
                // TODO: The task was marked done, so cross out the text field and make it gray.
                Log.d(TAG, "CHECKED")
            } else {
                // TODO: The task was marked undone, so make the text field normal again.
                Log.d(TAG, "NOT CHECKED")
            }
        }
    }

    private fun populateTaskList(tasks: DataSnapshot) {
        // Render the retrieved tasks into a ListView with the necessary attributes.
        Log.d(TAG, "populateTaskList:tasks: $tasks")
        // Ensure mutual exclusion while updating the critical section.
        while (updatingTaskList) { }
        updatingTaskList = true
        taskEntries.clear()
        // val taskMap = arrayListOf<Map<String, String>>()
        tasks.children.forEach {
            val tid = it.key.toString()
            val description = it.child("description").value.toString()
            val status = it.child("status").value.toString()
            val created = it.child("created").value.toString()
            taskEntries.add(mapOf(
                "tid" to tid,
                "description" to description,
                "status" to status,
                "created" to created))
        }
        Log.d(TAG, "taskEntries: ${taskEntries.toTypedArray().contentToString()}")
        // Sort the list by creation date, recent ones first.
        taskEntries.sortWith(compareBy {
            Log.d(TAG, "it: $it")
            it.getValue("created")
        })
        taskEntries.reverse()
        Log.d(TAG,"sorted taskEntries: ${taskEntries.toTypedArray().contentToString()}")
        updatingTaskList = false
        // Refresh the task list view.
        taskAdapter.notifyDataSetChanged()
        Log.d(TAG, "taskEntries:${taskEntries.toTypedArray().contentToString()}")
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
                        "populateTaskList" -> populateTaskList(dataSnapShot)
                    }
                } else {
                    // A key-value pair was not found at the given database path.
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // An error occurred (probably due to incorrect Firebase Database rules).
                Log.d(TAG, "readFromDatabase:onCancelled: $databaseError")
                Toast.makeText(baseContext, "Error while reading from database",
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
            val pid = intent.extras?.getString("pid")
            val projectPath = database.child("projects").child(pid!!)
            val tasksPath = projectPath.child("tasks")
            // The user is signed in, so fetch all tasks here, sorted by creation date.
            // For each, display a checkbox next to it.
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
