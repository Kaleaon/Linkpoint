package com.linkpoint.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.ListPreference
import androidx.preference.SeekBarPreference
import com.linkpoint.BuildConfig
import com.linkpoint.R
import com.linkpoint.ui.tos.TosActivity

/**
 * Settings Activity
 * Based on Lumiya's SettingsActivity
 * 
 * Includes required disclosures per Third-Party Viewer Policy Section 1.g:
 * - Viewer name and version displayed in About section
 * - Links to Terms of Service and Privacy Policy
 */
class SettingsActivity : AppCompatActivity() {
    
    companion object {
        // GitHub URLs for compliance documentation
        private const val GITHUB_BASE_URL = "https://github.com/Kaleaon/Linkpoint"
        private const val PRIVACY_POLICY_URL = "$GITHUB_BASE_URL/blob/main/PRIVACY_POLICY.md"
        private const val TPV_COMPLIANCE_URL = "$GITHUB_BASE_URL/blob/main/THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Settings"
        }
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsContainer, SettingsFragment())
                .commit()
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            // Graphics settings
            findPreference<ListPreference>("graphics_quality")?.setOnPreferenceChangeListener { _, newValue ->
                updateGraphicsQuality(newValue as String)
                true
            }
            
            // XR settings
            findPreference<SwitchPreferenceCompat>("enable_xr")?.apply {
                isEnabled = com.linkpoint.LinkpointApp.getInstance().isXRAvailable()
            }
            
            // Voice settings
            findPreference<SwitchPreferenceCompat>("enable_voice")?.setOnPreferenceChangeListener { _, newValue ->
                updateVoice(newValue as Boolean)
                true
            }
            
            // About section - Required by TPV Policy Section 1.g
            setupAboutSection()
            
            // ToS viewing
            findPreference<Preference>("view_tos")?.setOnPreferenceClickListener {
                startActivity(TosActivity.createIntent(requireContext(), requireAcceptance = false))
                true
            }
        }
        
        /**
         * Setup About section with required disclosures per Third-Party Viewer Policy
         */
        private fun setupAboutSection() {
            findPreference<Preference>("about")?.apply {
                // Display version name and number as required by TPV Policy Section 1.g
                val versionName = BuildConfig.VERSION_NAME
                val versionCode = BuildConfig.VERSION_CODE
                summary = buildString {
                    appendLine("Linkpoint v$versionName (Build $versionCode)")
                    appendLine()
                    appendLine("This software is not provided or supported by Linden Lab, the makers of Second Life.")
                    appendLine()
                    appendLine("Customer Support: Community support via GitHub Issues")
                    appendLine()
                    appendLine("Tap for more information")
                }
                
                // Make it clickable to show full about dialog
                isSelectable = true
                setOnPreferenceClickListener {
                    showAboutDialog()
                    true
                }
            }
            
            // Add Privacy Policy preference
            findPreference<Preference>("privacy_policy")?.setOnPreferenceClickListener {
                openPrivacyPolicy()
                true
            }
            
            // Add TPV Compliance preference
            findPreference<Preference>("tpv_compliance")?.setOnPreferenceClickListener {
                openTpvCompliance()
                true
            }
        }
        
        /**
         * Show detailed About dialog with all required disclosures
         * Per Third-Party Viewer Policy Section 1.c and 5.e
         */
        private fun showAboutDialog() {
            val versionName = BuildConfig.VERSION_NAME
            val versionCode = BuildConfig.VERSION_CODE
            
            val message = buildString {
                appendLine("Linkpoint")
                appendLine("Version: $versionName (Build $versionCode)")
                appendLine("Channel: Linkpoint")
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("IMPORTANT DISCLAIMER")
                appendLine()
                appendLine("This software is not provided or supported by Linden Lab, the makers of Second Life.")
                appendLine()
                appendLine("Linkpoint is an independent, community-developed third-party viewer for Second Life on Android.")
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("CUSTOMER SUPPORT")
                appendLine()
                appendLine("Community support via GitHub:")
                appendLine("https://github.com/Kaleaon/Linkpoint/issues")
                appendLine()
                appendLine("For Second Life account issues, contact Linden Lab directly.")
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("MOBILE LIMITATIONS")
                appendLine()
                appendLine("As a mobile viewer, Linkpoint has certain limitations:")
                appendLine("• Limited building/editing functionality")
                appendLine("• Simplified graphics for mobile devices")
                appendLine("• Touch-based controls")
                appendLine("• Voice via WebRTC implementation")
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("COMPLIANCE")
                appendLine()
                appendLine("Linkpoint complies with Linden Lab's Third-Party Viewer Policy.")
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("Second Life is a trademark of Linden Lab.")
            }
            
            AlertDialog.Builder(requireContext())
                .setTitle("About Linkpoint")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNeutralButton("GitHub") { _, _ ->
                    openGitHub()
                }
                .show()
        }
        
        private fun openPrivacyPolicy() {
            try {
                val intent = Intent(Intent.ACTION_VIEW, 
                    Uri.parse(PRIVACY_POLICY_URL))
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Could not open Privacy Policy",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        private fun openTpvCompliance() {
            try {
                val intent = Intent(Intent.ACTION_VIEW, 
                    Uri.parse(TPV_COMPLIANCE_URL))
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Could not open TPV Compliance document",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        private fun openGitHub() {
            try {
                val intent = Intent(Intent.ACTION_VIEW, 
                    Uri.parse(GITHUB_BASE_URL))
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Could not open GitHub",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        private fun updateGraphicsQuality(quality: String) {
            when (quality) {
                "low" -> {
                    // Low graphics settings
                }
                "medium" -> {
                    // Medium graphics settings
                }
                "high" -> {
                    // High graphics settings
                }
                "ultra" -> {
                    // Ultra graphics settings
                }
            }
        }
        
        private fun updateVoice(enabled: Boolean) {
            // Enable/disable voice chat
        }
    }
}
