// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessageEventListener, SLAgentCircuit, SLGridConnection, SLMessage

class this._cls0
    implements SLMessageEventListener
{

    final SLAgentCircuit this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        Debug.Log("Logout: Logout request acknowledged.");
        gridConn.processDisconnect(true, "Logged out.");
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
        Debug.Log("Logout: LogoutRequest timed out!");
        gridConn.processDisconnect(false, "Logout request has timed out.");
    }

    tener()
    {
        this$0 = SLAgentCircuit.this;
        super();
    }
}
