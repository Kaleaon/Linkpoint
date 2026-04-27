// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.Collection;
import com.google.common.collect.ImmutableList;

public class SLTaskInventory
{
    public final ImmutableList<SLInventoryEntry> entries;
    
    public SLTaskInventory() {
        this.entries = ImmutableList.of();
    }
    
    public SLTaskInventory(final Collection<SLInventoryEntry> collection) {
        this.entries = ImmutableList.copyOf((Collection<? extends SLInventoryEntry>)collection);
    }
}
