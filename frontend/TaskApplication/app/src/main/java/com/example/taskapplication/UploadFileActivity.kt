package com.example.taskapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import androidx.core.net.toUri
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.util.Date

class UploadFileActivity : BaseActivity(), View.OnClickListener{

    private lateinit var textViewFileInfo: TextView // for displaying data about file
    private lateinit var imageViewPreview: ImageView // for displaying preview of images

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private lateinit var selectedFile: File // File that will be selected during this activity

    // Keep track of the project ID and name for this task.
    private lateinit var projectId: String
    private lateinit var projectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)
        setSupportActionBar(findViewById(R.id.toolbar))

        textViewFileInfo = findViewById(R.id.tv_file_information)
        imageViewPreview = findViewById(R.id.imagePreview)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        findViewById<Button>(R.id.button_pick_file).setOnClickListener(this)
        findViewById<Button>(R.id.button_upload_file).setOnClickListener(this)

        // Initialize Firebase instances.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        // The projectId of this project must be passed as
        // an extra from an Activity calling this activity.
        projectId = intent.extras?.getString("pid")!!
        projectName = intent.extras?.getString("name")!!
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_pick_file -> when (intent.getStringExtra("type")) {
                "file" -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)

                    intent.type = "*/*"
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                        "text/plain",
                        "application/pdf",  // .pdf
                        "image/jpeg",       // .jpg (https://stackoverflow.com/a/37266399)
                        "audio/mpeg"        // .mp3 (https://stackoverflow.com/a/10688641)
                    ))
                    //intent.putExtra("return-data", true)
                    startActivityForResult(intent, FILE_PICK_REQUEST)
                }
                "image" -> {
                    // TODO: implement capturing photo also

                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, IMAGE_PICK_REQUEST)
                    }
                }
                else -> {
                    throw Exception("wrong intent type")
                }
            }
            R.id.button_upload_file -> {
                val pid = intent.getStringExtra("pid")
                if (pid != null)
                {
                    val storage_ref = storage.reference
                        .child("project_files/$pid/${selectedFile.name}")

                    Log.d(TAG, "storage ref: ${selectedFile.name}")
                    Log.d(TAG, "storage ref: $storage_ref")

                    val uploadSection = findViewById<LinearLayout>(R.id.upload_progress)
                    val progressStatus = findViewById<TextView>(R.id.tv_upload_progress_status)
                    val progress = findViewById<ProgressBar>(R.id.upload_progress_bar)

                    uploadSection.visibility = View.VISIBLE // mark whole section as visible
                    progress.setProgress(0, false)

                    // Add the project ID to the metadata to limit access to project members.
                    val metadata = StorageMetadata.Builder()
                        .setCustomMetadata("pid", projectId)
                        .build()

                    // TODO: Handle activity lifecycle changes!
                    storage_ref.putFile(selectedFile.toUri(), metadata)
                        .addOnSuccessListener { taskSnapshot ->
                            // Uri: taskSnapshot.downloadUrl
                            // Name: taskSnapshot.metadata!!.name
                            // Path: taskSnapshot.metadata!!.path
                            // Size: taskSnapshot.metadata!!.sizeBytes
                            progress.setProgress(100, true)
                            progressStatus.text = "Upload successful!\n" +
                                    "Path: ${taskSnapshot.metadata!!.path}\n" +
                                    "Uploaded size: ${taskSnapshot.metadata!!.sizeBytes / 1000} KB"
                            successRedirect()
                        }
                        .addOnFailureListener { exception ->
                            progressStatus.text = "Upload failed: $exception"
                        }
                        .addOnProgressListener { taskSnapshot ->
                            val percentage =
                                taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                            progress.setProgress(percentage.toInt(), true)
                        }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
        {
            Log.d(TAG, "requestCode $requestCode resultCode $resultCode data $data")
            // NB: Data should have a URI independent of the activity.
            if (data != null) {
                try
                {
                    Log.d(TAG, "data.data ${data.data}")
                    Log.d(TAG, "Uri file data.data ${Uri.fromFile(File(data.data.toString()))}")
                    var selectedUri: String? = null
                    val cursor = applicationContext.contentResolver.query(
                        data.data as Uri,
                        null,
                        null,
                        null,
                        null)
                    Log.d(TAG, "cursor $cursor")
                    if (cursor!!.moveToFirst())
                    {
                        selectedUri = Uri.parse(cursor.getString(0)).path
                    }
                    cursor.close()

                    if (selectedUri != null) {
                        selectedFile = File(selectedUri)
                    } else {
                        Log.d(TAG, "--------------------------- selectedFile not set!")
                    }

                    if (!selectedFile.exists()) {
                        Log.w(TAG, "file doesn't exist? $selectedFile")
                    }

                    textViewFileInfo.visibility = View.VISIBLE
                    textViewFileInfo.text =
                        "Selected file: ${selectedFile.name}\n" +
                        "File size: ${selectedFile.length() / 1000} KB\n" +
                        "Last modified: ${Date(selectedFile.lastModified())}"

                    if (requestCode == IMAGE_PICK_REQUEST)
                    {
                        /*
                        // Load thumbnail of a specific media item.
                        val thumbnail: Bitmap =
                        applicationContext.contentResolver.loadThumbnail(
                        content-uri, Size(640, 480), null)
                         */
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver, selectedFile.toUri())
                        imageViewPreview.setImageBitmap(bitmap)
                        imageViewPreview.setBackgroundResource(android.R.color.transparent)
                        imageViewPreview.visibility = View.VISIBLE
                    }
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun successRedirect() {
        // Redirect to ProjectActivity upon success.
        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("pid", projectId)
        intent.putExtra("name", projectName)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "UploadFileActivity"

        const val IMAGE_PICK_REQUEST = 1000
        const val FILE_PICK_REQUEST = 1001
    }
}