// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ChildAgentPositionUpdate

public static class Q
{

    public UUID AgentID;
    public LLVector3 AgentPos;
    public LLVector3 AgentVel;
    public LLVector3 AtAxis;
    public LLVector3 Center;
    public boolean ChangedGrid;
    public LLVector3 LeftAxis;
    public long RegionHandle;
    public UUID SessionID;
    public LLVector3 Size;
    public LLVector3 UpAxis;
    public int ViewerCircuitCode;

    public Q()
    {
    }
}
