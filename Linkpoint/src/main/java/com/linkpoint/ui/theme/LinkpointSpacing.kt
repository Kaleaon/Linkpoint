package com.linkpoint.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class LinkpointSpacing(
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp
)

enum class DensityProfile {
    COMPACT,
    STANDARD,
    COMFORTABLE
}

fun ThemePack.resolvedDensityProfile(): DensityProfile {
    if (densityProfile != null) return densityProfile
    val kthemeDensity = ktheme?.adaptation?.layout?.density?.lowercase()
    return when (kthemeDensity) {
        "compact" -> DensityProfile.COMPACT
        "comfortable", "spacious" -> DensityProfile.COMFORTABLE
        "standard" -> DensityProfile.STANDARD
        else -> DensityProfile.STANDARD
    }
}

fun ThemePack.toSpacing(): LinkpointSpacing {
    val scale = ktheme?.adaptation?.layout?.spacingScale ?: 1.0f
    return when (resolvedDensityProfile()) {
        DensityProfile.COMPACT -> LinkpointSpacing(
            xxs = (2 * scale).dp,
            xs = (4 * scale).dp,
            sm = (6 * scale).dp,
            md = (10 * scale).dp,
            lg = (14 * scale).dp,
            xl = (18 * scale).dp,
            xxl = (24 * scale).dp
        )

        DensityProfile.STANDARD -> LinkpointSpacing(
            xxs = (4 * scale).dp,
            xs = (8 * scale).dp,
            sm = (12 * scale).dp,
            md = (16 * scale).dp,
            lg = (20 * scale).dp,
            xl = (24 * scale).dp,
            xxl = (32 * scale).dp
        )

        DensityProfile.COMFORTABLE -> LinkpointSpacing(
            xxs = (6 * scale).dp,
            xs = (10 * scale).dp,
            sm = (14 * scale).dp,
            md = (18 * scale).dp,
            lg = (24 * scale).dp,
            xl = (30 * scale).dp,
            xxl = (40 * scale).dp
        )
    }
}

val LocalLinkpointSpacing = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT.toSpacing() }
