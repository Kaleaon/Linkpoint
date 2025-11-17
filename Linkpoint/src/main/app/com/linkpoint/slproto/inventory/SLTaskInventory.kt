package com.linkpoint.slproto.inventory

import com.google.common.collect.ImmutableList

class SLTaskInventory(entries: Collection<SLInventoryEntry>? = null) {
    val entries: ImmutableList<SLInventoryEntry> =
        if (entries != null) {
            ImmutableList.copyOf(entries)
        } else {
            ImmutableList.of()
        }
}
