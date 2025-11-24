package com.lumiyaviewer.lumiya.modern.chat

import android.util.Log
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer
// import com.linkpoint.slproto.messages.ChatFromViewerMessage
import com.lumiyaviewer.lumiya.modern.protocol.HybridProtocolManager
import com.lumiyaviewer.lumiya.slproto.auth.SessionManager
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Modern Chat Manager with real network integration
 */
class ModernChatManager(private val protocolManager: HybridProtocolManager?) {

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
    private var isInitialized = false

    fun initializeAsync(): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                if (protocolManager == null) {
                    Log.w(TAG, "Protocol manager not available, chat will work in offline mode")
                    isInitialized = true
                    return@supplyAsync true
                }

                val session = SessionManager.current()
                if (session == null) {
                    Log.w(TAG, "No active session, chat initialization deferred")
                    isInitialized = false
                    return@supplyAsync false
                }

                Log.i(TAG, "Chat manager initialized successfully")
                isInitialized = true
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize chat manager", e)
                listener?.onChatError("Failed to initialize chat: ${e.message}")
                false
            }
        }
    }

    fun setChatListener(listener: ChatEventListener?) {
        this.listener = listener
    }

    fun sendLocalChatMessage(message: String, channel: Int = 0): CompletableFuture<Boolean> {
        val chat = ChatMessage(content = message, channel = channel)
        history += chat

        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Local chat message stored locally: $message")
            listener?.onLocalChatReceived(chat)
            return CompletableFuture.completedFuture(true)
        }

        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send chat: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                val chatMessage = ChatFromViewer().apply {
                    AgentData_Field.AgentID = session.reply.agentID ?: UUID.randomUUID()
                    AgentData_Field.SessionID = session.reply.sessionID ?: UUID.randomUUID()
                    ChatData_Field.Message = message.toByteArray(Charsets.UTF_8)
                    ChatData_Field.Type = 1 // Normal chat
                    ChatData_Field.Channel = channel
                }

                val success = protocolManager.sendMessageAsync(chatMessage).get()
                
                if (success) {
                    Log.d(TAG, "Local chat message sent successfully: $message")
                    listener?.onLocalChatReceived(chat)
                } else {
                    Log.e(TAG, "Failed to send local chat message")
                    listener?.onChatError("Failed to send message")
                }
                
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error sending local chat message", e)
                listener?.onChatError("Error sending message: ${e.message}")
                false
            }
        }
    }

    fun sendGroupChatMessage(message: String, groupId: String): CompletableFuture<Boolean> {
        val chat = ChatMessage(
            type = ChatMessage.Type.GROUP,
            content = message,
            sessionId = groupId,
        )
        history += chat

        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Group chat message stored locally: $message")
            listener?.onGroupChatReceived(chat)
            return CompletableFuture.completedFuture(true)
        }

        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send group chat: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                val chatMessage = ChatFromViewer().apply {
                    AgentData_Field.AgentID = session.reply.agentID ?: UUID.randomUUID()
                    AgentData_Field.SessionID = try { UUID.fromString(groupId) } catch (e: Exception) { UUID.randomUUID() }
                    ChatData_Field.Message = message.toByteArray(Charsets.UTF_8)
                    ChatData_Field.Type = 1 // Normal chat
                    ChatData_Field.Channel = 0
                }

                val success = protocolManager.sendMessageAsync(chatMessage).get()
                
                if (success) {
                    Log.d(TAG, "Group chat message sent successfully: $message")
                    listener?.onGroupChatReceived(chat)
                } else {
                    Log.e(TAG, "Failed to send group chat message")
                    listener?.onChatError("Failed to send group message")
                }
                
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error sending group chat message", e)
                listener?.onChatError("Error sending group message: ${e.message}")
                false
            }
        }
    }

    fun sendDirectMessage(message: String, recipientId: String): CompletableFuture<Boolean> {
        val chat = ChatMessage(
            type = ChatMessage.Type.DIRECT,
            content = message,
            sessionId = recipientId,
        )
        history += chat

        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Direct message stored locally: $message")
            return CompletableFuture.completedFuture(true)
        }

        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send direct message: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                val chatMessage = ChatFromViewer().apply {
                    AgentData_Field.AgentID = session.reply.agentID ?: UUID.randomUUID()
                    AgentData_Field.SessionID = try { UUID.fromString(recipientId) } catch (e: Exception) { UUID.randomUUID() }
                    ChatData_Field.Message = message.toByteArray(Charsets.UTF_8)
                    ChatData_Field.Type = 1 // Normal chat
                    ChatData_Field.Channel = 0
                }

                val success = protocolManager.sendMessageAsync(chatMessage).get()
                
                if (success) {
                    Log.d(TAG, "Direct message sent successfully: $message")
                } else {
                    Log.e(TAG, "Failed to send direct message")
                    listener?.onChatError("Failed to send direct message")
                }
                
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error sending direct message", e)
                listener?.onChatError("Error sending direct message: ${e.message}")
                false
            }
        }
    }

    fun getChatHistory(): List<ChatMessage> = history.toList()

    fun clearHistory() {
        history.clear()
        Log.d(TAG, "Chat history cleared")
    }

    fun isInitialized(): Boolean = isInitialized

    companion object {
        private const val TAG = "ModernChatManager"
    }
}
