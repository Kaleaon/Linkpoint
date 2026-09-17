package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import com.linkpoint.chat.SessionType
import com.linkpoint.ui.friends.FriendData
import com.linkpoint.ui.friends.FriendStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Compose-first IM LIST destination wired to the real
 * [com.linkpoint.chat.IMManager] active sessions and unread counts,
 * as well as [com.linkpoint.world.FriendsManager] for the "All" friends view.
 */
@Composable
fun L2IMListRoute(
    onBack: () -> Unit,
    onOpenConversation: (ConversationSummary) -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()

    // Shared formatter — one instance per composition, not one per row.
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    val conversations: List<ConversationSummary> = if (app == null || !app.isIMManagerInitialized()) {
        emptyList()
    } else {
        val sessions by app.imManager.activeSessions.collectAsState()
        val unread by app.imManager.unreadCounts.collectAsState()
        sessions.map { session ->
            // getLastSessionMessage avoids copying the full history list.
            val lastMsg = app.imManager.getLastSessionMessage(session.sessionId)
            ConversationSummary(
                id = session.sessionId.toString(),
                name = session.name,
                lastMessage = lastMsg?.message ?: "",
                timestamp = lastMsg?.timestamp?.let { timeFormatter.format(Date(it)) } ?: "",
                unread = unread[session.sessionId] ?: 0,
                online = session.isActive,
                isGroup = session.type == SessionType.GROUP || session.type == SessionType.CONFERENCE,
            )
        }
    }

    val friends: List<FriendData> = if (app == null || !app.isFriendsManagerInitialized()) {
        emptyList()
    } else {
        val onlineSet by app.friendsManager.onlineFriends.collectAsState()
        val rawFriends = app.friendsManager.getAllFriends()
        rawFriends.map { f ->
            val isOnline = f.agentId in onlineSet || f.isOnline
            FriendData(
                id = f.agentId,
                name = f.name,
                status = if (isOnline) FriendStatus.ONLINE else FriendStatus.OFFLINE,
                location = null,
                canSeeOnline = f.canSeeOnline,
                canSeeMap = f.canTrack,
                canModifyObjects = f.canModifyObjects,
            )
        }.sortedWith(compareByDescending<FriendData> { it.status == FriendStatus.ONLINE }.thenBy { it.name })
    }

    IMListScreen(
        conversations = conversations,
        friends = friends,
        onBack = onBack,
        onOpenConversation = onOpenConversation,
        onOpenFriendIM = { friend ->
            onOpenConversation(
                ConversationSummary(
                    id = friend.id.toString(),
                    name = friend.name,
                    lastMessage = "",
                    timestamp = "",
                    online = friend.status == FriendStatus.ONLINE,
                )
            )
        },
        onCompose = onCompose,
        modifier = modifier,
    )
}
