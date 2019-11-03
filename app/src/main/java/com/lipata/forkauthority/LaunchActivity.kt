package com.lipata.forkauthority

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lipata.forkauthority.ui.RestaurantListActivity
import kotlinx.android.synthetic.main.activity_debug.*

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG) {
            launchRestaurantListFeature()
        } else {
            showDebugActivity()
        }
    }

    private fun showDebugActivity() {
        setContentView(R.layout.activity_debug)
        textViewRestaurantList.setOnClickListener {
            launchRestaurantListFeature()
        }
        textViewPoll.setOnClickListener {

        }

    }

    private fun launchRestaurantListFeature() {
        startActivity(Intent(this, RestaurantListActivity::class.java))
    }
}