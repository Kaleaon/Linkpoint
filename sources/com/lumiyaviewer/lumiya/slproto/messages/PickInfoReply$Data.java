// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            PickInfoReply

public static class A
{

    public UUID CreatorID;
    public byte Desc[];
    public boolean Enabled;
    public byte Name[];
    public byte OriginalName[];
    public UUID ParcelID;
    public UUID PickID;
    public LLVector3d PosGlobal;
    public byte SimName[];
    public UUID SnapshotID;
    public int SortOrder;
    public boolean TopPick;
    public byte User[];

    public A()
    {
    }
}
