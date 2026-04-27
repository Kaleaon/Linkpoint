// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.Set;

public class SLInventoryUpdatedEvent
{
    private boolean needsReload;
    private Set<Long> updatedFolders;
    private Set<Long> updatedItems;
    
    public SLInventoryUpdatedEvent(final Set<Long> updatedFolders, final Set<Long> updatedItems, final boolean needsReload) {
        this.updatedFolders = updatedFolders;
        this.updatedItems = updatedItems;
        this.needsReload = needsReload;
    }
    
    public boolean isFolderUpdated(final long l) {
        return this.updatedFolders != null && this.updatedFolders.contains(l);
    }
    
    public boolean isItemUpdated(final long l) {
        return this.updatedItems != null && this.updatedItems.contains(l);
    }
    
    public boolean isReloadNeeded() {
        return this.needsReload;
    }
}
