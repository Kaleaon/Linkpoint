package com.linkpoint.ui.components.linkpoint2.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * Wrap a Composable subtree in Linkpoint 2.0 tokens. The token bundle is
 * derived from the active [MaterialTheme.colorScheme] so theme switching
 * (Aurora / Navy Gold / LCARS / Metro …) automatically retints aurora,
 * glass and coord text.
 *
 * Place this just inside [com.linkpoint.ui.theme.LinkpointTheme] or directly
 * around any 2.0 screen root.
 */
@Composable
fun ProvideLinkpoint2Tokens(
    radii: L2Radii = L2Radii.Default,
    spacing: L2Spacing = L2Spacing.Balanced,
    content: @Composable () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tokens = remember(cs, radii, spacing) {
        Linkpoint2Tokens(
            radii = radii,
            spacing = spacing,
            type = L2TypeScale(),
            aurora = L2Aurora(
                color1 = cs.primary.copy(alpha = 0.22f),
                color2 = cs.secondary.copy(alpha = 0.22f),
                color3 = cs.tertiary.copy(alpha = 0.16f),
            ),
            glass = L2Glass(
                background = cs.surface.copy(alpha = 0.55f),
                stroke = cs.onSurface.copy(alpha = 0.10f),
            ),
            coordColor = cs.onSurfaceVariant,
            onSurfaceDim = cs.onSurfaceVariant,
            outlineSubtle = cs.outlineVariant,
            success = Color(0xFF7CFFD8),
            warning = Color(0xFFFFD56D),
            unreadBadge = cs.primary,
            onUnreadBadge = cs.onPrimary,
        )
    }
    CompositionLocalProvider(LocalLinkpoint2Tokens provides tokens, content = content)
}
