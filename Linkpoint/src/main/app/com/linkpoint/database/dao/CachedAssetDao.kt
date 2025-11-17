package com.linkpoint.database.dao

import androidx.room.*
import com.linkpoint.database.entities.CachedAssetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Modern Room DAO for CachedAsset entities
 */
@Dao
interface CachedAssetDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: CachedAssetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<CachedAssetEntity>)
    
    @Update
    suspend fun update(asset: CachedAssetEntity)
    
    @Delete
    suspend fun delete(asset: CachedAssetEntity)
    
    @Query("SELECT * FROM cached_assets WHERE key = :key")
    suspend fun getByKey(key: String): CachedAssetEntity?
    
    @Query("SELECT * FROM cached_assets WHERE key = :key")
    fun getByKeyFlow(key: String): Flow<CachedAssetEntity?>
    
    @Query("SELECT * FROM cached_assets WHERE status = :status")
    suspend fun getByStatus(status: Int): List<CachedAssetEntity>
    
    @Query("SELECT * FROM cached_assets WHERE data IS NOT NULL")
    suspend fun getAllWithData(): List<CachedAssetEntity>
    
    @Query("SELECT * FROM cached_assets")
    suspend fun getAll(): List<CachedAssetEntity>
    
    @Query("DELETE FROM cached_assets WHERE key = :key")
    suspend fun deleteByKey(key: String)
    
    @Query("DELETE FROM cached_assets WHERE mustRevalidate = 1")
    suspend fun deleteStale()
    
    @Query("DELETE FROM cached_assets WHERE data IS NULL")
    suspend fun deleteEmpty()
    
    @Query("DELETE FROM cached_assets")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM cached_assets")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM cached_assets WHERE data IS NOT NULL")
    suspend fun getCountWithData(): Int
    
    @Query("SELECT SUM(LENGTH(data)) FROM cached_assets WHERE data IS NOT NULL")
    suspend fun getTotalSize(): Long?
    
    @Query("UPDATE cached_assets SET status = :status WHERE key = :key")
    suspend fun updateStatus(key: String, status: Int)
}