package com.lumiyaviewer.lumiya.render.spatial
import java.util.*

import com.lumiyaviewer.lumiya.utils.InlineList
import com.lumiyaviewer.lumiya.utils.InlineListEntry
import javax.annotation.Nonnull

abstract class DrawListEntry implements InlineListEntry<DrawListEntry> {
    @Nonnull
    float[] boundingBox = FloatArray(6)
    private volatile InlineList<DrawListEntry> list
    private var next: DrawListEntry? = null
    private var prev: DrawListEntry? = null

    abstract fun addToDrawList(drawList: DrawList): Unit

    fun getList(): InlineList<DrawListEntry> {
        return this.list
    }

    fun getNext(): DrawListEntry {
        return this.next
    }

    fun getPrev(): DrawListEntry {
        return this.prev
    }

    fun requestEntryRemoval(): Unit {
        InlineList inlineList = this.list
        if (inlineList != null) {
            inlineList.requestEntryRemoval(this)
        }
    }

    fun setList(inlineList: InlineList<DrawListEntry>): Unit {
        this.list = inlineList
    }

    fun setNext(drawListEntry: DrawListEntry): Unit {
        this.next = drawListEntry
    }

    fun setPrev(drawListEntry: DrawListEntry): Unit {
        this.prev = drawListEntry
    }
}
