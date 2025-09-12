// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataRequest;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            SLGroupManager

class this._cls0 extends SimpleRequestHandler
{

    final SLGroupManager this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        Debug.Printf("GroupRoles: [%s] network requesting for group %s", new Object[] {
            Thread.currentThread().getName(), uuid.toString()
        });
        GroupRoleDataRequest grouproledatarequest = new GroupRoleDataRequest();
        grouproledatarequest.AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
        grouproledatarequest.AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
        grouproledatarequest.GroupData_Field.GroupID = uuid;
        grouproledatarequest.GroupData_Field.RequestID = UUID.randomUUID();
        grouproledatarequest.isReliable = true;
        SendMessage(grouproledatarequest);
    }

    oupData()
    {
        this$0 = SLGroupManager.this;
        super();
    }
}
