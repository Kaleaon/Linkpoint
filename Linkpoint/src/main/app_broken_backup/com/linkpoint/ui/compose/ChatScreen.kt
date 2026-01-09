package com.linkpoint.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.theme.LinkpointTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Chat screen implementation using Jetpack Compose
 * 
 * Features:
 * - Local, IM, and Group chat support
 * - Message bubbles with timestamps
 * - Avatar thumbnails
 * - Input field with send button
 * - Auto-scroll to bottom
 * - Message history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatType: String = "local",
    targetName: String = "Local Chat",
    onNavigateBack: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(getSampleMessages()) }
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(targetName)
                            Text(
                                text = chatType.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Show chat options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                    }
                )
            },
            bottomBar = {
                ChatInputBar(
                    messageText = messageText,
                    onMessageTextChanged = { messageText = it },
                    onSendMessage = {
                        if (messageText.isNotBlank()) {
                            messages = messages + ChatMessage(
                                id = UUID.randomUUID().toString(),
                                sender = "You",
                                message = messageText,
                                timestamp = System.currentTimeMillis(),
                                isOwn = true
                            )
                            messageText = ""
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(messages) { message ->
                    ChatMessageBubble(message)
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Chat message bubble
 */
@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isOwn) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isOwn) {
            // Avatar for other users
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = message.sender.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isOwn) Alignment.End else Alignment.Start
        ) {
            // Sender name (if not own message)
            if (!message.isOwn) {
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
            
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (message.isOwn) 16.dp else 4.dp,
                    topEnd = if (message.isOwn) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (message.isOwn) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            
            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
        
        if (message.isOwn) {
            Spacer(modifier = Modifier.width(8.dp))
            // Avatar for own messages
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * Chat input bar with text field and send button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(
    messageText: String,
    onMessageTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Emoji button
            IconButton(onClick = { /* Show emoji picker */ }) {
                Icon(
                    imageVector = Icons.Default.EmojiEmotions,
                    contentDescription = "Emoji"
                )
            }
            
            // Text input field
            TextField(
                value = messageText,
                onValueChange = onMessageTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                maxLines = 4,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Send button
            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp),
                containerColor = if (messageText.isBlank()) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

/**
 * Format timestamp to readable time
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Data class for chat messages
 */
data class ChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val isOwn: Boolean = false
)

/**
 * Sample messages for demo purposes
 */
private fun getSampleMessages(): List<ChatMessage> {
    val currentTime = System.currentTimeMillis()
    return listOf(
        ChatMessage(
            id = "1",
            sender = "Alice Avatar",
            message = "Hey everyone! Welcome to Second Life!",
            timestamp = currentTime - 600000,
            isOwn = false
        ),
        ChatMessage(
            id = "2",
            sender = "Bob Builder",
            message = "Hi Alice! Thanks for the warm welcome.",
            timestamp = currentTime - 540000,
            isOwn = false
        ),
        ChatMessage(
            id = "3",
            sender = "You",
            message = "Hello! Happy to be here.",
            timestamp = currentTime - 480000,
            isOwn = true
        ),
        ChatMessage(
            id = "4",
            sender = "Charlie Creator",
            message = "Check out my new build at the teleport hub!",
            timestamp = currentTime - 420000,
            isOwn = false
        ),
        ChatMessage(
            id = "5",
            sender = "You",
            message = "Sounds great! I'll head over there now.",
            timestamp = currentTime - 360000,
            isOwn = true
        ),
        ChatMessage(
            id = "6",
            sender = "Alice Avatar",
            message = "Anyone want to explore the new region together?",
            timestamp = currentTime - 300000,
            isOwn = false
        ),
        ChatMessage(
            id = "7",
            sender = "You",
            message = "I'm in! Let's meet at the landing point.",
            timestamp = currentTime - 240000,
            isOwn = true
        )
    )
}
