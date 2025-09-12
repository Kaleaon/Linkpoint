// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ChildAgentUpdate

public static class I
{

    public UUID ActiveGroupID;
    public int AgentAccess;
    public UUID AgentID;
    public LLVector3 AgentPos;
    public byte AgentTextures[];
    public LLVector3 AgentVel;
    public boolean AlwaysRun;
    public float Aspect;
    public LLVector3 AtAxis;
    public LLQuaternion BodyRotation;
    public LLVector3 Center;
    public boolean ChangedGrid;
    public int ControlFlags;
    public float EnergyLevel;
    public float Far;
    public int GodLevel;
    public LLQuaternion HeadRotation;
    public LLVector3 LeftAxis;
    public int LocomotionState;
    public UUID PreyAgent;
    public long RegionHandle;
    public UUID SessionID;
    public LLVector3 Size;
    public byte Throttles[];
    public LLVector3 UpAxis;
    public int ViewerCircuitCode;

    public I()
    {
    }
}
