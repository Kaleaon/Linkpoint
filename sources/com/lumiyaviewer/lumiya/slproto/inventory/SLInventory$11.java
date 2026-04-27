// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory, SLInventoryEntry

class tListener extends com.lumiyaviewer.lumiya.slproto.istener.SLMessageBaseEventListener
{

    final SLInventory this$0;
    final SLInventoryEntry val$newParent;
    final UUID val$oldParentUUID;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(val$oldParentUUID);
        SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(val$newParent.uuid);
    }

    y()
    {
        this$0 = final_slinventory;
        val$oldParentUUID = uuid;
        val$newParent = SLInventoryEntry.this;
        super();
    }
}
