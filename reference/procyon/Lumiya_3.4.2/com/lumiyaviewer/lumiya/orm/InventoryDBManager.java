// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import java.io.File;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryOpenHelper;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class InventoryDBManager
{
    private static final Object lock;
    private static final Map<UUID, InventoryDB> userDBs;
    
    static {
        lock = new Object();
        userDBs = new HashMap<UUID, InventoryDB>();
    }
    
    @Nullable
    public static InventoryDB getUserInventoryDB(@Nullable final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        synchronized (InventoryDBManager.lock) {
            InventoryDB inventoryDB;
            if ((inventoryDB = InventoryDBManager.userDBs.get(uuid)) == null) {
                inventoryDB = new InventoryDB(SLInventoryOpenHelper.getInstance().openOrCreateDatabase(new File(GlobalOptions.getInstance().getCacheDir("database"), "inventory-" + uuid.toString() + ".db").getAbsolutePath()));
                InventoryDBManager.userDBs.put(uuid, inventoryDB);
            }
            return inventoryDB;
        }
    }
}
