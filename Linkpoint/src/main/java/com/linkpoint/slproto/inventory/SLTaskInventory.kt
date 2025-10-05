package com.linkpoint.slproto.inventory

import com.google.common.collect.ImmutableList

class SLTaskInventory {
    val entries: ImmutableList<SLInventoryEntry>

    constructor() {
        this.entries = ImmutableList.of()
    }

    constructor(collection: Collection<SLInventoryEntry>) {
        this.entries = ImmutableList.copyOf(collection)
    }
}