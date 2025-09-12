// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import com.google.common.base.Joiner;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager.assets:
//            AutoValue_AssetKey

public abstract class AssetKey
{

    private static final Joiner toStringJoiner = Joiner.on(':').useForNull("null");

    public AssetKey()
    {
    }

    public static AssetKey createAssetKey(UUID uuid, UUID uuid1, UUID uuid2, int i)
    {
        return new AutoValue_AssetKey(2, 2, uuid2, i, uuid1, uuid, null);
    }

    public static AssetKey createInventoryKey(SLInventoryEntry slinventoryentry, UUID uuid)
    {
        return new AutoValue_AssetKey(2, 3, slinventoryentry.assetUUID, slinventoryentry.assetType, slinventoryentry.ownerUUID, slinventoryentry.uuid, uuid);
    }

    public abstract int assetType();

    public abstract UUID assetUUID();

    public abstract int channelType();

    public abstract UUID itemUUID();

    public abstract UUID ownerUUID();

    public abstract int sourceType();

    public abstract UUID taskUUID();

    public String toString()
    {
        return toStringJoiner.join(Integer.valueOf(channelType()), Integer.valueOf(sourceType()), new Object[] {
            assetUUID(), Integer.valueOf(assetType()), ownerUUID(), itemUUID(), taskUUID()
        });
    }

}
