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
import android.provider.OpenableColumns
import java.io.File


class UploadFileActivity : BaseActivity(), View.OnClickListener{

    private lateinit var textViewFileInfo: TextView
    private lateinit var imageViewPreview: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
                    intent.putExtra("return-data", true)
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
            R.id.button_upload_file -> true
        }
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
        {
            Log.d(TAG, "Activity Result: $requestCode, $data")

            // NB: Data should have an URI independent of the activity
            if (data != null) {
                try
                {
                    val selectedUri = data.data

                    textViewFileInfo.text = "Selected file: ${getFileName(selectedUri as Uri)}"
                    textViewFileInfo.visibility = View.VISIBLE

                    if (requestCode == IMAGE_PICK_REQUEST)
                    {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedUri)
                        imageViewPreview.setImageBitmap(bitmap)
                        imageViewPreview.setBackgroundResource(android.R.color.transparent)
                        imageViewPreview.visibility = View.VISIBLE

                        textViewFileInfo.text = textViewFileInfo.text.toString() + "\nSize: ${bitmap.byteCount / 1000} KB"
                    }

                    // TODO: upload to firebase cloud and link to project
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // From https://stackoverflow.com/a/5569478
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    companion object {
        private const val TAG = "UploadFileActivity"

        const val IMAGE_PICK_REQUEST = 1000
        const val FILE_PICK_REQUEST = 1001
    }
}