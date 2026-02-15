package com.linkpoint.ui.settings

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.EditTextPreference
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.ListPreference
import androidx.preference.SeekBarPreference
import com.linkpoint.BuildConfig
import com.linkpoint.R
import com.linkpoint.network.NetworkLogger
import com.linkpoint.service.BackgroundResumeScheduler
import com.linkpoint.ui.tos.TosActivity
import com.linkpoint.ui.world.WorldViewActivity
import com.linkpoint.utils.CodexUploadService
import com.linkpoint.utils.CrashReporter
import com.linkpoint.utils.DebugReportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Settings Activity
 * Based on the reference viewer's SettingsActivity
 * 
 * Includes required disclosures per Third-Party Viewer Policy Section 1.g:
 * - Viewer name and version displayed in About section
 * - Links to Terms of Service and Privacy Policy
 * 
 * Also includes debug and diagnostics features:
 * - Crash log viewing
 * - Crash reporter status
 * - Test crash generation
 * - Cache size configuration (3-500 MB)
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
        
        // ActivityResultLauncher for saving the exported log file
        private var pendingLogContent: String? = null
        private val createDocumentLauncher: ActivityResultLauncher<Intent> = 
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        writeLogToUri(uri)
                    }
                }
                pendingLogContent = null
            }
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            // Display settings
            setupDisplaySettings()

            // Interface settings
            setupInterfaceSettings()
            
            // Graphics settings
            findPreference<ListPreference>("graphics_quality")?.setOnPreferenceChangeListener { _, newValue ->
                updateGraphicsQuality(newValue as String)
                true
            }

            findPreference<SwitchPreferenceCompat>("enable_secondary_renderer")?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                Toast.makeText(
                    requireContext(),
                    if (enabled) "Secondary renderer enabled. Reopen world view to apply."
                    else "Primary renderer enabled. Reopen world view to apply.",
                    Toast.LENGTH_LONG
                ).show()
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
            
            // RLV settings
            findPreference<SwitchPreferenceCompat>("rlv_enabled")?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                try {
                    com.linkpoint.LinkpointApp.getInstance().rlvController.setEnabled(enabled)
                    Toast.makeText(requireContext(), 
                        "RLV ${if (enabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("SettingsActivity", "Failed to update RLV setting", e)
                }
                true
            }
            
            // About section - Required by TPV Policy Section 1.g
            setupAboutSection()
            
            // Debug and Diagnostics section
            setupDebugSection()
            
            // Cache settings
            setupCacheSettings()

            setupBackgroundRuntimeSettings()

            
            // ToS viewing
            findPreference<Preference>("view_tos")?.setOnPreferenceClickListener {
                startActivity(TosActivity.createIntent(requireContext(), requireAcceptance = false))
                true
            }
            
            // XML buffer size setting (3-500 MB)
            setupNetworkBufferSettings()
        }
        
        private fun setupBackgroundRuntimeSettings() {
            findPreference<SwitchPreferenceCompat>("background_resume_enabled")?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                if (enabled) {
                    BackgroundResumeScheduler.schedule(requireContext(), immediate = true)
                } else {
                    BackgroundResumeScheduler.cancel(requireContext())
                }
                true
            }

            findPreference<SwitchPreferenceCompat>("push_wakeups_enabled")?.setOnPreferenceChangeListener { _, _ ->
                true
            }

            findPreference<ListPreference>("background_intensity_profile")?.setOnPreferenceChangeListener { _, _ ->
                BackgroundResumeScheduler.schedule(requireContext(), immediate = false)
                true
            }
        }

        /**
         * Setup cache settings - Linkpoint configurable caches
         */
        private fun setupCacheSettings() {
            val cacheManager = com.linkpoint.assets.CacheManager(requireContext())
            
            // Cache location
            findPreference<ListPreference>("cache_location")?.apply {
                value = cacheManager.getCacheLocation()
                setOnPreferenceChangeListener { _, newValue ->
                    val location = newValue as String
                    if (location == com.linkpoint.assets.CacheManager.LOCATION_EXTERNAL && 
                        !cacheManager.isExternalStorageAvailable()) {
                        Toast.makeText(requireContext(), 
                            "External storage not available", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        cacheManager.setCacheLocation(location)
                        AlertDialog.Builder(requireContext())
                            .setTitle("Restart Required")
                            .setMessage("Cache location changed. The app needs to restart for this to take effect.")
                            .setPositiveButton("OK", null)
                            .show()
                        true
                    }
                }
            }
            
            // Total disk cache (in GB now)
            findPreference<SeekBarPreference>("disk_cache_size")?.apply {
                val currentGB = cacheManager.getDiskCacheSizeMB() / 1024
                value = currentGB.coerceIn(1, 50)
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeGB = newValue as Int
                    cacheManager.setDiskCacheSizeMB(sizeGB * 1024)
                    Toast.makeText(requireContext(), "Disk cache set to ${sizeGB}GB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Memory cache (MB)
            findPreference<SeekBarPreference>("memory_cache_size")?.apply {
                value = cacheManager.getMemoryCacheSizeMB()
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeMB = newValue as Int
                    cacheManager.setMemoryCacheSizeMB(sizeMB)
                    Toast.makeText(requireContext(), 
                        "Memory cache set to ${sizeMB}MB (restart required)", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Texture memory (RAM dedication)
            findPreference<SeekBarPreference>("texture_memory")?.apply {
                value = cacheManager.getTextureMemoryMB()
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeMB = newValue as Int
                    cacheManager.setTextureMemoryMB(sizeMB)
                    Toast.makeText(requireContext(), 
                        "Texture RAM set to ${sizeMB}MB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Texture cache (in GB)
            findPreference<SeekBarPreference>("texture_cache_size")?.apply {
                val currentGB = cacheManager.getTextureCacheSizeMB() / 1024
                value = currentGB.coerceIn(1, 20)
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeGB = newValue as Int
                    cacheManager.setTextureCacheSizeMB(sizeGB * 1024)
                    Toast.makeText(requireContext(), "Texture cache set to ${sizeGB}GB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Mesh cache (in GB)
            findPreference<SeekBarPreference>("mesh_cache_size")?.apply {
                val currentGB = cacheManager.getMeshCacheSizeMB() / 1024
                value = currentGB.coerceIn(1, 10)
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeGB = newValue as Int
                    cacheManager.setMeshCacheSizeMB(sizeGB * 1024)
                    Toast.makeText(requireContext(), "Mesh cache set to ${sizeGB}GB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Sound cache (in GB)
            findPreference<SeekBarPreference>("sound_cache_size")?.apply {
                val currentGB = cacheManager.getSoundCacheSizeMB() / 1024
                value = currentGB.coerceIn(1, 4)
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeGB = newValue as Int
                    cacheManager.setSoundCacheSizeMB(sizeGB * 1024)
                    Toast.makeText(requireContext(), "Sound cache set to ${sizeGB}GB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Animation cache (in GB)
            findPreference<SeekBarPreference>("animation_cache_size")?.apply {
                val currentGB = cacheManager.getAnimationCacheSizeMB() / 1024
                value = currentGB.coerceIn(1, 4)
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeGB = newValue as Int
                    cacheManager.setAnimationCacheSizeMB(sizeGB * 1024)
                    Toast.makeText(requireContext(), "Animation cache set to ${sizeGB}GB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Auto-clear on low space
            findPreference<SwitchPreferenceCompat>("auto_clear_cache")?.apply {
                isChecked = cacheManager.isAutoClearOnLowSpaceEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    cacheManager.setAutoClearOnLowSpace(newValue as Boolean)
                    true
                }
            }
            
            // View cache statistics
            findPreference<Preference>("view_cache_stats")?.setOnPreferenceClickListener {
                showCacheStatistics(cacheManager)
                true
            }
            
            // Clear all cache
            findPreference<Preference>("clear_cache")?.setOnPreferenceClickListener {
                confirmClearAllCache(cacheManager)
                true
            }
        }
        
        /**
         * Show cache statistics dialog
         */
        private fun showCacheStatistics(cacheManager: com.linkpoint.assets.CacheManager) {
            viewLifecycleOwner.lifecycleScope.launch {
                val stats = cacheManager.getCacheStats()
                val locations = cacheManager.getAvailableCacheLocations()
                
                val message = buildString {
                    appendLine("=== Cache Usage ===")
                    appendLine()
                    appendLine("Total: ${stats.getFormattedTotalSize()} / ${stats.getFormattedMaxSize()} (${stats.usagePercent}%)")
                    appendLine("Files: ${stats.totalFileCount}")
                    appendLine()
                    appendLine("=== By Type ===")
                    appendLine("Textures: ${cacheManager.formatSize(stats.texturesSizeBytes)} (${stats.texturesCount} files)")
                    appendLine("Meshes: ${cacheManager.formatSize(stats.meshesSizeBytes)} (${stats.meshesCount} files)")
                    appendLine("Sounds: ${cacheManager.formatSize(stats.soundsSizeBytes)} (${stats.soundsCount} files)")
                    appendLine("Animations: ${cacheManager.formatSize(stats.animationsSizeBytes)} (${stats.animationsCount} files)")
                    appendLine("General: ${cacheManager.formatSize(stats.generalSizeBytes)} (${stats.generalCount} files)")
                    appendLine()
                    appendLine("=== Storage ===")
                    appendLine("Current Location: ${cacheManager.getCacheLocation()}")
                    appendLine("Available Space: ${cacheManager.formatSize(stats.availableSpaceBytes)}")
                    if (stats.isLowSpace) {
                        appendLine("⚠️ LOW SPACE WARNING")
                    }
                    appendLine()
                    appendLine("=== Configured Limits ===")
                    appendLine("Disk Cache: ${cacheManager.getDiskCacheSizeMB() / 1024} GB")
                    appendLine("Memory Cache: ${cacheManager.getMemoryCacheSizeMB()} MB")
                    appendLine("Texture Memory: ${cacheManager.getTextureMemoryMB()} MB")
                    appendLine("Texture Cache: ${cacheManager.getTextureCacheSizeMB() / 1024} GB")
                    appendLine("Mesh Cache: ${cacheManager.getMeshCacheSizeMB() / 1024} GB")
                    appendLine("Sound Cache: ${cacheManager.getSoundCacheSizeMB() / 1024} GB")
                    appendLine("Animation Cache: ${cacheManager.getAnimationCacheSizeMB() / 1024} GB")
                }
                
                AlertDialog.Builder(requireContext())
                    .setTitle("Cache Statistics")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Copy") { _, _ ->
                        copyToClipboard("Cache Statistics", message)
                        Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
        
        /**
         * Confirm and clear all cache
         */
        private fun confirmClearAllCache(cacheManager: com.linkpoint.assets.CacheManager) {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Cache")
                .setMessage("This will delete all cached textures, meshes, sounds, and animations.\n\nThis cannot be undone and may cause slower loading next time you log in.")
                .setPositiveButton("Clear All") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        val result = cacheManager.clearAllCache()
                        val message = if (result.success) {
                            "Cleared ${cacheManager.formatSize(result.clearedBytes)} (${result.clearedFiles} files)"
                        } else {
                            "Cleared ${cacheManager.formatSize(result.clearedBytes)} with ${result.errors} errors"
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        /**
         * Setup network buffer settings (XML response buffer 3-500 MB)
         */
        private fun setupNetworkBufferSettings() {
            val networkSettings = com.linkpoint.network.NetworkSettings.getInstance(requireContext())
            
            // XML buffer size preference
            findPreference<SeekBarPreference>("xml_buffer_size")?.apply {
                value = networkSettings.getXmlBufferSizeMB()
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeMB = newValue as Int
                    networkSettings.setXmlBufferSizeMB(sizeMB)
                    Toast.makeText(requireContext(), "Login buffer set to ${sizeMB}MB", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        
        /**
         * Setup Debug and Diagnostics section for crash log viewing
         */
        private fun setupDebugSection() {
            // Debug Floater Toggle
            findPreference<SwitchPreferenceCompat>("enable_debug_floater")?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                Toast.makeText(
                    requireContext(),
                    if (enabled) "Debug floater will appear in world view" else "Debug floater disabled",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }

            // Codex auto-upload toggle
            findPreference<SwitchPreferenceCompat>("codex_auto_upload")?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                Toast.makeText(
                    requireContext(),
                    if (enabled) "Codex auto-upload enabled" else "Codex auto-upload disabled",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }

            // Codex endpoint configuration
            findPreference<EditTextPreference>("codex_upload_endpoint")?.apply {
                updateCodexEndpointSummary(this)
                setOnPreferenceChangeListener { preference, newValue ->
                    val endpoint = newValue?.toString()?.trim().orEmpty()
                    CodexUploadService.getInstance(requireContext()).updateEndpoint(endpoint)
                    updateCodexEndpointSummary(preference as EditTextPreference)
                    true
                }
            }

            // Upload pending Codex reports
            findPreference<Preference>("codex_upload_now")?.setOnPreferenceClickListener {
                val codexService = CodexUploadService.getInstance(requireContext())
                Toast.makeText(requireContext(), "Uploading reports to Codex...", Toast.LENGTH_SHORT).show()
                codexService.uploadLatestReportsAsync("manual_upload") { result ->
                    val message = when (result) {
                        is CodexUploadService.UploadResult.Success ->
                            "Uploaded ${result.fileCount} report(s) to Codex"
                        is CodexUploadService.UploadResult.NoData ->
                            "No reports available to upload"
                        is CodexUploadService.UploadResult.Disabled ->
                            "Enable Codex auto-upload to send reports"
                        is CodexUploadService.UploadResult.Failed ->
                            "Codex upload failed: ${result.error.message}"
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
                true
            }
            
            // Capture Debug Report Now
            findPreference<Preference>("capture_debug_report")?.setOnPreferenceClickListener {
                captureDebugReportNow()
                true
            }
            
            // View Debug Reports
            findPreference<Preference>("view_debug_reports")?.setOnPreferenceClickListener {
                showDebugReportsDialog()
                true
            }
            
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
            
            // Export Log
            findPreference<Preference>("export_log")?.setOnPreferenceClickListener {
                exportLog()
                true
            }
        }
        
        /**
         * Capture a debug report immediately
         */
        private fun captureDebugReportNow() {
            val debugService = com.linkpoint.utils.DebugReportService.getInstance(requireContext())
            debugService.captureDebugReportAsync("Manual capture from Settings") { file ->
                if (file != null) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Debug Report Captured")
                        .setMessage("Report saved to:\n${file.name}\n\nWould you like to view it?")
                        .setPositiveButton("View") { _, _ ->
                            showDebugReportContent(file.name, debugService.readReport(file))
                        }
                        .setNegativeButton("Close", null)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Failed to capture debug report", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun updateCodexEndpointSummary(pref: EditTextPreference) {
            val endpoint = CodexUploadService.getInstance(requireContext()).getEndpoint()
            pref.summary = if (pref.text.isNullOrBlank()) {
                "Using default endpoint: $endpoint"
            } else {
                endpoint
            }
        }
        
        /**
         * Show list of debug reports
         */
        private fun showDebugReportsDialog() {
            val debugService = com.linkpoint.utils.DebugReportService.getInstance(requireContext())
            val reports = debugService.getDebugReports()
            
            if (reports.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Debug Reports")
                    .setMessage("No debug reports found.\n\nTap the bug floater icon in world view to capture a report, or use 'Capture Debug Report Now' above.")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }
            
            val reportNames = reports.map { it.name }.toTypedArray()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Debug Reports (${reports.size})")
                .setItems(reportNames) { _, which ->
                    val selectedReport = reports[which]
                    showDebugReportContent(selectedReport.name, debugService.readReport(selectedReport))
                }
                .setNegativeButton("Close", null)
                .setNeutralButton("Clear All") { _, _ ->
                    confirmClearDebugReports()
                }
                .show()
        }
        
        /**
         * Show content of a specific debug report
         */
        private fun showDebugReportContent(filename: String, content: String?) {
            if (content == null) {
                Toast.makeText(requireContext(), "Failed to read debug report", Toast.LENGTH_SHORT).show()
                return
            }
            
            AlertDialog.Builder(requireContext())
                .setTitle(filename)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .setNeutralButton("Copy") { _, _ ->
                    copyToClipboard("Debug Report", content)
                    Toast.makeText(requireContext(), "Debug report copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        
        /**
         * Confirm and clear all debug reports
         */
        private fun confirmClearDebugReports() {
            val debugService = com.linkpoint.utils.DebugReportService.getInstance(requireContext())
            val reportsCount = debugService.getDebugReports().size
            
            if (reportsCount == 0) {
                Toast.makeText(requireContext(), "No debug reports to clear", Toast.LENGTH_SHORT).show()
                return
            }
            
            AlertDialog.Builder(requireContext())
                .setTitle("Clear Debug Reports")
                .setMessage("Delete all $reportsCount debug reports?\n\nThis cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    debugService.clearReports()
                    Toast.makeText(requireContext(), "Debug reports cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
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
                        // Create a test exception with current timestamp
                        val timestamp = System.currentTimeMillis()
                        val testException = RuntimeException("Test crash generated from Settings at $timestamp")
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
         * Export combined log to a user-selected location in the file system
         */
        private fun exportLog() {
            viewLifecycleOwner.lifecycleScope.launch {
                Toast.makeText(requireContext(), "Generating combined log...", Toast.LENGTH_SHORT).show()
                
                val logContent = withContext(Dispatchers.IO) {
                    generateCombinedLog()
                }
                
                // Store the content for use by the result callback
                pendingLogContent = logContent
                
                // Generate a default filename with timestamp
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
                val defaultFilename = "linkpoint_log_$timestamp.txt"
                
                // Use Storage Access Framework to let user choose location
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, defaultFilename)
                }
                
                try {
                    createDocumentLauncher.launch(intent)
                } catch (e: Exception) {
                    pendingLogContent = null
                    Toast.makeText(
                        requireContext(),
                        "Could not open file picker: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        
        /**
         * Write the log content to the selected URI
         */
        private fun writeLogToUri(uri: Uri) {
            val content = pendingLogContent
            if (content == null) {
                Toast.makeText(requireContext(), "No log content to save", Toast.LENGTH_SHORT).show()
                return
            }
            
            viewLifecycleOwner.lifecycleScope.launch {
                val success = withContext(Dispatchers.IO) {
                    try {
                        requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.bufferedWriter().use { writer ->
                                writer.write(content)
                            }
                        }
                        true
                    } catch (e: Exception) {
                        android.util.Log.e("SettingsActivity", "Failed to write log file", e)
                        false
                    }
                }
                
                if (success) {
                    Toast.makeText(requireContext(), "Log exported successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to export log", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        /**
         * Generate a combined log from all log sources
         */
        private fun generateCombinedLog(): String {
            return buildString {
                appendLine("╔══════════════════════════════════════════════════════════════════╗")
                appendLine("║               LINKPOINT COMBINED LOG EXPORT                       ║")
                appendLine("╚══════════════════════════════════════════════════════════════════╝")
                appendLine()
                appendLine("Export Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(Date())}")
                appendLine("Version: ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                appendLine()
                
                // Include most recent debug report
                appendLine("┌──────────────────────────────────────────────────────────────────┐")
                appendLine("│ MOST RECENT DEBUG REPORT                                          │")
                appendLine("└──────────────────────────────────────────────────────────────────┘")
                appendLine()
                try {
                    val debugService = DebugReportService.getInstanceOrNull()
                    if (debugService != null) {
                        val reports = debugService.getDebugReports()
                        if (reports.isNotEmpty()) {
                            val latestReport = reports.first()
                            val content = debugService.readReport(latestReport)
                            if (content != null) {
                                appendLine("Source: ${latestReport.name}")
                                appendLine()
                                appendLine(content)
                            } else {
                                appendLine("Could not read debug report")
                            }
                        } else {
                            appendLine("No debug reports available")
                        }
                    } else {
                        appendLine("Debug report service not initialized")
                    }
                } catch (e: Exception) {
                    appendLine("Error reading debug reports: ${e.message}")
                }
                appendLine()
                
                // Include crash logs
                appendLine("┌──────────────────────────────────────────────────────────────────┐")
                appendLine("│ CRASH LOGS                                                        │")
                appendLine("└──────────────────────────────────────────────────────────────────┘")
                appendLine()
                try {
                    val crashReporter = CrashReporter.getInstanceOrNull()
                    if (crashReporter != null) {
                        val crashLogs = crashReporter.getCrashLogs()
                        if (crashLogs.isNotEmpty()) {
                            appendLine("Total crash logs: ${crashLogs.size}")
                            appendLine()
                            // Include last 5 crash logs
                            crashLogs.take(5).forEach { logFile ->
                                appendLine("--- ${logFile.name} ---")
                                val content = crashReporter.readCrashLog(logFile)
                                if (content != null) {
                                    appendLine(content)
                                } else {
                                    appendLine("Could not read crash log")
                                }
                                appendLine()
                            }
                            if (crashLogs.size > 5) {
                                appendLine("... and ${crashLogs.size - 5} more crash logs")
                            }
                        } else {
                            appendLine("No crash logs found (this is good!)")
                        }
                    } else {
                        appendLine("Crash reporter not initialized")
                    }
                } catch (e: Exception) {
                    appendLine("Error reading crash logs: ${e.message}")
                }
                appendLine()
                
                // Include recent network logs
                appendLine("┌──────────────────────────────────────────────────────────────────┐")
                appendLine("│ RECENT NETWORK ACTIVITY                                           │")
                appendLine("└──────────────────────────────────────────────────────────────────┘")
                appendLine()
                try {
                    val networkLogs = NetworkLogger.getRecentLogs(100)
                    if (networkLogs.isNotEmpty()) {
                        appendLine(networkLogs)
                    } else {
                        appendLine("No recent network activity logged")
                    }
                    
                    appendLine()
                    appendLine("Network Statistics:")
                    val stats = NetworkLogger.getStatistics()
                    appendLine("  Requests: ${stats.requestCount}")
                    appendLine("  Responses: ${stats.responseCount}")
                    appendLine("  Errors: ${stats.errorCount}")
                    appendLine("  Warnings: ${stats.warningCount}")
                    appendLine("  Retries: ${stats.retryCount}")
                    appendLine("  Timeouts: ${stats.timeoutCount}")
                } catch (e: Exception) {
                    appendLine("Error reading network logs: ${e.message}")
                }
                appendLine()
                
                appendLine("═══════════════════════════════════════════════════════════════════")
                appendLine("End of Combined Log Export")
                appendLine("═══════════════════════════════════════════════════════════════════")
            }
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
        
        /**
         * Setup Display settings (screen orientation)
         */
        private fun setupDisplaySettings() {
            findPreference<ListPreference>("screen_orientation")?.apply {
                // Update summary to show current value - use first entry as fallback if available
                summary = entry ?: entries?.takeIf { it.isNotEmpty() }?.get(0) ?: "Portrait"
                setOnPreferenceChangeListener { preference, newValue ->
                    val listPref = preference as ListPreference
                    val index = listPref.findIndexOfValue(newValue as String)
                    if (index >= 0) {
                        listPref.summary = listPref.entries[index]
                    }
                    Toast.makeText(
                        requireContext(),
                        "Screen orientation will apply when you return to the world view",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
            }
        }

        /**
         * Setup interface settings (HUD/controls visibility + layout editor).
         */
        private fun setupInterfaceSettings() {
            val visibilityPreferences = listOf(
                "show_hud",
                "show_joysticks",
                "show_action_buttons",
                "show_movement_buttons"
            )

            visibilityPreferences.forEach { key ->
                findPreference<SwitchPreferenceCompat>(key)?.setOnPreferenceChangeListener { _, _ ->
                    Toast.makeText(
                        requireContext(),
                        "Interface changes will apply when you return to the world view",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
            }

            findPreference<Preference>("customize_layout")?.setOnPreferenceClickListener {
                startActivity(WorldViewActivity.createLayoutEditorIntent(requireContext()))
                true
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
