package com.lumiyaviewer.lumiya.utils

interface InlineListEntry<T : InlineListEntry<T>> {
    fun getList(): InlineList<T>?
    fun getNext(): T?
    fun getPrev(): T?
    fun requestEntryRemoval()
    fun setList(inlineList: InlineList<T>?)
    fun setNext(t: T?)
    fun setPrev(t: T?)
}