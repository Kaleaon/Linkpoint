package com.linkpoint.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

@Immutable
data class LinkpointTypographyTokens(
    val materialTypography: Typography,
    val coordinateText: TextStyle,
    val lindenDollarText: TextStyle
)

val LocalLinkpointTypography = staticCompositionLocalOf {
    linkpointTypographyFor(ThemePack.ThemeFamily.DEFAULT)
}

fun linkpointTypographyFor(family: ThemePack.ThemeFamily): LinkpointTypographyTokens {
    val baseFamily = when (family) {
        ThemePack.ThemeFamily.CLASSIC -> FontFamily.Serif
        ThemePack.ThemeFamily.ELEGANT -> FontFamily.Serif
        ThemePack.ThemeFamily.MODERN -> FontFamily.SansSerif
        ThemePack.ThemeFamily.INDUSTRIAL -> FontFamily.SansSerif
        ThemePack.ThemeFamily.DEFAULT -> FontFamily.SansSerif
    }

    val typography = Typography().run {
        copy(
            displayLarge = displayLarge.copy(fontFamily = baseFamily),
            displayMedium = displayMedium.copy(fontFamily = baseFamily),
            titleLarge = titleLarge.copy(fontFamily = baseFamily),
            titleMedium = titleMedium.copy(fontFamily = baseFamily),
            bodyLarge = bodyLarge.copy(fontFamily = baseFamily),
            bodyMedium = bodyMedium.copy(fontFamily = baseFamily),
            labelLarge = labelLarge.copy(fontFamily = baseFamily)
        )
    }

    val mono = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
    return LinkpointTypographyTokens(
        materialTypography = typography,
        coordinateText = mono,
        lindenDollarText = mono
    )
}
