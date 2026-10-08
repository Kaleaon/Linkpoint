package com.linkpoint.inventory.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_folders")
data class InventoryFolderEntity(
    @PrimaryKey val folderId: String,
    val parentId: String,
    val name: String,
    val type: Int,
    val version: Int,
    val updatedAtEpochMs: Long
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val itemId: String,
    val parentId: String,
    val name: String,
    val assetId: String,
    val inventoryType: Int,
    val assetType: Int,
    val version: Int,
    val updatedAtEpochMs: Long
)

@Entity(tableName = "inventory_folder_versions")
data class InventoryFolderVersionEntity(
    @PrimaryKey val folderId: String,
    val version: Int,
    val updatedAtEpochMs: Long
)
