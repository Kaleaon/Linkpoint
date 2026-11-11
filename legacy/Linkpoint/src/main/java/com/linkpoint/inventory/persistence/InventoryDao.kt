package com.linkpoint.inventory.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InventoryFolderDao {
    @Query("SELECT * FROM inventory_folders WHERE folderId = :folderId LIMIT 1")
    suspend fun getFolder(folderId: String): InventoryFolderEntity?

    @Query("SELECT * FROM inventory_folders WHERE parentId = :parentId")
    suspend fun getFoldersByParent(parentId: String): List<InventoryFolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InventoryFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<InventoryFolderEntity>)

    @Query("DELETE FROM inventory_folders WHERE folderId = :folderId")
    suspend fun delete(folderId: String)

    @Query("DELETE FROM inventory_folders WHERE parentId = :parentId")
    suspend fun deleteChildrenOfParent(parentId: String)
}

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items WHERE itemId = :itemId LIMIT 1")
    suspend fun getItem(itemId: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE parentId = :parentId")
    suspend fun getItemsByParent(parentId: String): List<InventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InventoryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<InventoryItemEntity>)

    @Query("DELETE FROM inventory_items WHERE itemId = :itemId")
    suspend fun delete(itemId: String)

    @Query("DELETE FROM inventory_items WHERE parentId = :parentId")
    suspend fun deleteByParentId(parentId: String)
}

@Dao
interface InventoryFolderVersionDao {
    @Query("SELECT * FROM inventory_folder_versions WHERE folderId = :folderId LIMIT 1")
    suspend fun getVersion(folderId: String): InventoryFolderVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InventoryFolderVersionEntity)

    @Query("DELETE FROM inventory_folder_versions WHERE folderId = :folderId")
    suspend fun delete(folderId: String)
}
