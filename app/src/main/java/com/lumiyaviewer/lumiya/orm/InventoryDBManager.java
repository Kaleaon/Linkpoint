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
        InventoryDB inventoryDB;
        if (uuid == null) {
            return null;
        }
        synchronized (lock) {
            inventoryDB = userDBs.get(uuid);
            if (inventoryDB == null) {
                inventoryDB = new InventoryDB(SLInventoryOpenHelper.getInstance().openOrCreateDatabase(new File(GlobalOptions.getInstance().getCacheDir("database"), "inventory-" + uuid.toString() + ".db").getAbsolutePath()));
                userDBs.put(uuid, inventoryDB);
            }
        }
        return inventoryDB;
    }
}
