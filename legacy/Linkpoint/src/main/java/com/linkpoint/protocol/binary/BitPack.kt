package com.linkpoint.protocol.binary

import kotlin.math.min

/**
 * Bit-level pack/unpack utility for protocol payloads.
 *
 * Bits are packed and unpacked MSB-first within each byte to match
 * Second Life terrain/message bitstream conventions.
 */
class BitPack private constructor(
    private val buffer: ByteArray,
    private val limitBytes: Int,
    private val readOnly: Boolean,
    private var writeByteIndex: Int,
    private var readByteIndex: Int,
) {
    private var writeAccumulator = 0
    private var writeBitCount = 0

    private var readBitOffset = 0

    constructor(maxBytes: Int) : this(
        buffer = ByteArray(maxBytes),
        limitBytes = maxBytes,
        readOnly = false,
        writeByteIndex = 0,
        readByteIndex = 0,
    )

    constructor(source: ByteArray) : this(
        buffer = source.copyOf(),
        limitBytes = source.size,
        readOnly = true,
        writeByteIndex = source.size,
        readByteIndex = 0,
    )

    /** Number of fully written bytes, excluding any unflushed accumulator bits. */
    fun byteSize(): Int = writeByteIndex

    /** Number of bits currently staged in the write accumulator. */
    fun pendingBits(): Int = writeBitCount

    /**
     * Pack [bitCount] bits from [value], taking bits from most-significant to least-significant.
     */
    fun packBits(value: Int, bitCount: Int) {
        require(!readOnly) { "BitPack is read-only" }
        require(bitCount >= 0) { "bitCount must be non-negative" }
        require(bitCount <= 32) { "bitCount must be <= 32" }

        if (bitCount == 0) return

        for (bitIndex in bitCount - 1 downTo 0) {
            val bit = (value ushr bitIndex) and 0x01
            appendBit(bit)
        }
    }

    /**
     * Pack [bitCount] bits from [source], reading source bits MSB-first.
     */
    fun packBits(source: ByteArray, bitCount: Int) {
        require(!readOnly) { "BitPack is read-only" }
        require(bitCount >= 0) { "bitCount must be non-negative" }
        require(bitCount <= source.size * 8) {
            "bitCount exceeds source bit length"
        }

        var remaining = bitCount
        var sourceByte = 0

        while (remaining > 0) {
            val bitsThisByte = min(remaining, 8)
            val value = source[sourceByte].toInt() and 0xFF
            for (bitIndex in 7 downTo 8 - bitsThisByte) {
                appendBit((value ushr bitIndex) and 0x01)
            }
            sourceByte++
            remaining -= bitsThisByte
        }
    }

    /**
     * Flushes the write accumulator into the backing buffer.
     *
     * Any partial byte is left-aligned and padded with zero bits.
     */
    fun flush() {
        require(!readOnly) { "BitPack is read-only" }

        if (writeBitCount == 0) return

        val padded = writeAccumulator shl (8 - writeBitCount)
        writeByte(padded and 0xFF)
        writeAccumulator = 0
        writeBitCount = 0
    }

    /**
     * Returns a copy of the written bytes.
     *
     * If [flushPending] is true, a partial accumulator byte is flushed first.
     */
    fun toByteArray(flushPending: Boolean = true): ByteArray {
        if (!readOnly && flushPending) {
            flush()
        }
        return buffer.copyOf(writeByteIndex)
    }

    /**
     * Unpacks [bitCount] bits into an unsigned int represented as [Int].
     */
    fun unpackBits(bitCount: Int): Int {
        require(bitCount >= 0) { "bitCount must be non-negative" }
        require(bitCount <= 32) { "bitCount must be <= 32" }

        var value = 0
        repeat(bitCount) {
            value = (value shl 1) or readBit()
        }
        return value
    }

    private fun appendBit(bit: Int) {
        writeAccumulator = (writeAccumulator shl 1) or (bit and 0x01)
        writeBitCount++

        if (writeBitCount == 8) {
            writeByte(writeAccumulator and 0xFF)
            writeAccumulator = 0
            writeBitCount = 0
        }
    }

    private fun writeByte(value: Int) {
        check(writeByteIndex < limitBytes) {
            "BitPack overflow: limit=$limitBytes bytes"
        }

        buffer[writeByteIndex] = value.toByte()
        writeByteIndex++
    }

    private fun readBit(): Int {
        check(readByteIndex < limitBytes) {
            "BitPack underflow: attempted to read past $limitBytes bytes"
        }

        val current = buffer[readByteIndex].toInt() and 0xFF
        val bit = (current ushr (7 - readBitOffset)) and 0x01

        readBitOffset++
        if (readBitOffset == 8) {
            readBitOffset = 0
            readByteIndex++
        }

        return bit
    }
}
