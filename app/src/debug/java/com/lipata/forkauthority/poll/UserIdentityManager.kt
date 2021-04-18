package com.lipata.forkauthority.poll

import android.content.Context
import android.content.SharedPreferences
import com.lipata.forkauthority.data.SharedPreferencesKeys.USER_EMAIL_KEY
import javax.inject.Inject


class UserIdentityManager @Inject constructor(val sharedPreferences: SharedPreferences,
                                              val emailDialog: EmailDialog) {

    val email: String?
        get() = sharedPreferences.getString(USER_EMAIL_KEY, null)


    fun hasIdentity(): Boolean {
        val userEmail = email
        return userEmail != null && userEmail.isNotBlank()
    }

    fun checkUserIdentity(context: Context, emailUpdatedListener: () -> Unit) {
        if (!hasIdentity()) {
            promptUserForEmail(context, emailUpdatedListener)
        }
    }

    fun promptUserForEmail(context: Context, emailUpdatedListener: () -> Unit) {
        emailDialog.show(context) {
            sharedPreferences.edit().putString(USER_EMAIL_KEY, it).apply()
            emailUpdatedListener()
        }
    }
}



