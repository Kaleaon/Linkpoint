package com.linkpoint.render.spatial

class SpatialListEntry<T>(val data: T) {
    var next: SpatialListEntry<T>? = null
    var node: SpatialTreeNode? = null
    var prev: SpatialListEntry<T>? = null

    fun getNext(): SpatialListEntry<T>? {
        return next
    }
}