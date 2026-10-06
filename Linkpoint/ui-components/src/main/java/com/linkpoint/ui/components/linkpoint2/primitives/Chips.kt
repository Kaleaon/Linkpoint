package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

enum class L2ChipVariant { Neutral, Primary, Success, Warn, Error }

@Composable
fun L2Chip(
    label: String,
    modifier: Modifier = Modifier,
    variant: L2ChipVariant = L2ChipVariant.Neutral,
    onClick: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
) {
    val cs = MaterialTheme.colorScheme
    val tokens = Linkpoint2.tokens
    val (bg, fg, stroke) = when (variant) {
        L2ChipVariant.Neutral -> Triple(cs.surfaceVariant, cs.onSurfaceVariant, tokens.outlineSubtle)
        L2ChipVariant.Primary -> Triple(
            cs.primary.copy(alpha = 0.18f),
            cs.primary,
            cs.primary.copy(alpha = 0.35f),
        )
        L2ChipVariant.Success -> Triple(
            tokens.success.copy(alpha = 0.18f),
            tokens.success,
            tokens.success.copy(alpha = 0.35f),
        )
        L2ChipVariant.Warn -> Triple(
            tokens.warning.copy(alpha = 0.18f),
            tokens.warning,
            tokens.warning.copy(alpha = 0.35f),
        )
        L2ChipVariant.Error -> Triple(
            cs.error.copy(alpha = 0.18f),
            cs.error,
            cs.error.copy(alpha = 0.35f),
        )
    }
    val shape = RoundedCornerShape(Linkpoint2.radii.pill)
    Row(
        modifier = modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, stroke, shape)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (leading != null) leading()
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
        )
    }
}

/**
 * Compact unread / count badge — circular pill with bold number.
 */
@Composable
fun L2UnreadBadge(
    count: Int,
    modifier: Modifier = Modifier,
    background: Color = Linkpoint2.tokens.unreadBadge,
    onBackground: Color = Linkpoint2.tokens.onUnreadBadge,
) {
    if (count <= 0) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(background)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = onBackground,
        )
    }
}
