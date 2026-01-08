package com.lumiyaviewer.lumiya.repository

import com.lumiyaviewer.lumiya.dao.Friend
import com.lumiyaviewer.lumiya.dao.FriendDao
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user and friend-related data operations.
 * 
 * Provides a clean API for accessing friend data with a focus
 * on friend management operations.
 * 
 * This follows the Repository pattern from Android Architecture Components.
 */
@Singleton
class UserRepository @Inject constructor(
    private val friendDao: FriendDao
) {
    
    // ========== FRIENDS ==========
    
    /**
     * Get all friends
     */
    suspend fun getAllFriends(): List<Friend> {
        return friendDao.getAll()
    }
    
    /**
     * Get online friends
     */
    suspend fun getOnlineFriends(): List<Friend> {
        return friendDao.getOnline()
    }
    
    /**
     * Add a friend
     */
    suspend fun addFriend(friend: Friend): Long {
        return friendDao.insert(friend)
    }
    
    /**
     * Remove a friend
     */
    suspend fun removeFriend(uuid: UUID) {
        friendDao.deleteByUUID(uuid)
    }
    
    /**
     * Update friend
     */
    suspend fun updateFriend(friend: Friend) {
        friendDao.update(friend)
    }
    
    /**
     * Get friend by UUID
     */
    suspend fun getFriendByUUID(uuid: UUID): Friend? {
        return friendDao.getByUUID(uuid)
    }

}
