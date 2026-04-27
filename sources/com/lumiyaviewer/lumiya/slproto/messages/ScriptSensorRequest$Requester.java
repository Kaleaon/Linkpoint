// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ScriptSensorRequest

public static class 
{

    public float Arc;
    public float Range;
    public long RegionHandle;
    public UUID RequestID;
    public LLQuaternion SearchDir;
    public UUID SearchID;
    public byte SearchName[];
    public LLVector3 SearchPos;
    public int SearchRegions;
    public UUID SourceID;
    public int Type;

    public ()
    {
    }
}
