package com.linkpoint.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
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

@Immutable
data class LinkpointRadii(
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val pill: Dp
)

@Immutable
data class LinkpointElevation(
    val low: Dp,
    val mid: Dp,
    val high: Dp
)

@Immutable
data class LinkpointMotion(
    val shortMs: Int,
    val mediumMs: Int,
    val longMs: Int
)

@Immutable
data class LinkpointDesignTokens(
    val spacing: LinkpointSpacing,
    val radii: LinkpointRadii,
    val elevation: LinkpointElevation,
    val motion: LinkpointMotion
)

val LocalDesignTokens = staticCompositionLocalOf { linkpointDesignTokensFor(ThemePack.DensityMode.BALANCED) }

fun linkpointDesignTokensFor(density: ThemePack.DensityMode): LinkpointDesignTokens {
    val spacing = when (density) {
        ThemePack.DensityMode.COMPACT -> LinkpointSpacing(2.dp, 4.dp, 6.dp, 10.dp, 14.dp, 18.dp, 24.dp)
        ThemePack.DensityMode.BALANCED -> LinkpointSpacing(4.dp, 8.dp, 12.dp, 16.dp, 20.dp, 24.dp, 32.dp)
        ThemePack.DensityMode.SPACIOUS -> LinkpointSpacing(6.dp, 10.dp, 14.dp, 20.dp, 26.dp, 32.dp, 40.dp)
    }

    val radii = when (density) {
        ThemePack.DensityMode.COMPACT -> LinkpointRadii(4.dp, 8.dp, 12.dp, 16.dp, 999.dp)
        ThemePack.DensityMode.BALANCED -> LinkpointRadii(6.dp, 12.dp, 16.dp, 20.dp, 999.dp)
        ThemePack.DensityMode.SPACIOUS -> LinkpointRadii(8.dp, 14.dp, 20.dp, 24.dp, 999.dp)
    }

    return LinkpointDesignTokens(
        spacing = spacing,
        radii = radii,
        elevation = LinkpointElevation(low = 1.dp, mid = 4.dp, high = 8.dp),
        motion = LinkpointMotion(shortMs = 120, mediumMs = 220, longMs = 320)
    )
}

object LinkpointTokens {
    val design: LinkpointDesignTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalDesignTokens.current
}
