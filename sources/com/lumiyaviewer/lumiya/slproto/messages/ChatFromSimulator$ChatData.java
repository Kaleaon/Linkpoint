// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ChatFromSimulator

public static class I
{

    public int Audible;
    public int ChatType;
    public byte FromName[];
    public byte Message[];
    public UUID OwnerID;
    public LLVector3 Position;
    public UUID SourceID;
    public int SourceType;

    public I()
    {
    }
}
