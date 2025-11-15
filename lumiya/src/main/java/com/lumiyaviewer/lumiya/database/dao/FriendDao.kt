package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.FriendEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for Friend entities
 */
@Dao
interface FriendDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: FriendEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(friends: List<FriendEntity>)
    
    @Update
    suspend fun update(friend: FriendEntity)
    
    @Delete
    suspend fun delete(friend: FriendEntity)
    
    @Query("DELETE FROM friends WHERE uuid = :uuid")
    suspend fun deleteByUUID(uuid: UUID)
    
    @Query("DELETE FROM friends")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM friends WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: UUID): FriendEntity?
    
    @Query("SELECT * FROM friends WHERE uuid = :uuid")
    fun getByUUIDFlow(uuid: UUID): Flow<FriendEntity?>
    
    @Query("SELECT * FROM friends WHERE isOnline = 1 ORDER BY uuid")
    suspend fun getOnlineFriends(): List<FriendEntity>
    
    @Query("SELECT * FROM friends WHERE isOnline = 1 ORDER BY uuid")
    fun getOnlineFriendsFlow(): Flow<List<FriendEntity>>
    
    @Query("SELECT * FROM friends ORDER BY uuid")
    suspend fun getAll(): List<FriendEntity>
    
    @Query("SELECT * FROM friends ORDER BY uuid")
    fun getAllFlow(): Flow<List<FriendEntity>>
    
    @Query("SELECT COUNT(*) FROM friends")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM friends WHERE isOnline = 1")
    suspend fun getOnlineCount(): Int
    
    @Query("UPDATE friends SET isOnline = :isOnline WHERE uuid = :uuid")
    suspend fun updateOnlineStatus(uuid: UUID, isOnline: Boolean)
    
    @Query("UPDATE friends SET rightsGiven = :rights WHERE uuid = :uuid")
    suspend fun updateRightsGiven(uuid: UUID, rights: Int)
    
    @Query("UPDATE friends SET rightsHas = :rights WHERE uuid = :uuid")
    suspend fun updateRightsHas(uuid: UUID, rights: Int)
}