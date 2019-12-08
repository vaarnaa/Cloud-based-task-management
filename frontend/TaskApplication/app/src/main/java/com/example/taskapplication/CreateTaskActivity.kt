package com.example.taskapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import kotlinx.android.synthetic.main.activity_create_task.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class CreateTaskActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference
    // Keep track of the project ID and name for this task.
    private lateinit var projectId: String
    private lateinit var projectName: String
    // Keep track of the user-set deadline for the task.
    private lateinit var deadline: LocalDateTime
    private lateinit var  imageView: ImageView
    private lateinit var buttonAddImage: Button
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Initialize click listeners.
        buttonSaveTask.setOnClickListener(this)
        buttonSetTaskDeadline.setOnClickListener(this)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        imageView = findViewById(R.id.descriptionImage)
        buttonAddImage = findViewById(R.id.buttonAddImage)
        buttonAddImage.setOnClickListener(this)

    }

    override fun onStart() {
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

    private fun saveTask() {
        // Process input fields here, and then send them to `createTask`.
        val description = et_task_description.text.toString()
        createTask(description, deadline.toString())
    }

    private fun createTask(description: String, deadline: String) {
        showProgressDialog()
        // Create a task with our API.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                val idToken = t.result!!.token!!
                Log.d(TAG, "idToken $idToken")
                val requestBody = mapOf(
                    "description" to description,
                    "status" to "pending",
                    "deadline" to deadline
                )
                Log.d(TAG, "---------- requestBody $requestBody")
                val jsonParams = JSONObject(requestBody)
                Log.d(TAG, "---------- jsonParams $jsonParams")
                val entity = StringEntity(jsonParams.toString())
                Log.d(TAG, "---------- entity $entity")
                // POST https://mcc-fall-2019-g09.appspot.com/project/{projectId}/task
                APIClient.post(
                    applicationContext,
                    "project/$projectId/task",
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
                            successRedirect()
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

    private fun successRedirect() {
        // Redirect to ProjectActivity upon success.
        finish()
        /*
        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("pid", projectId)
        intent.putExtra("name", projectName)
        startActivity(intent)
        */
    }

    private fun pickDeadline() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                val myFormat = "dd-MM-yyyy HH:mm:ss"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                et_task_deadline.text = sdf.format(cal.time)
                deadline = LocalDateTime.of(year, month, day, hour, minute, 0)
            }, hour, minute, true)
        tpd.show()

        val dpd = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }, year, month, day)
        dpd.show()
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The projectId of this project must be passed as
            // an extra from an Activity calling this activity.
            projectId = intent.extras?.getString("pid")!!
            projectName = intent.extras?.getString("name")!!
            val cal = Calendar.getInstance()
            val myFormat = "dd-MM-yyyy HH:mm:ss"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            et_task_deadline.text = sdf.format(cal.time)
            deadline = LocalDateTime.now()
        } else {
            // The user is signed out, so redirect to the login page.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_GET_CONTENT,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        //intent.putExtra("aspectX", 1)
        //intent.putExtra("aspectY", 1)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (data != null) {
                try {
                    Log.i(TAG, "Image loaded")
                    imageUri = data?.data
                    imageView.setImageURI(imageUri)
                    detectDescription()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun detectDescription() {
        val uri = imageUri
        if (uri != null) {
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            val image = FirebaseVisionImage.fromFilePath(this, uri)
            detector.processImage(image)
                .addOnSuccessListener { texts ->
                    Log.d(TAG, "Detected: ${texts.text}")
                    et_task_description.setText(texts.text)
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext,
                        "Could not detect text from image",
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Could not detect text, $it")
                }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSaveTask -> saveTask()
            R.id.buttonSetTaskDeadline -> pickDeadline()
            R.id.buttonAddImage -> pickImageFromGallery()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "CreateTaskActivity"
        const val IMAGE_PICK_REQUEST = 1000
    }
}
