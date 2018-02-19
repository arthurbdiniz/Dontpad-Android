package com.dontpad.DAO

import android.os.AsyncTask
import android.util.Log
import okhttp3.*
import java.io.IOException

class GetDao(private val url: String, private val mediaType: MediaType?) : AsyncTask<String, String, String>() {

    public override fun doInBackground(vararg params: String?): String? {
        val client = OkHttpClient()
//        val body = RequestBody.create(mediaType, stringBody)

        Log.d("SERVER_DAO", url)

        val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = client.newCall(request).execute()
//            Log.d("SERVER_DAO", response.body()!!.toString())
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