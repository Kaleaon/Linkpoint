package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList

class MyAvatarTreeNode(
    private val spatialTree: SpatialTree
) : InlineList<DrawListEntry>() {

    fun addDrawables(drawList: DrawList) {
        var drawListEntry = first as? DrawListEntry
        while (drawListEntry != null) {
            drawListEntry.addToDrawList(drawList)
            drawListEntry = drawListEntry.next as? DrawListEntry
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
