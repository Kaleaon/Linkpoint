// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessageEventListener, SLAgentCircuit, SLGridConnection, SLMessage

class this._cls0
    implements SLMessageEventListener
{

    final SLAgentCircuit this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.");
        if (!authReply.isTemporary)
        {
            if (authReply.fromTeleport)
            {
                Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.");
                SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(true, null));
            } else
            {
                gridConn.notifyLoginSuccess();
            }
            SLAgentCircuit._2D_wrap0(SLAgentCircuit.this);
            if (SLAgentCircuit._2D_get1(SLAgentCircuit.this) != null)
            {
                SLAgentCircuit._2D_get1(SLAgentCircuit.this).HandleCircuitReady();
            }
        }
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
        if (authReply.fromTeleport)
        {
            SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Timed out while connecting to the simulator."));
            return;
        } else
        {
            gridConn.notifyLoginError("Timed out while connecting to the simulator.");
            return;
        }
    }

    ()
    {
        this$0 = SLAgentCircuit.this;
        super();
    }
}
