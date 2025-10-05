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
    InventoryDB getUserInventoryDB(UUID uuid) {
        if (uuid == null) {
            return null
        }
        
        synchronized (lock) {
            InventoryDB inventoryDB = userDBs.get(uuid)
            if (inventoryDB == null) {
                try {
                    File cacheDir = GlobalOptions.getInstance().getCacheDir("database")
                    if (cacheDir == null) {
                        throw IllegalStateException("Database cache directory is null")
                    }
                    
                    File dbFile = File(cacheDir, "inventory-" + uuid.toString() + ".db")
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
    Unit closeInventoryDB(UUID uuid) {
        if (uuid == null) {
            return
        }
        
        synchronized (lock) {
            InventoryDB inventoryDB = userDBs.remove(uuid)
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
    Unit closeAllInventoryDBs() {
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
