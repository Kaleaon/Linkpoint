package com.linkpoint.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import com.linkpoint.chat.IMMessage
import com.linkpoint.chat.SessionType
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.ui.common.UiLoadState
import java.util.UUID
import com.linkpoint.protocol.messages.ChatType as ProtocolChatType

/**
 * Compose-first CHAT destination wired to the real [com.linkpoint.chat.ChatManager]
 * and [com.linkpoint.chat.IMManager]. Replaces the placeholder L2Demo wiring in
 * [com.linkpoint.ui.linkpoint2.Linkpoint2NavGraph] which left every messaging
 * surface (local, IM, group) silently broken — typed text vanished into a
 * no-op lambda and incoming chat never reached the screen.
 *
 * Channels:
 *  - LOCAL / NEARBY  → ChatManager (UDP `ChatFromViewer` / `ChatFromSimulator`)
 *  - IM              → IMManager P2P session (UDP `ImprovedInstantMessage`)
 *  - GROUP           → IMManager GROUP/CONFERENCE session (UDP IM Dialog=17 +
 *                      ChatterBox EventQueue replies)
 *
 * For IM/GROUP, the active session is resolved from
 * [IMManager.activeSessions] — i.e. whatever conversation last received a
 * message. A future revision should accept a session id as a route argument
 * so the user can pick a specific conversation.
 */
@Composable
fun L2ChatRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()

    if (app == null || !app.isChatManagerInitialized()) {
        ChatScreen(
            messages = emptyList(),
            onRetry = {},
            onSendMessage = { _, _ -> },
            onNavigateBack = onNavigateBack,
            uiLoadState = UiLoadState.Empty(
                title = "Not connected",
                message = "Chat will be available once you're logged in.",
            ),
            modifier = modifier,
        )
        return
    }

    val avatarName = app.sessionManager.getAvatarName().ifBlank { "You" }
    val myAgentId = app.sessionManager.getAgentId() ?: UUID(0L, 0L)
    val region by app.sessionManager.currentRegion.collectAsState()

    val messages = remember { mutableStateListOf<ChatMessage>() }
    var activeImSessionId by remember { mutableStateOf<UUID?>(null) }
    var activeGroupSessionId by remember { mutableStateOf<UUID?>(null) }

    // Seed with local-chat history so the screen isn't empty mid-session.
    LaunchedEffect(Unit) {
        app.chatManager.getHistory().forEach { chat ->
            messages.add(chat.toUiMessage(myAgentId))
        }
    }

    // Resolve the active IM/GROUP session from whatever IMManager currently
    // has. We don't drive the channel selector from outside, so we stash the
    // ids and the existing ChatScreen filter picks them up by ChatChannel.
    val sessions by app.imManager.activeSessions.collectAsState()
    LaunchedEffect(sessions) {
        if (activeImSessionId == null) {
            activeImSessionId = sessions.firstOrNull { it.type == SessionType.P2P }?.sessionId
        }
        if (activeGroupSessionId == null) {
            activeGroupSessionId = sessions.firstOrNull {
                it.type == SessionType.GROUP || it.type == SessionType.CONFERENCE
            }?.sessionId
        }
    }

    // Stream incoming nearby chat into the visible list.
    LaunchedEffect(Unit) {
        app.chatManager.chatFlow.collect { chat ->
            messages.add(chat.toUiMessage(myAgentId))
        }
    }

    // Stream incoming IMs, scoped to the currently-active sessions so an
    // unrelated group doesn't pollute the visible list.
    LaunchedEffect(activeImSessionId, activeGroupSessionId) {
        app.imManager.messageFlow.collect { im ->
            val channel = when (im.sessionId) {
                activeImSessionId -> ChatChannel.IM
                activeGroupSessionId -> ChatChannel.GROUP
                else -> return@collect
            }
            messages.add(im.toUiMessage(myAgentId, channel))
        }
    }

    ChatScreen(
        messages = messages,
        currentAvatarName = avatarName,
        threadAvatarName = sessions
            .firstOrNull { it.sessionId == activeImSessionId }
            ?.name
            ?: "Local",
        threadOnline = true,
        threadLocation = region?.name ?: "Local",
        uiLoadState = UiLoadState.Content,
        onRetry = {},
        onSendMessage = { text, channel ->
            if (text.isBlank()) return@ChatScreen
            when (channel) {
                ChatChannel.LOCAL, ChatChannel.NEARBY -> {
                    val (chatType, body) = when {
                        text.startsWith("/shout ") ->
                            ProtocolChatType.SHOUT to text.removePrefix("/shout ")
                        text.startsWith("/whisper ") ->
                            ProtocolChatType.WHISPER to text.removePrefix("/whisper ")
                        else -> ProtocolChatType.NORMAL to text
                    }
                    app.chatManager.sendChat(body, chatType, channel = 0)
                }
                ChatChannel.IM -> {
                    val sid = activeImSessionId
                    if (sid != null) app.imManager.sendIM(sid, text)
                }
                ChatChannel.GROUP -> {
                    val sid = activeGroupSessionId
                    if (sid != null) app.imManager.sendIM(sid, text)
                }
            }
        },
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

private fun com.linkpoint.chat.ChatMessage.toUiMessage(myAgentId: UUID): ChatMessage {
    val type = when {
        chatType == ProtocolChatType.SHOUT -> MessageType.SHOUT
        chatType == ProtocolChatType.WHISPER -> MessageType.WHISPER
        sourceType == ChatSourceType.OBJECT -> MessageType.OBJECT
        message.startsWith("/me ") -> MessageType.EMOTE
        else -> MessageType.NORMAL
    }
    return ChatMessage(
        id = id.toString(),
        sender = if (isOutgoing) "You" else fromName,
        content = message,
        timestamp = timestamp,
        type = type,
        // Local/Nearby share the simulator-driven nearby-chat stream; bucket
        // everything into LOCAL so the default channel selector renders it.
        channel = ChatChannel.LOCAL,
        isMine = isOutgoing || sourceId == myAgentId,
    )
}

private fun IMMessage.toUiMessage(myAgentId: UUID, channel: ChatChannel): ChatMessage {
    return ChatMessage(
        id = id.toString(),
        sender = if (isOutgoing || fromAgentId == myAgentId) "You" else fromName,
        content = message,
        timestamp = timestamp,
        type = MessageType.NORMAL,
        channel = channel,
        isMine = isOutgoing || fromAgentId == myAgentId,
    )
}
