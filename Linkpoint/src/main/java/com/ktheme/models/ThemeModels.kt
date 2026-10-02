package com.ktheme.models

data class ThemeMetadata(
    val id: String,
    val name: String,
    val description: String,
    val author: String,
    val version: String,
    val tags: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ColorScheme(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String,
    val onPrimaryContainer: String,
    val secondary: String,
    val onSecondary: String,
    val secondaryContainer: String,
    val onSecondaryContainer: String,
    val tertiary: String,
    val onTertiary: String,
    val tertiaryContainer: String,
    val onTertiaryContainer: String,
    val error: String,
    val onError: String,
    val errorContainer: String,
    val onErrorContainer: String,
    val background: String,
    val onBackground: String,
    val surface: String,
    val onSurface: String,
    val surfaceVariant: String,
    val onSurfaceVariant: String,
    val outline: String,
    val outlineVariant: String,
    val scrim: String,
    val inverseSurface: String,
    val inverseOnSurface: String,
    val inversePrimary: String
)

enum class LayoutStructure(
    val id: String,
    val displayName: String
) {
    METRO("metro", "Metro"),
    LCARS("lcars", "LCARS"),
    FRUTIGER_AERO("frutiger_aero", "Frutiger Aero"),
    ART_DECO("art_deco", "Art Deco"),
    TERMINAL("terminal", "Terminal"),
    MODERN_GLASS("modern_glass", "Modern Glass"),
    MATERIAL3("material3", "Material3"),
    CYBERPUNK("cyberpunk", "Cyberpunk");

    companion object {
        fun fromId(id: String): LayoutStructure {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
                ?: MATERIAL3
        }
    }
}

data class Theme(
    val metadata: ThemeMetadata,
    val darkMode: Boolean,
    val colorScheme: ColorScheme,
    val layoutStructure: LayoutStructure = LayoutStructure.MATERIAL3
)
