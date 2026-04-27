// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager.assets:
//            AssetKey

final class AutoValue_AssetKey extends AssetKey
{

    private final int assetType;
    private final UUID assetUUID;
    private final int channelType;
    private final UUID itemUUID;
    private final UUID ownerUUID;
    private final int sourceType;
    private final UUID taskUUID;

    AutoValue_AssetKey(int i, int j, UUID uuid, int k, UUID uuid1, UUID uuid2, UUID uuid3)
    {
        channelType = i;
        sourceType = j;
        assetUUID = uuid;
        assetType = k;
        ownerUUID = uuid1;
        itemUUID = uuid2;
        taskUUID = uuid3;
    }

    public int assetType()
    {
        return assetType;
    }

    public UUID assetUUID()
    {
        return assetUUID;
    }

    public int channelType()
    {
        return channelType;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof AssetKey)
        {
            obj = (AssetKey)obj;
            if (channelType == ((AssetKey) (obj)).channelType() && sourceType == ((AssetKey) (obj)).sourceType() && (assetUUID != null ? assetUUID.equals(((AssetKey) (obj)).assetUUID()) : ((AssetKey) (obj)).assetUUID() == null) && assetType == ((AssetKey) (obj)).assetType() && (ownerUUID != null ? ownerUUID.equals(((AssetKey) (obj)).ownerUUID()) : ((AssetKey) (obj)).ownerUUID() == null) && (itemUUID != null ? itemUUID.equals(((AssetKey) (obj)).itemUUID()) : ((AssetKey) (obj)).itemUUID() == null))
            {
                if (taskUUID == null)
                {
                    return ((AssetKey) (obj)).taskUUID() == null;
                } else
                {
                    return taskUUID.equals(((AssetKey) (obj)).taskUUID());
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int l = 0;
        int i1 = channelType;
        int j1 = sourceType;
        int i;
        int j;
        int k;
        int k1;
        if (assetUUID == null)
        {
            i = 0;
        } else
        {
            i = assetUUID.hashCode();
        }
        k1 = assetType;
        if (ownerUUID == null)
        {
            j = 0;
        } else
        {
            j = ownerUUID.hashCode();
        }
        if (itemUUID == null)
        {
            k = 0;
        } else
        {
            k = itemUUID.hashCode();
        }
        if (taskUUID != null)
        {
            l = taskUUID.hashCode();
        }
        return (k ^ (j ^ ((i ^ ((i1 ^ 0xf4243) * 0xf4243 ^ j1) * 0xf4243) * 0xf4243 ^ k1) * 0xf4243) * 0xf4243) * 0xf4243 ^ l;
    }

    public UUID itemUUID()
    {
        return itemUUID;
    }

    public UUID ownerUUID()
    {
        return ownerUUID;
    }

    public int sourceType()
    {
        return sourceType;
    }

    public UUID taskUUID()
    {
        return taskUUID;
    }
}
