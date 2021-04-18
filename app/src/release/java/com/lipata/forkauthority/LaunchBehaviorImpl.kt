package com.lipata.forkauthority

import android.app.Activity
import android.content.Intent
import com.lipata.forkauthority.businesslist.BusinessListActivity

class LaunchBehaviorImpl : LaunchBehavior {
    override fun invoke(activity: Activity) {
        with(activity) {
            startActivity(Intent(this@with, BusinessListActivity::class.java))
            finishAffinity()
        }
    }
}