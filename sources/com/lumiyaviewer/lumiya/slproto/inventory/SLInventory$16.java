// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.base.Function;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory, SLInventoryEntry

class tListener extends com.lumiyaviewer.lumiya.slproto.istener.SLMessageBaseEventListener
{

    final SLInventory this$0;
    final UUID val$newFolderUUID;
    final Function val$onFolderCreated;
    final SLInventoryEntry val$parentEntry;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        Debug.Printf("Inventory: new folder created with uuid = %s, parent %s", new Object[] {
            val$newFolderUUID, val$parentEntry.uuid
        });
        if (SLInventory._2D_get4(SLInventory.this) != null)
        {
            SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(val$parentEntry.uuid);
        }
        if (val$onFolderCreated != null)
        {
            val$onFolderCreated.apply(val$newFolderUUID);
        }
    }

    y()
    {
        this$0 = final_slinventory;
        val$newFolderUUID = uuid;
        val$parentEntry = slinventoryentry;
        val$onFolderCreated = Function.this;
        super();
    }
}
