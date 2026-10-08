package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Linkpoint 2.0 top app bar — sticky, 56dp, pads for status bar handled by
 * the parent Scaffold.
 */
@Composable
fun L2TopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leading != null) leading()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                CompositionLocalProvider(LocalContentColor provides Linkpoint2.tokens.onSurfaceDim) {
                    subtitle()
                }
            }
        }
        if (trailing != null) trailing()
    }
}

/**
 * Convenience overload taking a plain subtitle string.
 */
@Composable
fun L2TopBar(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leading != null) leading()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Linkpoint2.tokens.onSurfaceDim,
                )
            }
        }
        if (trailing != null) trailing()
    }
}

/**
 * 5-tab bottom navigation matching the design's TabBar component.
 * The active tab gets a pill-shaped highlight under its icon.
 */
data class L2Tab(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun L2TabBar(
    tabs: List<L2Tab>,
    activeId: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
) {
    val tokens = Linkpoint2.tokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(tokens.glass.background)
            .border(width = 1.dp, color = tokens.glass.stroke, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        tabs.forEach { tab ->
            val active = tab.id == activeId
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Linkpoint2.radii.md))
                    .clickable { onSelect(tab.id) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .widthIn(min = 28.dp)
                        .let {
                            if (active) it
                                .width(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
                            else it
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (active) MaterialTheme.colorScheme.primary else tokens.onSurfaceDim,
                    )
                }
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (active) MaterialTheme.colorScheme.primary else tokens.onSurfaceDim,
                )
            }
        }
    }
}

/**
 * Circular icon button matching the JSX IconBtn — 40dp hit target.
 */
@Composable
fun L2IconBtn(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    backgroundOverride: Color? = null,
) {
    val mod = modifier
        .size(40.dp)
        .clip(CircleShape)
        .let { if (backgroundOverride != null) it.background(backgroundOverride) else it }
    IconButton(onClick = onClick, modifier = mod) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
fun L2NavPill(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
        )
    }
}

/**
 * Convenience: a vertical 2-line subtitle for the TopBar that places a leading
 * online dot before plain text.
 */
@Composable
fun L2TopBarPresenceSubtitle(
    online: Boolean,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (online) Linkpoint2.tokens.success else Linkpoint2.tokens.onSurfaceDim,
                ),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Linkpoint2.tokens.onSurfaceDim,
        )
    }
    Spacer(Modifier.height(0.dp))
}
