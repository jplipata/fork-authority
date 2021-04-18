package com.lipata.forkauthority.poll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.R

class PollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        (application as ForkAuthorityApp).appComponent.inject(this)

    }

}