package com.dontpad

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.Toolbar
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.dontpad.DAO.GetDao
import okhttp3.HttpUrl
import org.json.JSONObject
import java.util.*
import android.text.Editable
import android.view.Menu
import com.dontpad.DAO.RequestHandler


class TextActivity : AppCompatActivity() {

    val TAG: String = "TextActivity"

    var lastUpdate: String? = "0"
    var body: String? = ""
    var changed: Boolean = false

    var serverResponse: String? = ""

    var textChanged: Boolean = false
    private var toolbar: Toolbar? = null


    var textArea: EditText? = null
    var url = "http://www.dontpad.com/"
    var urlGet = ".body.json"
    val INTENT_PATH= "path"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        textArea = findViewById(R.id.edit_text_area)
        initToolbar()



        textArea!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (before > 0){
                    textChanged = true
                }
                Log.d(TAG, "Text Changed... " + start + " " + before + " " + count)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val path = intent.getStringExtra(INTENT_PATH)
                ?: throw IllegalStateException("field $INTENT_PATH missing in Intent")

        title = "/" + path

        url += path
        Log.d(TAG, url)

        update()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun update() {
        val handler = Handler()
        val timer = Timer()
        val doAsynchronousTask = object : TimerTask() {
            @SuppressLint("LongLogTag")
            override fun run() {
                handler.post({


                    if(textChanged){
                        textChanged = false

                        lastUpdate = RequestHandler(url, getTextParams()).execute().get()
                        Log.d(TAG, lastUpdate)
                    }

                    Log.d(TAG + "lastUpdatemidle", lastUpdate)

                    val urlGetWithParameters = getLastUpdateUrl(url + urlGet, lastUpdate)
                    serverResponse = GetDao(urlGetWithParameters, null).execute("").get()

                    Log.d(TAG, serverResponse)

                    val jsonObject = JSONObject(serverResponse)
                    changed = jsonObject.getBoolean("changed")

                    if(changed){
                        lastUpdate = jsonObject.getString("lastUpdate")
                        body = jsonObject.getString("body")


                        textArea!!.setText(body)
                    }

                    Log.d(TAG + "lastUpdate", lastUpdate)

                })
            }
        }
        timer.schedule(doAsynchronousTask, 0, 10000) //execute in every 50000 ms
    }

    private fun getTextParams(): HashMap<String, String> {

        val params = HashMap<String, String>()
        params["text"] = textArea!!.text.toString()

        return params

    }

    // Method that creates a url with parameters and sends it to api, it returns a response if it worked or not
    // Creates json
    private fun getLastUpdateUrl(url: String, lastUpdate: String?): String {
        val builder = HttpUrl.parse(url)!!.newBuilder()

        builder.addQueryParameter("lastUpdate", lastUpdate)
        return builder.build().toString()
    }

    fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

}
