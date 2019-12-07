package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_projects.*

class ProjectsActivity : BaseActivity(), View.OnClickListener  {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Declare an instance of ListView to display the list of projects.
    private lateinit var listView: ListView
    private lateinit var customAdapter: ProjectsCustomAdapter
    private val projectEntries = arrayListOf<Map<String, String>>()
    private var updatingProjectList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Handle clicks in `onClick`.
        fab_projects.setOnClickListener(this)
        // Initialize the project list and the adapter used to populate it.
        listView = projectsListView
        customAdapter = ProjectsCustomAdapter(applicationContext, projectEntries)
        listView.adapter = customAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                // Redirect to the clicked project item,
                // passing on the projectId as an Intent extra.
                Log.d(TAG, "position $position")
                Log.d(TAG, "getItemAtPosition ${parent.getItemAtPosition(position)}")
                val pid = customAdapter.getItem(position)!!.getValue("pid")
                val name = customAdapter.getItem(position)!!.getValue("name")
                val intent = Intent(this, ProjectActivity::class.java)
                intent.putExtra("pid", pid)
                intent.putExtra("name", name)
                startActivity(intent)
            }
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
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun createProject() {
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivity(intent)
    }

    private fun fetchProjects(projectIds: DataSnapshot) {
        // Fetch the list of the current user's projects.
        val l = arrayListOf<String>()
        projectIds.children.forEach { l.add(it.key.toString()) }
        val p = l.toTypedArray()
        Log.d(TAG, "fetchProjects:p: ${p.contentToString()}")

        // Fetch the project contents for each project ID.
        projectEntries.clear()
        for (id in p) {
            val ref = database.child("projects").child(id)
            readFromDatabase(ref,"populateProjectList")
        }
    }

    private fun populateProjectList(projects: DataSnapshot) {
        // Render the retrieved projects into a ListView with the necessary attributes.
        Log.d(TAG, "populateProjectList:projects: $projects")
        val pid = projects.key.toString()
        val name = projects.child("name").value.toString()
        val modified = projects.child("modified").value.toString()
        val projectMap = mapOf(
            "pid" to pid,
            "name" to name,
            "modified" to modified)

        Log.d(TAG, "pid: $pid name: $name modified: $modified")

        // Ensure mutual exclusion while updating the critical section.
        while (updatingProjectList) { }
        updatingProjectList = true
        projectEntries.add(projectMap)

        // Sort the list by modification date, recent ones first.
        projectEntries.sortWith(compareBy {
            Log.d(TAG, "it: $it")
            it.getValue("modified")
        })
        projectEntries.reverse()
        Log.d(TAG,"sorted projectEntries: ${projectEntries.toTypedArray().contentToString()}")
        updatingProjectList = false

        // Refresh the project list view.
        customAdapter.notifyDataSetChanged()
        Log.d(TAG, "projectEntries:${projectEntries.toTypedArray().contentToString()}")
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
                        "fetchProjects" -> fetchProjects(dataSnapShot)
                        "populateProjectList" -> populateProjectList(dataSnapShot)
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
            val userProjectsPath = database.child("users")
                .child(auth.uid!!)
                .child("projects")
            // The user is signed in, so fetch all projects here, sorted by modification date.
            // For each, show modification date, media icon, and up to 3 profile images.
            readFromDatabase(userProjectsPath, "fetchProjects")

        } else {
            // The user is signed out, so redirect to the login page.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_projects -> createProject()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ProjectsActivity"
    }
}
