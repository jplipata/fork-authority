package com.lipata.forkauthority.businesslist

import android.content.Context
import androidx.preference.PreferenceManager
import com.lipata.forkauthority.R
import timber.log.Timber

class JustAteHereExpirationProviderImpl(val context: Context) : ExpirationProvider {
    override fun get(): Int {
        val default = context.getString(R.string.preference_default_value_just_ate_here_expiration)

        val expirationPrefString = PreferenceManager.getDefaultSharedPreferences(context).getString(
            context.getString(R.string.preference_key_just_ate_here_expiration),
            default) ?: default

        return try {
            expirationPrefString.toInt()
        } catch (e: Exception) {
            Timber.e(e)
            default.toInt()
        }
    }
}