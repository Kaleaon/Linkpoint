package com.linkpoint.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.linkpoint.R

/**
 * Settings activity for Linkpoint configuration
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
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
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
            
            // Chat timestamps
            findPreference<SwitchPreferenceCompat>("show_timestamps")?.apply {
                setOnPreferenceChangeListener { _, _ ->
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
            
            // Clear cache preference
            findPreference<Preference>("clear_cache")?.apply {
                setOnPreferenceClickListener {
                    clearCache()
                    true
                }
            }
            
            // About preference
            findPreference<Preference>("about")?.apply {
                summary = "Linkpoint v1.0.0\nSecond Life Viewer for Android"
            }
        }
        
        private fun showLogoutDialog() {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    // Navigate to login
                    val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or 
                                   android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        private fun clearCache() {
            try {
                requireContext().cacheDir.deleteRecursively()
                Toast.makeText(context, "Cache cleared successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to clear cache: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
