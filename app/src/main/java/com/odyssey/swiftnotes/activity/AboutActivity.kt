package com.odyssey.swiftnotes.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

import com.odyssey.swiftnotes.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = "A propos"

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {
            android.R.id.home -> super.onBackPressed()
        }//            default:

        return super.onOptionsItemSelected(item)
    }
}
