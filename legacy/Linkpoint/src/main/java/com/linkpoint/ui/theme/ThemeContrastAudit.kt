package com.linkpoint.ui.theme

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Pure Kotlin contrast audit utility for validating WCAG AA text/background combinations
 * without Compose UI or Android framework runtime dependencies.
 */
object ThemeContrastAudit {
    const val MIN_TEXT_CONTRAST = 4.5f
    const val MIN_LARGE_TEXT_CONTRAST = 3.0f

    /**
     * Asserts WCAG AA contrast for key text/background combinations of a ThemePack.
     */
    fun assertTextContrast(themePack: ThemePack) {
        assertPair(
            foregroundHex = themePack.colorOnSurface,
            backgroundHex = themePack.colorSurface,
            name = "onSurface/surface",
            themeName = themePack.name,
            minContrast = MIN_TEXT_CONTRAST
        )
        assertPair(
            foregroundHex = themePack.colorOnPrimary,
            backgroundHex = themePack.colorPrimary,
            name = "onPrimary/primary",
            themeName = themePack.name,
            minContrast = MIN_LARGE_TEXT_CONTRAST
        )
    }

    /**
     * Asserts contrast ratio between foreground and background colors.
     */
    fun assertPair(
        foregroundHex: String,
        backgroundHex: String,
        name: String,
        themeName: String,
        minContrast: Float = MIN_TEXT_CONTRAST
    ) {
        val ratio = contrastRatio(foregroundHex, backgroundHex)
        check(ratio >= minContrast) {
            "Theme contrast audit failed for $themeName: $name ratio %.2f < %.1f".format(
                ratio,
                minContrast
            )
        }
    }

    /**
     * Calculates WCAG relative contrast ratio between two hex color strings.
     */
    fun contrastRatio(color1Hex: String, color2Hex: String): Float {
        val l1 = calculateLuminance(color1Hex) + 0.05f
        val l2 = calculateLuminance(color2Hex) + 0.05f
        return max(l1, l2) / min(l1, l2)
    }

    /**
     * Calculates relative luminance of a color per WCAG 2.x standard.
     */
    fun calculateLuminance(hexColor: String): Float {
        val (r, g, b) = parseRgb(hexColor)
        val rLin = linearize(r)
        val gLin = linearize(g)
        val bLin = linearize(b)
        return (0.2126f * rLin + 0.7152f * gLin + 0.0722f * bLin)
    }

    private fun linearize(colorChannel: Float): Float {
        return if (colorChannel <= 0.04045f) {
            colorChannel / 12.92f
        } else {
            ((colorChannel + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
        }
    }

    private fun parseRgb(hexColor: String): FloatArray {
        val clean = hexColor.trim().removePrefix("#").removePrefix("0x")
        val argb = when (clean.length) {
            6 -> clean.toLong(16) or 0xFF000000L
            8 -> clean.toLong(16)
            3 -> {
                val r = clean[0].toString().repeat(2)
                val g = clean[1].toString().repeat(2)
                val b = clean[2].toString().repeat(2)
                ("FF" + r + g + b).toLong(16)
            }
            else -> throw IllegalArgumentException("Invalid hex color string: $hexColor")
        }
        val r = ((argb shr 16) and 0xFF) / 255.0f
        val g = ((argb shr 8) and 0xFF) / 255.0f
        val b = (argb and 0xFF) / 255.0f
        return floatArrayOf(r, g, b)
    }
}
