// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLInventoryNewContentsEvent
{

    public boolean firstIsFolder;
    public String firstItemName;
    public UUID firstParentUUID;
    public int newFolderCount;
    public int newItemCount;

    public SLInventoryNewContentsEvent()
    {
        newFolderCount = 0;
        newItemCount = 0;
        firstParentUUID = null;
        firstItemName = null;
        firstIsFolder = false;
    }

    public void AddItem(boolean flag, UUID uuid, String s)
    {
        if (flag)
        {
            newFolderCount = newFolderCount + 1;
        } else
        {
            newItemCount = newItemCount + 1;
        }
        if (firstParentUUID == null)
        {
            firstIsFolder = flag;
            firstParentUUID = uuid;
            firstItemName = s;
        } else
        if (flag && firstIsFolder ^ true)
        {
            firstIsFolder = flag;
            firstParentUUID = uuid;
            firstItemName = s;
            return;
        }
    }

    public boolean isEmpty()
    {
        boolean flag1 = false;
        boolean flag = flag1;
        if (newFolderCount == 0)
        {
            flag = flag1;
            if (newItemCount == 0)
            {
                flag = true;
            }
        }
        return flag;
    }
}
