package com.lumiyaviewer.lumiya.modern.chat

import android.util.Log
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

class ModernChatManager(@Suppress("UNUSED_PARAMETER") protocolManager: Any?) {

    interface ChatEventListener {
        fun onLocalChatReceived(message: ChatMessage) {}
        fun onGroupChatReceived(message: ChatMessage) {}
        fun onGroupChatInvitation(invitation: GroupChatInvitation) {}
        fun onChatError(error: String) {}
        fun onTypingIndicator(userId: String, sessionId: String, isTyping: Boolean) {}
    }

    data class ChatMessage(
        val id: String = UUID.randomUUID().toString(),
        val type: Type = Type.LOCAL,
        val content: String,
        val timestamp: Long = System.currentTimeMillis(),
        val senderId: String = "self",
        val sessionId: String? = null,
        val channel: Int = 0,
    ) {
        enum class Type { LOCAL, GROUP, DIRECT }
    }

    data class GroupChatInvitation(val groupId: String, val inviterId: String)

    private val history = CopyOnWriteArrayList<ChatMessage>()
    private var listener: ChatEventListener? = null

    fun initializeAsync(): CompletableFuture<Boolean> =
        CompletableFuture.completedFuture(true)

    fun setChatListener(listener: ChatEventListener?) {
        this.listener = listener
    }

    fun sendLocalChatMessage(message: String, channel: Int = 0): CompletableFuture<Boolean> {
        val chat = ChatMessage(content = message, channel = channel)
        history += chat
        Log.d(TAG, "Stub local chat message queued: $message")
        listener?.onLocalChatReceived(chat)
        return CompletableFuture.completedFuture(true)
    }

    fun sendGroupChatMessage(message: String, groupId: String): CompletableFuture<Boolean> {
        val chat = ChatMessage(
            type = ChatMessage.Type.GROUP,
            content = message,
            sessionId = groupId,
        )
        history += chat
        Log.d(TAG, "Stub group chat message queued: $message")
        listener?.onGroupChatReceived(chat)
        return CompletableFuture.completedFuture(true)
    }

    fun getChatHistory(): List<ChatMessage> = history.toList()

    companion object {
        private const val TAG = "ModernChatManager"
    }
}
