package com.linkpoint.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
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
import com.linkpoint.utils.CrashReporter

/**
 * Settings Activity
 * Based on Lumiya's SettingsActivity
 * 
 * Includes required disclosures per Third-Party Viewer Policy Section 1.g:
 * - Viewer name and version displayed in About section
 * - Links to Terms of Service and Privacy Policy
 * 
 * Also includes debug and diagnostics features:
 * - Crash log viewing
 * - Crash reporter status
 * - Test crash generation
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
            
            // Debug and Diagnostics section
            setupDebugSection()
            
            // ToS viewing
            findPreference<Preference>("view_tos")?.setOnPreferenceClickListener {
                startActivity(TosActivity.createIntent(requireContext(), requireAcceptance = false))
                true
            }
        }
        
        /**
         * Setup Debug and Diagnostics section for crash log viewing
         */
        private fun setupDebugSection() {
            // View Crash Logs
            findPreference<Preference>("view_crash_logs")?.setOnPreferenceClickListener {
                showCrashLogsDialog()
                true
            }
            
            // Crash Reporter Status
            findPreference<Preference>("crash_reporter_status")?.apply {
                updateCrashReporterStatusSummary(this)
                setOnPreferenceClickListener {
                    showCrashReporterDiagnostics()
                    true
                }
            }
            
            // Test Crash
            findPreference<Preference>("test_crash")?.setOnPreferenceClickListener {
                testCrashReporter()
                true
            }
            
            // Clear Crash Logs
            findPreference<Preference>("clear_crash_logs")?.setOnPreferenceClickListener {
                confirmClearCrashLogs()
                true
            }
        }
        
        /**
         * Helper method to execute action with crash reporter or show error if not available.
         * Reduces code duplication for crash reporter null checks.
         */
        private inline fun withCrashReporter(
            notAvailableMessage: String = "Crash reporter not initialized",
            action: (CrashReporter) -> Unit
        ) {
            val crashReporter = CrashReporter.getInstanceOrNull()
            if (crashReporter != null) {
                action(crashReporter)
            } else {
                Toast.makeText(requireContext(), notAvailableMessage, Toast.LENGTH_SHORT).show()
            }
        }
        
        /**
         * Update the crash reporter status summary
         */
        private fun updateCrashReporterStatusSummary(pref: Preference) {
            val crashReporter = CrashReporter.getInstanceOrNull()
            if (crashReporter != null) {
                val diagnostics = crashReporter.getDiagnostics()
                val statusText = when {
                    diagnostics.isWorking() -> "✓ Working"
                    else -> "⚠ Not initialized"
                }
                val logsCount = crashReporter.getCrashLogs().size
                pref.summary = "$statusText • $logsCount crash logs saved"
            } else {
                pref.summary = "⚠ Crash reporter not initialized"
            }
        }
        
        /**
         * Show crash logs dialog with list of available crash logs
         */
        private fun showCrashLogsDialog() = withCrashReporter { crashReporter ->
            
            val crashLogs = crashReporter.getCrashLogs()
            
            if (crashLogs.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Crash Logs")
                    .setMessage("No crash logs found.\n\nThis is good! It means the app hasn't crashed.")
                    .setPositiveButton("OK", null)
                    .show()
                return@withCrashReporter
            }
            
            val logNames = crashLogs.map { it.name }.toTypedArray()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Crash Logs (${crashLogs.size})")
                .setItems(logNames) { _, which ->
                    val selectedLog = crashLogs[which]
                    showCrashLogContent(selectedLog.name, crashReporter.readCrashLog(selectedLog))
                }
                .setNegativeButton("Close", null)
                .setNeutralButton("View Summary") { _, _ ->
                    showCrashSummary(crashReporter.generateCrashSummary())
                }
                .show()
        }
        
        /**
         * Show content of a specific crash log
         */
        private fun showCrashLogContent(filename: String, content: String?) {
            if (content == null) {
                Toast.makeText(requireContext(), "Failed to read crash log", Toast.LENGTH_SHORT).show()
                return
            }
            
            AlertDialog.Builder(requireContext())
                .setTitle(filename)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .setNeutralButton("Copy") { _, _ ->
                    copyToClipboard("Crash Log", content)
                    Toast.makeText(requireContext(), "Crash log copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        
        /**
         * Show crash summary
         */
        private fun showCrashSummary(summary: String) {
            AlertDialog.Builder(requireContext())
                .setTitle("Crash Summary")
                .setMessage(summary)
                .setPositiveButton("Close", null)
                .setNeutralButton("Copy") { _, _ ->
                    copyToClipboard("Crash Summary", summary)
                    Toast.makeText(requireContext(), "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        
        /**
         * Show crash reporter diagnostics
         */
        private fun showCrashReporterDiagnostics() {
            val crashReporter = CrashReporter.getInstanceOrNull()
            if (crashReporter == null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Crash Reporter Status")
                    .setMessage("⚠ Crash Reporter is NOT initialized!\n\nThis means crashes will not be logged.\n\nPossible causes:\n• App initialization failed\n• Storage access issue\n\nTry restarting the app.")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }
            
            val diagnostics = crashReporter.getDiagnostics()
            val report = diagnostics.toReport()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Crash Reporter Status")
                .setMessage(report)
                .setPositiveButton("Close", null)
                .setNeutralButton("Copy") { _, _ ->
                    copyToClipboard("Crash Reporter Diagnostics", report)
                    Toast.makeText(requireContext(), "Diagnostics copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Reinitialize") { _, _ ->
                    val success = crashReporter.reinitializeStorage()
                    if (success) {
                        Toast.makeText(requireContext(), "Crash reporter reinitialized", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Reinitialization failed", Toast.LENGTH_SHORT).show()
                    }
                    // Update the status summary
                    findPreference<Preference>("crash_reporter_status")?.let {
                        updateCrashReporterStatusSummary(it)
                    }
                }
                .show()
        }
        
        /**
         * Test the crash reporter by generating a test exception
         */
        private fun testCrashReporter() = withCrashReporter { crashReporter ->
            AlertDialog.Builder(requireContext())
                .setTitle("Test Crash Reporter")
                .setMessage("This will generate a non-fatal test exception to verify crash reporting is working.\n\nThe exception will be logged but the app will not crash.")
                .setPositiveButton("Generate Test Crash") { _, _ ->
                    try {
                        // Create a test exception
                        val testException = RuntimeException("Test crash generated from Settings at ${java.util.Date()}")
                        crashReporter.reportException(testException, "Settings test crash")
                        
                        Toast.makeText(requireContext(), "Test crash report generated", Toast.LENGTH_SHORT).show()
                        
                        // Update the status
                        findPreference<Preference>("crash_reporter_status")?.let {
                            updateCrashReporterStatusSummary(it)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to generate test crash: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        /**
         * Confirm and clear all crash logs
         */
        private fun confirmClearCrashLogs() = withCrashReporter { crashReporter ->
            val logsCount = crashReporter.getCrashLogs().size
            
            if (logsCount == 0) {
                Toast.makeText(requireContext(), "No crash logs to clear", Toast.LENGTH_SHORT).show()
                return@withCrashReporter
            }
            
            AlertDialog.Builder(requireContext())
                .setTitle("Clear Crash Logs")
                .setMessage("Delete all $logsCount crash logs?\n\nThis cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    crashReporter.clearCrashLogs()
                    Toast.makeText(requireContext(), "Crash logs cleared", Toast.LENGTH_SHORT).show()
                    
                    // Update the status
                    findPreference<Preference>("crash_reporter_status")?.let {
                        updateCrashReporterStatusSummary(it)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        /**
         * Copy text to clipboard
         */
        private fun copyToClipboard(label: String, text: String) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
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
