// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import java.util.UUID;

public class SLTaskInventoryReceivedEvent
{
    public final UUID taskID;
    public final SLTaskInventory taskInventory;
    
    public SLTaskInventoryReceivedEvent(final UUID taskID, final SLTaskInventory taskInventory) {
        this.taskID = taskID;
        this.taskInventory = taskInventory;
    }
}
