package com.linkpoint.utils.wlist

import java.util.AbstractList
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Iterator
import java.util.List
import java.util.RandomAccess
import javax.annotation.Nonnull

class ChunkedList<E> : AbstractList<E>, RandomAccess {
    private val List<List<E>> chunks = ArrayList()
    private Int count = 0
    private List<E> lastChunk = null
    private Int lastChunkIndex = 0
    private Int lastChunkSize = 0
    private Int lastChunkStart = 0

    interface ChunkFactory<E> {
        List<E> createEmptyChunk()
    }

     private fun checkConsistency() {
        val i: Int = 0
        for (List size : this.chunks) {
            i = size.size() + i
        }
        if (i != this.count) {
            throw IllegalStateException(String.format("newCount %d, count %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(this.count)}))
        }
    }

     private fun replaceElementInChunk(list: List<E>, E e, comparator: Comparator<E>): Int {
        Int binarySearch
        if (list.isEmpty() || (binarySearch = Collections.binarySearch(list, e, comparator)) < 0) {
            return -1
        }
        return replaceFoundElement(list, binarySearch, e)
    }

     private fun replaceFoundElement(list: List<E>, i: Int, E e): Int {
        list.set(i, e)
        val i2: Int = 0
        val it: Iterator<T> = this.chunks.iterator()
        while (true) {
            val i3: Int = i2
            if (!it.hasNext()) {
                return -1
            }
            val list2: List<E> = (List) it.next()
            if (list2 == list) {
                return i3 + i
            }
            i2 = list2.size() + i3
        }
    }

     private fun resetLastPosition() {
        this.lastChunk = null
        checkConsistency()
    }

     private fun setLastChunk(i: Int) {
        if (i < 0 || i >= this.count) {
            throw IndexOutOfBoundsException(String.format("index %d, count %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(this.count)}))
        }
        checkConsistency()
        if (this.lastChunk == null) {
            this.lastChunkIndex = 0
            this.lastChunkStart = 0
            this.lastChunk = this.chunks.get(this.lastChunkIndex)
            this.lastChunkSize = this.lastChunk.size()
        }
        while (i < this.lastChunkStart) {
            this.lastChunkIndex--
            this.lastChunk = this.chunks.get(this.lastChunkIndex)
            this.lastChunkSize = this.lastChunk.size()
            this.lastChunkStart -= this.lastChunkSize
        }
        while (i >= this.lastChunkStart + this.lastChunkSize) {
            this.lastChunkIndex++
            this.lastChunkStart += this.lastChunkSize
            if (this.lastChunkIndex >= this.chunks.size()) {
                throw IllegalStateException(String.format("lastChunkIndex runaway, position %d, count %d, lastChunkStart %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(this.count), Integer.valueOf(this.lastChunkStart)}))
            } else {
                this.lastChunk = this.chunks.get(this.lastChunkIndex)
                this.lastChunkSize = this.lastChunk.size()
            }
        }
    }

    fun addChunkAtEnd(list: List<E>) {
        this.chunks.add(list)
        this.count += list.size()
        resetLastPosition()
    }

    fun addChunkAtStart(list: List<E>) {
        this.chunks.add(0, list)
        this.count += list.size()
        resetLastPosition()
    }

    fun addElement(E e, i: Int, chunkFactory: ChunkFactory<E>) {
        val list: List<E> = null
        if (this.chunks.size() > 0) {
            list = this.chunks.get(this.chunks.size() - 1)
        }
        if (list == null || list.size() >= i) {
            val createEmptyChunk: List<E> = chunkFactory.createEmptyChunk()
            createEmptyChunk.add(e)
            this.chunks.add(createEmptyChunk)
            this.count++
        } else {
            list.add(e)
            this.count++
            if (this.lastChunk == list) {
                this.lastChunkSize++
            }
        }
        checkConsistency()
    }

    fun clear() {
        this.chunks.clear()
        this.count = 0
        resetLastPosition()
    }

    public E get(Int i) {
        setLastChunk(i)
        if (i >= this.lastChunkStart && i < this.lastChunkStart + this.lastChunkSize) {
            return this.lastChunk.get(i - this.lastChunkStart)
        }
        throw IndexOutOfBoundsException(String.format("index %d, count %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(this.count)}))
    }

     public fun removeChunkAtEnd(): Int {
        if (this.chunks.size() <= 0) {
            return 0
        }
        val remove: List = this.chunks.remove(this.chunks.size() - 1)
        val size: Int = remove != null ? remove.size() : 0
        this.count -= size
        resetLastPosition()
        return size
    }

     public fun removeChunkAtStart(): Int {
        if (this.chunks.size() <= 0) {
            return 0
        }
        val remove: List = this.chunks.remove(0)
        val size: Int = remove != null ? remove.size() : 0
        this.count -= size
        resetLastPosition()
        return size
    }

     public fun removeElementsAfter(i: Int): Int {
        checkConsistency()
        if (i < 0 || i >= this.count) {
            return 0
        }
        setLastChunk(i)
        if (i < this.lastChunkStart || i >= this.lastChunkStart + this.lastChunkSize) {
            return 0
        }
        val i2: Int = this.lastChunkIndex + 2
        val i3: Int = 0
        for (Int size = this.chunks.size() - 1; size >= i2; size--) {
            i3 += this.chunks.get(size).size()
            this.chunks.remove(size)
        }
        this.count -= i3
        checkConsistency()
        return i3
    }

     public fun removeElementsBefore(i: Int): Int {
        checkConsistency()
        if (i < 0 || i >= this.count) {
            return 0
        }
        setLastChunk(i)
        if (i < this.lastChunkStart || i >= this.lastChunkStart + this.lastChunkSize) {
            return 0
        }
        val i4: Int = this.lastChunkIndex - 2
        if (i4 >= 0) {
            i3 = i4 + 1
            i2 = 0
        } else {
            i3 = 0
            i2 = 0
        }
        while (i3 > 0) {
            i2 += this.chunks.get(0).size()
            this.chunks.remove(0)
            i3--
        }
        this.count -= i2
        resetLastPosition()
        return i2
    }

     public fun replaceElement(E e, comparator: Comparator<E>): Int {
        Char c
        if (this.chunks.isEmpty()) {
            return -1
        }
        val c2: Char = 0
        val size: Int = this.chunks.size() / 2
        while (true) {
            val list: List = this.chunks.get(size)
            if (!list.isEmpty()) {
                val obj: Object = list.get(0)
                val obj2: Object = list.get(list.size() - 1)
                val compare: Int = comparator.compare(e, obj)
                if (compare == 0) {
                    return replaceFoundElement(list, 0, e)
                }
                if (compare < 0) {
                    i = size - 1
                    if (i < 0) {
                        return -1
                    }
                    c = 65535
                    size = i
                    c2 = c
                } else {
                    val compare2: Int = comparator.compare(e, obj2)
                    if (compare2 == 0) {
                        return replaceFoundElement(list, list.size() - 1, e)
                    }
                    if (compare2 <= 0) {
                        return replaceElementInChunk(list, e, comparator)
                    }
                    i2 = size + 1
                    c2 = 1
                    if (i2 >= this.chunks.size()) {
                        return -1
                    }
                }
            } else if (c2 < 0) {
                i2 = size - 1
                if (i2 < 0) {
                    return -1
                }
            } else if (c2 > 0) {
                i2 = size + 1
                if (i2 >= this.chunks.size()) {
                    return -1
                }
            } else {
                val i3: Int = 0
                while (true) {
                    if (i3 >= this.chunks.size()) {
                        i2 = -1
                        break
                    } else if (!this.chunks.get(i3).isEmpty()) {
                        i2 = i3
                        break
                    } else {
                        i3++
                    }
                }
                if (i2 == -1) {
                    return -1
                }
            }
            val c3: Char = c2
            i = i2
            c = c3
            size = i
            c2 = c
        }
    }

     public fun size(): Int {
        return this.count
    }
}
