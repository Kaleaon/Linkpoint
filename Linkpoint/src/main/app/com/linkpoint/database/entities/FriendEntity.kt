package com.linkpoint.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.linkpoint.database.converters.UUIDConverter
import java.util.UUID

/**
 * Modern Room entity for Friend.
 * 
 * Represents a friend in the Second Life friends list.
 * Tracks online status and permission rights.
 * 
 * Reference: 
 * - Legacy Lumiya Friend.java
 * - Firestorm: indra/newview/llavataractions.h
 */
@Entity(tableName = "friends")
@TypeConverters(UUIDConverter::class)
data class FriendEntity(
    @PrimaryKey
    val uuid: UUID,
    
    // Permission rights given to friend
    val rightsGiven: Int = 0,
    
    // Permission rights friend has given us
    val rightsHas: Int = 0,
    
    // Online status
    val isOnline: Boolean = false
) {
    companion object {
        // Friend rights constants from Second Life protocol
        const val GRANT_ONLINE_STATUS = 1      // Can see online status
        const val GRANT_MAP_LOCATION = 2       // Can see map location
        const val GRANT_MODIFY_OBJECTS = 4     // Can modify objects
        
        /**
         * Check if specific right is granted
         */
        fun hasRight(rights: Int, right: Int): Boolean {
            return (rights and right) != 0
        }
        
        /**
         * Grant a specific right
         */
        fun grantRight(rights: Int, right: Int): Int {
            return rights or right
        }
        
        /**
         * Revoke a specific right
         */
        fun revokeRight(rights: Int, right: Int): Int {
            return rights and right.inv()
        }
    }
    
    /**
     * Check if friend can see our online status
     */
    fun canSeeOnlineStatus(): Boolean = hasRight(rightsGiven, GRANT_ONLINE_STATUS)
    
    /**
     * Check if friend can see our map location
     */
    fun canSeeMapLocation(): Boolean = hasRight(rightsGiven, GRANT_MAP_LOCATION)
    
    /**
     * Check if friend can modify our objects
     */
    fun canModifyObjects(): Boolean = hasRight(rightsGiven, GRANT_MODIFY_OBJECTS)
    
    /**
     * Check if we can see friend's online status
     */
    fun weCanSeeOnlineStatus(): Boolean = hasRight(rightsHas, GRANT_ONLINE_STATUS)
    
    /**
     * Check if we can see friend's map location
     */
    fun weCanSeeMapLocation(): Boolean = hasRight(rightsHas, GRANT_MAP_LOCATION)
    
    /**
     * Check if we can modify friend's objects
     */
    fun weCanModifyObjects(): Boolean = hasRight(rightsHas, GRANT_MODIFY_OBJECTS)
}