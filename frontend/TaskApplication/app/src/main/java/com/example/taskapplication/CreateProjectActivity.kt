package com.example.taskapplication


import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_create_project.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateProjectActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var cal: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)
        setSupportActionBar(findViewById(R.id.toolbar))
        buttonAddIcon.setOnClickListener(this)
        setDeadlineButton.setOnClickListener(this)
        val textViewDeadline: TextView = findViewById(R.id.tv_project_deadline_time)
        //textView.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        cal = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            textViewDeadline.text = sdf.format(cal.time)

        }
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
        DatePickerDialog(this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
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
        // image pick code
        const val IMAGE_PICK_REQUEST = 1000
    }
}
