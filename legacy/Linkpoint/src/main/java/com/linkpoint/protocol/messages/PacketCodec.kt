package com.linkpoint.protocol.messages

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Linkpoint-native SL packet codec inspired by Firestorm's DataPackerBinaryBuffer.
 *
 * Canonical SL field behavior:
 * - UUID: always 16 bytes in big-endian network order
 * - Numeric primitives: little-endian for message payload fields
 * - Variable-1/Variable-2 strings include the trailing NUL in the prefixed length
 */
class PacketCodec private constructor(private val buffer: ByteBuffer) {

    companion object {
        fun writing(capacity: Int): PacketCodec =
            PacketCodec(ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN))

        fun wrapping(data: ByteArray): PacketCodec =
            PacketCodec(ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN))

        fun fromBuffer(buffer: ByteBuffer): PacketCodec = PacketCodec(buffer)
    }

    fun buffer(): ByteBuffer = buffer

    fun toByteArray(): ByteArray = buffer.array()

    fun readUuid(): UUID {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        val msb = buffer.long
        val lsb = buffer.long
        buffer.order(originalOrder)
        return UUID(msb, lsb)
    }

    fun writeUuid(uuid: UUID): PacketCodec {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        buffer.order(originalOrder)
        return this
    }

    fun readLeInt(): Int = buffer.int
    fun readLeLong(): Long = buffer.long
    fun readLeShort(): Short = buffer.short
    fun readLeFloat(): Float = buffer.float
    fun readLeByte(): Byte = buffer.get()

    fun writeLeInt(value: Int): PacketCodec = apply { buffer.putInt(value) }
    fun writeLeLong(value: Long): PacketCodec = apply { buffer.putLong(value) }
    fun writeLeShort(value: Short): PacketCodec = apply { buffer.putShort(value) }
    fun writeLeFloat(value: Float): PacketCodec = apply { buffer.putFloat(value) }
    fun writeLeByte(value: Byte): PacketCodec = apply { buffer.put(value) }

    fun readVariable1Bytes(): ByteArray {
        val length = buffer.get().toInt() and 0xFF
        val bytes = ByteArray(length)
        if (length > 0) buffer.get(bytes)
        return bytes
    }

    fun readVariable2Bytes(): ByteArray {
        val length = buffer.short.toInt() and 0xFFFF
        val bytes = ByteArray(length)
        if (length > 0) buffer.get(bytes)
        return bytes
    }

    fun readVariable1String(): String {
        val bytes = readVariable1Bytes()
        return if (bytes.isNotEmpty() && bytes.last() == 0.toByte()) {
            String(bytes, 0, bytes.size - 1, Charsets.UTF_8)
        } else {
            String(bytes, Charsets.UTF_8)
        }
    }

    fun readVariable2String(): String {
        val bytes = readVariable2Bytes()
        return if (bytes.isNotEmpty() && bytes.last() == 0.toByte()) {
            String(bytes, 0, bytes.size - 1, Charsets.UTF_8)
        } else {
            String(bytes, Charsets.UTF_8)
        }
    }

    fun writeVariable1String(text: String): PacketCodec {
        val raw = text.toByteArray(Charsets.UTF_8)
        val capped = if (raw.size > 254) raw.copyOf(254) else raw
        buffer.put((capped.size + 1).toByte())
        buffer.put(capped)
        buffer.put(0.toByte())
        return this
    }

    fun writeVariable1Bytes(bytes: ByteArray): PacketCodec {
        val capped = if (bytes.size > 255) bytes.copyOf(255) else bytes
        buffer.put(capped.size.toByte())
        buffer.put(capped)
        return this
    }

    fun writeVariable2String(text: String): PacketCodec {
        val raw = text.toByteArray(Charsets.UTF_8)
        val capped = if (raw.size > 65534) raw.copyOf(65534) else raw
        buffer.putShort((capped.size + 1).toShort())
        buffer.put(capped)
        buffer.put(0.toByte())
        return this
    }

    fun writeVariable2Bytes(bytes: ByteArray): PacketCodec {
        val capped = if (bytes.size > 65535) bytes.copyOf(65535) else bytes
        buffer.putShort(capped.size.toShort())
        buffer.put(capped)
        return this
    }
}
