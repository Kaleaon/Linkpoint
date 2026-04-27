// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.Set;

public class SLInventoryUpdatedEvent
{

    private boolean needsReload;
    private Set updatedFolders;
    private Set updatedItems;

    public SLInventoryUpdatedEvent(Set set, Set set1, boolean flag)
    {
        updatedFolders = set;
        updatedItems = set1;
        needsReload = flag;
    }

    public boolean isFolderUpdated(long l)
    {
        if (updatedFolders == null)
        {
            return false;
        } else
        {
            return updatedFolders.contains(Long.valueOf(l));
        }
    }

    public boolean isItemUpdated(long l)
    {
        if (updatedItems == null)
        {
            return false;
        } else
        {
            return updatedItems.contains(Long.valueOf(l));
        }
    }

    public boolean isReloadNeeded()
    {
        return needsReload;
    }
}
