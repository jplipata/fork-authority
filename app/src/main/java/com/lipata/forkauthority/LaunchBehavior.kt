package com.lipata.forkauthority

import android.app.Activity

interface LaunchBehavior {
    fun invoke(activity: Activity)
}