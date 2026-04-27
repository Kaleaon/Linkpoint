package com.linkpoint.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

fun ThemePack.toMaterialTypography(): Typography {
    val scale = when (resolvedDensityProfile()) {
        DensityProfile.COMPACT -> 0.95f
        DensityProfile.STANDARD -> 1f
        DensityProfile.COMFORTABLE -> 1.08f
    }

    fun scaled(size: Int) = (size * scale).sp

    return Typography(
        displayLarge = TextStyle(fontSize = scaled(57), lineHeight = scaled(64)),
        displayMedium = TextStyle(fontSize = scaled(45), lineHeight = scaled(52)),
        displaySmall = TextStyle(fontSize = scaled(36), lineHeight = scaled(44)),
        headlineLarge = TextStyle(fontSize = scaled(32), lineHeight = scaled(40)),
        headlineMedium = TextStyle(fontSize = scaled(28), lineHeight = scaled(36)),
        headlineSmall = TextStyle(fontSize = scaled(24), lineHeight = scaled(32)),
        titleLarge = TextStyle(fontSize = scaled(22), lineHeight = scaled(28)),
        titleMedium = TextStyle(fontSize = scaled(16), lineHeight = scaled(24)),
        titleSmall = TextStyle(fontSize = scaled(14), lineHeight = scaled(20)),
        bodyLarge = TextStyle(fontSize = scaled(16), lineHeight = scaled(24)),
        bodyMedium = TextStyle(fontSize = scaled(14), lineHeight = scaled(20)),
        bodySmall = TextStyle(fontSize = scaled(12), lineHeight = scaled(16)),
        labelLarge = TextStyle(fontSize = scaled(14), lineHeight = scaled(20)),
        labelMedium = TextStyle(fontSize = scaled(12), lineHeight = scaled(16)),
        labelSmall = TextStyle(fontSize = scaled(11), lineHeight = scaled(16))
    )
}

val LocalLinkpointTypography = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT.toMaterialTypography() }
