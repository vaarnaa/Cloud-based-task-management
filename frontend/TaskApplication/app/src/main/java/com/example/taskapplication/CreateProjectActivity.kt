package com.example.taskapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_create_project.*
import java.io.IOException


class CreateProjectActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)
        setSupportActionBar(findViewById(R.id.toolbar))
        buttonAddIcon.setOnClickListener(this)
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
        }
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
