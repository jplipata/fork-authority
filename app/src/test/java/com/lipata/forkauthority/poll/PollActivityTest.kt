package com.lipata.forkauthority.poll

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.di.*
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PollActivityTest {
    @Test
    fun whenActivitystarts_shouldCheckUserIdentity() {
        // Given
        val appComponent = setUpAppWithTestDependencies()

        val userIdentityManager = appComponent.userIdentityManager()

        // When
        val activityScenario: ActivityScenario<PollActivity> = ActivityScenario.launch(PollActivity::class.java)

        activityScenario.moveToState(Lifecycle.State.STARTED)

        // Then
        verify(userIdentityManager, times(1)).checkUserIdentity()
    }

    private fun setUpAppWithTestDependencies(): TestAppComponent {
        val foo = ApplicationProvider.getApplicationContext<ForkAuthorityApp>().apply {
            appComponent = DaggerTestAppComponent.builder()
                .testAppModule(TestAppModule())
                .testFirebaseModule(TestFirebaseModule())
                .yelpModule(YelpModule())
                .build()
        }
        return foo.appComponent as TestAppComponent
    }
}