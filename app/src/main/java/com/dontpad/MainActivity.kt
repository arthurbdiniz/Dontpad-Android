package com.dontpad

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import com.dontpad.DAO.PostDao
import okhttp3.HttpUrl
import android.text.Editable
import android.text.TextWatcher
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"
    var lastUpdate: String? = "0"
    open var textArea: EditText? = null
    val url = "http://www.dontpad.com/asdasds"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textArea = findViewById(R.id.edit_text_area)
        update()
        
    }

    fun update() {
        val handler = Handler()
        val timer = Timer()
        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post({
                    val urlWithParameters = getTextUrl(url, textArea!!.text.toString())
                    lastUpdate = PostDao(urlWithParameters, null, "").execute("").get()
                    Log.d(TAG, lastUpdate)
                })
            }
        }
        timer.schedule(doAsynchronousTask, 0, 5000) //execute in every 50000 ms
    }

    // Method that creates a url with parameters and sends it to api, it returns a response if it worked or not
    // Creates json
    private fun getTextUrl(url: String, text: String): String {
        val builder = HttpUrl.parse(url)!!.newBuilder()

        builder.addQueryParameter("text", text)
        return builder.build().toString()
    }
}
