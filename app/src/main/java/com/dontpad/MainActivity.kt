package com.dontpad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"

    private lateinit var pathEditText: EditText
    private var goButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pathEditText = findViewById(R.id.path)
        goButton = findViewById(R.id.go_button)

        goButton!!.setOnClickListener({

            val intent = newIntent(applicationContext , pathEditText.text.toString())
            startActivity(intent)

        })
    }

    private fun newIntent(context: Context, path: String): Intent {
        val INTENT_PATH= "path"

        val intent = Intent(context, TextActivity::class.java)
        intent.putExtra(INTENT_PATH, path)
        return intent
    }

}
