package com.linkpoint.ui.components.linkpoint2.fx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Aurora drift wash used behind onboarding, login and as the screen-level
 * mood layer. It draws three radial gradient blobs that slowly translate.
 *
 * The animation respects the active [com.linkpoint.ui.theme.MotionPolicy] so a
 * user with reduced motion will see a static frame.
 */
@Composable
fun AuroraBackdrop(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val aurora = Linkpoint2.aurora
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift1",
    )
    val drift2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift2",
    )
    val drift3 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift3",
    )
    val animate = enabled && aurora.enabled
    val a1 = if (animate) drift1 else 0.5f
    val a2 = if (animate) drift2 else 0.5f
    val a3 = if (animate) drift3 else 0.5f

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAuroraBlob(0.15f + 0.05f * (a1 - 0.5f), 0.10f + 0.04f * a1, 0.55f, aurora.color1)
            drawAuroraBlob(0.90f - 0.05f * (a2 - 0.5f), 0.30f - 0.04f * a2, 0.55f, aurora.color2)
            drawAuroraBlob(0.50f, 1.10f + 0.05f * (a3 - 0.5f), 0.65f, aurora.color3)
        }
    }
}

private fun DrawScope.drawAuroraBlob(
    cx: Float,
    cy: Float,
    radiusFactor: Float,
    color: Color,
) {
    if (color.alpha == 0f) return
    val r = size.minDimension * radiusFactor
    val center = Offset(size.width * cx, size.height * cy)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color, color.copy(alpha = 0f)),
            center = center,
            radius = r,
        ),
        center = center,
        radius = r,
    )
}
