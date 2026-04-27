// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ChatPass

public static class 
{

    public int Channel;
    public UUID ID;
    public byte Message[];
    public byte Name[];
    public UUID OwnerID;
    public LLVector3 Position;
    public float Radius;
    public int SimAccess;
    public int SourceType;
    public int Type;

    public ()
    {
    }
}
