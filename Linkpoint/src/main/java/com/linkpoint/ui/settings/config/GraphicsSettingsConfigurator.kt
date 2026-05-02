package com.linkpoint.ui.settings.config

import android.content.Context
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.linkpoint.R
import com.linkpoint.ui.settings.SettingsKeys

class GraphicsSettingsConfigurator(
    private val fragment: PreferenceFragmentCompat,
    private val context: Context,
    private val onGraphicsQualityUpdated: (String) -> Unit,
) {
    fun configure() {
        fragment.findPreference<ListPreference>(SettingsKeys.GRAPHICS_QUALITY)?.setOnPreferenceChangeListener { _, newValue ->
            onGraphicsQualityUpdated(newValue as String)
            true
        }
        fragment.findPreference<SwitchPreferenceCompat>(SettingsKeys.ENABLE_SECONDARY_RENDERER)?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            val message = if (enabled) R.string.settings_secondary_renderer_enabled else R.string.settings_secondary_renderer_disabled
            Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show()
            true
        }
    }
}
