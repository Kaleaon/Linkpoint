package com.linkpoint.utils
import java.util.*

class InlineList<T : InlineListEntry<T>> {
    private T first = null

    fun addEntry(T t) {
        InlineList list = t.getList()
        if (list != this) {
            if (list != null) {
                list.removeEntry(t)
            }
            t.setNext(this.first)
            t.setPrev(null)
            if (this.first != null) {
                this.first.setPrev(t)
            }
            this.first = t
            t.setList(this)
        }
    }

    val T getFirst() {
        return this.first
    }

    fun removeEntry(T t) {
        if (t.getList() == this) {
            InlineListEntry next = t.getNext()
            InlineListEntry prev = t.getPrev()
            if (prev != null) {
                prev.setNext(next)
            } else {
                this.first = next
            }
            if (next != null) {
                next.setPrev(prev)
            }
            t.setPrev(null)
            t.setNext(null)
            t.setList(null)
        }
    }

    fun requestEntryRemoval(T t) {
    }
}
