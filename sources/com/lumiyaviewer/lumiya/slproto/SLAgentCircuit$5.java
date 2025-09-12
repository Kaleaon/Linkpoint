// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessageEventListener, SLAgentCircuit, SLMessage

class this._cls0
    implements SLMessageEventListener
{

    final SLAgentCircuit this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
        SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
    }

    ResultEvent()
    {
        this$0 = SLAgentCircuit.this;
        super();
    }
}
