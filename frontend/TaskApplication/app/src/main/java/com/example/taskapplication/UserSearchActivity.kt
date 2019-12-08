package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import kotlinx.android.synthetic.main.activity_user_search.*
import org.json.JSONObject

class UserSearchActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Keep track of the project ID.
    private lateinit var projectId: String
    // Store the lists of all users.
    private lateinit var userIds: Array<String>
    private lateinit var usernames: Array<String>
    private lateinit var usersMap: Map<String, String>
    // ListView used to display all search results.
    private lateinit var lv: ListView
    // ArrayAdapter used to manage individual search results.
    private lateinit var adapter: ArrayAdapter<String>
    private val minInputLength = 3
    // An arbitrary filter string to display empty search results.
    private val emptyFilter = "204596i02459630495683049568304956"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        // Initialize the list of all usernames passed as an Intent extra.
        projectId = intent.extras?.getString("pid")!!
        userIds = intent.extras?.getStringArray("userIds")!!
        usernames = intent.extras?.getStringArray("usernames")!!
        Log.d(TAG, "-------- SEARCH usernames: ${usernames.contentToString()}")
        usersMap = usernames.zip(userIds).toMap()
        lv = list_view
        adapter = ArrayAdapter(applicationContext, R.layout.activity_user_search_item,
            R.id.user_name_item, usernames)
        lv.adapter = adapter
        adapter.filter.filter(emptyFilter)
        lv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                // Pass on the selected username to our API.
                Log.d(TAG, "position $position")
                Log.d(TAG, "getItemAtPosition ${parent.getItemAtPosition(position)}")
                val username = parent.getItemAtPosition(position).toString()
                val userId = usersMap.getValue(username)
                addUserToProject(projectId, arrayOf(mapOf("id" to userId)), username)
            }
        // Filter the search results as the user types.
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= minInputLength) {
                    adapter.filter.filter(s)
                } else {
                    adapter.filter.filter(emptyFilter)
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) { }
        })
    }

    private fun addUserToProject(projectId: String,
                                 userIds: Array<Map<String, String>>,
                                 username: String) {
        // Add the given usernames to the given project using our API.
        showProgressDialog()
        // Update the status of the given task with our API.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result!!.token!!
                val requestBody = mapOf(
                    "members" to userIds
                )
                val jsonParams = JSONObject(requestBody)
                val entity = StringEntity(jsonParams.toString())
                // POST https://mcc-fall-2019-g09.appspot.com/project/{projectId}/members
                APIClient.post(
                    applicationContext,
                    "project/$projectId/members",
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
                            Toast.makeText(applicationContext,
                                "User $username added to project",
                                Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(applicationContext,
                                "Something went wrong while adding the user " +
                                        username,
                                Toast.LENGTH_SHORT).show()
                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            response: String?,
                            error: Throwable?
                        ) {
                            // Called when response HTTP status is "4XX" (eg. 401, 403, 404).
                            Log.d(TAG, "createProject:APIClient:onFailure")
                            Log.d(TAG, "statusCode $statusCode")
                            Log.d(TAG, "headers ${headers?.forEach(::println)}")
                            Log.d(TAG, "response $response")
                            Log.d(TAG, "error $error")
                            /*Toast.makeText(applicationContext,
                                "Something went wrong while adding the user " +
                                        username,
                                Toast.LENGTH_SHORT).show()*/
                        }
                    })
            } else {
                // Handle error -> task.getException();
            }
            hideProgressDialog()
        }
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

    override fun onClick(v: View) {
        when (v.id) {
            // R.id.buttonChangeImgQSubmit -> changeImageQuality()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "UserSearchActivity"
    }
}