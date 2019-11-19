package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_projects.*

class ProjectsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        setSupportActionBar(findViewById(R.id.toolbar))

        // create a new project
        fab.setOnClickListener { view ->
            val intent = Intent(this,CreateProjectActivity::class.java)
            startActivity(intent)
        }
    }

}
