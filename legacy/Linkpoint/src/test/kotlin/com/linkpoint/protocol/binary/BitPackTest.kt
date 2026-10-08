package com.linkpoint.protocol.binary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BitPackTest {

    @Test
    fun `round-trip pack and unpack works across non byte aligned fields`() {
        val writer = BitPack(maxBytes = 8)

        writer.packBits(0b101, 3)
        writer.packBits(0b11011, 5)
        writer.packBits(0b1010110011, 10)
        writer.packBits(0b1111111, 7)

        val packed = writer.toByteArray(flushPending = true)
        val reader = BitPack(packed)

        assertEquals(0b101, reader.unpackBits(3))
        assertEquals(0b11011, reader.unpackBits(5))
        assertEquals(0b1010110011, reader.unpackBits(10))
        assertEquals(0b1111111, reader.unpackBits(7))
    }

    @Test
    fun `flush writes pending accumulator bits left aligned and zero padded`() {
        val writer = BitPack(maxBytes = 2)

        writer.packBits(0b101, 3)
        assertEquals(0, writer.byteSize())
        assertEquals(3, writer.pendingBits())

        writer.flush()
        assertEquals(1, writer.byteSize())
        assertEquals(0, writer.pendingBits())

        assertArrayEquals(byteArrayOf(0b10100000.toByte()), writer.toByteArray(flushPending = false))
    }

    @Test
    fun `max size boundary is enforced for packing and unpacking`() {
        val writer = BitPack(maxBytes = 1)
        writer.packBits(0b11001010, 8)
        assertEquals(1, writer.byteSize())

        val overflowError = runCatching {
            writer.packBits(1, 1)
            writer.flush()
        }.exceptionOrNull()
        assertTrue(overflowError is IllegalStateException)

        val reader = BitPack(byteArrayOf(0x7F))
        assertEquals(0x7F, reader.unpackBits(8))

        val underflowError = runCatching {
            reader.unpackBits(1)
        }.exceptionOrNull()
        assertTrue(underflowError is IllegalStateException)
    }
}
