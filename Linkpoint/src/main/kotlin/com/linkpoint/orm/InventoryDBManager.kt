package com.linkpoint.orm

import com.linkpoint.GlobalOptions
import com.linkpoint.slproto.inventory.SLInventoryOpenHelper
import java.io.File
import java.util.HashMap
import java.util.Map
import java.util.UUID
import javax.annotation.Nullable

class InventoryDBManager {
    private const val Object lock = Object()
    private const val Map<UUID, InventoryDB> userDBs = HashMap()

    @JvmStatic
     fun getUserInventoryDB(uuid: UUID): InventoryDB {
        if (uuid == null) {
            return null
        }
        
        synchronized (lock) {
            val inventoryDB: InventoryDB = userDBs.get(uuid)
            if (inventoryDB == null) {
                try {
                    val cacheDir: File = GlobalOptions.getInstance().getCacheDir("database")
                    if (cacheDir == null) {
                        throw IllegalStateException("Database cache directory is null")
                    }
                    
                    val dbFile: File = File(cacheDir, "inventory-" + uuid.toString() + ".db")
                    inventoryDB = InventoryDB(SLInventoryOpenHelper.getInstance().openOrCreateDatabase(dbFile.getAbsolutePath()))
                    userDBs.put(uuid, inventoryDB)
                } catch (Exception e) {
                    android.util.Log.e("InventoryDBManager", "Failed to create InventoryDB for user " + uuid, e)
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
     fun closeInventoryDB(uuid: UUID) {
        if (uuid == null) {
            return
        }
        
        synchronized (lock) {
            val inventoryDB: InventoryDB = userDBs.remove(uuid)
            if (inventoryDB != null) {
                try {
                    inventoryDB.getDatabase().close()
                } catch (Exception e) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user " + uuid, e)
                }
            }
        }
    }
    
    /**
     * Closes all open InventoryDBs - should be called on app shutdown
     */
    @JvmStatic
     fun closeAllInventoryDBs() {
        synchronized (lock) {
            for (Map.Entry<UUID, InventoryDB> entry : userDBs.entrySet()) {
                try {
                    entry.getValue().getDatabase().close()
                } catch (Exception e) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user " + entry.getKey(), e)
                }
            }
            userDBs.clear()
        }
    }
}
