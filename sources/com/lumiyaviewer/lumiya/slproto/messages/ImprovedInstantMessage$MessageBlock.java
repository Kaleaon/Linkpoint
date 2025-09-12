// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ImprovedInstantMessage

public static class 
{

    public byte BinaryBucket[];
    public int Dialog;
    public byte FromAgentName[];
    public boolean FromGroup;
    public UUID ID;
    public byte Message[];
    public int Offline;
    public int ParentEstateID;
    public LLVector3 Position;
    public UUID RegionID;
    public int Timestamp;
    public UUID ToAgentID;

    public ()
    {
    }
}
