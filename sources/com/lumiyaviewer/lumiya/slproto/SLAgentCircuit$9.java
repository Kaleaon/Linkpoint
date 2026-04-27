// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessageEventListener, SLAgentCircuit, SLMessage

class val.folderUUID
    implements SLMessageEventListener
{

    final SLAgentCircuit this$0;
    final UUID val$folderUUID;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        if (SLAgentCircuit._2D_get2(SLAgentCircuit.this) != null)
        {
            SLAgentCircuit._2D_get2(SLAgentCircuit.this).getInventoryManager().requestFolderUpdate(val$folderUUID);
        }
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
    }

    entoryManager()
    {
        this$0 = final_slagentcircuit;
        val$folderUUID = UUID.this;
        super();
    }
}
