package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_project_members.*

class ProjectMembersActivity : BaseActivity() {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Usernames of project members.
    private lateinit var usernames: Array<String>
    // ListView used to display all search results.
    private lateinit var lv: ListView
    // ArrayAdapter used to manage individual search results.
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_members)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        // Initialize the list of all usernames passed as an Intent extra.
        usernames = intent.extras?.getStringArray("usernames")!!
        Log.d(TAG, "-------- MEMBERS usernames: ${usernames.contentToString()}")
        lv = project_members_list
        adapter = ArrayAdapter(applicationContext, R.layout.activity_user_search_item,
            R.id.user_name_item, usernames)
        lv.adapter = adapter
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            // The user is signed out, so redirect to the login page.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "ProjectMembersActivity"
    }
}