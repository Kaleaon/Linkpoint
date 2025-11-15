package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.CachedResponseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Modern Room DAO for CachedResponse entities
 */
@Dao
interface CachedResponseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: CachedResponseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(responses: List<CachedResponseEntity>)
    
    @Update
    suspend fun update(response: CachedResponseEntity)
    
    @Delete
    suspend fun delete(response: CachedResponseEntity)
    
    @Query("SELECT * FROM cached_responses WHERE url = :url")
    suspend fun getByUrl(url: String): CachedResponseEntity?
    
    @Query("SELECT * FROM cached_responses WHERE url = :url")
    fun getByUrlFlow(url: String): Flow<CachedResponseEntity?>
    
    @Query("SELECT * FROM cached_responses")
    suspend fun getAll(): List<CachedResponseEntity>
    
    @Query("DELETE FROM cached_responses WHERE url = :url")
    suspend fun deleteByUrl(url: String)
    
    @Query("DELETE FROM cached_responses WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long): Int
    
    @Query("DELETE FROM cached_responses")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM cached_responses")
    suspend fun getCount(): Int
}