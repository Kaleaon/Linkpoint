// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.os.Bundle;
import android.os.Parcel;
import com.lumiyaviewer.lumiya.utils.UUIDPool;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            InventoryQuery

static final class 
    implements android.os.r
{

    public InventoryQuery createFromParcel(Parcel parcel)
    {
        parcel = parcel.readBundle(getClass().getClassLoader());
        return InventoryQuery.create(UUIDPool.getUUID(parcel.getString("folderId")), parcel.getString("containsString"), parcel.getBoolean("includeFolders"), parcel.getBoolean("includeItems"), parcel.getBoolean("newestFirst"), parcel.getInt("assetType", -1));
    }

    public volatile Object createFromParcel(Parcel parcel)
    {
        return createFromParcel(parcel);
    }

    public InventoryQuery[] newArray(int i)
    {
        return new InventoryQuery[i];
    }

    public volatile Object[] newArray(int i)
    {
        return newArray(i);
    }

    ()
    {
    }
}
