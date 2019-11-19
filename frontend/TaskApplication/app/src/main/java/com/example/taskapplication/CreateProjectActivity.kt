package com.example.taskapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle



class CreateProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}
