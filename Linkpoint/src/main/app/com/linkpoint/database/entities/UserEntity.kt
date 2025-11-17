package com.linkpoint.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.linkpoint.database.converters.UUIDConverter
import java.util.UUID

/**
 * Modern Room entity for User.
 * 
 * Represents a Second Life user/avatar with cached information.
 * Stores display name, username, and friend status.
 * 
 * Reference:
 * - Legacy Lumiya User.java
 * - Firestorm: indra/newview/llvoavatar.h
 */
@Entity(tableName = "users")
@TypeConverters(UUIDConverter::class)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // User identification
    val uuid: UUID,
    
    // Names
    val userName: String? = null,        // Legacy "Firstname Lastname" format
    val displayName: String? = null,     // Display name (can be custom)
    
    // Status flags
    val badUUID: Boolean = false,        // Invalid UUID flag
    val isFriend: Boolean = false,       // Is this user a friend?
    
    // Friend rights (if applicable)
    val rightsGiven: Int = 0,           // Rights we gave to this user
    val rightsHas: Int = 0              // Rights this user gave us
) {
    /**
     * Check if user name needs to be fetched from server
     */
    fun nameNeedsFetching(): Boolean {
        return (userName == null || displayName == null) && !badUUID
    }
    
    /**
     * Get the best available name for display
     */
    fun getBestName(): String {
        return displayName ?: userName ?: "Unknown User"
    }
    
    /**
     * Check if this is a valid user
     */
    fun isValid(): Boolean {
        return !badUUID && uuid != UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
    
    companion object {
        // Friend rights constants
        const val GRANT_ONLINE_STATUS = 1
        const val GRANT_MAP_LOCATION = 2
        const val GRANT_MODIFY_OBJECTS = 4
        
        /**
         * Check if specific right is granted
         */
        fun hasRight(rights: Int, right: Int): Boolean {
            return (rights and right) != 0
        }
    }
}