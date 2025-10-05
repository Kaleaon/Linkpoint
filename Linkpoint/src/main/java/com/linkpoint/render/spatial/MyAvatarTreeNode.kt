package com.linkpoint.render.spatial

import com.linkpoint.utils.InlineList

class MyAvatarTreeNode(private val spatialTree: SpatialTree) : InlineList<DrawListEntry>() {

    fun addDrawables(drawList: DrawList) {
        var entry = first as DrawListEntry?
        while (entry != null) {
            entry.addToDrawList(drawList)
            entry = entry.next
        }
    }

    override fun addEntry(drawListEntry: DrawListEntry) {
        super.addEntry(drawListEntry)
        spatialTree.setDrawListChanged()
    }

    override fun removeEntry(drawListEntry: DrawListEntry) {
        super.removeEntry(drawListEntry)
        spatialTree.setDrawListChanged()
    }

    fun requestEntryRemoval(drawListEntry: DrawListEntry) {
        spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry)
    }
}