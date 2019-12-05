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
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_create_project.*
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
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val date = currentDate.format(formatter)
        // TODO:

        var keywords = listOf<String>()
        try {
            val input = editTextName.text.toString()
            if (!input.matches(("^[a-zA-Z0-9]+$").toRegex())) { // It's not working...
                Toast.makeText(this,
                    "Only alphanumeric characters allowed in keywords. Use space as delimiter.",
                    Toast.LENGTH_LONG).show()
                return
            }
            keywords = input.split(" ", limit = 3)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            //
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
        val projectType = radioButton.text.toString()

        createProject(name, description, currentDate, keywords, imageB64, projectType)
    }

    private fun createProject(name: String,
                              description: String,
                              date: LocalDateTime,
                              keywords: List<String>,
                              imageB64: String?,
                              projectType: String) {
        showProgressDialog()
        // Create a project with our API which returns the new project ID.
        val user = auth.currentUser
        user!!.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result!!.token
                Log.d(TAG, "idToken $idToken")
                val params = RequestParams()
                Log.d(TAG, "----- 1 ----- params $params")
                params.put("name", name)
                params.put("description", description)
                params.put("date", date)
                Log.d(TAG, "----- 2 ----- params $params")
                params.put("access_token", idToken) // Must be included to identify the user.
                // params.put("keywords", keywords)
                // params.put("projectType", projectType)
                /* Structure is:
                    projectId: {
                        admin: userId
                        badge: badgeUrl (in Firebase Storage),
                        created: "Tue, 3 Dec 2019 07:00:00 GMT",
                        deadline: "Wed, 14 Jun 2020 07:00:00 GMT",
                        description: description,
                        name: projectName,
                        keywords: {
                            keyword1: "",
                            keyword2: "",
                            keyword3: ""
                        },
                        members: {
                            userId1: username1,
                            userId2: username2,
                            ...
                        }
                    }
                 */
                // POST https://mcc-fall-2019-g09.appspot.com/project
                APIClient.post("project", params, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        response: ByteArray
                    ) {
                        // Called when response HTTP status is "200 OK".
                        Log.d(TAG, "createProject:APIClient:Post:onSuccess")
                        successRedirect()
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseBody: ByteArray?,
                        error: Throwable?
                    ) {
                        // Called when response HTTP status is "4XX" (eg. 401, 403, 404).
                        Log.d(TAG, "createProject:APIClient:Post:onFailure")
                        Log.d(TAG, "statusCode $statusCode")
                        Log.d(TAG, "headers ${headers?.forEach(::println)}")
                        Log.d(TAG, "responseBody ${responseBody.toString()}")
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
