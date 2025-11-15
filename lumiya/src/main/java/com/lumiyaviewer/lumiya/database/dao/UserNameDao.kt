package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.UserNameEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for UserName cache
 */
@Dao
interface UserNameDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userName: UserNameEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userNames: List<UserNameEntity>)
    
    @Update
    suspend fun update(userName: UserNameEntity)
    
    @Delete
    suspend fun delete(userName: UserNameEntity)
    
    @Query("SELECT * FROM user_names WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: UUID): UserNameEntity?
    
    @Query("SELECT * FROM user_names WHERE uuid = :uuid")
    fun getByUUIDFlow(uuid: UUID): Flow<UserNameEntity?>
    
    @Query("SELECT * FROM user_names WHERE isBadUUID = 0 AND (userName IS NULL OR displayName IS NULL)")
    suspend fun getIncomplete(): List<UserNameEntity>
    
    @Query("SELECT * FROM user_names")
    suspend fun getAll(): List<UserNameEntity>
    
    @Query("SELECT * FROM user_names")
    fun getAllFlow(): Flow<List<UserNameEntity>>
    
    @Query("DELETE FROM user_names WHERE uuid = :uuid")
    suspend fun deleteByUUID(uuid: UUID)
    
    @Query("DELETE FROM user_names")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM user_names")
    suspend fun getCount(): Int
}