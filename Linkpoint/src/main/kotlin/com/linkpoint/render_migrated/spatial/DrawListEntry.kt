package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList
import com.linkpoint.utils.InlineListEntry
import javax.annotation.Nonnull

abstract class DrawListEntry : InlineListEntry<DrawListEntry> {
    final Float[] boundingBox = Float[6]
    private volatile InlineList<DrawListEntry> list
    private DrawListEntry next
    private DrawListEntry prev

    public abstract Unit addToDrawList(DrawList drawList)

    public InlineList<DrawListEntry> getList() {
        return this.list
    }

    public DrawListEntry getNext() {
        return this.next
    }

    public DrawListEntry getPrev() {
        return this.prev
    }

    fun requestEntryRemoval() {
        InlineList inlineList = this.list
        if (inlineList != null) {
            inlineList.requestEntryRemoval(this)
        }
    }

    fun setList(InlineList<DrawListEntry> inlineList) {
        this.list = inlineList
    }

    fun setNext(DrawListEntry drawListEntry) {
        this.next = drawListEntry
    }

    fun setPrev(DrawListEntry drawListEntry) {
        this.prev = drawListEntry
    }
}
