package com.linkpoint.database.dao

import androidx.room.*
import com.linkpoint.database.entities.SearchGridResultEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for SearchGridResult entities
 */
@Dao
interface SearchGridResultDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: SearchGridResultEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<SearchGridResultEntity>): List<Long>
    
    @Update
    suspend fun update(result: SearchGridResultEntity)
    
    @Delete
    suspend fun delete(result: SearchGridResultEntity)
    
    @Query("SELECT * FROM search_grid_results WHERE id = :id")
    suspend fun getById(id: Long): SearchGridResultEntity?
    
    @Query("SELECT * FROM search_grid_results WHERE searchTerm = :searchTerm ORDER BY timestamp DESC")
    suspend fun getBySearchTerm(searchTerm: String): List<SearchGridResultEntity>
    
    @Query("SELECT * FROM search_grid_results WHERE searchTerm = :searchTerm ORDER BY timestamp DESC")
    fun getBySearchTermFlow(searchTerm: String): Flow<List<SearchGridResultEntity>>
    
    @Query("SELECT * FROM search_grid_results WHERE resultType = :type ORDER BY timestamp DESC")
    suspend fun getByType(type: String): List<SearchGridResultEntity>
    
    @Query("SELECT * FROM search_grid_results WHERE resultUUID = :uuid")
    suspend fun getByUUID(uuid: UUID): List<SearchGridResultEntity>
    
    @Query("SELECT * FROM search_grid_results ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<SearchGridResultEntity>
    
    @Query("SELECT DISTINCT searchTerm FROM search_grid_results ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSearchTerms(limit: Int): List<String>
    
    @Query("DELETE FROM search_grid_results WHERE searchTerm = :searchTerm")
    suspend fun deleteBySearchTerm(searchTerm: String)
    
    @Query("DELETE FROM search_grid_results WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long): Int
    
    @Query("DELETE FROM search_grid_results")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM search_grid_results")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(DISTINCT searchTerm) FROM search_grid_results")
    suspend fun getUniqueSearchTermCount(): Int
}