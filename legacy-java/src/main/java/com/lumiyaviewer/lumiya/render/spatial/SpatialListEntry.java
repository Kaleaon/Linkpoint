package com.lumiyaviewer.lumiya.render.spatial;

/* loaded from: classes.dex */
public class SpatialListEntry<T> {
    public final T data;
    SpatialTreeNode node = null;
    SpatialListEntry<T> next = null;
    SpatialListEntry<T> prev = null;

    public SpatialListEntry(T t) {
        this.data = t;
    }

    public final SpatialListEntry<T> getNext() {
        return this.next;
    }
}
