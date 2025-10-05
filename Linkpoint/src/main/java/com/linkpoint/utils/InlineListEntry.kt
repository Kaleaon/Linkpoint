package com.linkpoint.utils

interface InlineListEntry<T : InlineListEntry<T>> {
    fun getList(): InlineList<T>?
    fun getNext(): T?
    fun getPrev(): T?
    fun requestEntryRemoval()
    fun setList(inlineList: InlineList<T>?)
    fun setNext(next: T?)
    fun setPrev(prev: T?)
}