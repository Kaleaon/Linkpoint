package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private data class AvPalette(val bg: List<Color>, val head: Color, val body: Color)

private val PALETTES = listOf(
    AvPalette(listOf(Color(0xFF6DE8FF), Color(0xFF8C7CFF)), Color(0xFFFFE9C9), Color(0xFF2A6F85)),
    AvPalette(listOf(Color(0xFFFF8FB1), Color(0xFF8C7CFF)), Color(0xFFF5D0C5), Color(0xFF7A2454)),
    AvPalette(listOf(Color(0xFF7CFFD8), Color(0xFF39B6F0)), Color(0xFFD7B591), Color(0xFF0A6FA0)),
    AvPalette(listOf(Color(0xFFFFD56D), Color(0xFFFF8FB1)), Color(0xFFE8C39A), Color(0xFF856D34)),
    AvPalette(listOf(Color(0xFFA073FF), Color(0xFF39B6F0)), Color(0xFFD9A574), Color(0xFF3B1E66)),
    AvPalette(listOf(Color(0xFF79D87E), Color(0xFF39B6F0)), Color(0xFFFFE2C2), Color(0xFF1F5D33)),
    AvPalette(listOf(Color(0xFFFF6B6B), Color(0xFFFFD56D)), Color(0xFFC9876B), Color(0xFF931C29)),
    AvPalette(listOf(Color(0xFF39B6F0), Color(0xFF0A6FA0)), Color(0xFFF2C098), Color(0xFF0A2E47)),
)

internal fun hashName(name: String): Int {
    var h = 0
    for (ch in name) {
        h = (h * 31 + ch.code) and 0x7FFFFFFF
    }
    return abs(h)
}

enum class AvatarSize(val dp: Dp) {
    XS(20.dp),
    SM(28.dp),
    MD(40.dp),
    LG(64.dp),
    XL(96.dp),
    XXL(128.dp),
}

/**
 * Linkpoint 2.0 generative avatar identicon. Hashes [name] to a deterministic
 * palette + body variant matching design/primitives.jsx.
 */
@Composable
fun L2Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MD,
    online: Boolean = false,
    shape: Shape = CircleShape,
) {
    val palette = remember(name) { PALETTES[hashName(name) % PALETTES.size] }
    val variant = remember(name) { hashName(name + "v") % 3 }
    val bgBrush = remember(palette) {
        Brush.linearGradient(
            colors = palette.bg,
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        )
    }
    val total = size.dp

    Box(
        modifier = modifier
            .size(total)
            .clip(shape)
            .background(bgBrush),
    ) {
        // Head — circle ~38% of avatar, sitting around 22% from the top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = total * (if (variant == 1) 0.18f else 0.22f))
                .size(total * 0.40f)
                .clip(CircleShape)
                .background(palette.head),
        )
        // Body — large circle peeking out of the bottom (shoulders)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = total * 0.20f)
                .size(total * 0.95f)
                .clip(CircleShape)
                .background(palette.body.copy(alpha = 0.9f)),
        )
        // Hair / hat suggestion
        when (variant) {
            0 -> Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = total * 0.13f)
                    .size(width = total * 0.46f, height = total * 0.20f)
                    .clip(domeShape)
                    .background(palette.body),
            )
            2 -> Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = total * 0.12f)
                    .size(width = total * 0.52f, height = total * 0.14f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.body),
            )
        }

        if (online) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(total * 0.28f)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
            )
        }
    }
}

private val domeShape: Shape = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(0f, size.height / 2f)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false,
    )
    lineTo(size.width, size.height)
    close()
}
