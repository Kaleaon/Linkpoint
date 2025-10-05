package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList

class MyAvatarTreeNode : InlineList()<DrawListEntry> {
    private val SpatialTree spatialTree

    public MyAvatarTreeNode(SpatialTree spatialTree) {
        this.spatialTree = spatialTree
    }

    public Unit addDrawables(DrawList drawList) {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList)
        }
    }

    public Unit addEntry(DrawListEntry drawListEntry) {
        super.addEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    public Unit removeEntry(DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry)
        this.spatialTree.setDrawListChanged()
    }

    public Unit requestEntryRemoval(DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry)
    }
}
