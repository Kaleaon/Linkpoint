package com.linkpoint.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBarPresenceSubtitle
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChatChannel(val displayName: String) {
    LOCAL("Local"),
    IM("IMs"),
    GROUP("Groups"),
    NEARBY("Nearby"),
}

enum class MessageType {
    NORMAL,
    WHISPER,
    SHOUT,
    EMOTE,
    SYSTEM,
    OBJECT,
}

enum class MessageStatus { SENDING, SENT, DELIVERED, READ, FAILED }

data class ChatMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val type: MessageType,
    val channel: ChatChannel,
    val status: MessageStatus = MessageStatus.SENT,
    val isMine: Boolean = false,
)

/**
 * Linkpoint 2.0 chat screen — channel chips along the top, bubble list with
 * inline avatars on remote messages and pill-radius bubbles, glass composer
 * docked at the bottom matching design/screens-1.jsx → ChatThreadScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    currentAvatarName: String = "You",
    threadAvatarName: String = "Local",
    threadOnline: Boolean = true,
    threadLocation: String = "Local distance 18m",
    uiLoadState: UiLoadState = UiLoadState.Content,
    onRetry: () -> Unit,
    onSendMessage: (String, ChatChannel) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedChannel by remember { mutableIntStateOf(0) }
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val tokens = Linkpoint2.tokens

    val channels = ChatChannel.entries
    val currentChannel = channels[selectedChannel]
    val filteredMessages = messages.filter { it.channel == currentChannel }

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        L2TopBar(
            title = if (currentChannel == ChatChannel.IM) threadAvatarName else "Chat",
            subtitle = {
                L2TopBarPresenceSubtitle(threadOnline, threadLocation)
            },
            leading = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                if (currentChannel == ChatChannel.IM) {
                    L2Avatar(name = threadAvatarName, size = AvatarSize.SM, online = threadOnline)
                }
            },
            trailing = {
                IconButton(onClick = { /* call */ }) {
                    Icon(Icons.Default.Call, contentDescription = "Voice call")
                }
                IconButton(onClick = { /* more */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            },
        )

        if (uiLoadState is UiLoadState.Reconnecting) {
            ReconnectingBanner(message = uiLoadState.message)
        }

        // Channel chip strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            channels.forEachIndexed { index, channel ->
                L2Chip(
                    label = channel.displayName,
                    variant = if (index == selectedChannel) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                    onClick = { selectedChannel = index },
                )
            }
        }

        when (uiLoadState) {
            is UiLoadState.Loading -> LoadingState(
                message = uiLoadState.message,
                modifier = Modifier.weight(1f),
            )
            is UiLoadState.Error -> ErrorState(
                title = uiLoadState.title,
                message = uiLoadState.message,
                retryLabel = uiLoadState.retryLabel,
                modifier = Modifier.weight(1f),
                onRetry = {
                    logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "chat", uiLoadState)
                    onRetry()
                },
            )
            is UiLoadState.Empty -> EmptyState(
                title = uiLoadState.title,
                message = uiLoadState.message,
                actionLabel = uiLoadState.actionLabel,
                onAction = onRetry,
                modifier = Modifier.weight(1f),
            )
            UiLoadState.Content, is UiLoadState.Reconnecting, is UiLoadState.LowBandwidth -> {
                if (filteredMessages.isEmpty()) {
                    EmptyState(
                        title = "No messages yet",
                        message = "Start chatting in ${currentChannel.displayName}",
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(filteredMessages, key = { it.id }) { message ->
                            ChatBubble(message = message)
                        }
                    }
                }
            }
        }

        // Composer
        L2GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* attach */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Attach")
                }
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = {
                        Text("Send a message as $currentAvatarName…")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(tokens.radii.pill),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
                    trailingIcon = {
                        IconButton(onClick = { /* camera */ }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera")
                        }
                    },
                )

                Spacer(Modifier.width(4.dp))
                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            onSendMessage(messageInput, currentChannel)
                            messageInput = ""
                        }
                    },
                    enabled = messageInput.isNotBlank(),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (messageInput.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (messageInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                        else tokens.onSurfaceDim,
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val tokens = Linkpoint2.tokens
    val mine = message.isMine

    val typeColor = when (message.type) {
        MessageType.SYSTEM -> tokens.onSurfaceDim
        MessageType.WHISPER -> Color(0xFF9999FF)
        MessageType.SHOUT -> Color(0xFFFF6666)
        MessageType.EMOTE -> Color(0xFF66FF66)
        MessageType.OBJECT -> Color(0xFFFFAA00)
        MessageType.NORMAL -> if (mine) cs.onPrimary else cs.onSurface
    }
    val bubbleBg = if (mine) cs.primary else cs.surfaceVariant
    val bubbleShape = if (mine) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 6.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 6.dp, bottomEnd = 18.dp)
    }

    val timestamp = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!mine) {
            L2Avatar(name = message.sender, size = AvatarSize.SM)
            Spacer(Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (mine) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            if (!mine) {
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleBg)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = typeColor,
                    fontStyle = if (message.type == MessageType.EMOTE) FontStyle.Italic else FontStyle.Normal,
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.onSurfaceDim,
                )
                if (mine) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = when (message.status) {
                            MessageStatus.SENDING -> "· sending"
                            MessageStatus.SENT -> "· sent"
                            MessageStatus.DELIVERED -> "· delivered"
                            MessageStatus.READ -> "· read"
                            MessageStatus.FAILED -> "· failed"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (message.status == MessageStatus.FAILED) cs.error else tokens.onSurfaceDim,
                    )
                }
            }
        }
    }
}

/**
 * Backwards-compatible alias. Existing call sites use [ChatMessageItem].
 */
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    ChatBubble(message = message, modifier = modifier)
}
