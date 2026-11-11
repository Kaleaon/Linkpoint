package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Cluster — Empty state reference. Contains the canonical 4 patterns
 * documented in design/screens-extra.jsx → EmptyStatesScreen, exposed both
 * as standalone Composables and as a stacked reference screen.
 */
@Composable
fun EmptyStatePattern(
    icon: ImageVector,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
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
                imageVector = icon,
                contentDescription = null,
                tint = tokens.onSurfaceDim,
                modifier = Modifier.size(36.dp),
            )
        }
        Text(
            title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            body,
            style = MaterialTheme.typography.bodySmall,
            color = tokens.onSurfaceDim,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (actionLabel != null && onAction != null) {
            L2FilledButton(text = actionLabel, onClick = onAction, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun EmptyStatesReferenceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Empty states",
                subtitle = "Reference",
                leading = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EmptyStatePattern(
                icon = Icons.Default.Group,
                title = "No friends yet",
                body = "Add a friend to start messaging and teleporting together.",
                actionLabel = "Add friend",
                onAction = {},
            )
            EmptyStatePattern(
                icon = Icons.Default.Inbox,
                title = "Empty folder",
                body = "Drop or create items here.",
            )
            EmptyStatePattern(
                icon = Icons.Default.SearchOff,
                title = "No results",
                body = "Try a different search or clear filters.",
            )
            EmptyStatePattern(
                icon = Icons.Default.Notifications,
                title = "All caught up",
                body = "You'll see new IMs, group pings and offers here.",
            )
        }
    }
}
