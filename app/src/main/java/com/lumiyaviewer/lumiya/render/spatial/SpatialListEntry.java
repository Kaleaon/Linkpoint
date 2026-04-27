package com.lumiyaviewer.lumiya.render.spatial;
import java.util.*;

public class SpatialListEntry<T> {
    public final T data;
    SpatialListEntry<T> next = null;
    SpatialTreeNode node = null;
    SpatialListEntry<T> prev = null;

    public SpatialListEntry(T t) {
        this.data = t;
    }

    public final SpatialListEntry<T> getNext() {
        return this.next;
    }
}
