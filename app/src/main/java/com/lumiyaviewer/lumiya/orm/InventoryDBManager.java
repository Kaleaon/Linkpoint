package com.lumiyaviewer.lumiya.orm;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryOpenHelper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class InventoryDBManager {
    private static final Object lock = new Object();
    private static final Map<UUID, InventoryDB> userDBs = new HashMap();

    @Nullable
    public static InventoryDB getUserInventoryDB(@Nullable UUID uuid) {
        if (uuid == null) {
            return null;
        }
        
        synchronized (lock) {
            InventoryDB inventoryDB = userDBs.get(uuid);
            if (inventoryDB == null) {
                try {
                    File cacheDir = GlobalOptions.getInstance().getCacheDir("database");
                    if (cacheDir == null) {
                        throw new IllegalStateException("Database cache directory is null");
                    }
                    
                    File dbFile = new File(cacheDir, "inventory-" + uuid.toString() + ".db");
                    inventoryDB = new InventoryDB(SLInventoryOpenHelper.getInstance().openOrCreateDatabase(dbFile.getAbsolutePath()));
                    userDBs.put(uuid, inventoryDB);
                } catch (Exception e) {
                    android.util.Log.e("InventoryDBManager", "Failed to create InventoryDB for user " + uuid, e);
                    return null;
                }
            }
            return inventoryDB;
        }
    }
    
    /**
     * Closes and removes the InventoryDB for the given user UUID
     */
    public static void closeInventoryDB(@Nullable UUID uuid) {
        if (uuid == null) {
            return;
        }
        
        synchronized (lock) {
            InventoryDB inventoryDB = userDBs.remove(uuid);
            if (inventoryDB != null) {
                try {
                    inventoryDB.getDatabase().close();
                } catch (Exception e) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user " + uuid, e);
                }
            }
        }
    }
    
    /**
     * Closes all open InventoryDBs - should be called on app shutdown
     */
    public static void closeAllInventoryDBs() {
        synchronized (lock) {
            for (Map.Entry<UUID, InventoryDB> entry : userDBs.entrySet()) {
                try {
                    entry.getValue().getDatabase().close();
                } catch (Exception e) {
                    android.util.Log.w("InventoryDBManager", "Error closing inventory database for user " + entry.getKey(), e);
                }
            }
            userDBs.clear();
        }
    }
}
