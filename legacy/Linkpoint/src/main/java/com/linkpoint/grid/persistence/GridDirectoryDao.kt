package com.linkpoint.grid.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local grid profiles SQLite cache.
 */
@Dao
interface GridDirectoryDao {

    @Query("SELECT * FROM grid_profiles ORDER BY isCustom ASC, name ASC")
    fun getAllGridsFlow(): Flow<List<GridProfileEntity>>

    @Query("SELECT * FROM grid_profiles ORDER BY isCustom ASC, name ASC")
    suspend fun getAllGrids(): List<GridProfileEntity>

    @Query("SELECT * FROM grid_profiles WHERE id = :id LIMIT 1")
    suspend fun getGridById(id: String): GridProfileEntity?

    @Query("SELECT * FROM grid_profiles WHERE loginUri = :loginUri OR loginUri = :loginUriWithSlash LIMIT 1")
    suspend fun getGridByLoginUri(loginUri: String, loginUriWithSlash: String = "$loginUri/"): GridProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrids(grids: List<GridProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrid(grid: GridProfileEntity)

    @Query("DELETE FROM grid_profiles WHERE id = :id")
    suspend fun deleteGrid(id: String)

    @Query("DELETE FROM grid_profiles WHERE isCustom = 0")
    suspend fun clearDirectoryGrids()
}
