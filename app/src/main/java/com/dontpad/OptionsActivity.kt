package com.dontpad

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Switch

class OptionsActivity : AppCompatActivity() {

    private val TAG: String = "OptionActivity"

    private var toolbar: Toolbar? = null
    private var swithUpdate: Switch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        swithUpdate = findViewById(R.id.automatic_update_switch)

        swithUpdate!!.isChecked = getUpdteModeSession()
        swithUpdate!!.setOnCheckedChangeListener({ _, isChecked ->
            editUpdateModeSession(isChecked)
        })

        initToolbar()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun editUpdateModeSession(updateMode: Boolean){
        val session = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        session.edit()
                .putBoolean("updateAutomatic", updateMode)
                .apply()

        Log.d(TAG, "automaticUpdate : " + updateMode)
    }

    private fun getUpdteModeSession(): Boolean {
        val session = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return session.getBoolean("updateAutomatic", true)
//        Log.d(TAG, "automaticUpdate : " + updateMode)
    }
}
