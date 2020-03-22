package com.lipata.forkauthority.testutils

import androidx.test.core.app.ApplicationProvider
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.di.*

fun setUpAppWithTestDependencies(): TestAppComponent {
    val app = ApplicationProvider.getApplicationContext<ForkAuthorityApp>().apply {
        appComponent = DaggerTestAppComponent.builder()
            .testAppModule(TestAppModule())
            .testFirebaseModule(TestFirebaseModule())
            .yelpModule(YelpModule())
            .build()
    }
    return app.appComponent as TestAppComponent
}