package com.linkpoint.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * CompositionLocal for providing LinkpointColors throughout the app
 */
val LocalLinkpointColors = staticCompositionLocalOf { LinkpointColors.Default }

/**
 * CompositionLocal for providing the current ThemePack
 */
val LocalThemePack = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT }

/**
 * Linkpoint Material 3 Theme wrapper.
 * 
 * This theme system supports:
 * - Built-in theme packs from CleverFerret
 * - User-created custom themes via JSON
 * - Theme pack sharing/import/export
 * 
 * Usage:
 * ```kotlin
 * LinkpointTheme(themePack = myThemePack) {
 *     // Your Compose content
 * }
 * ```
 */
@Composable
fun LinkpointTheme(
    themePack: ThemePack? = null,
    densityMode: ThemePack.DensityMode? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember(context) { ThemeManager.getInstance(context) }
    val densitySettingsStore = remember(context) { DensitySettingsStore(context) }
    val activeTheme by themeManager.activeTheme.collectAsState()
    val persistedDensityMode by densitySettingsStore.densityMode.collectAsState(initial = ThemePack.DensityMode.BALANCED)
    val resolvedThemePack = themePack ?: activeTheme
    val resolvedDensityMode = densityMode
        ?: persistedDensityMode
        .takeIf { it != ThemePack.DensityMode.BALANCED }
        ?: resolvedThemePack.resolvedDensityDefault()
    val linkpointColors = resolvedThemePack.toComposeColors()
    val designTokens = remember(resolvedDensityMode) { linkpointDesignTokensFor(resolvedDensityMode) }
    val typographyTokens = remember(resolvedThemePack.id) {
        linkpointTypographyFor(resolvedThemePack.resolvedThemeFamily())
    }
    val shapeTokens = remember(resolvedThemePack.id, resolvedDensityMode) {
        linkpointShapesFor(
            cornerStyle = resolvedThemePack.resolvedCornerStyleProfile(),
            designTokens = designTokens
        )
    }

    // Create Material 3 color scheme from ThemePack colors
    // Support both dark and light themes based on system preference
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = linkpointColors.primary,
            onPrimary = linkpointColors.onPrimary,
            primaryContainer = linkpointColors.primaryVariant,
            onPrimaryContainer = linkpointColors.onPrimary,
            secondary = linkpointColors.secondary,
            onSecondary = linkpointColors.onSecondary,
            secondaryContainer = linkpointColors.secondary,
            onSecondaryContainer = linkpointColors.onSecondary,
            background = linkpointColors.background,
            onBackground = linkpointColors.onSurface,
            surface = linkpointColors.surface,
            onSurface = linkpointColors.onSurface,
            surfaceVariant = linkpointColors.surfaceVariant,
            onSurfaceVariant = linkpointColors.onSurfaceVariant,
            error = linkpointColors.error,
            onError = linkpointColors.onError
        )
    } else {
        // Light theme - invert some colors for better contrast
        lightColorScheme(
            primary = linkpointColors.primary,
            onPrimary = linkpointColors.onPrimary,
            primaryContainer = linkpointColors.primaryVariant,
            onPrimaryContainer = linkpointColors.onPrimary,
            secondary = linkpointColors.secondary,
            onSecondary = linkpointColors.onSecondary,
            secondaryContainer = linkpointColors.secondary,
            onSecondaryContainer = linkpointColors.onSecondary,
            background = linkpointColors.onSurface,  // Inverted for light
            onBackground = linkpointColors.background,
            surface = linkpointColors.onSurface,  // Inverted for light
            onSurface = linkpointColors.background,
            surfaceVariant = linkpointColors.onSurfaceVariant,
            onSurfaceVariant = linkpointColors.surface,
            error = linkpointColors.error,
            onError = linkpointColors.onError
        )
    }
    
    CompositionLocalProvider(
        LocalLinkpointColors provides linkpointColors,
        LocalThemePack provides resolvedThemePack,
        LocalDensityMode provides resolvedDensityMode,
        LocalDesignTokens provides designTokens,
        LocalLinkpointTypography provides typographyTokens,
        LocalLinkpointShapes provides shapeTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typographyTokens.materialTypography,
            shapes = shapeTokens.materialShapes,
            content = content
        )
    }
}

/**
 * Accessor for Linkpoint-specific colors within a Composable.
 * 
 * Usage:
 * ```kotlin
 * val colors = LinkpointTheme.colors
 * Box(modifier = Modifier.background(colors.success))
 * ```
 */
object LinkpointTheme {
    /**
     * Current Linkpoint colors from the theme
     */
    val colors: LinkpointColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpointColors.current
    
    /**
     * Current theme pack
     */
    val themePack: ThemePack
        @Composable
        @ReadOnlyComposable
        get() = LocalThemePack.current

    val density: ThemePack.DensityMode
        @Composable
        @ReadOnlyComposable
        get() = LocalDensityMode.current
}
