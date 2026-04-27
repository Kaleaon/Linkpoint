package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class SLTaskInventory {
    public final ImmutableList<SLInventoryEntry> entries;

    public SLTaskInventory() {
        this.entries = ImmutableList.of();
    }

    public SLTaskInventory(Collection<SLInventoryEntry> collection) {
        this.entries = ImmutableList.copyOf(collection);
    }
}
