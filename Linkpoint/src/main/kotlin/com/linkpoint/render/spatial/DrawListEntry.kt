package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList
import com.linkpoint.utils.InlineListEntry
import javax.annotation.Nonnull

abstract class DrawListEntry : InlineListEntry<DrawListEntry> {
    final FloatArray boundingBox = Float[6]
    private volatile InlineList<DrawListEntry> list
    private DrawListEntry next
    private DrawListEntry prev

    public abstract Unit addToDrawList(DrawList drawList)

    public InlineList<DrawListEntry> getList() {
        return this.list
    }

     public fun getNext(): DrawListEntry {
        return this.next
    }

     public fun getPrev(): DrawListEntry {
        return this.prev
    }

    fun requestEntryRemoval() {
        val inlineList: InlineList = this.list
        if (inlineList != null) {
            inlineList.requestEntryRemoval(this)
        }
    }

    fun setList(inlineList: InlineList<DrawListEntry>) {
        this.list = inlineList
    }

    fun setNext(drawListEntry: DrawListEntry) {
        this.next = drawListEntry
    }

    fun setPrev(drawListEntry: DrawListEntry) {
        this.prev = drawListEntry
    }
}
