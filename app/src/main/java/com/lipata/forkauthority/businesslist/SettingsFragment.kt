package com.lipata.forkauthority.businesslist

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.lipata.forkauthority.R
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)

        findPreference<EditTextPreference?>(
            getString(R.string.preference_key_just_ate_here_expiration)
        )?.apply {

            initNumericInputOnly()

            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                val value = getPrefValueOrDefault(preference)

                if (value != 1) {
                    "${preference.text} days"
                } else {
                    "1 day"
                }
            }
        }
    }

    private fun getPrefValueOrDefault(preference: EditTextPreference): Any {
        return try {
            preference.text.toInt()
        } catch (e: Exception) {
            Timber.e(e)
            getString(R.string.preference_default_value_just_ate_here_expiration)
        }
    }

    private fun EditTextPreference.initNumericInputOnly() {
        setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}