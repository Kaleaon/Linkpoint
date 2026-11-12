package com.ktheme.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThemeMetadata(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val version: String = "1.0.0",
    val tags: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class ColorScheme(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String = primary,
    val onPrimaryContainer: String = onPrimary,
    val secondary: String = primary,
    val onSecondary: String = onPrimary,
    val secondaryContainer: String = secondary,
    val onSecondaryContainer: String = onSecondary,
    val tertiary: String = secondary,
    val onTertiary: String = onSecondary,
    val tertiaryContainer: String = tertiary,
    val onTertiaryContainer: String = onTertiary,
    val error: String = "#F44336",
    val onError: String = "#FFFFFF",
    val errorContainer: String = error,
    val onErrorContainer: String = onError,
    val background: String = "#121212",
    val onBackground: String = "#FFFFFF",
    val surface: String = "#1E1E1E",
    val onSurface: String = "#FFFFFF",
    val surfaceVariant: String = surface,
    val onSurfaceVariant: String = onSurface,
    val outline: String = onSurfaceVariant,
    val outlineVariant: String = outline,
    val scrim: String = "#000000",
    val inverseSurface: String = onSurface,
    val inverseOnSurface: String = surface,
    val inversePrimary: String = primary,
    val axisX: String = "#FF6B6B",
    val axisY: String = "#7CFFD8",
    val axisZ: String = "#6DE8FF"
)

@Serializable
enum class LayoutStructure(
    val id: String,
    val displayName: String
) {
    @SerialName("metro") METRO("metro", "Metro"),
    @SerialName("lcars") LCARS("lcars", "LCARS"),
    @SerialName("frutiger_aero") FRUTIGER_AERO("frutiger_aero", "Frutiger Aero"),
    @SerialName("art_deco") ART_DECO("art_deco", "Art Deco"),
    @SerialName("terminal") TERMINAL("terminal", "Terminal"),
    @SerialName("modern_glass") MODERN_GLASS("modern_glass", "Modern Glass"),
    @SerialName("material3") MATERIAL3("material3", "Material3"),
    @SerialName("cyberpunk") CYBERPUNK("cyberpunk", "Cyberpunk");

    companion object {
        fun fromId(id: String): LayoutStructure {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
                ?: MATERIAL3
        }
    }
}

@Serializable
data class LayoutAdaptation(
    val density: String? = null,
    val spacingScale: Float = 1.0f
)

@Serializable
data class Adaptation(
    val layout: LayoutAdaptation? = null
)

@Serializable
data class Theme(
    val metadata: ThemeMetadata,
    val darkMode: Boolean = true,
    val colorScheme: ColorScheme,
    val layoutStructure: LayoutStructure = LayoutStructure.MATERIAL3,
    val adaptation: Adaptation? = null
)

