// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryOpenHelper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            InventoryDB

public class InventoryDBManager
{

    private static final Object lock = new Object();
    private static final Map userDBs = new HashMap();

    public InventoryDBManager()
    {
    }

    public static InventoryDB getUserInventoryDB(UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        InventoryDB inventorydb = (InventoryDB)userDBs.get(uuid);
        Object obj;
        obj = inventorydb;
        if (inventorydb != null)
        {
            break MISSING_BLOCK_LABEL_103;
        }
        obj = new File(GlobalOptions.getInstance().getCacheDir("database"), (new StringBuilder()).append("inventory-").append(uuid.toString()).append(".db").toString());
        obj = new InventoryDB(SLInventoryOpenHelper.getInstance().openOrCreateDatabase(((File) (obj)).getAbsolutePath()));
        userDBs.put(uuid, obj);
        obj1;
        JVM INSTR monitorexit ;
        return ((InventoryDB) (obj));
        uuid;
        throw uuid;
    }

}
