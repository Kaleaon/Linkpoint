package com.linkpoint.render.spatial
import java.util.*

class SpatialListEntry<T> {
    val T data
    SpatialListEntry<T> next = null
    SpatialTreeNode node = null
    SpatialListEntry<T> prev = null

    public SpatialListEntry(T t) {
        this.data = t
    }

    val SpatialListEntry<T> getNext() {
        return this.next
    }
}
