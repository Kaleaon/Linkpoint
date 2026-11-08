package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.primitives.L2UnreadBadge
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

data class ConversationSummary(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val unread: Int = 0,
    val online: Boolean = false,
    val isGroup: Boolean = false,
)

/**
 * Cluster D — IM list. See design/screens-2.jsx → IMListScreen.
 */
@Composable
fun IMListScreen(
    conversations: List<ConversationSummary>,
    onBack: () -> Unit,
    onOpenConversation: (ConversationSummary) -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Messages",
                subtitle = "${conversations.sumOf { it.unread }} unread",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCompose) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Compose")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(conversations, key = { it.id }) { c ->
                L2Row(
                    headline = c.name,
                    supporting = c.lastMessage,
                    leading = {
                        if (c.isGroup) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(tokens.radii.md))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = c.name.first().uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        } else {
                            L2Avatar(name = c.name, size = AvatarSize.MD, online = c.online)
                        }
                    },
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(c.timestamp, style = MaterialTheme.typography.labelSmall, color = tokens.onSurfaceDim)
                            if (c.unread > 0) {
                                L2UnreadBadge(count = c.unread, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    },
                    onClick = { onOpenConversation(c) },
                )
            }
        }
    }
}
