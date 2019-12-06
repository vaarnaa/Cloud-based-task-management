package com.example.taskapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_projects_list_view.view.*

class ProjectsCustomAdapter(applicationContext: Context,
                            countryList: ArrayList<String>) : BaseAdapter() {
    private val inflater = LayoutInflater.from(applicationContext)
    private val countries = countryList

    override fun getCount(): Int {
        return countries.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var retView = view
        if (retView == null) {
            retView = inflater
                .inflate(R.layout.activity_projects_list_view, viewGroup, false)
        }
        val country = retView!!.projectsListTextView
        country.text = countries[position]
        return retView
    }
}