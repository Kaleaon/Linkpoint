package com.linkpoint.ui.compose.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linkpoint.database.entities.ChatMessageEntity
import kotlinx.coroutines.launch

/**
 * Chat Screen with real messaging functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    currentUserId: Long,
    onSendMessage: (String) -> Unit,
    getChatterName: (Long) -> String = { "User $it" },
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message,
                    currentUserId = currentUserId,
                    getChatterName = getChatterName
                )
            }
        }

        // Message input
        Surface(
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    singleLine = false,
                    maxLines = 4
                )
                
                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText.trim())
                            messageText = ""
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessageEntity,
    currentUserId: Long,
    getChatterName: (Long) -> String
) {
    val isCurrentUser = message.chatterID == currentUserId
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Sender name - resolved from ChatterEntity via ViewModel
                Text(
                    text = if (isCurrentUser) "You" else getChatterName(message.chatterID),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Message content
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Timestamp
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> {
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(date)
        }
    }
}