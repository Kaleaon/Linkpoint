package com.lumiyaviewer.lumiya.repository

import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.dao.ChatMessageDao
import com.lumiyaviewer.lumiya.dao.Chatter
import com.lumiyaviewer.lumiya.dao.ChatterDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for chat-related data operations.
 * 
 * Provides a clean API for accessing chat messages and chatters,
 * with caching and data synchronization logic.
 * 
 * This follows the Repository pattern from Android Architecture Components.
 */
@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val chatterDao: ChatterDao
) {
    
    // ========== CHAT MESSAGES ==========
    
    /**
     * Get all messages for a specific chatter
     */
    fun getMessagesForChatter(chatterId: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getByChatterIdFlow(chatterId)
    }
    
    /**
     * Get recent messages for a chatter
     */
    suspend fun getRecentMessages(chatterId: Long, limit: Int = 50): List<ChatMessage> {
        return chatMessageDao.getRecentByChatterId(chatterId, limit)
    }
    
    /**
     * Send/save a new message
     */
    suspend fun sendMessage(message: ChatMessage): Long {
        val messageId = chatMessageDao.insert(message)
        
        // Update chatter's last message
        chatterDao.updateLastMessage(
            message.chatterID,
            messageId,
            message.sessionID
        )
        
        return messageId
    }
    
    /**
     * Search messages by text
     */
    suspend fun searchMessages(query: String): List<ChatMessage> {
        return chatMessageDao.searchByText(query)
    }
    
    /**
     * Get offline messages
     */
    fun getOfflineMessages(): Flow<List<ChatMessage>> {
        return chatMessageDao.getOfflineMessagesFlow()
    }
    
    /**
     * Get unsynced messages for cloud backup
     */
    suspend fun getUnsyncedMessages(): List<ChatMessage> {
        return chatMessageDao.getUnsyncedMessages()
    }
    
    /**
     * Mark messages as synced
     */
    suspend fun markMessagesSynced(messageIds: List<Long>) {
        chatMessageDao.markAsSynced(messageIds)
    }
    
    /**
     * Delete old messages
     */
    suspend fun deleteOldMessages(beforeDate: Date): Int {
        return chatMessageDao.deleteOlderThan(beforeDate)
    }
    
    // ========== CHATTERS ==========
    
    /**
     * Get all active chatters
     */
    fun getActiveChatters(): Flow<List<Chatter>> {
        return chatterDao.getActiveFlow()
    }
    
    /**
     * Get chatters with unread messages
     */
    fun getChattersWithUnread(): Flow<List<Chatter>> {
        return chatterDao.getWithUnreadFlow()
    }
    
    /**
     * Get chatter by ID
     */
    suspend fun getChatterById(chatterId: Long): Chatter? {
        return chatterDao.getById(chatterId)
    }

    /**
     * Get or create chatter by UUID
     */
    suspend fun getOrCreateChatter(
        uuid: UUID,
        type: Int
    ): Chatter {
        val existing = chatterDao.getByUUID(uuid)
        if (existing != null) {
            return existing
        }
        
        val newChatter = Chatter(
            uuid = uuid,
            type = type,
            active = true
        )
        
        val id = chatterDao.insert(newChatter)
        return newChatter.copy(id = id)
    }
    
    /**
     * Increment unread count for chatter
     */
    suspend fun incrementUnreadCount(chatterId: Long) {
        val chatter = chatterDao.getById(chatterId) ?: return
        chatterDao.updateUnreadCount(chatterId, chatter.unreadCount + 1)
    }
    
    /**
     * Clear unread count for chatter
     */
    suspend fun clearUnreadCount(chatterId: Long) {
        chatterDao.clearUnreadCount(chatterId)
    }
    
    /**
     * Mute/unmute chatter
     */
    suspend fun setChatterMuted(chatterId: Long, muted: Boolean) {
        chatterDao.setMuted(chatterId, muted)
    }
    
    /**
     * Get total unread message count
     */
    fun getTotalUnreadCount(): Flow<Int> {
        return chatterDao.getWithUnreadFlow().map { chatters ->
            chatters.sumOf { it.unreadCount }
        }
    }
    
    /**
     * Delete inactive chatters
     */
    suspend fun cleanupInactiveChatters() {
        chatterDao.deleteInactive()
    }
}
