package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

/**
 * FriendDao - DAO for Friend entities
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

/**
 * UserDao - DAO for User entities
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
    
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
    
    @Query("UPDATE users SET displayName = :displayName WHERE uuid = :uuid")
    suspend fun updateDisplayName(uuid: UUID, displayName: String)
    
    @Query("UPDATE users SET userName = :userName WHERE uuid = :uuid")
    suspend fun updateUserName(uuid: UUID, userName: String)
    
    @Query("UPDATE users SET isFriend = :isFriend WHERE uuid = :uuid")
    suspend fun updateFriendStatus(uuid: UUID, isFriend: Boolean)
}

/**
 * UserNameDao - DAO for UserName cache
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
    
    @Query("DELETE FROM user_names WHERE uuid = :uuid")
    suspend fun deleteByUUID(uuid: UUID)
}

/**
 * UserPicDao - DAO for UserPic cache
 */
@Dao
interface UserPicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPic: UserPicEntity): Long
    
    @Update
    suspend fun update(userPic: UserPicEntity)
    
    @Delete
    suspend fun delete(userPic: UserPicEntity)
    
    @Query("SELECT * FROM user_pics WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: String): UserPicEntity?
    
    @Query("SELECT * FROM user_pics WHERE bitmap IS NOT NULL")
    suspend fun getAllWithPictures(): List<UserPicEntity>
    
    @Query("DELETE FROM user_pics WHERE uuid = :uuid")
    suspend fun deleteByUUID(uuid: String)
    
    @Query("DELETE FROM user_pics WHERE bitmap IS NULL")
    suspend fun deleteEmpty()
}

/**
 * GroupMemberDao - DAO for GroupMember entities
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
    
    @Query("SELECT * FROM group_members WHERE groupID = :groupId AND isOwner = 1")
    suspend fun getOwners(groupId: UUID): List<GroupMemberEntity>
    
    @Query("DELETE FROM group_members WHERE groupID = :groupId")
    suspend fun deleteByGroupId(groupId: UUID)
    
    @Query("SELECT COUNT(*) FROM group_members WHERE groupID = :groupId")
    suspend fun getMemberCount(groupId: UUID): Int
}

/**
 * MoneyTransactionDao - DAO for MoneyTransaction entities
 */
@Dao
interface MoneyTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: MoneyTransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<MoneyTransactionEntity>)
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<MoneyTransactionEntity>>
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getByDateRange(startDate: Date, endDate: Date): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE transactionAmount > 0 ORDER BY timestamp DESC")
    suspend fun getCredits(): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE transactionAmount < 0 ORDER BY timestamp DESC")
    suspend fun getDebits(): List<MoneyTransactionEntity>
    
    @Query("SELECT SUM(transactionAmount) FROM money_transactions")
    suspend fun getTotalChange(): Int?
    
    @Query("DELETE FROM money_transactions WHERE timestamp < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: Date)
}

/**
 * CachedAssetDao - DAO for CachedAsset entities
 */
@Dao
interface CachedAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: CachedAssetEntity)
    
    @Update
    suspend fun update(asset: CachedAssetEntity)
    
    @Query("SELECT * FROM cached_assets WHERE key = :key")
    suspend fun getByKey(key: String): CachedAssetEntity?
    
    @Query("SELECT * FROM cached_assets WHERE status = :status")
    suspend fun getByStatus(status: Int): List<CachedAssetEntity>
    
    @Query("DELETE FROM cached_assets WHERE key = :key")
    suspend fun deleteByKey(key: String)
    
    @Query("DELETE FROM cached_assets WHERE mustRevalidate = 1")
    suspend fun deleteStale()
    
    @Query("SELECT COUNT(*) FROM cached_assets")
    suspend fun getCount(): Int
    
    @Query("SELECT SUM(LENGTH(data)) FROM cached_assets WHERE data IS NOT NULL")
    suspend fun getTotalSize(): Long?
}

/**
 * CachedResponseDao - DAO for CachedResponse entities
 */
@Dao
interface CachedResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: CachedResponseEntity)
    
    @Query("SELECT * FROM cached_responses WHERE url = :url")
    suspend fun getByUrl(url: String): CachedResponseEntity?
    
    @Query("DELETE FROM cached_responses WHERE url = :url")
    suspend fun deleteByUrl(url: String)
    
    @Query("DELETE FROM cached_responses WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)
    
    @Query("DELETE FROM cached_responses")
    suspend fun deleteAll()
}

/**
 * SearchGridResultDao - DAO for SearchGridResult entities
 */
@Dao
interface SearchGridResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: SearchGridResultEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<SearchGridResultEntity>)
    
    @Query("SELECT * FROM search_grid_results WHERE searchTerm = :searchTerm ORDER BY timestamp DESC")
    suspend fun getBySearchTerm(searchTerm: String): List<SearchGridResultEntity>
    
    @Query("SELECT * FROM search_grid_results WHERE resultType = :type ORDER BY timestamp DESC")
    suspend fun getByType(type: String): List<SearchGridResultEntity>
    
    @Query("DELETE FROM search_grid_results WHERE searchTerm = :searchTerm")
    suspend fun deleteBySearchTerm(searchTerm: String)
    
    @Query("DELETE FROM search_grid_results WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)
    
    @Query("DELETE FROM search_grid_results")
    suspend fun deleteAll()
}