package com.linkpoint.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.linkpoint.database.converters.DateConverter
import com.linkpoint.database.converters.UUIDConverter
import java.util.Date
import java.util.UUID

/**
 * GroupMember Entity
 * Represents a member of a Second Life group.
 */
@Entity(tableName = "group_members")
@TypeConverters(UUIDConverter::class)
data class GroupMemberEntity(
    @PrimaryKey
    val userID: UUID,
    
    val groupID: UUID,
    val requestID: UUID,
    val contribution: Int = 0,
    val onlineStatus: String? = null,
    val agentPowers: Long = 0,
    val title: String? = null,
    val isOwner: Boolean = false
)

/**
 * GroupMemberList Entity
 * Tracks group member list requests.
 */
@Entity(tableName = "group_member_lists")
@TypeConverters(UUIDConverter::class)
data class GroupMemberListEntity(
    @PrimaryKey
    val groupID: UUID,
    
    val requestID: UUID
)

/**
 * GroupRoleMember Entity
 * Represents a member's role in a group.
 */
@Entity(tableName = "group_role_members")
@TypeConverters(UUIDConverter::class)
data class GroupRoleMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val groupID: UUID,
    val requestID: UUID,
    val roleID: UUID,
    val memberID: UUID
)

/**
 * GroupRoleMemberList Entity
 * Tracks group role member list requests.
 */
@Entity(tableName = "group_role_member_lists")
@TypeConverters(UUIDConverter::class)
data class GroupRoleMemberListEntity(
    @PrimaryKey
    val groupID: UUID,
    
    val requestID: UUID
)

/**
 * MoneyTransaction Entity
 * Tracks L$ (Linden Dollar) transactions.
 */
@Entity(tableName = "money_transactions")
@TypeConverters(DateConverter::class, UUIDConverter::class)
data class MoneyTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val timestamp: Date,
    val agentUUID: UUID,
    val transactionAmount: Int,
    val newBalance: Int
) {
    /**
     * Check if this is a credit (positive) transaction
     */
    fun isCredit(): Boolean = transactionAmount > 0
    
    /**
     * Check if this is a debit (negative) transaction
     */
    fun isDebit(): Boolean = transactionAmount < 0
    
    /**
     * Get absolute transaction amount
     */
    fun getAbsoluteAmount(): Int = kotlin.math.abs(transactionAmount)
}

/**
 * CachedAsset Entity
 * Caches Second Life assets (textures, sounds, etc.).
 */
@Entity(tableName = "cached_assets")
data class CachedAssetEntity(
    @PrimaryKey
    val key: String,
    
    val status: Int = 0,
    val data: ByteArray? = null,
    val mustRevalidate: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as CachedAssetEntity
        
        if (key != other.key) return false
        if (status != other.status) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (mustRevalidate != other.mustRevalidate) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + status
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + mustRevalidate.hashCode()
        return result
    }
    
    /**
     * Check if asset data is available
     */
    fun hasData(): Boolean = data != null && data.isNotEmpty()
    
    /**
     * Get asset size in bytes
     */
    fun getSize(): Int = data?.size ?: 0
    
    companion object {
        // Asset status codes
        const val STATUS_PENDING = 0
        const val STATUS_COMPLETE = 1
        const val STATUS_ERROR = 2
    }
}

/**
 * CachedResponse Entity
 * Caches HTTP responses.
 */
@Entity(tableName = "cached_responses")
data class CachedResponseEntity(
    @PrimaryKey
    val url: String,
    
    val response: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Check if cache is still valid (within 1 hour)
     */
    fun isValid(maxAgeMillis: Long = 3600000): Boolean {
        return System.currentTimeMillis() - timestamp < maxAgeMillis
    }
}

/**
 * MuteListCachedData Entity
 * Caches mute list data.
 */
@Entity(tableName = "mute_list_cached_data")
data class MuteListCachedDataEntity(
    @PrimaryKey
    val key: String,
    
    val data: String? = null
)

/**
 * SearchGridResult Entity
 * Caches grid search results.
 */
@Entity(tableName = "search_grid_results")
@TypeConverters(UUIDConverter::class)
data class SearchGridResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val searchTerm: String,
    val resultUUID: UUID,
    val resultName: String,
    val resultType: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Check if result is still fresh (within 5 minutes)
     */
    fun isFresh(maxAgeMillis: Long = 300000): Boolean {
        return System.currentTimeMillis() - timestamp < maxAgeMillis
    }
}