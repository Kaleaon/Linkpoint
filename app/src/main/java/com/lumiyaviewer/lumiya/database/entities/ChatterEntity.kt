package com.lumiyaviewer.lumiya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lumiyaviewer.lumiya.database.converters.UUIDConverter
import java.util.UUID

/**
 * Modern Room entity for Chatter (conversation/contact).
 * 
 * Represents a conversation thread with an agent, group, or object.
 * Tracks active status, mute state, and unread message count.
 * 
 * Reference: Legacy Lumiya Chatter.java
 */
@Entity(tableName = "chatters")
@TypeConverters(UUIDConverter::class)
data class ChatterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Chatter identification
    val uuid: UUID,
    
    // Chatter type (IM, Group, Object, etc.)
    val type: ChatterType,
    
    // Status flags
    val active: Boolean = true,
    val muted: Boolean = false,
    
    // Message tracking
    val unreadCount: Int = 0,
    val lastMessageID: Long? = null,
    val lastSessionID: UUID? = null
) {
    /**
     * Chatter types based on Second Life chat system
     */
    enum class ChatterType(val value: Int) {
        AGENT(0),           // Individual avatar
        GROUP(1),           // Group chat
        OBJECT(2),          // Object/scripted item
        CONFERENCE(3),      // Conference/multi-party
        SYSTEM(4);          // System messages
        
        companion object {
            fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: AGENT
        }
    }
}