package com.example.taskapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_projects_list_view.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

class ProjectsCustomAdapter(applicationContext: Context,
                            projects: ArrayList<Map<String, String>>) : BaseAdapter() {
    private val context = applicationContext
    private val inflater = LayoutInflater.from(applicationContext)
    private val ps = projects

    override fun getCount(): Int {
        return ps.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var returnedView = view
        if (returnedView == null) {
            returnedView = inflater
                .inflate(R.layout.activity_projects_list_view, viewGroup, false)
        }
        val nameView = returnedView!!.projectsListName
        val modifiedView = returnedView.projectsListModified
        val project = ps[position]
        nameView.text = project.getValue("name")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val modified = project.getValue("modified")
        val y = modified.slice(0 until 4).toInt()
        val mo = modified.slice(5 until 7).toInt()
        val d = modified.slice(8 until 10).toInt()
        val h = modified.slice(11 until 13).toInt()
        val m = modified.slice(14 until 16).toInt()
        val s = modified.slice(17 until 19).toInt()
        val date = LocalDateTime.of(y, mo, d, h, m, s).format(formatter)
        Log.d(TAG, "modified: $modified date: $date")
        modifiedView.text = context.getString(R.string.projects_list_modified_text, date)
        return returnedView
    }

    private val TAG = "ProjectsCustomAdapter"
}