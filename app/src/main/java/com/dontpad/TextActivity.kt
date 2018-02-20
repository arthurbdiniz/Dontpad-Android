package com.dontpad

import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent
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

import android.graphics.Color
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.MenuItem

import android.widget.TextView
import android.widget.Toast





class TextActivity : AppCompatActivity() {

    private val TAG: String = "TextActivity"

    private var sbIsShown: Boolean = false
    private var textChanged: Boolean = false
    private var changed: Boolean = false

    private var toolbar: Toolbar? = null
    private var snackbar: Snackbar?= null
    private var textArea: EditText? = null

    private var lastUpdate: String? = "0"
    private var body: String? = ""

    private var serverResponse: String? = ""
    private var url = "http://www.dontpad.com/"
    private var urlGet = ".body.json"
    private val INTENT_PATH= "path"

    private var timer: Timer? = null
    private var doAsynchronousTask: TimerTask? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        textArea = findViewById(R.id.edit_text_area)
        initToolbar()

        textArea!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                textChanged = true
                Log.d(TAG, "textChanged")
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

    override fun onPause() {
        Log.d(TAG, "OnPause")
        if(getUpdateType()){ //Automatic Update
            Log.d(TAG, "isAutomaticUpdate")
            timer!!.cancel()
        }
        super.onPause()
    }

    override fun onResume() {
        Log.d(TAG, "OnResume")
        if(getUpdateType()) { //Automatic Update
            Log.d(TAG, "isAutomaticUpdate")
            createAsynchronousTask()
            timer = Timer()
            timer!!.schedule(doAsynchronousTask, 0, 5000)
        }else{ //Manual Update
            getData()
        }
        super.onResume()
    }



    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //menu!!.clear()

        val refresh = menu!!.findItem(R.id.refresh)
        refresh.isVisible = !getUpdateType()

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item!!.itemId) {
            R.id.share_path -> {
                sharePathIntent()
                true
            }

            R.id.download_page ->{
                val msg = Toast.makeText(this, "//TODO", Toast.LENGTH_LONG)
                msg.show()
                true
            }

            R.id.options -> {
                val intent = Intent(applicationContext, OptionsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.refresh -> {
                postData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun update() {
        //createAsynchronousTask()
//        timer = Timer()
//        timer!!.schedule(doAsynchronousTask, 0, 5000) //execute in every 50000 ms
    }

    private fun createAsynchronousTask(){
        val handler = Handler()
        doAsynchronousTask = object : TimerTask() {
            @SuppressLint("LongLogTag")
            override fun run() {
                handler.post({
                    Log.d(TAG, "Update")
                    getData()
                    postData()
                })
            }
        }
    }

    private fun getTextParams(): HashMap<String, String> {
        val params = HashMap<String, String>()
        params["text"] = textArea!!.text.toString()

        return params

    }

    private fun getLastUpdateUrl(url: String, lastUpdate: String?): String {
        val builder = HttpUrl.parse(url)!!.newBuilder()

        builder.addQueryParameter("lastUpdate", lastUpdate)
        return builder.build().toString()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    fun isOnline(): Boolean {
        val conMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo

        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            if(!sbIsShown){
                snackbarDisplay()
                sbIsShown = true
            }
            return false
        }
        return true
    }

    private fun snackbarDisplay(){
        snackbar = Snackbar
                .make(findViewById(R.id.linearLayout), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)

        val sbView = snackbar!!.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))

        val textView = sbView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar!!.show()
    }

    private fun getData(){
        if(isOnline()){
            Log.d(TAG, "Get Data")
            val urlGetWithParameters = getLastUpdateUrl(url + urlGet, lastUpdate)
            serverResponse = GetDao(urlGetWithParameters, null).execute("").get()

            if(serverResponse != null){
                val jsonObject = JSONObject(serverResponse)
                changed = jsonObject.getBoolean("changed")

                if(changed){
                    lastUpdate = jsonObject.getString("lastUpdate")
                    body = jsonObject.getString("body")
                    textArea!!.setText(body)
                    Log.d(TAG, serverResponse)
                }

//                Log.d(TAG + "lastUpdate", lastUpdate)

                if(sbIsShown){
                    snackbar!!.dismiss()
                    sbIsShown = false
                }
            }
        }
    }

    private fun postData()   {
        if(isOnline()) {

            if (textChanged) {
                textChanged = false
                Log.d(TAG, "Post Data")

                lastUpdate = RequestHandler(url, getTextParams()).execute().get()
                Log.d(TAG, lastUpdate)
            }

            if(sbIsShown){
                snackbar!!.dismiss()
                sbIsShown = false
            }
        }
    }

    private fun sharePathIntent(){
        var shareBody = "This is my path in Dontpad! www.dontpad.com" + title
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_using)))
    }

    private fun getUpdateType(): Boolean {
        val session = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return session.getBoolean("updateAutomatic", true)

    }

}
