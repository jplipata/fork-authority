package com.lipata.forkauthority

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.lipata.forkauthority.di.YelpModule
import com.lipata.forkauthority.di.AppComponent
import com.lipata.forkauthority.di.AppModule
import com.lipata.forkauthority.di.DaggerAppComponent
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class ForkAuthorityApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initFabric()
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics())
        val fabric = Fabric.Builder(this)
            .kits(Answers())
            .debuggable(true)
            .build()
        Fabric.with(fabric)
    }

    fun initDagger(application: ForkAuthorityApp): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .yelpModule(YelpModule())
            .build()
    }
}
