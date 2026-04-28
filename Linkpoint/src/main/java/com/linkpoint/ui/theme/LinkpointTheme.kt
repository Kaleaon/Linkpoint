package com.linkpoint.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.linkpoint.BuildConfig

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember(context) { ThemeManager.getInstance(context) }
    val activeTheme by themeManager.activeTheme.collectAsState()
    val resolvedThemePack = themePack ?: activeTheme
    val linkpointColors = resolvedThemePack.toComposeColors()
    val motionPolicy = remember(context) {
        MotionPolicy(readSystemAnimatorDurationScale(context))
    }
    val typography = resolvedThemePack.toMaterialTypography()
    val shapes = resolvedThemePack.toMaterialShapes()
    val spacing = resolvedThemePack.toSpacing()
    val motion = resolvedThemePack.toMotion()
    
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
    
    if (BuildConfig.DEBUG) {
        LaunchedEffect(colorScheme) {
            ThemeContrastAudit.assertTextContrast(colorScheme, resolvedThemePack.name)
        }
    }

    CompositionLocalProvider(
        LocalLinkpointColors provides linkpointColors,
        LocalThemePack provides resolvedThemePack,
        LocalMotionPolicy provides motionPolicy,
        LocalLinkpointTypography provides typography,
        LocalLinkpointShapes provides shapes,
        LocalLinkpointSpacing provides spacing,
        LocalLinkpointMotion provides motion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

private fun ThemePack.toMaterialColorScheme(
    linkpointColors: LinkpointColors,
    darkTheme: Boolean
) = if (darkTheme) {
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
    lightColorScheme(
        primary = linkpointColors.primary,
        onPrimary = linkpointColors.onPrimary,
        primaryContainer = linkpointColors.primaryVariant,
        onPrimaryContainer = linkpointColors.onPrimary,
        secondary = linkpointColors.secondary,
        onSecondary = linkpointColors.onSecondary,
        secondaryContainer = linkpointColors.secondary,
        onSecondaryContainer = linkpointColors.onSecondary,
        background = linkpointColors.onSurface,
        onBackground = linkpointColors.background,
        surface = linkpointColors.onSurface,
        onSurface = linkpointColors.background,
        surfaceVariant = linkpointColors.onSurfaceVariant,
        onSurfaceVariant = linkpointColors.surface,
        error = linkpointColors.error,
        onError = linkpointColors.onError
    )
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

    val spacing: LinkpointSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpointSpacing.current

    val motion: LinkpointMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpointMotion.current

    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpointTypography.current

    val shapes: androidx.compose.material3.Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpointShapes.current
}
