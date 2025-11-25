package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.utils.InlineList

class SpatialTree(
    val depthBins: Int,
    val xSize: Float,
    val ySize: Float,
    val zSize: Float,
    val spatialObjectIndex: SpatialObjectIndex
) {
    fun addDrawables(drawList: DrawList) {
        // Stub
    }

    fun isDrawListChanged(): Boolean {
        return false
    }

    fun isTreeWalkNeeded(): Boolean {
        return false
    }

    fun removeEntry(entry: InlineList<DrawListEntry>) {
        // Stub
    }

    fun removeObject(entry: DrawListEntry) {
        // Stub
    }

    fun setDrawListChanged() {
        // Stub
    }

    fun setEntryDepth(entry: InlineList<DrawListEntry>, depth: Float) {
        // Stub
    }

    fun setTreeWalkNeeded() {
        // Stub
    }

    fun updateObject(entry: DrawListEntry) {
        // Stub
    }

    fun walkTree(planes: FrustrumPlanes, viewDistance: Float) {
        // Stub
    }
    
    fun getObjectAvatarInfo(info: SLObjectInfo): Any? {
        return null
    }
}
