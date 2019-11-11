package com.lipata.forkauthority.data.user

import android.content.SharedPreferences
import com.lipata.forkauthority.data.SharedPreferencesKeys.USER_EMAIL_KEY
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test

class UserIdentityManagerTest {
    @Test
    fun hasIdentity_whenUserHasEmail_returnTrue() {
        // Given
        val sharedPrefs: SharedPreferences = mock()
        whenever(sharedPrefs.getString(USER_EMAIL_KEY, null)).thenReturn("asdf@email.com")

        val sut = UserIdentityManager(sharedPrefs)

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

        val sut = UserIdentityManager(sharedPrefs)

        // When
        val result = sut.hasIdentity()

        // Then
        assertThat(result, equalTo(false))
    }
}