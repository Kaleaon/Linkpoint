package com.lumiyaviewer.lumiya.utils

interface InlineListEntry<T : InlineListEntry<T>> {
    fun getList(): InlineList<T>?
    fun getNext(): T?
    fun getPrev(): T?
    fun requestEntryRemoval()
    fun setList(list: InlineList<T>?)
    fun setNext(next: T?)
    fun setPrev(prev: T?)
}
