package com.linkpoint.ui.settings.config

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.linkpoint.service.BackgroundResumeScheduler
import com.linkpoint.ui.settings.SettingsKeys

class BackgroundSettingsConfigurator(
    private val fragment: PreferenceFragmentCompat,
    private val context: Context,
) {
    fun configure() {
        fragment.findPreference<SwitchPreferenceCompat>(SettingsKeys.BACKGROUND_RESUME_ENABLED)?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) BackgroundResumeScheduler.schedule(context, immediate = true)
            else BackgroundResumeScheduler.cancel(context)
            true
        }
        fragment.findPreference<SwitchPreferenceCompat>(SettingsKeys.PUSH_WAKEUPS_ENABLED)?.setOnPreferenceChangeListener { _, _ -> true }
        fragment.findPreference<ListPreference>(SettingsKeys.BACKGROUND_INTENSITY_PROFILE)?.setOnPreferenceChangeListener { _, _ ->
            BackgroundResumeScheduler.schedule(context, immediate = false)
            true
        }
    }
}
