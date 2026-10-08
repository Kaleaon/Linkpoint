package com.linkpoint.ui.settings.config

import android.content.Context
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.linkpoint.R
import com.linkpoint.ui.settings.SettingsKeys

class DisplaySettingsConfigurator(
    private val fragment: PreferenceFragmentCompat,
    private val context: Context,
) {
    fun configure() {
        fragment.findPreference<ListPreference>(SettingsKeys.SCREEN_ORIENTATION)?.apply {
            summary = entry ?: entries?.takeIf { it.isNotEmpty() }?.get(0) ?: "Portrait"
            setOnPreferenceChangeListener { preference, newValue ->
                val listPref = preference as ListPreference
                val index = listPref.findIndexOfValue(newValue as String)
                if (index >= 0) listPref.summary = listPref.entries[index]
                Toast.makeText(context, context.getString(R.string.settings_orientation_apply_hint), Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}
