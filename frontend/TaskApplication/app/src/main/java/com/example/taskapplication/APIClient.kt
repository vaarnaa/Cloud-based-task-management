package com.example.taskapplication

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

/* Used to make HTTPS requests to the Cloud Endpoints API. */
object APIClient {
    private const val BASE_URL = "https://mcc-fall-2019-g09.appspot.com/"
    private val client = AsyncHttpClient()

    fun get(url: String, params: RequestParams? = null, rh: AsyncHttpResponseHandler? = null) {
        client.get(getAbsoluteUrl(url), params, rh)
    }

    fun put(url: String, params: RequestParams? = null, rh: AsyncHttpResponseHandler? = null) {
        client.put(getAbsoluteUrl(url), params, rh)
    }

    fun post(url: String, params: RequestParams? = null, rh: AsyncHttpResponseHandler? = null) {
        Log.d(TAG, "POST url ${getAbsoluteUrl(url)}")
        Log.d(TAG, "POST params $params")
        client.post(getAbsoluteUrl(url), params, rh)
    }

    fun delete(url: String, params: RequestParams? = null, rh: AsyncHttpResponseHandler? = null) {
        client.delete(getAbsoluteUrl(url), params, rh)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        return BASE_URL + relativeUrl
    }

    private const val TAG = "APIClient"
}