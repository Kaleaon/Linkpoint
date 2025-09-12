// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersRequest;
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
        Debug.Printf("GroupRoleMemberList: [%s] network requesting for %s", new Object[] {
            Thread.currentThread().getName(), uuid.toString()
        });
        GroupRoleMembersRequest grouprolemembersrequest = new GroupRoleMembersRequest();
        grouprolemembersrequest.AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
        grouprolemembersrequest.AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
        grouprolemembersrequest.GroupData_Field.GroupID = uuid;
        grouprolemembersrequest.GroupData_Field.RequestID = UUID.randomUUID();
        grouprolemembersrequest.isReliable = true;
        SendMessage(grouprolemembersrequest);
    }

    .GroupData()
    {
        this$0 = SLGroupManager.this;
        super();
    }
}
