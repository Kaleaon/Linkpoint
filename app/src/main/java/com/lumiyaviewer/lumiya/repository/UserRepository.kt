package com.lumiyaviewer.lumiya.repository

import com.lumiyaviewer.lumiya.database.dao.FriendDao
import com.lumiyaviewer.lumiya.database.dao.UserDao
import com.lumiyaviewer.lumiya.database.dao.UserNameDao
import com.lumiyaviewer.lumiya.database.dao.UserPicDao
import com.lumiyaviewer.lumiya.database.entities.FriendEntity
import com.lumiyaviewer.lumiya.database.entities.UserEntity
import com.lumiyaviewer.lumiya.database.entities.UserNameEntity
import com.lumiyaviewer.lumiya.database.entities.UserPicEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user-related data operations.
 * 
 * Manages users, friends, user names, and profile pictures.
 * Provides caching and synchronization with Second Life servers.
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val friendDao: FriendDao,
    private val userNameDao: UserNameDao,
    private val userPicDao: UserPicDao
) {
    
    // ========== USERS ==========
    
    /**
     * Get user by UUID
     */
    suspend fun getUserByUUID(uuid: UUID): UserEntity? {
        return userDao.getByUUID(uuid)
    }
    
    /**
     * Get user by UUID as Flow
     */
    fun getUserByUUIDFlow(uuid: UUID): Flow<UserEntity?> {
        return userDao.getByUUIDFlow(uuid)
    }
    
    /**
     * Get or create user
     */
    suspend fun getOrCreateUser(uuid: UUID): UserEntity {
        val existing = userDao.getByUUID(uuid)
        if (existing != null) {
            return existing
        }
        
        val newUser = UserEntity(uuid = uuid)
        val id = userDao.insert(newUser)
        return newUser.copy(id = id)
    }
    
    /**
     * Update user names
     */
    suspend fun updateUserNames(uuid: UUID, userName: String?, displayName: String?) {
        userName?.let { userDao.updateUserName(uuid, it) }
        displayName?.let { userDao.updateDisplayName(uuid, it) }
        
        // Also update name cache
        val nameEntity = UserNameEntity(
            uuid = uuid,
            userName = userName,
            displayName = displayName
        )
        userNameDao.insert(nameEntity)
    }
    
    /**
     * Search users
     */
    suspend fun searchUsers(query: String): List<UserEntity> {
        return userDao.search(query)
    }
    
    /**
     * Get users needing name fetch
     */
    suspend fun getUsersNeedingNameFetch(): List<UserEntity> {
        return userDao.getUsersNeedingNameFetch()
    }
    
    // ========== FRIENDS ==========
    
    /**
     * Get all friends
     */
    fun getAllFriends(): Flow<List<FriendEntity>> {
        return friendDao.getAllFlow()
    }
    
    /**
     * Get online friends
     */
    fun getOnlineFriends(): Flow<List<FriendEntity>> {
        return friendDao.getOnlineFriendsFlow()
    }
    
    /**
     * Add friend
     */
    suspend fun addFriend(uuid: UUID, rightsGiven: Int = 0, rightsHas: Int = 0) {
        val friend = FriendEntity(
            uuid = uuid,
            rightsGiven = rightsGiven,
            rightsHas = rightsHas,
            isOnline = false
        )
        friendDao.insert(friend)
        
        // Update user's friend status
        userDao.updateFriendStatus(uuid, true)
    }
    
    /**
     * Remove friend
     */
    suspend fun removeFriend(uuid: UUID) {
        friendDao.deleteByUUID(uuid)
        userDao.updateFriendStatus(uuid, false)
    }
    
    /**
     * Update friend online status
     */
    suspend fun updateFriendOnlineStatus(uuid: UUID, isOnline: Boolean) {
        friendDao.updateOnlineStatus(uuid, isOnline)
    }
    
    /**
     * Update friend rights
     */
    suspend fun updateFriendRights(uuid: UUID, rightsGiven: Int, rightsHas: Int) {
        friendDao.updateRightsGiven(uuid, rightsGiven)
        friendDao.updateRightsHas(uuid, rightsHas)
    }
    
    /**
     * Get friend count
     */
    suspend fun getFriendCount(): Int {
        return friendDao.getCount()
    }
    
    /**
     * Get online friend count
     */
    suspend fun getOnlineFriendCount(): Int {
        return friendDao.getOnlineCount()
    }
    
    // ========== USER NAMES ==========
    
    /**
     * Get cached user name
     */
    suspend fun getCachedUserName(uuid: UUID): UserNameEntity? {
        return userNameDao.getByUUID(uuid)
    }
    
    /**
     * Cache user name
     */
    suspend fun cacheUserName(uuid: UUID, userName: String?, displayName: String?) {
        val nameEntity = UserNameEntity(
            uuid = uuid,
            userName = userName,
            displayName = displayName
        )
        userNameDao.insert(nameEntity)
    }
    
    /**
     * Get incomplete user names (need fetching)
     */
    suspend fun getIncompleteUserNames(): List<UserNameEntity> {
        return userNameDao.getIncomplete()
    }
    
    // ========== USER PICTURES ==========
    
    /**
     * Get cached user picture
     */
    suspend fun getCachedUserPic(uuid: String): UserPicEntity? {
        return userPicDao.getByUUID(uuid)
    }
    
    /**
     * Cache user picture
     */
    suspend fun cacheUserPic(uuid: String, bitmap: ByteArray) {
        val picEntity = UserPicEntity(
            uuid = uuid,
            bitmap = bitmap
        )
        userPicDao.insert(picEntity)
    }
    
    /**
     * Delete cached user picture
     */
    suspend fun deleteCachedUserPic(uuid: String) {
        userPicDao.deleteByUUID(uuid)
    }
    
    /**
     * Clean up empty picture entries
     */
    suspend fun cleanupEmptyPictures() {
        userPicDao.deleteEmpty()
    }
}