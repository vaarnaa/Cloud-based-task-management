package com.example.taskapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import kotlinx.android.synthetic.main.activity_project.*
import org.json.JSONObject

class ProjectActivity : BaseActivity(),
    View.OnClickListener,
    TasksFragment.OnFragmentInteractionListener,
    PicturesFragment.OnFragmentInteractionListener,
    FilesFragment.OnFragmentInteractionListener{
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Keep track of the project ID for this task.
    lateinit var projectId: String
    // Declare an instance of ListView to display the list of tasks.
    private lateinit var listView: ListView
    private lateinit var taskAdapter: TasksCustomAdapter
    private val taskEntries = arrayListOf<Map<String, String>>()
    private var updatingTaskList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Handle clicks in `onClick`.
        fab_project.setOnClickListener(this)
        // Initialize the task list and the adapter used to populate it.
        listView = projectTasksView
        taskAdapter = TasksCustomAdapter(applicationContext, taskEntries)
        listView.adapter = taskAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                // Update the status of a clicked task using our API.
                Log.d(TAG, "position $position")
                Log.d(TAG, "getItemAtPosition ${parent.getItemAtPosition(position)}")
                val id = taskAdapter.getItem(position)!!.getValue("tid")
                var st = taskAdapter.getItem(position)!!.getValue("status")
                val assigned = false // TODO: Check if database path tasks/<tid>/users has children.
                // If the checkbox was checked, mark it unchecked.
                // Change the status depending whether it was assigned to a user or not.
                if (st == "completed") {
                    if (assigned) st = "on-going" else st = "pending"
                } else {
                    st = "completed"
                }
                updateTask(id, st)
            }

        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Set default fragment and place in layout
        if (savedInstanceState == null) {
            val firstFragment = TasksFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_place_holder, firstFragment).commit()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        // Open fragment for the specific menu item
        when (item.itemId) {
            R.id.navigation_tasks -> {
                Toast.makeText(this,
                    "Tasks clicked",
                    Toast.LENGTH_SHORT).show()
                val tasksFragment = TasksFragment.newInstance()
                openFragment(tasksFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pictures -> {
                Toast.makeText(this,
                    "Pictures clicked",
                    Toast.LENGTH_SHORT).show()
                val picturesFragment = PicturesFragment.newInstance()
                openFragment(picturesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_files -> {
                Toast.makeText(this,
                    "Files clicked",
                    Toast.LENGTH_SHORT).show()
                val filesFragment = FilesFragment.newInstance()
                openFragment(filesFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_place_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun createTask() {
        val intent = Intent(this, CreateTaskActivity::class.java)
        intent.putExtra("pid", projectId)
        startActivity(intent)
    }

    fun updateTask(tid: String, status: String) {
        showProgressDialog()
        // Update the status of the given task with our API.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result!!.token!!
                Log.d(TAG, "idToken $idToken")
                val requestBody = mapOf(
                    "status" to status
                )
                Log.d(TAG, "---------- requestBody $requestBody")
                val jsonParams = JSONObject(requestBody)
                Log.d(TAG, "---------- jsonParams $jsonParams")
                val entity = StringEntity(jsonParams.toString())
                Log.d(TAG, "---------- entity $entity")
                // PUT https://mcc-fall-2019-g09.appspot.com/project/{pid}/task/{tid}/status
                APIClient.put(
                    applicationContext,
                    "project/$projectId/task/$tid/status",
                    idToken,
                    entity,
                    ContentType.APPLICATION_JSON.mimeType,
                    object : JsonHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            response: JSONObject
                        ) {
                            // Called when response HTTP status is "200 OK".
                            Log.d(TAG, "createProject:APIClient:onSuccess")
                            updateUI(user)
                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            error: Throwable?,
                            data: JSONObject
                        ) {
                            // Called when response HTTP status is "4XX" (eg. 401, 403, 404).
                            Log.d(TAG, "createProject:APIClient:onFailure")
                            Log.d(TAG, "statusCode $statusCode")
                            Log.d(TAG, "headers ${headers?.forEach(::println)}")
                            Log.d(TAG, "data ${data.toString(2)}")
                            Log.d(TAG, "error $error")
                        }
                    })
            } else {
                // Handle error -> task.getException();
            }
            hideProgressDialog()
        }
    }

    private fun populateTaskList(tasks: DataSnapshot) {
        // Render the retrieved tasks into a ListView with the necessary attributes.
        Log.d(TAG, "populateTaskList:tasks: $tasks")
        // Ensure mutual exclusion while updating the critical section.
        while (updatingTaskList) { }
        updatingTaskList = true
        taskEntries.clear()
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
            projectId = intent.extras?.getString("pid")!!
            val projectName = intent.extras?.getString("name")!!
            supportActionBar?.setTitle(projectName)
            val projectPath = database.child("projects").child(projectId)
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
            R.id.fab_project -> createTask()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ProjectActivity"
    }
}
