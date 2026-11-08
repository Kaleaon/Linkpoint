package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Placeholder world scene — gradient sky, distant mountains and ground band.
 * Real production builds replace this with the GL viewport (see
 * [com.linkpoint.ui.world.WorldViewportHost]). This implementation lets
 * design-fidelity screens (camera, low bandwidth, build tools, TP offer modal)
 * render correctly when the renderer is unavailable.
 */
@Composable
fun L2WorldSceneBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A1830),
                        Color(0xFF1D4060),
                        Color(0xFF4A7BA8),
                        Color(0xFFC8B88A),
                        Color(0xFFB89968),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                ),
            ),
    ) {
        content()
    }
}
