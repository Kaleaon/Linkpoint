// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            InventoryQuery

final class AutoValue_InventoryQuery extends InventoryQuery
{

    private final int assetType;
    private final String containsString;
    private final UUID folderId;
    private final int folderType;
    private final boolean includeFolders;
    private final boolean includeItems;
    private final boolean newestFirst;

    AutoValue_InventoryQuery(UUID uuid, String s, boolean flag, boolean flag1, boolean flag2, int i, int j)
    {
        folderId = uuid;
        containsString = s;
        includeFolders = flag;
        includeItems = flag1;
        newestFirst = flag2;
        folderType = i;
        assetType = j;
    }

    public int assetType()
    {
        return assetType;
    }

    public String containsString()
    {
        return containsString;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof InventoryQuery)
        {
            obj = (InventoryQuery)obj;
            if ((folderId != null ? folderId.equals(((InventoryQuery) (obj)).folderId()) : ((InventoryQuery) (obj)).folderId() == null) && (containsString != null ? containsString.equals(((InventoryQuery) (obj)).containsString()) : ((InventoryQuery) (obj)).containsString() == null) && (includeFolders == ((InventoryQuery) (obj)).includeFolders() && includeItems == ((InventoryQuery) (obj)).includeItems() && newestFirst == ((InventoryQuery) (obj)).newestFirst() && folderType == ((InventoryQuery) (obj)).folderType()))
            {
                return assetType == ((InventoryQuery) (obj)).assetType();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public UUID folderId()
    {
        return folderId;
    }

    public int folderType()
    {
        return folderType;
    }

    public int hashCode()
    {
        int j = 0;
        char c2 = '\u04CF';
        int i;
        char c;
        char c1;
        if (folderId == null)
        {
            i = 0;
        } else
        {
            i = folderId.hashCode();
        }
        if (containsString != null)
        {
            j = containsString.hashCode();
        }
        if (includeFolders)
        {
            c = '\u04CF';
        } else
        {
            c = '\u04D5';
        }
        if (includeItems)
        {
            c1 = '\u04CF';
        } else
        {
            c1 = '\u04D5';
        }
        if (!newestFirst)
        {
            c2 = '\u04D5';
        }
        return (((c1 ^ (c ^ ((i ^ 0xf4243) * 0xf4243 ^ j) * 0xf4243) * 0xf4243) * 0xf4243 ^ c2) * 0xf4243 ^ folderType) * 0xf4243 ^ assetType;
    }

    public boolean includeFolders()
    {
        return includeFolders;
    }

    public boolean includeItems()
    {
        return includeItems;
    }

    public boolean newestFirst()
    {
        return newestFirst;
    }

    public String toString()
    {
        return (new StringBuilder()).append("InventoryQuery{folderId=").append(folderId).append(", ").append("containsString=").append(containsString).append(", ").append("includeFolders=").append(includeFolders).append(", ").append("includeItems=").append(includeItems).append(", ").append("newestFirst=").append(newestFirst).append(", ").append("folderType=").append(folderType).append(", ").append("assetType=").append(assetType).append("}").toString();
    }
}
