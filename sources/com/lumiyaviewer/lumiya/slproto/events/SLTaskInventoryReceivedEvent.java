// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import java.util.UUID;

public class SLTaskInventoryReceivedEvent
{

    public final UUID taskID;
    public final SLTaskInventory taskInventory;

    public SLTaskInventoryReceivedEvent(UUID uuid, SLTaskInventory sltaskinventory)
    {
        taskID = uuid;
        taskInventory = sltaskinventory;
    }
}
