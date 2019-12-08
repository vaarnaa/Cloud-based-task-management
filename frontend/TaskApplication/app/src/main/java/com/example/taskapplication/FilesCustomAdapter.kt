package com.example.taskapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_files_list_view.view.*

class FilesCustomAdapter(applicationContext: Context,
                         files: ArrayList<Map<String, String>>) : BaseAdapter() {
    private val inflater = LayoutInflater.from(applicationContext)
    private val ts = files

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
            returnedView = inflater.inflate(R.layout.activity_files_list_view, viewGroup, false)
        }
        val fileView = returnedView!!.projectFileListView

        val file = getItem(position)
        if (file != null)
        {
            fileView.text = file.getValue("name")
        }

        return returnedView
    }

    private val TAG = "FilesCustomAdapter"
}