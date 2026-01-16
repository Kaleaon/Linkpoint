package com.linkpoint.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Extension function to convert a ThemePack to Compose Colors.
 * This bridges the JSON-serializable ThemePack with Compose's Color system.
 */
fun ThemePack.toComposeColors(): LinkpointColors {
    return LinkpointColors(
        primary = Color(parseColor(colorPrimary)),
        primaryVariant = Color(parseColor(colorPrimaryDark)),
        onPrimary = Color(parseColor(colorOnPrimary)),
        secondary = Color(parseColor(colorSecondary)),
        onSecondary = Color(parseColor(colorOnSecondary)),
        background = Color(parseColor(colorBackground)),
        surface = Color(parseColor(colorSurface)),
        surfaceVariant = Color(parseColor(getSurfaceVariant())),
        onSurface = Color(parseColor(colorOnSurface)),
        onSurfaceVariant = Color(parseColor(colorOnSurfaceVariant)),
        error = Color(parseColor(colorError)),
        onError = Color.White,
        success = Color(parseColor(colorSuccess)),
        warning = Color(parseColor(colorWarning))
    )
}

/**
 * Linkpoint color scheme that can be created from a ThemePack.
 * This is used by LinkpointTheme to provide colors to Compose components.
 */
data class LinkpointColors(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val warning: Color
) {
    companion object {
        /**
         * Default Linkpoint colors (blue theme)
         */
        val Default = BuiltInThemes.LINKPOINT_DEFAULT.toComposeColors()
    }
}
