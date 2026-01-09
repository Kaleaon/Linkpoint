package com.linkpoint.database.dao

import androidx.room.*
import com.linkpoint.database.entities.UserPicEntity
import kotlinx.coroutines.flow.Flow

/**
 * Modern Room DAO for UserPic cache
 */
@Dao
interface UserPicDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPic: UserPicEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userPics: List<UserPicEntity>)
    
    @Update
    suspend fun update(userPic: UserPicEntity)
    
    @Delete
    suspend fun delete(userPic: UserPicEntity)
    
    @Query("SELECT * FROM user_pics WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: String): UserPicEntity?
    
    @Query("SELECT * FROM user_pics WHERE uuid = :uuid")
    fun getByUUIDFlow(uuid: String): Flow<UserPicEntity?>
    
    @Query("SELECT * FROM user_pics WHERE bitmap IS NOT NULL")
    suspend fun getAllWithPictures(): List<UserPicEntity>
    
    @Query("SELECT * FROM user_pics WHERE bitmap IS NOT NULL")
    fun getAllWithPicturesFlow(): Flow<List<UserPicEntity>>
    
    @Query("SELECT * FROM user_pics")
    suspend fun getAll(): List<UserPicEntity>
    
    @Query("DELETE FROM user_pics WHERE uuid = :uuid")
    suspend fun deleteByUUID(uuid: String)
    
    @Query("DELETE FROM user_pics WHERE bitmap IS NULL")
    suspend fun deleteEmpty()
    
    @Query("DELETE FROM user_pics")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM user_pics")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM user_pics WHERE bitmap IS NOT NULL")
    suspend fun getCountWithPictures(): Int
    
    @Query("SELECT SUM(LENGTH(bitmap)) FROM user_pics WHERE bitmap IS NOT NULL")
    suspend fun getTotalSize(): Long?
}