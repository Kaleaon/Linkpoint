package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList
import com.lumiyaviewer.lumiya.utils.InlineListEntry

abstract class DrawListEntry : InlineListEntry<DrawListEntry> {
    
    val boundingBox = FloatArray(6)
    
    @Volatile
    private var list: InlineList<DrawListEntry>? = null
    
    private var next: DrawListEntry? = null
    private var prev: DrawListEntry? = null

    abstract fun addToDrawList(drawList: DrawList)

    override fun getList(): InlineList<DrawListEntry>? = list

    override fun getNext(): DrawListEntry? = next

    override fun getPrev(): DrawListEntry? = prev

    override fun setList(list: InlineList<DrawListEntry>?) {
        this.list = list
    }

    override fun setNext(entry: DrawListEntry?) {
        next = entry
    }

    override fun setPrev(entry: DrawListEntry?) {
        prev = entry
    }

    fun requestEntryRemoval() {
        list?.requestEntryRemoval(this)
    }
}
