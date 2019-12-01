package com.example.taskapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ProjectActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onClick(v: View) {
        when (v.id) {
        }
    }
}
