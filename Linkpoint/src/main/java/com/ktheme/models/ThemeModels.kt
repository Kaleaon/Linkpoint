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

data class Theme(
    val metadata: ThemeMetadata,
    val darkMode: Boolean,
    val colorScheme: ColorScheme
)
