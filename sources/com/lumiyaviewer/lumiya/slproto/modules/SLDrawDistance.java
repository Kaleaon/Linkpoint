// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules, SLAvatarControl

public class SLDrawDistance extends SLModule
{

    public static final float CHAT_RANGE = 20F;
    private static final long DRAW_RANGE_TIMEOUT = 10000L;
    public static final float MIN_DRAW_RANGE = 10.5F;
    private float activeDrawDistance;
    private long defaultDrawDistanceSince;
    private boolean defaultTimerSet;
    private volatile boolean keepDrawDistance;
    private float keepSelectDistance;
    private float objectSelectDistance;
    private volatile boolean objectSelectionActive;
    private float wantedDrawDistance;
    private float worldDrawDistance;
    private volatile boolean worldViewActive;

    public SLDrawDistance(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        worldViewActive = false;
        objectSelectionActive = false;
        keepDrawDistance = false;
        worldDrawDistance = 20F;
        objectSelectDistance = 20F;
        keepSelectDistance = 20F;
        activeDrawDistance = 0.0F;
        wantedDrawDistance = 10.5F;
        defaultDrawDistanceSince = 0L;
        defaultTimerSet = false;
    }

    private void updateWantedDrawDistance()
    {
        boolean flag1 = true;
        this;
        JVM INSTR monitorenter ;
        float f1 = 10.5F;
        if (worldViewActive)
        {
            f1 = Math.max(10.5F, worldDrawDistance);
        }
        float f = f1;
        if (objectSelectionActive)
        {
            f = Math.max(f1, objectSelectDistance);
        }
        f1 = f;
        if (keepDrawDistance)
        {
            f1 = Math.max(f, keepSelectDistance);
        }
        boolean flag = flag1;
        if (worldViewActive)
        {
            break MISSING_BLOCK_LABEL_89;
        }
        flag = flag1;
        if (!objectSelectionActive)
        {
            flag = keepDrawDistance;
        }
        if (flag) goto _L2; else goto _L1
_L1:
        if (!defaultTimerSet)
        {
            defaultDrawDistanceSince = System.currentTimeMillis();
            defaultTimerSet = true;
        }
        f = f1;
        if (f1 == wantedDrawDistance)
        {
            break MISSING_BLOCK_LABEL_146;
        }
        f = f1;
        if (System.currentTimeMillis() < defaultDrawDistanceSince + 10000L)
        {
            f = wantedDrawDistance;
        }
_L3:
        wantedDrawDistance = f;
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        defaultTimerSet = false;
        f = f1;
          goto _L3
        Exception exception;
        exception;
        throw exception;
    }

    public void Disable3DView()
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Log("DrawDistance: Disable 3D View.");
        if (worldViewActive)
        {
            worldViewActive = false;
            agentCircuit.getModules().avatarControl.DisableFastUpdates();
        }
        agentCircuit.TryWakeUp();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void DisableKeepDistance()
    {
        this;
        JVM INSTR monitorenter ;
        keepDrawDistance = false;
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void DisableObjectSelect()
    {
        this;
        JVM INSTR monitorenter ;
        objectSelectionActive = false;
        agentCircuit.TryWakeUp();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void Enable3DView(int i)
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Log((new StringBuilder()).append("Enable3DView: Setting drawDistance to ").append(i).toString());
        worldDrawDistance = i;
        if (!worldViewActive)
        {
            worldViewActive = true;
            agentCircuit.getModules().avatarControl.EnableFastUpdates();
        }
        gridConn.parcelInfo.setDrawDistance(i);
        agentCircuit.TryWakeUp();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void EnableKeepDistance(float f)
    {
        this;
        JVM INSTR monitorenter ;
        keepDrawDistance = true;
        keepSelectDistance = f;
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void EnableObjectSelect()
    {
        this;
        JVM INSTR monitorenter ;
        objectSelectionActive = true;
        agentCircuit.TryWakeUp();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public float getDrawDistanceForUpdate()
    {
        this;
        JVM INSTR monitorenter ;
        float f;
        updateWantedDrawDistance();
        activeDrawDistance = wantedDrawDistance;
        f = activeDrawDistance;
        this;
        JVM INSTR monitorexit ;
        return f;
        Exception exception;
        exception;
        throw exception;
    }

    public float getObjectSelectRange()
    {
        this;
        JVM INSTR monitorenter ;
        float f = objectSelectDistance;
        this;
        JVM INSTR monitorexit ;
        return f;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean is3DViewEnabled()
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = worldViewActive;
        this;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean isObjectSelectEnabled()
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = objectSelectionActive;
        this;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean needUpdateDrawDistance()
    {
        this;
        JVM INSTR monitorenter ;
        float f;
        float f1;
        updateWantedDrawDistance();
        f = wantedDrawDistance;
        f1 = activeDrawDistance;
        boolean flag;
        if (f != f1)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        this;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public void setObjectSelectRange(float f)
    {
        this;
        JVM INSTR monitorenter ;
        objectSelectDistance = f;
        agentCircuit.TryWakeUp();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
