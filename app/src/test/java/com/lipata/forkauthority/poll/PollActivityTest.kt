package com.lipata.forkauthority.poll

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.R
import com.lipata.forkauthority.di.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.android.synthetic.main.activity_poll.view.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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
        verify(userIdentityManager, times(1)).checkUserIdentity(any())
    }

    @Test
    fun whenUserHasEmail_shouldShowIt() {
        val component = setUpAppWithTestDependencies()

        // Given
        val TEST_EMAIL = "asdf@asdf.com"

        component.userIdentityManager().run {
            whenever(this.email).thenReturn(TEST_EMAIL)
        }

        // When
        val activityScenario: ActivityScenario<PollActivity> = ActivityScenario.launch(PollActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.STARTED)

        // Then
        var textviewEmail: TextView? = null

        activityScenario.onActivity {
            textviewEmail = it.findViewById(R.id.tvEmail)
        }

        assertThat(textviewEmail!!.text.toString(), equalTo(TEST_EMAIL))
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