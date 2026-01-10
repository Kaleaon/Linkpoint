package com.linkpoint.ui.compose.theme

import kotlin.math.*

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Theme Utilities for Linkpoint
 * 
 * Provides utilities for theme management including:
 * - Theme preference storage
 * - Dark/Light theme switching
 * - System theme following
 * - Color utilities
 * - Accessibility helpers
 */

/**
 * Theme Mode
 * 
 * Represents the theme mode preference.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;
    
    companion object {
        fun fromString(value: String): ThemeMode {
            return when (value.uppercase()) {
                "LIGHT" -> LIGHT
                "DARK" -> DARK
                "SYSTEM" -> SYSTEM
                else -> SYSTEM
            }
        }
    }
}

/**
 * Theme Preferences Manager
 * 
 * Manages theme preferences using SharedPreferences.
 */
class ThemePreferencesManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_USE_DYNAMIC_COLOR = "use_dynamic_color"
        private const val KEY_HIGH_CONTRAST = "high_contrast"
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get current theme mode
     */
    fun getThemeMode(): ThemeMode {
        val value = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return ThemeMode.fromString(value)
    }
    
    /**
     * Set theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
    
    /**
     * Check if dynamic color is enabled
     */
    fun isDynamicColorEnabled(): Boolean {
        return prefs.getBoolean(KEY_USE_DYNAMIC_COLOR, true)
    }
    
    /**
     * Set dynamic color preference
     */
    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USE_DYNAMIC_COLOR, enabled).apply()
    }
    
    /**
     * Check if high contrast is enabled
     */
    fun isHighContrastEnabled(): Boolean {
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false)
    }
    
    /**
     * Set high contrast preference
     */
    fun setHighContrastEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
    }
}

/**
 * Remember theme preferences manager
 */
@Composable
fun rememberThemePreferencesManager(): ThemePreferencesManager {
    val context = LocalContext.current
    return remember { ThemePreferencesManager(context) }
}

/**
 * Theme State
 * 
 * Manages theme state for the app.
 */
class ThemeState(
    private val preferencesManager: ThemePreferencesManager,
    private val isSystemInDarkTheme: Boolean
) {
    var themeMode by mutableStateOf(preferencesManager.getThemeMode())
        private set
    
    var useDynamicColor by mutableStateOf(preferencesManager.isDynamicColorEnabled())
        private set
    
    var useHighContrast by mutableStateOf(preferencesManager.isHighContrastEnabled())
        private set
    
    /**
     * Check if dark theme should be used
     */
    val isDarkTheme: Boolean
        get() = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme
        }
    
    /**
     * Set theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        themeMode = mode
        preferencesManager.setThemeMode(mode)
    }
    
    /**
     * Toggle theme mode
     */
    fun toggleTheme() {
        val newMode = when (themeMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
        setThemeMode(newMode)
    }
    
    /**
     * Set dynamic color preference
     */
    fun setDynamicColor(enabled: Boolean) {
        useDynamicColor = enabled
        preferencesManager.setDynamicColorEnabled(enabled)
    }
    
    /**
     * Set high contrast preference
     */
    fun setHighContrast(enabled: Boolean) {
        useHighContrast = enabled
        preferencesManager.setHighContrastEnabled(enabled)
    }
}

/**
 * Remember theme state
 */
@Composable
fun rememberThemeState(): ThemeState {
    val preferencesManager = rememberThemePreferencesManager()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    
    return remember(preferencesManager, isSystemInDarkTheme) {
        ThemeState(preferencesManager, isSystemInDarkTheme)
    }
}

/**
 * Color Utilities
 */
object ColorUtils {
    
    /**
     * Calculate contrast ratio between two colors
     */
    fun contrastRatio(foreground: Color, background: Color): Float {
        val foregroundLuminance = foreground.luminance()
        val backgroundLuminance = background.luminance()
        
        val lighter = maxOf(foregroundLuminance, backgroundLuminance)
        val darker = minOf(foregroundLuminance, backgroundLuminance)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * Check if color combination meets WCAG AA standard (4.5:1)
     */
    fun meetsWCAGAA(foreground: Color, background: Color): Boolean {
        return contrastRatio(foreground, background) >= 4.5f
    }
    
    /**
     * Check if color combination meets WCAG AAA standard (7:1)
     */
    fun meetsWCAGAAA(foreground: Color, background: Color): Boolean {
        return contrastRatio(foreground, background) >= 7.0f
    }
    
    /**
     * Darken a color by a percentage
     */
    fun darken(color: Color, percentage: Float): Color {
        val factor = 1f - percentage.coerceIn(0f, 1f)
        return Color(
            red = color.red * factor,
            green = color.green * factor,
            blue = color.blue * factor,
            alpha = color.alpha
        )
    }
    
    /**
     * Lighten a color by a percentage
     */
    fun lighten(color: Color, percentage: Float): Color {
        val factor = percentage.coerceIn(0f, 1f)
        return Color(
            red = color.red + (1f - color.red) * factor,
            green = color.green + (1f - color.green) * factor,
            blue = color.blue + (1f - color.blue) * factor,
            alpha = color.alpha
        )
    }
    
    /**
     * Adjust color opacity
     */
    fun withOpacity(color: Color, opacity: Float): Color {
        return color.copy(alpha = opacity.coerceIn(0f, 1f))
    }
}

/**
 * Calculate luminance of a color
 */
private fun Color.luminance(): Float {
    fun adjustComponent(component: Float): Float {
        return if (component <= 0.03928f) {
            component / 12.92f
        } else {
            kotlin.math.pow(((component + 0.055) / 1.055).toDouble(), 2.4).toFloat()
        }
    }
    
    val r = adjustComponent(red)
    val g = adjustComponent(green)
    val b = adjustComponent(blue)
    
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

/**
 * Theme Preview Helper
 * 
 * Provides utilities for theme preview in Android Studio.
 */
object ThemePreview {
    
    /**
     * Get preview color scheme for light theme
     */
    @Composable
    fun lightColorScheme(): ColorScheme {
        return MaterialTheme.colorScheme
    }
    
    /**
     * Get preview color scheme for dark theme
     */
    @Composable
    fun darkColorScheme(): ColorScheme {
        return MaterialTheme.colorScheme
    }
}

/**
 * Accessibility Helpers
 */
object AccessibilityUtils {
    
    /**
     * Check if device is in high contrast mode
     */
    fun isHighContrastMode(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
    
    /**
     * Get recommended minimum touch target size (48dp)
     */
    const val MIN_TOUCH_TARGET_SIZE_DP = 48
    
    /**
     * Get recommended minimum text size for accessibility
     */
    const val MIN_TEXT_SIZE_SP = 12
    
    /**
     * Check if text size is accessible
     */
    fun isTextSizeAccessible(textSizeSp: Float): Boolean {
        return textSizeSp >= MIN_TEXT_SIZE_SP
    }
}

/**
 * Semantic Color Names
 * 
 * Provides semantic color naming for better code readability.
 */
object SemanticColors {
    
    @Composable
    fun success(): Color = Color(0xFF4CAF50)
    
    @Composable
    fun warning(): Color = Color(0xFFFFC107)
    
    @Composable
    fun error(): Color = MaterialTheme.colorScheme.error
    
    @Composable
    fun info(): Color = MaterialTheme.colorScheme.primary
    
    @Composable
    fun onlineStatus(): Color = Color(0xFF4CAF50)
    
    @Composable
    fun offlineStatus(): Color = Color(0xFF9E9E9E)
    
    @Composable
    fun awayStatus(): Color = Color(0xFFFFC107)
    
    @Composable
    fun busyStatus(): Color = Color(0xFFF44336)
}

/**
 * Theme Extension Functions
 */

/**
 * Get surface color with elevation
 */
@Composable
fun ColorScheme.surfaceWithElevation(elevation: Int): Color {
    return when (elevation) {
        0 -> surface
        1 -> surfaceVariant
        2 -> surfaceVariant
        else -> surfaceVariant
    }
}

/**
 * Get appropriate text color for background
 */
@Composable
fun ColorScheme.textColorFor(backgroundColor: Color): Color {
    val contrastWithOnSurface = ColorUtils.contrastRatio(onSurface, backgroundColor)
    val contrastWithSurface = ColorUtils.contrastRatio(surface, backgroundColor)
    
    return if (contrastWithOnSurface > contrastWithSurface) {
        onSurface
    } else {
        surface
    }
}