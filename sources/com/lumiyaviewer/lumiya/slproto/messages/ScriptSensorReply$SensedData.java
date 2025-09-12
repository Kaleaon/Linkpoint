// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ScriptSensorReply

public static class 
{

    public UUID GroupID;
    public byte Name[];
    public UUID ObjectID;
    public UUID OwnerID;
    public LLVector3 Position;
    public float Range;
    public LLQuaternion Rotation;
    public int Type;
    public LLVector3 Velocity;

    public ()
    {
    }
}
