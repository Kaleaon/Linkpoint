package com.linkpoint.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.linkpoint.R
import com.linkpoint.assets.CacheManager
import com.linkpoint.assets.CacheableAssetType
import com.linkpoint.ui.tos.TosActivity
import kotlinx.coroutines.launch

/**
 * Settings activity for Linkpoint configuration
 * 
 * Includes comprehensive cache management controls to prevent
 * cache-related issues that can cause login and asset loading failures.
 */
class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        
        private lateinit var cacheManager: CacheManager
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            cacheManager = CacheManager(requireContext())
            
            setupGraphicsPreferences()
            setupCachePreferences()
            setupAccountPreferences()
            setupAboutPreference()
        }
        
        override fun onResume() {
            super.onResume()
            // Refresh cache status when returning to settings
            updateCacheStatus()
        }
        
        private fun setupGraphicsPreferences() {
            // Graphics quality preference
            findPreference<ListPreference>("graphics_quality")?.apply {
                setOnPreferenceChangeListener { _, value ->
                    Toast.makeText(context, "Graphics quality changed to: $value", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Draw distance preference
            findPreference<SeekBarPreference>("draw_distance")?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    summary = "Draw distance: ${newValue}m"
                    true
                }
            }
        }
        
        private fun setupCachePreferences() {
            // Cache status - shows current usage
            findPreference<Preference>("cache_status")?.apply {
                setOnPreferenceClickListener {
                    showCacheDetailsDialog()
                    true
                }
            }
            updateCacheStatus()
            
            // Disk cache size
            findPreference<SeekBarPreference>("disk_cache_size")?.apply {
                value = cacheManager.getDiskCacheSizeMB()
                summary = "Maximum storage: ${value}MB"
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeMB = newValue as Int
                    cacheManager.setDiskCacheSizeMB(sizeMB)
                    summary = "Maximum storage: ${sizeMB}MB"
                    updateCacheStatus()
                    true
                }
            }
            
            // Memory cache size
            findPreference<SeekBarPreference>("memory_cache_size")?.apply {
                value = cacheManager.getMemoryCacheSizeMB()
                summary = "RAM used: ${value}MB"
                setOnPreferenceChangeListener { _, newValue ->
                    val sizeMB = newValue as Int
                    cacheManager.setMemoryCacheSizeMB(sizeMB)
                    summary = "RAM used: ${sizeMB}MB"
                    Toast.makeText(context, "Memory cache size will apply on next restart", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // Cache textures toggle
            findPreference<SwitchPreferenceCompat>("cache_textures")?.apply {
                isChecked = cacheManager.isCacheEnabled(CacheableAssetType.TEXTURES)
                setOnPreferenceChangeListener { _, newValue ->
                    cacheManager.setCacheEnabled(CacheableAssetType.TEXTURES, newValue as Boolean)
                    true
                }
            }
            
            // Cache meshes toggle
            findPreference<SwitchPreferenceCompat>("cache_meshes")?.apply {
                isChecked = cacheManager.isCacheEnabled(CacheableAssetType.MESHES)
                setOnPreferenceChangeListener { _, newValue ->
                    cacheManager.setCacheEnabled(CacheableAssetType.MESHES, newValue as Boolean)
                    true
                }
            }
            
            // Cache sounds toggle
            findPreference<SwitchPreferenceCompat>("cache_sounds")?.apply {
                isChecked = cacheManager.isCacheEnabled(CacheableAssetType.SOUNDS)
                setOnPreferenceChangeListener { _, newValue ->
                    cacheManager.setCacheEnabled(CacheableAssetType.SOUNDS, newValue as Boolean)
                    true
                }
            }
            
            // Auto-clear on low space
            findPreference<SwitchPreferenceCompat>("auto_clear_low_space")?.apply {
                isChecked = cacheManager.isAutoClearOnLowSpaceEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    cacheManager.setAutoClearOnLowSpace(newValue as Boolean)
                    true
                }
            }
            
            // Clear all cache
            findPreference<Preference>("clear_cache")?.apply {
                setOnPreferenceClickListener {
                    showClearCacheConfirmation()
                    true
                }
            }
            
            // Clear textures cache only
            findPreference<Preference>("clear_textures_cache")?.apply {
                setOnPreferenceClickListener {
                    clearTexturesCache()
                    true
                }
            }
        }
        
        private fun setupAccountPreferences() {
            // View Terms of Service
            findPreference<Preference>("view_tos")?.apply {
                setOnPreferenceClickListener {
                    showTermsOfService()
                    true
                }
            }
            
            // Logout preference
            findPreference<Preference>("logout")?.apply {
                setOnPreferenceClickListener {
                    showLogoutDialog()
                    true
                }
            }
        }
        
        private fun setupAboutPreference() {
            findPreference<Preference>("about")?.apply {
                summary = "Linkpoint v1.0.0\nSecond Life Viewer for Android"
            }
        }
        
        private fun updateCacheStatus() {
            lifecycleScope.launch {
                try {
                    val stats = cacheManager.getCacheStats()
                    findPreference<Preference>("cache_status")?.apply {
                        val statusText = buildString {
                            append("Using ${stats.getFormattedTotalSize()} of ${stats.getFormattedMaxSize()}")
                            append(" (${stats.usagePercent}%)")
                            if (stats.isLowSpace) {
                                append("\n⚠️ Device storage is low!")
                            }
                        }
                        summary = statusText
                    }
                } catch (e: Exception) {
                    findPreference<Preference>("cache_status")?.summary = "Unable to read cache status"
                }
            }
        }
        
        private fun showCacheDetailsDialog() {
            lifecycleScope.launch {
                try {
                    val stats = cacheManager.getCacheStats()
                    
                    val details = buildString {
                        appendLine("=== Cache Usage ===")
                        appendLine("Total: ${stats.getFormattedTotalSize()} (${stats.totalFileCount} files)")
                        appendLine("Maximum: ${stats.getFormattedMaxSize()}")
                        appendLine("Usage: ${stats.usagePercent}%")
                        appendLine()
                        appendLine("=== By Type ===")
                        appendLine("Textures: ${cacheManager.formatSize(stats.texturesSizeBytes)} (${stats.texturesCount} files)")
                        appendLine("Meshes: ${cacheManager.formatSize(stats.meshesSizeBytes)} (${stats.meshesCount} files)")
                        appendLine("Sounds: ${cacheManager.formatSize(stats.soundsSizeBytes)} (${stats.soundsCount} files)")
                        appendLine("Animations: ${cacheManager.formatSize(stats.animationsSizeBytes)} (${stats.animationsCount} files)")
                        appendLine("Other: ${cacheManager.formatSize(stats.generalSizeBytes)} (${stats.generalCount} files)")
                        appendLine()
                        appendLine("=== Device Storage ===")
                        appendLine("Available: ${cacheManager.formatSize(stats.availableSpaceBytes)}")
                        if (stats.isLowSpace) {
                            appendLine("⚠️ LOW SPACE - consider clearing cache")
                        }
                        appendLine()
                        appendLine("=== Recommendations ===")
                        if (stats.usagePercent > 90) {
                            appendLine("• Cache is nearly full - consider clearing")
                        }
                        if (stats.usagePercent < 20 && stats.totalFileCount < 100) {
                            appendLine("• Small cache may cause slow loading")
                            appendLine("• Consider increasing cache size")
                        }
                        if (stats.texturesSizeBytes > stats.maxSizeBytes * 0.7) {
                            appendLine("• Textures using most space")
                        }
                    }
                    
                    AlertDialog.Builder(requireContext())
                        .setTitle("Cache Details")
                        .setMessage(details)
                        .setPositiveButton("OK", null)
                        .setNeutralButton("Clear Cache") { _, _ ->
                            showClearCacheConfirmation()
                        }
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error reading cache: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        private fun showClearCacheConfirmation() {
            lifecycleScope.launch {
                val stats = cacheManager.getCacheStats()
                
                AlertDialog.Builder(requireContext())
                    .setTitle("Clear All Cache?")
                    .setMessage("This will delete ${stats.getFormattedTotalSize()} of cached data (${stats.totalFileCount} files).\n\nTextures and meshes will need to be re-downloaded, which may slow down initial loading.\n\nContinue?")
                    .setPositiveButton("Clear") { _, _ ->
                        clearAllCache()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        
        private fun clearAllCache() {
            lifecycleScope.launch {
                try {
                    val result = cacheManager.clearAllCache()
                    if (result.success) {
                        Toast.makeText(
                            context,
                            "Cleared ${cacheManager.formatSize(result.clearedBytes)} (${result.clearedFiles} files)",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Cleared with ${result.errors} errors",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateCacheStatus()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to clear cache: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        private fun clearTexturesCache() {
            lifecycleScope.launch {
                try {
                    val result = cacheManager.clearCache(CacheableAssetType.TEXTURES)
                    Toast.makeText(
                        context,
                        "Cleared ${cacheManager.formatSize(result.clearedBytes)} of textures",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateCacheStatus()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to clear texture cache: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        private fun showTermsOfService() {
            // Open Second Life ToS in browser or show cached version
            AlertDialog.Builder(requireContext())
                .setTitle("Terms of Service")
                .setMessage("By using Linkpoint to connect to Second Life, you agree to Linden Lab's Terms of Service and Community Standards.\n\nWould you like to view the full Terms of Service?")
                .setPositiveButton("View Online") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lindenlab.com/tos"))
                    startActivity(intent)
                }
                .setNegativeButton("Close", null)
                .show()
        }
        
        private fun showLogoutDialog() {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    // Clear saved credentials
                    requireContext().getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE)
                        .edit()
                        .remove("saved_password")
                        .apply()
                    
                    // Navigate to login
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
