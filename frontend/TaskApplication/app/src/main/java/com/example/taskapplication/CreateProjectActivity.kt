package com.example.taskapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import kotlinx.android.synthetic.main.activity_create_project.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreateProjectActivity : BaseActivity(), View.OnClickListener {
    private lateinit var textViewDeadline: TextView
    private lateinit var editTextKeywords: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth
    // Declare an instance of Firebase Realtime Database.
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        buttonAddIcon.setOnClickListener(this)
        setDeadlineButton.setOnClickListener(this)
        textViewDeadline = findViewById(R.id.tv_project_deadline_time)
        editTextKeywords = findViewById(R.id.et_project_keywords)
        editTextName = findViewById(R.id.et_project_name)
        editTextDescription = findViewById(R.id.et_project_description)
        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_save,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            saveProject()
            true
        }
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun saveProject() {
        // handle input and create new project
        val name = editTextName.text.toString()
        val description = editTextDescription.text.toString()
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this,
                "Name and description can't be empty!",
                Toast.LENGTH_LONG).show()
            return
        }

        // TODO: Does not use the given deadline date yet.
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val date = currentDate.format(formatter)

        var keywords = listOf<String>()
        try {
            val input = et_project_keywords.text.toString()
            val regex = """^[a-zA-Z0-9\s]+$""".toRegex()

            if (!input.matches(regex)) {
                Toast.makeText(this,
                    "Only alphanumeric characters allowed in keywords. Use space as delimiter.",
                    Toast.LENGTH_LONG).show()
                return
            }
            keywords = input.split(" ", limit = 3)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

        var imageB64:String? = null
        try {
            val stream = ByteArrayOutputStream()
            val bitmap = (projectIcon.getDrawable() as BitmapDrawable).bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            bitmap.recycle()
            val byteArray = stream.toByteArray()
            imageB64 = Base64.getEncoder().encodeToString(byteArray)
        }  catch (e: Exception) {
            // Image not set
        }

        // get selected radio button from radioGroup
        val selectedRadioId = radioButtons.getCheckedRadioButtonId()
        // find the selected radiobutton by id
        val radioButton = findViewById<View>(selectedRadioId) as RadioButton
        val projectType = radioButton.text.toString().toLowerCase(Locale.ENGLISH)

        createProject(name, description, currentDate, keywords, imageB64, projectType)
    }

    private fun createProject(name: String,
                              description: String,
                              deadline: LocalDateTime,
                              keywords: List<String>,
                              imageB64: String?,
                              projectType: String) {
        Log.d(TAG, String.format("createProject(%s, %s, %s, %s, %s)", name, description,
            deadline.toString(), keywords, projectType))
        showProgressDialog()
        // Create a project with our API which returns the new project ID.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result!!.token!!
                Log.d(TAG, "idToken $idToken")
                val requestBody = mapOf(
                    "name" to name,
                    "description" to description,
                    "deadline" to deadline,
                    "keywords" to keywords,
                    "type" to projectType
                )
                Log.d(TAG, "---------- requestBody $requestBody")
                val jsonParams = JSONObject(requestBody)
                Log.d(TAG, "---------- jsonParams $jsonParams")
                val entity = StringEntity(jsonParams.toString())
                Log.d(TAG, "---------- entity $entity")
                // POST https://mcc-fall-2019-g09.appspot.com/project
                APIClient.post(
                    applicationContext,
                    "project",
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
                            // Add the received project ID to this user's projects.
                            /*val pid = response.getString("project_id")
                            database.child("users")
                                .child(auth.uid!!)
                                .child("projects")
                                .child(pid)
                                .setValue("")
                                .addOnSuccessListener { successRedirect() }*/
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
                            // NB, these are printed with `System.out` tag
                            headers?.forEach(::println)
                            Log.d(TAG, "response data: ${data.toString(2)}")
                            Log.d(TAG, "error $error")
                        }
                    })
            } else {
                // Handle error -> task.getException()
                Log.w(TAG, String.format("user id get was not successful: %s",
                    task.exception.toString()))
            }
            hideProgressDialog()
        }
    }

    private fun successRedirect() {
        // Redirect to ProjectsActivity upon success.
        val intent = Intent(this, ProjectsActivity::class.java)
        startActivity(intent)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAddIcon -> pickImageFromGallery()
            R.id.setDeadlineButton -> pickDeadline()
        }
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
                textViewDeadline.text = sdf.format(cal.time)
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


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST) {
            if (data != null) {
                try
                {
                    val selectedImageUri = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                    projectIcon.setImageBitmap(bitmap)
                    projectIcon.setBackgroundResource(android.R.color.transparent)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "CreateProjectActivity"
        // image pick code
        const val IMAGE_PICK_REQUEST = 1000
    }
}
