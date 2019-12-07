package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTaskActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Keep track of the project ID for this task.
    private lateinit var projectId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        // Initialize click listeners.
        // buttonSaveTask.setOnClickListener(this) // TODO: Uncomment after this is implemented.
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun saveTask() {
        // TODO: Process input fields here, and then send them to `createTask`.
    }

    private fun createTask(description: String, status: String, deadline: String) {
        showProgressDialog()
        // Create a task with our API.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                val idToken = t.result!!.token
                Log.d(TAG, "idToken $idToken")
                val params = RequestParams()
                Log.d(TAG, "----- 1 ----- params $params")
                params.put("description", description)
                params.put("status", status)
                params.put("deadline", deadline)
                Log.d(TAG, "----- 2 ----- params $params")
                params.put("access_token", idToken) // Must be included to identify the user.
                // POST https://mcc-fall-2019-g09.appspot.com/project/{projectId}/task
                APIClient.post("project/$projectId/task", params, object : JsonHttpResponseHandler() {
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        response: JSONObject
                    ) {
                        // Called when response HTTP status is "200 OK".
                        Log.d(TAG, "createProject:APIClient:onSuccess")
                        successRedirect()
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseString: String,
                        error: Throwable?
                    ) {
                        // Called when response HTTP status is "4XX" (eg. 401, 403, 404).
                        Log.d(TAG, "createProject:APIClient:onFailure")
                        Log.d(TAG, "statusCode $statusCode")
                        Log.d(TAG, "headers ${headers?.forEach(::println)}")
                        Log.d(TAG, "responseString $responseString")
                        Log.d(TAG, "error $error")
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
                        Log.d(TAG, "data $data")
                        Log.d(TAG, "error $error")
                    }
                })
            } else {
                // Handle error -> task.getException();
            }
            hideProgressDialog()
        }
    }

    private fun successRedirect() {
        // Redirect to ProjectActivity upon success.
        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("pid", projectId)
        startActivity(intent)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The projectId of this project must be passed as
            // an extra from an Activity calling this activity.
            projectId = intent.extras?.getString("pid")!!
        } else {
            // The user is signed out, so redirect to the login page.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            // R.id.buttonSaveTask -> saveTask() // TODO: Uncomment after this is implemented.
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "CreateTaskActivity"
    }
}
