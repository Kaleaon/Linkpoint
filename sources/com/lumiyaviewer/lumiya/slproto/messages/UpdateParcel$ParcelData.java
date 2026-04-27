// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            UpdateParcel

public static class 
{

    public int ActualArea;
    public boolean AllowPublish;
    public UUID AuthorizedBuyerID;
    public int BillableArea;
    public int Category;
    public byte Description[];
    public boolean GroupOwned;
    public boolean IsForSale;
    public boolean MaturePublish;
    public byte MusicURL[];
    public byte Name[];
    public UUID OwnerID;
    public UUID ParcelID;
    public long RegionHandle;
    public float RegionX;
    public float RegionY;
    public int SalePrice;
    public boolean ShowDir;
    public UUID SnapshotID;
    public int Status;
    public LLVector3 UserLocation;

    public ()
    {
    }
}
