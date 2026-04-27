// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesRequest;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserProfiles

class  extends SimpleRequestHandler
{

    final SLUserProfiles this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        Debug.Printf("AvatarGroupList: Requesting avatar properties for %s", new Object[] {
            uuid.toString()
        });
        AvatarPropertiesRequest avatarpropertiesrequest = new AvatarPropertiesRequest();
        avatarpropertiesrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        avatarpropertiesrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        avatarpropertiesrequest.AgentData_Field.AvatarID = uuid;
        avatarpropertiesrequest.isReliable = true;
        SendMessage(avatarpropertiesrequest);
        if (uuid.equals(circuitInfo.agentID))
        {
            requestAgentDataUpdate();
        }
    }

    Request.AgentData()
    {
        this$0 = SLUserProfiles.this;
        super();
    }
}
