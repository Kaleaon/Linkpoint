package com.linkpoint.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modern Room entity for UserPic (avatar profile picture cache).
 * 
 * Caches avatar profile pictures to avoid repeated downloads.
 * Stores the image as a byte array (bitmap data).
 * 
 * Reference: Legacy Lumiya UserPic.java
 */
@Entity(tableName = "user_pics")
data class UserPicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // User UUID as string (for compatibility)
    val uuid: String,
    
    // Cached bitmap data
    val bitmap: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as UserPicEntity
        
        if (id != other.id) return false
        if (uuid != other.uuid) return false
        if (bitmap != null) {
            if (other.bitmap == null) return false
            if (!bitmap.contentEquals(other.bitmap)) return false
        } else if (other.bitmap != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + (bitmap?.contentHashCode() ?: 0)
        return result
    }
    
    /**
     * Check if picture data is available
     */
    fun hasPicture(): Boolean = bitmap != null && bitmap.isNotEmpty()
    
    /**
     * Get picture size in bytes
     */
    fun getPictureSize(): Int = bitmap?.size ?: 0
}