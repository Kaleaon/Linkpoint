package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList

class MyAvatarTreeNode(private val spatialTree: SpatialTree) : InlineList<DrawListEntry>() {

    fun addDrawables(drawList: DrawList) {
        var entry = first
        while (entry != null) {
            entry.addToDrawList(drawList)
            entry = entry.next
        }
    }

    override fun addEntry(entry: DrawListEntry) {
        super.addEntry(entry)
        spatialTree.setDrawListChanged()
    }

    override fun removeEntry(entry: DrawListEntry) {
        super.removeEntry(entry)
        spatialTree.setDrawListChanged()
    }

    override fun requestEntryRemoval(entry: DrawListEntry) {
        spatialTree.spatialObjectIndex.requestEntryRemoval(entry)
    }
}
