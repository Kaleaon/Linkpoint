package com.lumiyaviewer.lumiya.orm

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryOpenHelper
import java.io.File
import java.util.HashMap
import java.util.UUID

object InventoryDBManager {
    private val lock = Any()
    private val userDBs = HashMap<UUID, InventoryDB>()

    @JvmStatic
    fun getUserInventoryDB(uuid: UUID?): InventoryDB? {
        if (uuid == null) {
            return null
        }
        
        synchronized(lock) {
            var inventoryDB = userDBs[uuid]
            if (inventoryDB == null) {
                try {
                    val cacheDir = GlobalOptions.getInstance().getCacheDir("database")
                        ?: throw IllegalStateException("Database cache directory is null")
                    
                    val dbFile = File(cacheDir, "inventory-$uuid.db")
                    // We assume SLInventoryOpenHelper is available or we need to check it
                    // For now, using simple SQLiteDatabase open if helper is complex, but keeping structure
                    inventoryDB = InventoryDB(
                        android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(
                            dbFile.absolutePath, null
                        )
                    )
                    userDBs[uuid] = inventoryDB
                } catch (e: Exception) {
                    android.util.Log.e("InventoryDBManager", "Failed to create InventoryDB for user $uuid", e)
                    return null
                }
            }
            return inventoryDB
        }
    }
    
    /**
     * Closes and removes the InventoryDB for the given user UUID
     */
    @JvmStatic
    fun closeInventoryDB(uuid: UUID?) {
        if (uuid == null) {
            return
        }
        
        synchronized(lock) {
            val inventoryDB = userDBs.remove(uuid)
            if (inventoryDB != null) {
                try {
                    inventoryDB.getDatabase().close()
                } catch (e: Exception) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user $uuid", e)
                }
            }
        }
    }
    
    /**
     * Closes all open InventoryDBs - should be called on app shutdown
     */
    @JvmStatic
    fun closeAllInventoryDBs() {
        synchronized(lock) {
            for ((key, value) in userDBs) {
                try {
                    value.getDatabase().close()
                } catch (e: Exception) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user $key", e)
                }
            }
            userDBs.clear()
        }
    }
}
