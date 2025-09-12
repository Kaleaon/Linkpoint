// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            SLGroupManager

class aseEventListener extends com.lumiyaviewer.lumiya.slproto.ener.SLMessageBaseEventListener
{

    final SLGroupManager this$0;
    final UUID val$groupID;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        SLGroupManager._2D_get4(SLGroupManager.this).getGroupRoles().requestUpdate(val$groupID);
    }

    aseEventListener()
    {
        this$0 = final_slgroupmanager;
        val$groupID = UUID.this;
        super();
    }
}
