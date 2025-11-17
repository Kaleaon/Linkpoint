package com.linkpoint.database.dao

import androidx.room.*
import com.linkpoint.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for User entities
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>): List<Long>
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Delete
    suspend fun delete(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?
    
    @Query("SELECT * FROM users WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: UUID): UserEntity?
    
    @Query("SELECT * FROM users WHERE uuid = :uuid")
    fun getByUUIDFlow(uuid: UUID): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE isFriend = 1 ORDER BY displayName")
    suspend fun getFriends(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE isFriend = 1 ORDER BY displayName")
    fun getFriendsFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE userName LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE badUUID = 0 AND (userName IS NULL OR displayName IS NULL)")
    suspend fun getUsersNeedingNameFetch(): List<UserEntity>
    
    @Query("SELECT * FROM users ORDER BY displayName")
    suspend fun getAll(): List<UserEntity>
    
    @Query("SELECT * FROM users ORDER BY displayName")
    fun getAllFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE isFriend = 1")
    suspend fun getFriendCount(): Int
    
    @Query("UPDATE users SET displayName = :displayName WHERE uuid = :uuid")
    suspend fun updateDisplayName(uuid: UUID, displayName: String)
    
    @Query("UPDATE users SET userName = :userName WHERE uuid = :uuid")
    suspend fun updateUserName(uuid: UUID, userName: String)
    
    @Query("UPDATE users SET isFriend = :isFriend WHERE uuid = :uuid")
    suspend fun updateFriendStatus(uuid: UUID, isFriend: Boolean)
    
    @Query("UPDATE users SET rightsGiven = :rights WHERE uuid = :uuid")
    suspend fun updateRightsGiven(uuid: UUID, rights: Int)
    
    @Query("UPDATE users SET rightsHas = :rights WHERE uuid = :uuid")
    suspend fun updateRightsHas(uuid: UUID, rights: Int)
}