package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.ChatterEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for Chatter entities.
 */
@Dao
interface ChatterDao {
    
    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatter: ChatterEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chatters: List<ChatterEntity>): List<Long>
    
    // ========== UPDATE ==========
    @Update
    suspend fun update(chatter: ChatterEntity)
    
    @Query("UPDATE chatters SET unreadCount = :count WHERE id = :chatterId")
    suspend fun updateUnreadCount(chatterId: Long, count: Int)
    
    @Query("UPDATE chatters SET unreadCount = 0 WHERE id = :chatterId")
    suspend fun clearUnreadCount(chatterId: Long)
    
    @Query("UPDATE chatters SET muted = :muted WHERE id = :chatterId")
    suspend fun setMuted(chatterId: Long, muted: Boolean)
    
    @Query("UPDATE chatters SET active = :active WHERE id = :chatterId")
    suspend fun setActive(chatterId: Long, active: Boolean)
    
    @Query("UPDATE chatters SET lastMessageID = :messageId, lastSessionID = :sessionId WHERE id = :chatterId")
    suspend fun updateLastMessage(chatterId: Long, messageId: Long, sessionId: UUID?)
    
    // ========== DELETE ==========
    @Delete
    suspend fun delete(chatter: ChatterEntity)
    
    @Query("DELETE FROM chatters WHERE id = :chatterId")
    suspend fun deleteById(chatterId: Long)
    
    @Query("DELETE FROM chatters WHERE active = 0")
    suspend fun deleteInactive()
    
    @Query("DELETE FROM chatters")
    suspend fun deleteAll()
    
    // ========== QUERY ==========
    @Query("SELECT * FROM chatters WHERE id = :chatterId")
    suspend fun getById(chatterId: Long): ChatterEntity?
    
    @Query("SELECT * FROM chatters WHERE id = :chatterId")
    fun getByIdFlow(chatterId: Long): Flow<ChatterEntity?>
    
    @Query("SELECT * FROM chatters WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: UUID): ChatterEntity?
    
    @Query("SELECT * FROM chatters WHERE uuid = :uuid")
    fun getByUUIDFlow(uuid: UUID): Flow<ChatterEntity?>
    
    @Query("SELECT * FROM chatters WHERE active = 1 ORDER BY id DESC")
    suspend fun getActive(): List<ChatterEntity>
    
    @Query("SELECT * FROM chatters WHERE active = 1 ORDER BY id DESC")
    fun getActiveFlow(): Flow<List<ChatterEntity>>
    
    @Query("SELECT * FROM chatters WHERE muted = 0 ORDER BY id DESC")
    suspend fun getUnmuted(): List<ChatterEntity>
    
    @Query("SELECT * FROM chatters WHERE unreadCount > 0 ORDER BY unreadCount DESC")
    suspend fun getWithUnread(): List<ChatterEntity>
    
    @Query("SELECT * FROM chatters WHERE unreadCount > 0 ORDER BY unreadCount DESC")
    fun getWithUnreadFlow(): Flow<List<ChatterEntity>>
    
    @Query("SELECT * FROM chatters WHERE type = :type ORDER BY id DESC")
    suspend fun getByType(type: Int): List<ChatterEntity>
    
    @Query("SELECT * FROM chatters ORDER BY id DESC")
    suspend fun getAll(): List<ChatterEntity>
    
    @Query("SELECT * FROM chatters ORDER BY id DESC")
    fun getAllFlow(): Flow<List<ChatterEntity>>
    
    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM chatters")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM chatters WHERE active = 1")
    suspend fun getActiveCount(): Int
    
    @Query("SELECT COUNT(*) FROM chatters WHERE unreadCount > 0")
    suspend fun getUnreadCount(): Int
    
    @Query("SELECT SUM(unreadCount) FROM chatters")
    suspend fun getTotalUnreadMessages(): Int?
    
    @Query("SELECT COUNT(*) FROM chatters WHERE muted = 1")
    suspend fun getMutedCount(): Int
}