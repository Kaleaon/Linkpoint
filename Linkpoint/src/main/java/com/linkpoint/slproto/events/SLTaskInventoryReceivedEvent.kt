package com.linkpoint.slproto.events

import com.linkpoint.slproto.inventory.SLTaskInventory
import java.util.UUID

data class SLTaskInventoryReceivedEvent(
    val taskID: UUID,
    val taskInventory: SLTaskInventory
)