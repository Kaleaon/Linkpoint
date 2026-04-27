package com.lumiyaviewer.lumiya.slproto.events;

import java.util.Set;

public class SLInventoryUpdatedEvent {
    private boolean needsReload;
    private Set<Long> updatedFolders;
    private Set<Long> updatedItems;

    public SLInventoryUpdatedEvent(Set<Long> set, Set<Long> set2, boolean z) {
        this.updatedFolders = set;
        this.updatedItems = set2;
        this.needsReload = z;
    }

    public boolean isFolderUpdated(long j) {
        if (this.updatedFolders == null) {
            return false;
        }
        return this.updatedFolders.contains(Long.valueOf(j));
    }

    public boolean isItemUpdated(long j) {
        if (this.updatedItems == null) {
            return false;
        }
        return this.updatedItems.contains(Long.valueOf(j));
    }

    public boolean isReloadNeeded() {
        return this.needsReload;
    }
}
