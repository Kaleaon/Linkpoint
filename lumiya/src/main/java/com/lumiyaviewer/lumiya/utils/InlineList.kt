package com.lumiyaviewer.lumiya.utils

open class InlineList<T : InlineListEntry<T>> {
    protected var first: T? = null

    open fun addEntry(t: T) {
        val list = t.getList()
        if (list !== this) {
            list?.removeEntry(t)
            t.setNext(first)
            t.setPrev(null)
            first?.setPrev(t)
            first = t
            t.setList(this)
        }
    }

    fun getFirst(): T? = first

    open fun removeEntry(t: T) {
        if (t.getList() === this) {
            val next = t.getNext()
            val prev = t.getPrev()
            if (prev != null) {
                prev.setNext(next)
            } else {
                first = next
            }
            if (next != null) {
                next.setPrev(prev)
            }
            t.setPrev(null)
            t.setNext(null)
            t.setList(null)
        }
    }

    open fun requestEntryRemoval(t: T) {
        // Empty implementation
    }
}
