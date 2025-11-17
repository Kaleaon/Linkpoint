package com.linkpoint.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.linkpoint.database.converters.UUIDConverter
import java.util.UUID

/**
 * Modern Room entity for UserName cache.
 * 
 * Caches user names (both legacy username and display name) for avatars.
 * Used to avoid repeated name lookups from the server.
 * 
 * Reference: Legacy Lumiya UserName.java
 */
@Entity(tableName = "user_names")
@TypeConverters(UUIDConverter::class)
data class UserNameEntity(
    @PrimaryKey
    val uuid: UUID,
    
    // Names
    val userName: String? = null,        // Legacy "Firstname Lastname"
    val displayName: String? = null,     // Custom display name
    
    // Status
    val isBadUUID: Boolean = false       // Invalid UUID flag
) {
    /**
     * Check if name information is complete
     */
    fun isComplete(): Boolean {
        if (isBadUUID) return true
        return !userName.isNullOrEmpty() && !displayName.isNullOrEmpty()
    }
    
    /**
     * Get the best available name
     */
    fun getBestName(): String {
        return displayName ?: userName ?: "Unknown"
    }
    
    /**
     * Merge with another UserName, updating any missing information
     * Returns true if any changes were made
     */
    fun mergeWith(other: UserNameEntity): UserNameEntity {
        // If other is marked as bad UUID, mark this as bad
        val newIsBadUUID = if (other.isBadUUID && !this.isBadUUID) {
            true
        } else {
            this.isBadUUID
        }
        
        // If already bad UUID, don't merge names
        if (this.isBadUUID) {
            return this.copy(isBadUUID = newIsBadUUID)
        }
        
        // Merge username if other has it and it's different
        val newUserName = if (!other.userName.isNullOrEmpty() && other.userName != this.userName) {
            other.userName
        } else {
            this.userName
        }
        
        // Merge display name if other has it and it's different
        val newDisplayName = if (!other.displayName.isNullOrEmpty() && other.displayName != this.displayName) {
            other.displayName
        } else {
            this.displayName
        }
        
        return UserNameEntity(
            uuid = this.uuid,
            userName = newUserName,
            displayName = newDisplayName,
            isBadUUID = newIsBadUUID
        )
    }
}