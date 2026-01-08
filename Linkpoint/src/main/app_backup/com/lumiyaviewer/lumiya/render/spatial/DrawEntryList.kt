package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList

class DrawEntryList(private val listener: EntryRemovalListener?) : InlineList<DrawListEntry>() {
    interface EntryRemovalListener {
        fun onEntryRemovalRequested(drawListEntry: DrawListEntry)
    }

    override fun requestEntryRemoval(drawListEntry: DrawListEntry) {
        listener?.onEntryRemovalRequested(drawListEntry)
    }
}