// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            InventoryManager

class val.inventoryDB extends RequestProcessor
{

    final InventoryManager this$0;
    final InventoryDB val$inventoryDB;

    protected volatile boolean isRequestComplete(Object obj, Object obj1)
    {
        return isRequestComplete((UUID)obj, (SLInventoryEntry)obj1);
    }

    protected boolean isRequestComplete(UUID uuid, SLInventoryEntry slinventoryentry)
    {
        return slinventoryentry != null && Objects.equal(slinventoryentry.sessionID, InventoryManager._2D_get0(InventoryManager.this).get());
    }

    protected SLInventoryEntry processRequest(UUID uuid)
    {
        return val$inventoryDB.findEntry(uuid);
    }

    protected volatile Object processRequest(Object obj)
    {
        return processRequest((UUID)obj);
    }

    protected SLInventoryEntry processResult(UUID uuid, SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry != null)
        {
            Debug.Printf("Inventory: entry subscription got name: %s with folderId = '%s'", new Object[] {
                slinventoryentry.name, slinventoryentry.uuid
            });
        }
        InventoryManager._2D_wrap0(InventoryManager.this);
        return slinventoryentry;
    }

    protected volatile Object processResult(Object obj, Object obj1)
    {
        return processResult((UUID)obj, (SLInventoryEntry)obj1);
    }

    (Executor executor, InventoryDB inventorydb)
    {
        this$0 = final_inventorymanager;
        val$inventoryDB = inventorydb;
        super(RequestSource.this, executor);
    }
}
