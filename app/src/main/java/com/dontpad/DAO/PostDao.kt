package com.dontpad.DAO

import android.os.AsyncTask
import android.util.Log

import java.io.IOException

import okhttp3.*

class PostDao(private val url: String, private val mediaType: MediaType?, private val stringBody: String ) : AsyncTask<String, String, String>() {

    public override fun doInBackground(vararg params: String?): String? {
        val client = OkHttpClient()
        val body = RequestBody.create(mediaType, stringBody)

        Log.d("SERVERRESPONSEDAO", url)
        Log.d("SERVERRESPONSEDAO", stringBody)

        val request = okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build()
        try {
            val response = client.newCall(request).execute()
            Log.d("SERVERRESPONSEDAO", response.body()!!.toString())
            return response.body()!!.string()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("LOG", "IOException in doInBackground method")
        }

        return null
    }

    public override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }

}
