package com.linkpoint.protocol.messages

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class PacketCodecCompatibilityTest {

    @Test
    fun `UDPConnectionFixed legacy Variable-1 string encoding matches PacketCodec`() {
        val legacy = ByteBuffer.allocate(260).order(ByteOrder.LITTLE_ENDIAN)
        legacyPutVariable1String(legacy, "hello")

        val codecBuffer = ByteBuffer.allocate(260).order(ByteOrder.LITTLE_ENDIAN)
        PacketCodec.fromBuffer(codecBuffer).writeVariable1String("hello")

        assertArrayEquals(
            legacy.array().copyOf(legacy.position()),
            codecBuffer.array().copyOf(codecBuffer.position())
        )
    }

    @Test
    fun `UDPConnectionFixed legacy Variable-2 bytes encoding matches PacketCodec`() {
        val payload = ByteArray(32) { it.toByte() }
        val legacy = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN)
        legacyPutVariable2Bytes(legacy, payload)

        val codecBuffer = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN)
        PacketCodec.fromBuffer(codecBuffer).writeVariable2Bytes(payload)

        assertArrayEquals(
            legacy.array().copyOf(legacy.position()),
            codecBuffer.array().copyOf(codecBuffer.position())
        )
    }

    @Test
    fun `legacy UUID byte conversion matches PacketCodec big-endian UUID writes`() {
        val uuid = UUID.fromString("12345678-1234-5678-90ab-cdef12345678")
        val legacy = legacyUuidAsBytes(uuid)

        val codecBuffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
        PacketCodec.fromBuffer(codecBuffer).writeUuid(uuid)

        assertArrayEquals(legacy, codecBuffer.array())
    }

    @Test
    fun `LE primitive read-write roundtrip stays canonical`() {
        val codec = PacketCodec.writing(32)
        codec.writeLeInt(0x12345678)
            .writeLeShort(0x4321.toShort())
            .writeLeFloat(123.5f)
            .writeLeLong(0x0102030405060708L)

        val reader = PacketCodec.wrapping(codec.toByteArray())
        assertEquals(0x12345678, reader.readLeInt())
        assertEquals(0x4321.toShort(), reader.readLeShort())
        assertEquals(123.5f, reader.readLeFloat())
        assertEquals(0x0102030405060708L, reader.readLeLong())
    }

    private fun legacyPutVariable1String(buffer: ByteBuffer, text: String) {
        val raw = text.toByteArray(Charsets.UTF_8)
        val capped = if (raw.size > 254) raw.copyOf(254) else raw
        buffer.put((capped.size + 1).toByte())
        buffer.put(capped)
        buffer.put(0.toByte())
    }

    private fun legacyPutVariable2Bytes(buffer: ByteBuffer, bytes: ByteArray) {
        val capped = if (bytes.size > 65535) bytes.copyOf(65535) else bytes
        buffer.putShort(capped.size.toShort())
        buffer.put(capped)
    }

    private fun legacyUuidAsBytes(uuid: UUID): ByteArray {
        val bytes = ByteArray(16)
        val mostSignificantBits = uuid.mostSignificantBits
        val leastSignificantBits = uuid.leastSignificantBits
        for (i in 7 downTo 0) {
            bytes[7 - i] = (mostSignificantBits shr (i * 8)).toByte()
        }
        for (i in 7 downTo 0) {
            bytes[15 - i] = (leastSignificantBits shr (i * 8)).toByte()
        }
        return bytes
    }
}
