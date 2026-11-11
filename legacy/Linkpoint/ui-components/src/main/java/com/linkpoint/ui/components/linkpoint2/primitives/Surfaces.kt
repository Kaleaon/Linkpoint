package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Glass surface — translucent floating panel, the workhorse of the 2.0 layout.
 * Used for HUD overlays, login form card, chat composer, tab bar, modals.
 *
 * The Compose runtime does not have a real backdrop blur (`Modifier.blur` only
 * blurs the wrapped content), so we simulate frosted glass with a translucent
 * color + subtle stroke. Behind a busy background (e.g. World view) this still
 * reads as "glass."
 */
@Composable
fun L2GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    backgroundOverride: Color? = null,
    content: @Composable () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    val resolvedShape = shape ?: RoundedCornerShape(tokens.radii.lg)
    Box(
        modifier = modifier
            .clip(resolvedShape)
            .background(backgroundOverride ?: tokens.glass.background)
            .border(1.dp, tokens.glass.stroke, resolvedShape)
            .padding(contentPadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            content()
        }
    }
}

/**
 * Solid card surface — opaque, sits on the world surface.
 */
@Composable
fun L2Card(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    elevated: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tokens = Linkpoint2.tokens
    val resolvedShape = shape ?: RoundedCornerShape(tokens.radii.lg)
    Box(
        modifier = modifier
            .clip(resolvedShape)
            .background(cs.surface)
            .border(1.dp, cs.outlineVariant, resolvedShape)
            .padding(contentPadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides cs.onSurface) {
            content()
        }
    }
}

/**
 * In-world translucent HUD pill — used for region/coord/fps tags layered
 * over the GL viewport.
 */
@Composable
fun L2HudTag(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
    radius: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .background(tokens.hudBackground)
            .border(1.dp, tokens.hudStroke, RoundedCornerShape(radius))
            .padding(contentPadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            content()
        }
    }
}
