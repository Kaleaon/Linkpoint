// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

public class SpatialListEntry<T>
{
    public final T data;
    SpatialListEntry<T> next;
    SpatialTreeNode node;
    SpatialListEntry<T> prev;
    
    public SpatialListEntry(final T data) {
        this.node = null;
        this.next = null;
        this.prev = null;
        this.data = data;
    }
    
    public final SpatialListEntry<T> getNext() {
        return this.next;
    }
}
