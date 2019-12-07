package com.example.taskapplication

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_project_list_view.view.*

class TasksCustomAdapter(applicationContext: Context,
                         tasks: ArrayList<Map<String, String>>) : BaseAdapter() {
    private val inflater = LayoutInflater.from(applicationContext)
    private val ts = tasks

    override fun getCount(): Int {
        return ts.size
    }

    override fun getItem(i: Int): Map<String, String>? {
        return ts[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var returnedView = view
        if (returnedView == null) {
            returnedView = inflater
                .inflate(R.layout.activity_project_list_view, viewGroup, false)
        }
        val checkBoxView = returnedView!!.projectTaskCheckBox
        val task = ts[position]
        checkBoxView.text = task.getValue("description")
        // Mark the checkbox with a tick mark only if it is marked complete.
        // Otherwise, remove the tick mark. Also make the text strike through.
        if (task.getValue("status") == "completed") {
            checkBoxView.isChecked = true
            checkBoxView.paintFlags = checkBoxView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBoxView.isChecked = false
            checkBoxView.paintFlags = checkBoxView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        return returnedView
    }

    private val TAG = "TasksCustomAdapter"
}