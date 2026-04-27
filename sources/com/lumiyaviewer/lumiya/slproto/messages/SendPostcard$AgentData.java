// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SendPostcard

public static class 
{

    public UUID AgentID;
    public boolean AllowPublish;
    public UUID AssetID;
    public byte From[];
    public boolean MaturePublish;
    public byte Msg[];
    public byte Name[];
    public LLVector3d PosGlobal;
    public UUID SessionID;
    public byte Subject[];
    public byte To[];

    public ()
    {
    }
}
