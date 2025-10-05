package com.linkpoint.render.spatial

import com.linkpoint.utils.InlineList

class DrawEntryList(private val listener: EntryRemovalListener?) : InlineList<DrawListEntry>() {
    
    interface EntryRemovalListener {
        fun onEntryRemovalRequested(entry: DrawListEntry)
    }

    fun requestEntryRemoval(entry: DrawListEntry) {
        listener?.onEntryRemovalRequested(entry)
    }
}