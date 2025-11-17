package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList

class MyAvatarTreeNode : InlineList<DrawListEntry> {
    private SpatialTree spatialTree

    constructor(spatialTree: SpatialTree) {
        this.spatialTree = spatialTree
    }

    fun addDrawables(drawList: DrawList): Unit {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList)
        }
    }

    fun addEntry(drawListEntry: DrawListEntry): Unit {
        super.addEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    fun removeEntry(drawListEntry: DrawListEntry): Unit {
        super.removeEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    fun requestEntryRemoval(drawListEntry: DrawListEntry): Unit {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry)
    }
}
