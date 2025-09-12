// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLAgentCircuit

class  extends EventRateLimiter
{

    final SLAgentCircuit this$0;

    protected Object getEventToFire()
    {
        return null;
    }

    protected void onActualFire()
    {
        SLAgentCircuit._2D_wrap1(SLAgentCircuit.this);
    }

    (EventBus eventbus, long l)
    {
        this$0 = SLAgentCircuit.this;
        super(eventbus, l);
    }
}
