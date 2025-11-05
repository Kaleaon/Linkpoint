package com.lumiyaviewer.lumiya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lumiyaviewer.lumiya.database.converters.DateConverter
import com.lumiyaviewer.lumiya.database.converters.UUIDConverter
import java.util.Date
import java.util.UUID

/**
 * Modern Room database entity for chat messages.
 * 
 * Based on Firestorm C++ LLChat structure and Libremetaverse C# implementation.
 * Replaces legacy GreenDAO implementation with modern Room database.
 * 
 * Reference:
 * - Firestorm: indra/llui/llchat.h
 * - Libremetaverse: LibreMetaverse/Messages/
 */
@Entity(tableName = "chat_messages")
@TypeConverters(DateConverter::class, UUIDConverter::class)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Chatter relationship
    val chatterID: Long,
    
    // Timestamps
    val timestamp: Date,
    val origTimestamp: Date? = null,
    
    // Sender information
    val senderUUID: UUID? = null,
    val senderName: String? = null,
    val senderLegacyName: String? = null,
    val senderType: ChatSourceType = ChatSourceType.AGENT,
    
    // Message content
    val messageText: String,
    val messageType: ChatType = ChatType.NORMAL,
    
    // View and display
    val viewType: Int = 0,
    
    // Session and context
    val sessionID: UUID? = null,
    val userID: UUID? = null,
    
    // Offline message
    val isOffline: Boolean = false,
    
    // Item transfer related
    val itemID: UUID? = null,
    val itemName: String? = null,
    val assetType: Int? = null,
    
    // Transaction related
    val transactionAmount: Int? = null,
    val newBalance: Int? = null,
    
    // Chat channel
    val chatChannel: Int? = null,
    
    // Dialog related
    val dialogIgnored: Boolean = false,
    val dialogButtons: ByteArray? = null,
    val dialogSelectedOption: String? = null,
    val questionMask: Int? = null,
    val textBoxButtonIndex: Int? = null,
    
    // Object related
    val objectName: String? = null,
    
    // Event state
    val eventState: Int? = null,
    val origIMType: Int? = null,
    
    // Acceptance state
    val accepted: Boolean = false,
    
    // Cloud sync
    val syncedToGoogleDrive: Boolean = false
) {
    /**
     * Chat source types based on Firestorm EChatSourceType
     */
    enum class ChatSourceType(val value: Int) {
        SYSTEM(0),
        AGENT(1),
        OBJECT(2),
        TELEPORT(3),
        UNKNOWN(4),
        REGION(5);
        
        companion object {
            fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
    
    /**
     * Chat types based on Firestorm EChatType
     */
    enum class ChatType(val value: Int) {
        WHISPER(0),
        NORMAL(1),
        SHOUT(2),
        OOC(3),
        START(4),
        STOP(5),
        DEBUG_MSG(6),
        REGION(7),
        OWNER(8),
        DIRECT(9),
        IM(10),
        IM_GROUP(11),
        RADAR(12);
        
        companion object {
            fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: NORMAL
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as ChatMessageEntity
        
        if (id != other.id) return false
        if (chatterID != other.chatterID) return false
        if (timestamp != other.timestamp) return false
        if (messageText != other.messageText) return false
        if (dialogButtons != null) {
            if (other.dialogButtons == null) return false
            if (!dialogButtons.contentEquals(other.dialogButtons)) return false
        } else if (other.dialogButtons != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + chatterID.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + messageText.hashCode()
        result = 31 * result + (dialogButtons?.contentHashCode() ?: 0)
        return result
    }
}
