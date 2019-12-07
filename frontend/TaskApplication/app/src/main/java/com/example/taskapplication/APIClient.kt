package com.example.taskapplication

import android.content.Context
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.HttpEntity

/* Used to make HTTPS requests to the Cloud Endpoints API. */
object APIClient {
    private const val BASE_URL = "https://mcc-fall-2019-g09.appspot.com/"
    private val client = AsyncHttpClient()

    fun get(context: Context,
            url: String,
            idToken: String,
            entity: HttpEntity? = null,
            contentType: String? = null,
            responseHandler: AsyncHttpResponseHandler? = null) {
        client.addHeader("Authorization", "Bearer $idToken")
        client.get(context, getAbsoluteUrl(url), entity, contentType, responseHandler)
    }

    fun put(context: Context,
            url: String,
            idToken: String,
            entity: HttpEntity? = null,
            contentType: String? = null,
            responseHandler: AsyncHttpResponseHandler? = null) {
        client.addHeader("Authorization", "Bearer $idToken")
        client.put(context, getAbsoluteUrl(url), entity, contentType, responseHandler)
    }

    fun post(context: Context,
             url: String,
             idToken: String,
             entity: HttpEntity? = null,
             contentType: String? = null,
             responseHandler: AsyncHttpResponseHandler? = null) {
        client.addHeader("Authorization", "Bearer $idToken")
        client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler)
    }

    fun delete(context: Context,
               url: String,
               idToken: String,
               entity: HttpEntity? = null,
               contentType: String? = null,
               responseHandler: AsyncHttpResponseHandler? = null) {
        client.addHeader("Authorization", "Bearer $idToken")
        client.delete(context, getAbsoluteUrl(url), entity, contentType, responseHandler)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        return BASE_URL + relativeUrl
    }

    private const val TAG = "APIClient"
}