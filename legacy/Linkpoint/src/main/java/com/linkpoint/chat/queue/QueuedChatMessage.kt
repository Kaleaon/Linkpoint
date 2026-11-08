package com.linkpoint.chat.queue

import com.linkpoint.chat.ChatMessage as CoreChatMessage
import com.linkpoint.chat.IMMessage
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType as ProtocolChatType
import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage as UiChatMessage
import com.linkpoint.ui.chat.MessageType
import java.util.UUID

/**
 * Unified representation of queued chat messages ingested off the UI thread.
 */
sealed interface QueuedChatMessage {
    val id: String
    val timestamp: Long
    val channel: ChatChannel
    val isMine: Boolean

    /**
     * Local or nearby simulator chat message from ChatManager.
     */
    data class Core(val chat: CoreChatMessage, val myAgentId: UUID) : QueuedChatMessage {
        override val id: String get() = chat.id.toString()
        override val timestamp: Long get() = chat.timestamp
        override val channel: ChatChannel get() = ChatChannel.LOCAL
        override val isMine: Boolean get() = chat.isOutgoing || chat.sourceId == myAgentId

        fun toUiMessage(): UiChatMessage {
            val type = when {
                chat.chatType == ProtocolChatType.SHOUT -> MessageType.SHOUT
                chat.chatType == ProtocolChatType.WHISPER -> MessageType.WHISPER
                chat.sourceType == ChatSourceType.OBJECT -> MessageType.OBJECT
                chat.message.startsWith("/me ") -> MessageType.EMOTE
                else -> MessageType.NORMAL
            }
            return UiChatMessage(
                id = id,
                sender = if (chat.isOutgoing) "You" else chat.fromName,
                content = chat.message,
                timestamp = timestamp,
                type = type,
                channel = ChatChannel.LOCAL,
                isMine = isMine,
            )
        }
    }

    /**
     * Instant or group message from IMManager.
     */
    data class Instant(
        val im: IMMessage,
        val myAgentId: UUID,
        override val channel: ChatChannel = ChatChannel.IM,
    ) : QueuedChatMessage {
        override val id: String get() = im.id.toString()
        override val timestamp: Long get() = im.timestamp
        override val isMine: Boolean get() = im.isOutgoing || im.fromAgentId == myAgentId

        fun toUiMessage(): UiChatMessage {
            return UiChatMessage(
                id = id,
                sender = if (im.isOutgoing || im.fromAgentId == myAgentId) "You" else im.fromName,
                content = im.message,
                timestamp = timestamp,
                type = MessageType.NORMAL,
                channel = channel,
                isMine = isMine,
            )
        }
    }

    /**
     * Pre-constructed UI ChatMessage.
     */
    data class Ui(val uiMessage: UiChatMessage) : QueuedChatMessage {
        override val id: String get() = uiMessage.id
        override val timestamp: Long get() = uiMessage.timestamp
        override val channel: ChatChannel get() = uiMessage.channel
        override val isMine: Boolean get() = uiMessage.isMine
    }

    /**
     * System backpressure / warning notice injected when packet queue trimming occurs.
     */
    data class SystemNotice(
        override val id: String = UUID.randomUUID().toString(),
        val noticeText: String,
        override val timestamp: Long = System.currentTimeMillis(),
        override val channel: ChatChannel = ChatChannel.LOCAL,
    ) : QueuedChatMessage {
        override val isMine: Boolean get() = false

        fun toUiMessage(): UiChatMessage {
            return UiChatMessage(
                id = id,
                sender = "System",
                content = noticeText,
                timestamp = timestamp,
                type = MessageType.SYSTEM,
                channel = channel,
                isMine = false,
            )
        }
    }
}
