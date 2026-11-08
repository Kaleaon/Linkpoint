package com.linkpoint.linden.llcorehttp

class LLBufferArray {
    private val segments: MutableList<ByteArray> = mutableListOf()

    fun append(data: ByteArray) {
        if (data.isNotEmpty()) segments.add(data.copyOf())
    }

    fun append(data: ByteArray, offset: Int, length: Int) {
        if (length > 0) segments.add(data.copyOfRange(offset, offset + length))
    }

    fun read(dest: ByteArray, offset: Int, length: Int): Int {
        val total = toByteArray()
        val avail = minOf(length, total.size)
        total.copyInto(dest, offset, 0, avail)
        return avail
    }

    fun size(): Int = segments.sumOf { it.size }

    fun isEmpty(): Boolean = segments.isEmpty()

    fun toByteArray(): ByteArray {
        val result = ByteArray(size())
        var pos = 0
        for (seg in segments) { seg.copyInto(result, pos); pos += seg.size }
        return result
    }

    fun clear() = segments.clear()

    fun segmentCount(): Int = segments.size
}
