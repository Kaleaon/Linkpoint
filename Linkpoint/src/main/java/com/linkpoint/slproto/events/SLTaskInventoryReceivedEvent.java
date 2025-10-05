package com.linkpoint.slproto.events;

import com.linkpoint.slproto.inventory.SLTaskInventory;
import java.util.UUID;

public class SLTaskInventoryReceivedEvent {
    public final UUID taskID;
    public final SLTaskInventory taskInventory;

    public SLTaskInventoryReceivedEvent(UUID uuid, SLTaskInventory sLTaskInventory) {
        this.taskID = uuid;
        this.taskInventory = sLTaskInventory;
    }
}
