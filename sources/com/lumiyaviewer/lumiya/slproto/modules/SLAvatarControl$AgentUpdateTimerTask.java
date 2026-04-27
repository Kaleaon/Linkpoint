// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.TimerTask;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLAvatarControl, SLModules

private class <init> extends TimerTask
{

    private final int scheduledInterval;
    final SLAvatarControl this$0;

    int getScheduledInterval()
    {
        return scheduledInterval;
    }

    public void run()
    {
        if (SLAvatarControl._2D_get0(SLAvatarControl.this))
        {
            SLAvatarControl._2D_wrap1(SLAvatarControl.this, agentCircuit.getModules().drawDistance);
        }
    }

    private (int i)
    {
        this$0 = SLAvatarControl.this;
        super();
        scheduledInterval = i;
    }

    scheduledInterval(int i, scheduledInterval scheduledinterval)
    {
        this(i);
    }
}
