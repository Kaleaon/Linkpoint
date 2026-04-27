// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.orm.InventoryDB;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory, SLInventoryEntry

public abstract class SLInventoryFetchRequest
{

    protected final InventoryDB db;
    protected final SLInventoryEntry folderEntry;
    protected final long folderId;
    protected final UUID folderUUID;
    protected final SLInventory inventory;

    SLInventoryFetchRequest(SLInventory slinventory, UUID uuid)
        throws SLInventory.NoInventoryItemException
    {
        inventory = slinventory;
        db = slinventory.getDatabase();
        folderUUID = uuid;
        slinventory = db.findEntry(uuid);
        if (slinventory == null)
        {
            throw new SLInventory.NoInventoryItemException(uuid);
        } else
        {
            folderEntry = slinventory;
            folderId = slinventory.getId();
            return;
        }
    }

    public abstract void cancel();

    protected void completeFetch(boolean flag, boolean flag1)
    {
        inventory.onFetchComplete(this, folderUUID, folderId, flag, flag1);
    }

    public abstract void start();
}
