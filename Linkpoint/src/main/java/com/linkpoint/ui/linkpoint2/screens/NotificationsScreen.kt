package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

enum class NotificationKind { Im, FriendRequest, Money, GroupPing, Region, Generic }

data class NotificationItem(
    val id: String,
    val kind: NotificationKind,
    val title: String,
    val body: String,
    val timestamp: String,
    val acceptable: Boolean = false,
    val mentioned: Boolean = false,
)

/**
 * Cluster H — Notifications / feed. See design/screens-states.jsx →
 * NotificationsToastScreen and design/screens-5.jsx for the inbox.
 */
@Composable
fun NotificationsScreen(
    items: List<NotificationItem>,
    onBack: () -> Unit,
    onClearAll: () -> Unit,
    onAccept: (NotificationItem) -> Unit,
    onDecline: (NotificationItem) -> Unit,
    onTap: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Notifications",
                subtitle = "${items.size} unread",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onClearAll) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear all")
                    }
                },
            )
        },
    ) { padding ->
        if (items.isEmpty()) {
            Column(
                modifier = modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = tokens.onSurfaceDim,
                        modifier = Modifier.size(36.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("All caught up", fontWeight = FontWeight.SemiBold)
                Text("Nothing new right now.", style = MaterialTheme.typography.bodySmall, color = tokens.onSurfaceDim)
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items, key = { it.id }) { n ->
                    NotificationCard(
                        item = n,
                        onAccept = { onAccept(n) },
                        onDecline = { onDecline(n) },
                        onTap = { onTap(n) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onTap: () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    val (icon, variant) = iconFor(item.kind)
    val (iconTint, iconBg) = when (variant) {
        L2ChipVariant.Success -> tokens.success to tokens.success.copy(alpha = 0.18f)
        L2ChipVariant.Warn -> tokens.warning to tokens.warning.copy(alpha = 0.18f)
        L2ChipVariant.Error -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
        L2ChipVariant.Primary, L2ChipVariant.Neutral ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    }
    L2GlassSurface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onTap),
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(tokens.radii.md))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(item.title, fontWeight = FontWeight.SemiBold)
                    if (item.mentioned) {
                        L2Chip(label = "@you", variant = L2ChipVariant.Primary)
                    }
                }
                Text(item.body, style = MaterialTheme.typography.bodySmall, color = tokens.onSurfaceDim)
                Text(item.timestamp, style = MaterialTheme.typography.labelSmall, color = tokens.onSurfaceDim)
                if (item.acceptable) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        L2FilledButton(text = "Accept", onClick = onAccept)
                        L2GhostButton(text = "Decline", onClick = onDecline)
                    }
                }
            }
        }
    }
}

private fun iconFor(kind: NotificationKind): Pair<ImageVector, L2ChipVariant> = when (kind) {
    NotificationKind.Im -> Icons.Default.Mail to L2ChipVariant.Primary
    NotificationKind.FriendRequest -> Icons.Default.PersonAdd to L2ChipVariant.Success
    NotificationKind.Money -> Icons.Default.Payments to L2ChipVariant.Success
    NotificationKind.GroupPing -> Icons.Default.Group to L2ChipVariant.Primary
    NotificationKind.Region -> Icons.Default.Refresh to L2ChipVariant.Warn
    NotificationKind.Generic -> Icons.Default.Notifications to L2ChipVariant.Neutral
}
