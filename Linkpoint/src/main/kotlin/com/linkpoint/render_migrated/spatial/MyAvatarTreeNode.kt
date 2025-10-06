package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList

class MyAvatarTreeNode : InlineList()<DrawListEntry> {
    private val SpatialTree spatialTree

    public MyAvatarTreeNode(SpatialTree spatialTree) {
        this.spatialTree = spatialTree
    }

    fun addDrawables(DrawList drawList) {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList)
        }
    }

    fun addEntry(DrawListEntry drawListEntry) {
        super.addEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    fun removeEntry(DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    fun requestEntryRemoval(DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry)
    }
}
