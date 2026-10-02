package com.linkpoint.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.ktheme.models.ColorScheme as KthemeColorScheme

/**
 * Safely parse a hex color string to Compose Color.
 */
fun parseHexColor(hexColor: String, fallback: Color = Color.Magenta): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Throwable) {
        try {
            val cleanHex = hexColor.removePrefix("#")
            val colorLong = when (cleanHex.length) {
                6 -> 0xFF000000 or cleanHex.toLong(16)
                8 -> cleanHex.toLong(16)
                3 -> {
                    val r = cleanHex[0].toString().repeat(2)
                    val g = cleanHex[1].toString().repeat(2)
                    val b = cleanHex[2].toString().repeat(2)
                    0xFF000000 or "$r$g$b".toLong(16)
                }
                else -> return fallback
            }
            Color(colorLong.toInt())
        } catch (ex: Throwable) {
            fallback
        }
    }
}

/**
 * Convert canonical Ktheme ColorScheme into Compose Material 3 ColorScheme.
 * Preserves all 29 Material 3 color tokens (tertiary, outline, scrim, container variants, etc.) without truncation.
 */
fun KthemeColorScheme.toMaterial3ColorScheme(): ColorScheme {
    return ColorScheme(
        primary = parseHexColor(primary),
        onPrimary = parseHexColor(onPrimary),
        primaryContainer = parseHexColor(primaryContainer),
        onPrimaryContainer = parseHexColor(onPrimaryContainer),
        inversePrimary = parseHexColor(inversePrimary),
        secondary = parseHexColor(secondary),
        onSecondary = parseHexColor(onSecondary),
        secondaryContainer = parseHexColor(secondaryContainer),
        onSecondaryContainer = parseHexColor(onSecondaryContainer),
        tertiary = parseHexColor(tertiary),
        onTertiary = parseHexColor(onTertiary),
        tertiaryContainer = parseHexColor(tertiaryContainer),
        onTertiaryContainer = parseHexColor(onTertiaryContainer),
        background = parseHexColor(background),
        onBackground = parseHexColor(onBackground),
        surface = parseHexColor(surface),
        onSurface = parseHexColor(onSurface),
        surfaceVariant = parseHexColor(surfaceVariant),
        onSurfaceVariant = parseHexColor(onSurfaceVariant),
        surfaceTint = parseHexColor(primary),
        inverseSurface = parseHexColor(inverseSurface),
        inverseOnSurface = parseHexColor(inverseOnSurface),
        error = parseHexColor(error),
        onError = parseHexColor(onError),
        errorContainer = parseHexColor(errorContainer),
        onErrorContainer = parseHexColor(onErrorContainer),
        outline = parseHexColor(outline),
        outlineVariant = parseHexColor(outlineVariant),
        scrim = parseHexColor(scrim)
    )
}

/**
 * Convert ThemePack into Compose Material 3 ColorScheme.
 */
fun ThemePack.toMaterial3ColorScheme(darkTheme: Boolean = true): ColorScheme {
    val themeModel = toKthemeTheme()
    return themeModel.colorScheme.toMaterial3ColorScheme()
}

/**
 * Extension function to convert a ThemePack to Compose Colors.
 * This bridges the JSON-serializable ThemePack with Compose's Color system.
 */
fun ThemePack.toComposeColors(): LinkpointColors {
    return LinkpointColors(
        primary = parseHexColor(colorPrimary),
        primaryVariant = parseHexColor(colorPrimaryDark),
        onPrimary = parseHexColor(colorOnPrimary),
        secondary = parseHexColor(colorSecondary),
        onSecondary = parseHexColor(colorOnSecondary),
        background = parseHexColor(colorBackground),
        surface = parseHexColor(colorSurface),
        surfaceVariant = parseHexColor(getSurfaceVariant()),
        onSurface = parseHexColor(colorOnSurface),
        onSurfaceVariant = parseHexColor(colorOnSurfaceVariant),
        error = parseHexColor(colorError),
        onError = Color.White,
        success = parseHexColor(colorSuccess),
        warning = parseHexColor(colorWarning)
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
