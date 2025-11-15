package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList
import com.lumiyaviewer.lumiya.utils.InlineListEntry

/**
 * Base class for entries in a spatial draw list
 * Manages bounding box and linked list functionality
 */
abstract class DrawListEntry : InlineListEntry<DrawListEntry> {
    
    val boundingBox = FloatArray(6)
    
    @Volatile
    private var list: InlineList<DrawListEntry>? = null
    private var next: DrawListEntry? = null
    private var prev: DrawListEntry? = null

    /**
     * Add this entry to a draw list
     */
    abstract fun addToDrawList(drawList: DrawList)

    override fun getList(): InlineList<DrawListEntry>? = list

    override fun getNext(): DrawListEntry? = next

    override fun getPrev(): DrawListEntry? = prev

    /**
     * Request removal of this entry from its list
     */
    fun requestEntryRemoval() {
        list?.requestEntryRemoval(this)
    }

    override fun setList(inlineList: InlineList<DrawListEntry>?) {
        this.list = inlineList
    }

    override fun setNext(entry: DrawListEntry?) {
        this.next = entry
    }

    override fun setPrev(entry: DrawListEntry?) {
        this.prev = entry
    }
}
