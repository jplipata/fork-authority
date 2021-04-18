package com.lipata.forkauthority

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {

    private val launchBehavior: LaunchBehavior = LaunchBehaviorImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launchBehavior.invoke(this)
    }

}