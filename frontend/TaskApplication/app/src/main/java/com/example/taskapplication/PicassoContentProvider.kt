package com.example.taskapplication

// from https://github.com/square/picasso/blob/174ffe22e2d2d034afe03f398eb0ab3e3e1f4916/picasso-sample/src/main/java/com/example/picasso/provider/PicassoContentProvider.kt

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri

class PicassoContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        autoContext = context
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ) = null

    override fun getType(uri: Uri) = null

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ) = null

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var autoContext: Context? = null
    }
}