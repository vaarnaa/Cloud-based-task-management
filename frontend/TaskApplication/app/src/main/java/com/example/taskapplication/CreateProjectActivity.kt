package com.example.taskapplication


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_create_project.*
import java.io.IOException
import java.text.SimpleDateFormat
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.widget.RadioButton
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class CreateProjectActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var textViewDeadline: TextView
    private lateinit var editTextKeywords: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText

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

    private  fun saveProject() {

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
        var date = currentDate.format(formatter)

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
        val projectType = radioButton.text

        // TODO: Create a project with our API which returns the new project ID.
        // TODO: POST https://mcc-fall-2019-g09.appspot.com/project
        // TODO: Use Android Asynchronous Http Client (https://loopj.com/android-async-http/).
        // TODO: See "Recommended Usage: Make a Static Http Client".

        // Then redirect to UserActivity.
        // val intent = Intent(this, UserActivity::class.java)
        // startActivity(intent)
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
        // image pick code
        const val IMAGE_PICK_REQUEST = 1000
    }
}
