package com.linkpoint.ui.theme

import com.ktheme.models.ColorScheme
import com.ktheme.models.Theme
import com.ktheme.models.ThemeMetadata

/**
 * Map a Ktheme theme into Linkpoint's ThemePack format.
 */
fun Theme.toThemePack(): ThemePack {
    val densityProfile = metadata.tags.firstNotNullOfOrNull { tag ->
        tag.removePrefix("density:").takeIf { it != tag }?.let { runCatching { DensityProfile.valueOf(it.uppercase()) }.getOrNull() }
    }
    val cornerProfile = metadata.tags.firstNotNullOfOrNull { tag ->
        tag.removePrefix("corner:").takeIf { it != tag }?.let { runCatching { CornerProfile.valueOf(it.uppercase()) }.getOrNull() }
    }
    val motionProfile = metadata.tags.firstNotNullOfOrNull { tag ->
        tag.removePrefix("motion:").takeIf { it != tag }?.let { runCatching { MotionProfile.valueOf(it.uppercase()) }.getOrNull() }
    }

    return ThemePack(
        id = metadata.id,
        name = metadata.name,
        description = metadata.description,
        author = metadata.author,
        version = metadata.version,
        isBuiltIn = false,
        colorPrimary = colorScheme.primary,
        colorPrimaryDark = colorScheme.primaryContainer,
        colorOnPrimary = colorScheme.onPrimary,
        colorSecondary = colorScheme.secondary,
        colorOnSecondary = colorScheme.onSecondary,
        colorBackground = colorScheme.background,
        colorSurface = colorScheme.surface,
        colorOnSurface = colorScheme.onSurface,
        colorOnSurfaceVariant = colorScheme.onSurfaceVariant,
        colorSurfaceVariant = colorScheme.surfaceVariant,
        colorError = colorScheme.error,
        densityProfile = densityProfile,
        cornerProfile = cornerProfile,
        motionProfile = motionProfile
    )
}

/**
 * Map a Linkpoint theme into Ktheme's Theme model for KthemeAPI sharing.
 */
fun ThemePack.toKthemeTheme(): Theme {
    val now = System.currentTimeMillis().toString()
    return Theme(
        metadata = ThemeMetadata(
            id = id,
            name = name,
            description = description.ifBlank { "Linkpoint theme" },
            author = author,
            version = version,
            tags = buildList {
                add("linkpoint")
                densityProfile?.let { add("density:${it.name.lowercase()}") }
                cornerProfile?.let { add("corner:${it.name.lowercase()}") }
                motionProfile?.let { add("motion:${it.name.lowercase()}") }
            },
            createdAt = now,
            updatedAt = now
        ),
        darkMode = true,
        colorScheme = ColorScheme(
            primary = colorPrimary,
            onPrimary = colorOnPrimary,
            primaryContainer = colorPrimaryDark,
            onPrimaryContainer = colorOnPrimary,
            secondary = colorSecondary,
            onSecondary = colorOnSecondary,
            secondaryContainer = colorSecondary,
            onSecondaryContainer = colorOnSecondary,
            tertiary = colorSecondary,
            onTertiary = colorOnSecondary,
            tertiaryContainer = colorSecondary,
            onTertiaryContainer = colorOnSecondary,
            error = colorError,
            onError = "#FFFFFF",
            errorContainer = colorError,
            onErrorContainer = "#FFFFFF",
            background = colorBackground,
            onBackground = colorOnSurface,
            surface = colorSurface,
            onSurface = colorOnSurface,
            surfaceVariant = getSurfaceVariant(),
            onSurfaceVariant = colorOnSurfaceVariant,
            outline = colorOnSurfaceVariant,
            outlineVariant = colorOnSurfaceVariant,
            scrim = "#000000",
            inverseSurface = colorOnSurface,
            inverseOnSurface = colorSurface,
            inversePrimary = colorPrimary
        )
    )
}
