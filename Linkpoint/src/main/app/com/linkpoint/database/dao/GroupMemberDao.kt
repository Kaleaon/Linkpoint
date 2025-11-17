package com.linkpoint.database.dao

import androidx.room.*
import com.linkpoint.database.entities.GroupMemberEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Modern Room DAO for GroupMember entities
 */
@Dao
interface GroupMemberDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: GroupMemberEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<GroupMemberEntity>)
    
    @Update
    suspend fun update(member: GroupMemberEntity)
    
    @Delete
    suspend fun delete(member: GroupMemberEntity)
    
    @Query("SELECT * FROM group_members WHERE groupID = :groupId ORDER BY title")
    suspend fun getByGroupId(groupId: UUID): List<GroupMemberEntity>
    
    @Query("SELECT * FROM group_members WHERE groupID = :groupId ORDER BY title")
    fun getByGroupIdFlow(groupId: UUID): Flow<List<GroupMemberEntity>>
    
    @Query("SELECT * FROM group_members WHERE userID = :userId")
    suspend fun getByUserId(userId: UUID): List<GroupMemberEntity>
    
    @Query("SELECT * FROM group_members WHERE userID = :userId")
    fun getByUserIdFlow(userId: UUID): Flow<List<GroupMemberEntity>>
    
    @Query("SELECT * FROM group_members WHERE groupID = :groupId AND isOwner = 1")
    suspend fun getOwners(groupId: UUID): List<GroupMemberEntity>
    
    @Query("SELECT * FROM group_members WHERE groupID = :groupId AND onlineStatus = 'online'")
    suspend fun getOnlineMembers(groupId: UUID): List<GroupMemberEntity>
    
    @Query("DELETE FROM group_members WHERE groupID = :groupId")
    suspend fun deleteByGroupId(groupId: UUID)
    
    @Query("DELETE FROM group_members WHERE userID = :userId")
    suspend fun deleteByUserId(userId: UUID)
    
    @Query("DELETE FROM group_members")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM group_members WHERE groupID = :groupId")
    suspend fun getMemberCount(groupId: UUID): Int
    
    @Query("SELECT COUNT(*) FROM group_members WHERE groupID = :groupId AND onlineStatus = 'online'")
    suspend fun getOnlineMemberCount(groupId: UUID): Int
}