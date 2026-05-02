package com.linkpoint.ui.settings.config

import android.content.Context
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.linkpoint.R
import com.linkpoint.ui.settings.SettingsKeys

class DebugSettingsConfigurator(
    private val fragment: PreferenceFragmentCompat,
    private val context: Context,
    private val onCopyLog: () -> Unit,
    private val onExportLog: () -> Unit,
    private val onShareLog: () -> Unit,
) {
    fun configure() {
        fragment.findPreference<SwitchPreferenceCompat>(SettingsKeys.ENABLE_DEBUG_FLOATER)?.setOnPreferenceChangeListener { _, newValue ->
            val msg = if (newValue as Boolean) R.string.settings_debug_floater_enabled else R.string.settings_debug_floater_disabled
            Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
            true
        }
        fragment.findPreference<SwitchPreferenceCompat>(SettingsKeys.CODEX_AUTO_UPLOAD)?.setOnPreferenceChangeListener { _, newValue ->
            val msg = if (newValue as Boolean) R.string.settings_codex_auto_upload_enabled else R.string.settings_codex_auto_upload_disabled
            Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
            true
        }
        fragment.findPreference<Preference>(SettingsKeys.COPY_LOG)?.setOnPreferenceClickListener { onCopyLog(); true }
        fragment.findPreference<Preference>(SettingsKeys.EXPORT_LOG)?.setOnPreferenceClickListener { onExportLog(); true }
        fragment.findPreference<Preference>(SettingsKeys.SHARE_LOG)?.setOnPreferenceClickListener { onShareLog(); true }
    }
}
