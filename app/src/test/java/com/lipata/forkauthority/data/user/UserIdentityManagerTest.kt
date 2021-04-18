package com.lipata.forkauthority.data.user

import android.content.SharedPreferences
import com.lipata.forkauthority.data.SharedPreferencesKeys.USER_EMAIL_KEY
import com.lipata.forkauthority.poll.UserIdentityManager
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test

class UserIdentityManagerTest {
    @Test
    fun hasIdentity_whenUserHasEmail_returnTrue() {
        // Given
        val sharedPrefs: SharedPreferences = mock()
        whenever(sharedPrefs.getString(USER_EMAIL_KEY, null)).thenReturn("asdf@email.com")

        val sut = UserIdentityManager(sharedPrefs, mock())

        // When
        val result = sut.hasIdentity()

        // Then
        assertThat(result, equalTo(true))
    }

    @Test
    fun hasIdentity_whenUserDoesntHaveEmail_returnFalse() {
        // Given
        val sharedPrefs: SharedPreferences = mock()
        whenever(sharedPrefs.getString(USER_EMAIL_KEY, null)).thenReturn(null)

        val sut = UserIdentityManager(sharedPrefs, mock())

        // When
        val result = sut.hasIdentity()

        // Then
        assertThat(result, equalTo(false))
    }

    @Test
    fun checkUserIdentity() {
        // Given
        val sut = spy(UserIdentityManager(mock(), mock())) // user email not initialized
        val listener: () -> Unit = mock()

        // When
        sut.checkUserIdentity(mock(), listener)

        // Then
        verify(sut, times(1)).promptUserForEmail(any(), eq(listener))
    }
}