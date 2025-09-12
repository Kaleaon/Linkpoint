// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ClassifiedInfoReply

public static class 
{

    public int Category;
    public int ClassifiedFlags;
    public UUID ClassifiedID;
    public int CreationDate;
    public UUID CreatorID;
    public byte Desc[];
    public int ExpirationDate;
    public byte Name[];
    public UUID ParcelID;
    public byte ParcelName[];
    public int ParentEstate;
    public LLVector3d PosGlobal;
    public int PriceForListing;
    public byte SimName[];
    public UUID SnapshotID;

    public ()
    {
    }
}
