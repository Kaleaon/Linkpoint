package com.linkpoint.inventory.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        InventoryFolderEntity::class,
        InventoryItemEntity::class,
        InventoryFolderVersionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun inventoryFolderDao(): InventoryFolderDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun inventoryFolderVersionDao(): InventoryFolderVersionDao
}
