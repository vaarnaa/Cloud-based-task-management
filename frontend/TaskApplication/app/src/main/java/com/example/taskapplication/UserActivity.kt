package com.example.taskapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class UserActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)


        val buttonProjects = findViewById<Button>(R.id.buttonProjects) as Button
        val buttonSettings = findViewById<Button>(R.id.buttonSettings) as Button

        buttonProjects.setOnClickListener {
            val intent = Intent(this, ProjectsActivity::class.java)
            startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            //val intent = Intent(this, SettingsActivity::class.java)
            //startActivity(intent)
        }


    }
}
