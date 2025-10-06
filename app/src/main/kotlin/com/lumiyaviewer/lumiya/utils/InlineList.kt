package com.lumiyaviewer.lumiya.utils

class InlineList<T : InlineListEntry<T>> {
    private var first: T? = null

    fun addEntry(entry: T) {
        val list = entry.getList()
        if (list != this) {
            list?.removeEntry(entry)
            
            entry.setNext(first)
            entry.setPrev(null)
            first?.setPrev(entry)
            first = entry
            entry.setList(this)
        }
    }

    fun getFirst(): T? = first

    fun removeEntry(entry: T) {
        if (entry.getList() == this) {
            val next = entry.getNext()
            val prev = entry.getPrev()
            
            if (prev != null) {
                prev.setNext(next)
            } else {
                first = next
            }
            
            if (next != null) {
                next.setPrev(prev)
            }
            
            entry.setPrev(null)
            entry.setNext(null)
            entry.setList(null)
        }
    }

    fun requestEntryRemoval(entry: T) {
        // Empty implementation
    }
}
