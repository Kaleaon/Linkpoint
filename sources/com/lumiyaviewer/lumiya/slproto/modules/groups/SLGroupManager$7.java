// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            SLGroupManager

class this._cls0
    implements SLMessageEventListener
{

    final SLGroupManager this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        slmessage = new AgentDataUpdateRequest();
        ((AgentDataUpdateRequest) (slmessage)).AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
        ((AgentDataUpdateRequest) (slmessage)).AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
        slmessage.isReliable = true;
        SendMessage(slmessage);
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
    }

    AgentData()
    {
        this$0 = SLGroupManager.this;
        super();
    }
}
