package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import com.linkpoint.chat.SessionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Compose-first IM LIST destination wired to the real
 * [com.linkpoint.chat.IMManager] active sessions and unread counts.
 *
 * Replaces the placeholder `L2Demo.Conversations` wiring in the L2 nav graph
 * — that demo data made the UI look populated even when no real IM/group
 * sessions existed, so users couldn't tell whether messaging was working.
 */
@Composable
fun L2IMListRoute(
    onBack: () -> Unit,
    onOpenConversation: (ConversationSummary) -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()

    val conversations: List<ConversationSummary> = if (app == null || !app.isIMManagerInitialized()) {
        emptyList()
    } else {
        val sessions by app.imManager.activeSessions.collectAsState()
        val unread by app.imManager.unreadCounts.collectAsState()
        sessions.map { session ->
            val lastMsg = app.imManager.getSessionMessages(session.sessionId).lastOrNull()
            ConversationSummary(
                id = session.sessionId.toString(),
                name = session.name,
                lastMessage = lastMsg?.message ?: "",
                timestamp = lastMsg?.timestamp?.let {
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(it))
                } ?: "",
                unread = unread[session.sessionId] ?: 0,
                online = session.isActive,
                isGroup = session.type == SessionType.GROUP || session.type == SessionType.CONFERENCE,
            )
        }
    }

    IMListScreen(
        conversations = conversations,
        onBack = onBack,
        onOpenConversation = onOpenConversation,
        onCompose = onCompose,
        modifier = modifier,
    )
}
