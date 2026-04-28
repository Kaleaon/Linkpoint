package com.linkpoint.ui.components.linkpoint2.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

/**
 * Linkpoint 2.0 design tokens — derived from the design handoff in
 * design_handoff_linkpoint_v2/styles.css.
 *
 * These extend the existing [com.linkpoint.ui.theme.LinkpointColors] with the
 * additional surfaces, glass, hud and aurora tokens that the 2.0 design
 * language requires (translucent glass, aurora drift, mono coord, hud pill).
 */
@Immutable
data class L2Radii(
    val sm: Dp = 8.dp,
    val md: Dp = 14.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp,
    val pill: Dp = 100.dp,
) {
    companion object {
        val Default = L2Radii()
        val Sharp = L2Radii(sm = 0.dp, md = 0.dp, lg = 0.dp, xl = 0.dp, pill = 0.dp)
        val Soft = L2Radii(sm = 6.dp, md = 18.dp, lg = 24.dp, xl = 32.dp, pill = 100.dp)
    }
}

@Immutable
data class L2Spacing(
    val pad: Dp = 16.dp,
    val gap: Dp = 12.dp,
    val gapTight: Dp = 8.dp,
    val gapLoose: Dp = 16.dp,
    val rowMinHeight: Dp = 56.dp,
    val hitTarget: Dp = 44.dp,
) {
    companion object {
        val Compact = L2Spacing(pad = 12.dp, gap = 8.dp, gapTight = 6.dp, gapLoose = 12.dp)
        val Balanced = L2Spacing()
        val Spacious = L2Spacing(pad = 20.dp, gap = 16.dp, gapTight = 10.dp, gapLoose = 20.dp)
    }
}

@Immutable
data class L2TypeScale(
    val titleDisplay: TextUnit = 38.sp,
    val titleHero: TextUnit = 28.sp,
    val titleLarge: TextUnit = 22.sp,
    val title: TextUnit = 18.sp,
    val body: TextUnit = 14.sp,
    val bodyDim: TextUnit = 13.sp,
    val caption: TextUnit = 12.sp,
    val coord: TextUnit = 11.sp,
    val tab: TextUnit = 10.sp,
    val chip: TextUnit = 11.sp,
)

/**
 * Aurora drift layer — translucent radial gradients that animate.
 * Tokens are pulled from the active theme's primary / secondary / tertiary so
 * each theme tints its aurora differently.
 */
@Immutable
data class L2Aurora(
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val enabled: Boolean = true,
)

/**
 * Glass surface tokens. Used for floating cards, HUD overlays, chat composer,
 * tab bar background.
 */
@Immutable
data class L2Glass(
    val background: Color,
    val stroke: Color,
    val blurRadius: Dp = 20.dp,
)

/**
 * Linkpoint 2.0 token bundle.
 */
@Immutable
data class Linkpoint2Tokens(
    val radii: L2Radii,
    val spacing: L2Spacing,
    val type: L2TypeScale,
    val aurora: L2Aurora,
    val glass: L2Glass,
    val coordColor: Color,
    val onSurfaceDim: Color,
    val outlineSubtle: Color,
    val success: Color,
    val warning: Color,
    val unreadBadge: Color,
    val onUnreadBadge: Color,
    val hudBackground: Color = Color(0xAA000000),
    val hudStroke: Color = Color(0x33FFFFFF),
)

val LocalLinkpoint2Tokens = staticCompositionLocalOf {
    Linkpoint2Tokens(
        radii = L2Radii.Default,
        spacing = L2Spacing.Balanced,
        type = L2TypeScale(),
        aurora = L2Aurora(
            color1 = Color(0x386DE8FF),
            color2 = Color(0x388C7CFF),
            color3 = Color(0x287CFFD8),
        ),
        glass = L2Glass(
            background = Color(0x8C101C33),
            stroke = Color(0x14FFFFFF),
        ),
        coordColor = Color(0xFFB5C7E9),
        onSurfaceDim = Color(0xFFB5C7E9),
        outlineSubtle = Color(0xFF334364),
        success = Color(0xFF7CFFD8),
        warning = Color(0xFFFFD56D),
        unreadBadge = Color(0xFF6DE8FF),
        onUnreadBadge = Color(0xFF03212A),
    )
}

object Linkpoint2 {
    val tokens: Linkpoint2Tokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current

    val radii: L2Radii
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current.radii

    val spacing: L2Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current.spacing

    val type: L2TypeScale
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current.type

    val aurora: L2Aurora
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current.aurora

    val glass: L2Glass
        @Composable
        @ReadOnlyComposable
        get() = LocalLinkpoint2Tokens.current.glass
}
