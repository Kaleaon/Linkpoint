package com.linkpoint.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/**
 * Debug contrast audit for key text/background combinations.
 */
object ThemeContrastAudit {
    private const val MIN_TEXT_CONTRAST = 4.5f

    fun assertTextContrast(colorScheme: ColorScheme, themeName: String) {
        assertPair(
            foreground = colorScheme.onSurface,
            background = colorScheme.surface,
            name = "onSurface/surface",
            themeName = themeName
        )
        assertPair(
            foreground = colorScheme.onPrimary,
            background = colorScheme.primary,
            name = "onPrimary/primary",
            themeName = themeName
        )
    }

    private fun assertPair(
        foreground: Color,
        background: Color,
        name: String,
        themeName: String
    ) {
        val ratio = contrastRatio(foreground, background)
        check(ratio >= MIN_TEXT_CONTRAST) {
            "Theme contrast audit failed for $themeName: $name ratio %.2f < %.1f".format(
                ratio,
                MIN_TEXT_CONTRAST
            )
        }
    }

    private fun contrastRatio(foreground: Color, background: Color): Float {
        val l1 = foreground.luminance() + 0.05f
        val l2 = background.luminance() + 0.05f
        return max(l1, l2) / min(l1, l2)
    }
}
