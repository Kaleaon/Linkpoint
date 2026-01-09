package com.linkpoint.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Phase 3: Theme Manager for Material Design 3
 * 
 * Manages app-wide theme switching between Light, Dark, and System default modes.
 * Provides persistent theme storage and application of theme preferences.
 */
class ThemeManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "linkpoint_theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
        
        @Volatile
        private var instance: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    /**
     * Get the current theme mode
     */
    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }
    
    /**
     * Set and apply the theme mode
     */
    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
        applyTheme(mode)
    }
    
    /**
     * Apply the theme mode to the app
     */
    fun applyTheme(mode: Int = getThemeMode()) {
        when (mode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    /**
     * Check if dark theme is currently active
     */
    fun isDarkTheme(): Boolean {
        val mode = AppCompatDelegate.getDefaultNightMode()
        return when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                // Check system setting
                val uiMode = context.resources.configuration.uiMode and 
                           android.content.res.Configuration.UI_MODE_NIGHT_MASK
                uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
    
    /**
     * Get theme mode name for display
     */
    fun getThemeModeName(mode: Int = getThemeMode()): String {
        return when (mode) {
            THEME_LIGHT -> "Light"
            THEME_DARK -> "Dark"
            THEME_SYSTEM -> "System Default"
            else -> "Unknown"
        }
    }
    
    /**
     * Toggle between light and dark theme
     */
    fun toggleTheme() {
        val currentMode = getThemeMode()
        val newMode = when (currentMode) {
            THEME_LIGHT -> THEME_DARK
            THEME_DARK -> THEME_SYSTEM
            THEME_SYSTEM -> THEME_LIGHT
            else -> THEME_SYSTEM
        }
        setThemeMode(newMode)
    }
}
