package com.lipata.forkauthority.poll

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.R
import com.lipata.forkauthority.di.*
import com.lipata.forkauthority.testutils.setUpAppWithTestDependencies
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//class PollHomeFragmentTest {

    // This doesn't work because of this:
    // https://issuetracker.google.com/issues/144629519
    // https://stackoverflow.com/questions/58903155/fragment-testing-error-android-view-inflateexception-binary-xml-file-line-16/58903156#58903156
//    @Test
//    fun whenActivitystarts_shouldCheckUserIdentity() {
//        // Given
//        val appComponent = setUpAppWithTestDependencies()
//
//        val userIdentityManager = appComponent.userIdentityManager()
//
//        // When
//        launchFragmentInContainer<PollHomeFragment>()
//
//        //scenario.moveToState(Lifecycle.State.STARTED)
//
//        // Then
//        verify(userIdentityManager, times(1)).checkUserIdentity(any(), any())
//    }


    // This behavior was moved to PollHomeFragment
//    @Test
//    fun whenUserHasEmail_shouldShowIt() {
//        val component = setUpAppWithTestDependencies()
//
//        // Given
//        val TEST_EMAIL = "asdf@asdf.com"
//
//        component.userIdentityManager().run {
//            whenever(this.email).thenReturn(TEST_EMAIL)
//        }
//
//        // When
//        val activityScenario: ActivityScenario<PollActivity> = ActivityScenario.launch(
//            PollActivity::class.java)
//        activityScenario.moveToState(Lifecycle.State.STARTED)
//
//        // Then
//        var textviewEmail: TextView? = null
//
//        activityScenario.onActivity {
//            textviewEmail = it.findViewById(R.id.tvEmail)
//        }
//
//        assertThat(textviewEmail!!.text.toString(), equalTo(TEST_EMAIL))
//    }

//}