package com.linkpoint.ui.theme

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Represents a theme pack that can be created by users or loaded from built-in themes.
 * Theme packs are JSON-serializable for easy import/export.
 * 
 * Based on CleverFerret's theming system with metallic accent colors.
 * 
 * Note: This class uses android.graphics.Color for color parsing. While the data class
 * itself is serializable to JSON, the parseColor() method requires Android framework.
 * For cross-platform usage, consider using pure Kotlin hex parsing instead.
 */
@Serializable
data class ThemePack(
    /** Unique identifier for this theme */
    val id: String,
    
    /** Display name shown in theme picker */
    val name: String,
    
    /** Description of the theme */
    val description: String = "",
    
    /** Author of this theme pack */
    val author: String = "Linkpoint",
    
    /** Version of this theme pack */
    val version: String = "1.0.0",
    
    /** Whether this is a built-in theme (cannot be deleted) */
    val isBuiltIn: Boolean = false,
    
    /** Primary accent color (hex string like "#E5A00D") */
    val colorPrimary: String,
    
    /** Darker variant of primary color */
    val colorPrimaryDark: String,
    
    /** Color for content on primary color */
    val colorOnPrimary: String,
    
    /** Secondary accent color */
    val colorSecondary: String,
    
    /** Color for content on secondary color */
    val colorOnSecondary: String,
    
    /** Background color */
    val colorBackground: String,
    
    /** Surface color (cards, dialogs) */
    val colorSurface: String,
    
    /** Color for content on surface */
    val colorOnSurface: String,
    
    /** Variant color for secondary content on surface */
    val colorOnSurfaceVariant: String,
    
    /** Optional: Surface variant color */
    val colorSurfaceVariant: String? = null,
    
    /** Optional: Error color */
    val colorError: String = "#F44336",
    
    /** Optional: Success color */
    val colorSuccess: String = "#4CAF50",
    
    /** Optional: Warning color */
    val colorWarning: String = "#FFC107"
) {
    companion object {
        private val json = Json { 
            prettyPrint = true 
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        
        /**
         * Parse a ThemePack from JSON string
         */
        fun fromJson(jsonString: String): ThemePack? {
            return try {
                json.decodeFromString<ThemePack>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Create an example/template theme pack for users
         */
        fun createTemplate(): ThemePack {
            return ThemePack(
                id = "custom_theme_${System.currentTimeMillis()}",
                name = "My Custom Theme",
                description = "A custom theme created by me",
                author = "User",
                version = "1.0.0",
                isBuiltIn = false,
                colorPrimary = "#4A90D9",
                colorPrimaryDark = "#2E6AB0",
                colorOnPrimary = "#FFFFFF",
                colorSecondary = "#00BCD4",
                colorOnSecondary = "#000000",
                colorBackground = "#1A1A1A",
                colorSurface = "#2D2D2D",
                colorOnSurface = "#FFFFFF",
                colorOnSurfaceVariant = "#B0B0B0"
            )
        }
    }
    
    /**
     * Convert this theme pack to a JSON string
     */
    fun toJson(): String {
        return json.encodeToString(serializer(), this)
    }
    
    /**
     * Parse a hex color string to an Android color int
     */
    @ColorInt
    fun parseColor(hexColor: String): Int {
        return try {
            Color.parseColor(hexColor)
        } catch (e: Exception) {
            Color.MAGENTA // Return magenta for invalid colors (easy to spot)
        }
    }
    
    /**
     * Get the surface variant color, falling back to surface color if not specified
     */
    fun getSurfaceVariant(): String = colorSurfaceVariant ?: colorSurface
    
    /**
     * Validate that all color strings are valid hex colors
     */
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (id.isBlank()) errors.add("ID cannot be empty")
        if (name.isBlank()) errors.add("Name cannot be empty")
        
        // Validate all color fields
        val colorFields = listOf(
            "colorPrimary" to colorPrimary,
            "colorPrimaryDark" to colorPrimaryDark,
            "colorOnPrimary" to colorOnPrimary,
            "colorSecondary" to colorSecondary,
            "colorOnSecondary" to colorOnSecondary,
            "colorBackground" to colorBackground,
            "colorSurface" to colorSurface,
            "colorOnSurface" to colorOnSurface,
            "colorOnSurfaceVariant" to colorOnSurfaceVariant
        )
        
        for ((fieldName, colorValue) in colorFields) {
            if (!isValidHexColor(colorValue)) {
                errors.add("Invalid color for $fieldName: $colorValue")
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
    
    private fun isValidHexColor(color: String): Boolean {
        return try {
            Color.parseColor(color)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }
}
