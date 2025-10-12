package com.lumiyaviewer.lumiya.slproto.events

import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory
import java.util.UUID

data class SLTaskInventoryReceivedEvent(
    val taskID: UUID,
    val taskInventory: SLTaskInventory
)