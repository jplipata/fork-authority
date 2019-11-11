package com.lipata.forkauthority.data.user

import android.content.SharedPreferences
import com.lipata.forkauthority.data.SharedPreferencesKeys.USER_EMAIL_KEY
import javax.inject.Inject

class UserIdentityManager @Inject constructor(val sharedPreferences: SharedPreferences) {
    fun hasIdentity(): Boolean {
        val userEmail: String? = sharedPreferences.getString(USER_EMAIL_KEY, null)
        return userEmail != null && userEmail.isNotBlank()
    }

    fun checkUserIdentity() {


    }
}