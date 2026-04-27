// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            AgentUpdate

public static class 
{

    public UUID AgentID;
    public LLQuaternion BodyRotation;
    public LLVector3 CameraAtAxis;
    public LLVector3 CameraCenter;
    public LLVector3 CameraLeftAxis;
    public LLVector3 CameraUpAxis;
    public int ControlFlags;
    public float Far;
    public int Flags;
    public LLQuaternion HeadRotation;
    public UUID SessionID;
    public int State;

    public ()
    {
    }
}
