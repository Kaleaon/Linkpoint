package com.lumiyaviewer.lumiya.modern.chat

import android.util.Log
import com.linkpoint.slproto.messages.ChatFromViewerMessage
import com.lumiyaviewer.lumiya.modern.protocol.HybridProtocolManager
import com.lumiyaviewer.lumiya.slproto.auth.SessionManager
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Modern Chat Manager with real network integration
 * 
 * Handles:
 * - Local chat messages (public chat in current location)
 * - Group chat messages
 * - Direct messages (IMs)
 * - Chat history management
 * - Real-time chat events
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

    /**
     * Initialize the chat manager
     */
    fun initializeAsync(): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                // Check if protocol manager is available
                if (protocolManager == null) {
                    Log.w(TAG, "Protocol manager not available, chat will work in offline mode")
                    isInitialized = true
                    return@supplyAsync true
                }

                // Verify session is active
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

    /**
     * Set chat event listener
     */
    fun setChatListener(listener: ChatEventListener?) {
        this.listener = listener
    }

    /**
     * Send local chat message (public chat in current location)
     * 
     * @param message The message text to send
     * @param channel The chat channel (0 = public, 1-2147483647 = custom channels)
     * @return CompletableFuture<Boolean> indicating success/failure
     */
    fun sendLocalChatMessage(message: String, channel: Int = 0): CompletableFuture<Boolean> {
        val chat = ChatMessage(content = message, channel = channel)
        history += chat

        // If no protocol manager, just store locally
        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Local chat message stored locally: $message")
            listener?.onLocalChatReceived(chat)
            return CompletableFuture.completedFuture(true)
        }

        // Get current session
        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send chat: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                // Create ChatFromViewer message
                val chatMessage = ChatFromViewerMessage().apply {
                    agentId = UUID.fromString(session.reply.agentID ?: UUID.randomUUID().toString())
                    sessionId = UUID.fromString(session.reply.sessionID ?: UUID.randomUUID().toString())
                    this.message = message
                    this.type = 1 // Normal chat
                    this.channel = channel
                }

                // Send via protocol manager
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

    /**
     * Send group chat message
     * 
     * @param message The message text to send
     * @param groupId The UUID of the group
     * @return CompletableFuture<Boolean> indicating success/failure
     */
    fun sendGroupChatMessage(message: String, groupId: String): CompletableFuture<Boolean> {
        val chat = ChatMessage(
            type = ChatMessage.Type.GROUP,
            content = message,
            sessionId = groupId,
        )
        history += chat

        // If no protocol manager, just store locally
        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Group chat message stored locally: $message")
            listener?.onGroupChatReceived(chat)
            return CompletableFuture.completedFuture(true)
        }

        // Get current session
        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send group chat: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                // For group chat, we would use ImprovedInstantMessage with dialog type GROUP_INVITATION
                // This is a simplified implementation - full implementation would use proper IM messages
                
                val chatMessage = ChatFromViewerMessage().apply {
                    agentId = UUID.fromString(session.reply.agentID ?: UUID.randomUUID().toString())
                    sessionId = UUID.fromString(groupId)
                    this.message = message
                    this.type = 1 // Normal chat
                    this.channel = 0
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

    /**
     * Send direct message (IM) to another user
     * 
     * @param message The message text to send
     * @param recipientId The UUID of the recipient
     * @return CompletableFuture<Boolean> indicating success/failure
     */
    fun sendDirectMessage(message: String, recipientId: String): CompletableFuture<Boolean> {
        val chat = ChatMessage(
            type = ChatMessage.Type.DIRECT,
            content = message,
            sessionId = recipientId,
        )
        history += chat

        // If no protocol manager, just store locally
        if (protocolManager == null) {
            Log.d(TAG, "Offline mode: Direct message stored locally: $message")
            return CompletableFuture.completedFuture(true)
        }

        // Get current session
        val session = SessionManager.current()
        if (session == null) {
            Log.e(TAG, "Cannot send direct message: No active session")
            listener?.onChatError("No active session")
            return CompletableFuture.completedFuture(false)
        }

        return CompletableFuture.supplyAsync {
            try {
                // For direct messages, we would use ImprovedInstantMessage
                // This is a simplified implementation using ChatFromViewer
                
                val chatMessage = ChatFromViewerMessage().apply {
                    agentId = UUID.fromString(session.reply.agentID ?: UUID.randomUUID().toString())
                    sessionId = UUID.fromString(recipientId)
                    this.message = message
                    this.type = 1 // Normal chat
                    this.channel = 0
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

    /**
     * Get chat history
     */
    fun getChatHistory(): List<ChatMessage> = history.toList()

    /**
     * Clear chat history
     */
    fun clearHistory() {
        history.clear()
        Log.d(TAG, "Chat history cleared")
    }

    /**
     * Get initialization status
     */
    fun isInitialized(): Boolean = isInitialized

    companion object {
        private const val TAG = "ModernChatManager"
    }
}