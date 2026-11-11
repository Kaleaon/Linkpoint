package com.linkpoint.grid.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SQLite Entity representing a grid profile in the local database cache.
 */
@Entity(tableName = "grid_profiles")
data class GridProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gridNick: String,
    val loginUri: String,
    val helperUri: String? = null,
    val website: String? = null,
    val support: String? = null,
    val registerUri: String? = null,
    val passwordUri: String? = null,
    val logoUrl: String? = null,
    val status: String = "online", // "online", "offline", "degraded", "unknown"
    val isCustom: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
