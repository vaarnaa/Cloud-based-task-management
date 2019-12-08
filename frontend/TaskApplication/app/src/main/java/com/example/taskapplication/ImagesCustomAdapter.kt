package com.example.taskapplication

// Based on https://github.com/square/picasso/blob/9328ac88f920f5bd9f4a94e7ca263c33a2146960/picasso-sample/src/main/java/com/example/picasso/SampleGridViewAdapter.java

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_images_list_view.view.*

internal class ImagesCustomAdapter(applicationContext: Context,
                                   images: ArrayList<Map<String, String>>) : BaseAdapter() {
    private val ts = images
    private val context = applicationContext
    private val inflater = LayoutInflater.from(applicationContext)
    private val TAG = "ImagesCustomAdapter"

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var returnedView = view
        if (returnedView == null) {
            returnedView = inflater.inflate(R.layout.activity_images_list_view, viewGroup, false)
        }

        val imagePreview = returnedView!!.imagePreview

        // Get the image URL for the current position.
        val image = getItem(position)
        if (image != null)
        {
            val url = image.getValue("downloadURL")
            // Trigger the download of the URL asynchronously into the image view.
            PicassoProvider.get()
                .load(url)
                //.placeholder(R.drawable.placeholder)
                //.error(R.drawable.error)
                .fit()
                .tag(context)
                .into(imagePreview)
        }

        return returnedView
    }

    override fun getCount(): Int {
        return ts.size
    }

    override fun getItem(i: Int): Map<String, String>? {
        return ts[i]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}