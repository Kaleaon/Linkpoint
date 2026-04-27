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

fun ThemePack.resolvedDensityProfile(): DensityProfile = densityProfile ?: DensityProfile.STANDARD

fun ThemePack.toSpacing(): LinkpointSpacing {
    return when (resolvedDensityProfile()) {
        DensityProfile.COMPACT -> LinkpointSpacing(
            xxs = 2.dp,
            xs = 4.dp,
            sm = 6.dp,
            md = 10.dp,
            lg = 14.dp,
            xl = 18.dp,
            xxl = 24.dp
        )

        DensityProfile.STANDARD -> LinkpointSpacing(
            xxs = 4.dp,
            xs = 8.dp,
            sm = 12.dp,
            md = 16.dp,
            lg = 20.dp,
            xl = 24.dp,
            xxl = 32.dp
        )

        DensityProfile.COMFORTABLE -> LinkpointSpacing(
            xxs = 6.dp,
            xs = 10.dp,
            sm = 14.dp,
            md = 18.dp,
            lg = 24.dp,
            xl = 30.dp,
            xxl = 40.dp
        )
    }
}

val LocalLinkpointSpacing = staticCompositionLocalOf { BuiltInThemes.LINKPOINT_DEFAULT.toSpacing() }
