package com.linkpoint.chat.encoder

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

/**
 * Abstract base class for chat packet serialization across spatial chat,
 * direct instant messaging, and group chat channels.
 *
 * Encapsulates sequence tracking, 12-byte header framing, and UTF-8 string
 * payload packing using the Template Method pattern.
 */
abstract class AbstractChatPacketEncoder(
    initialSequence: Int = 1
) {
    private val sequenceCounter = AtomicInteger(initialSequence)

    companion object {
        const val HEADER_SIZE = 12
        const val MAX_PAYLOAD_SIZE = 4096
    }

    /**
     * Template method: Channel-specific header flags (Byte 0).
     */
    abstract fun getHeaderFlags(): Byte

    /**
     * Template method: Channel type identifier (Byte 5).
     */
    abstract fun getChannelTypeId(): Byte

    /**
     * Template method: Channel or session context identifier (Bytes 6..11, 6 bytes).
     */
    abstract fun getChannelSessionBytes(): ByteArray

    /**
     * Resets or sets the sequence counter state safely.
     */
    fun setSequenceNumber(sequence: Int) {
        sequenceCounter.set(sequence)
    }

    /**
     * Gets current sequence number without incrementing.
     */
    fun getCurrentSequenceNumber(): Int = sequenceCounter.get()

    /**
     * Increments and returns the next sequence number in a thread-safe manner.
     */
    protected fun getNextSequenceNumber(): Int = sequenceCounter.getAndIncrement()

    /**
     * Encodes a chat message payload into a fully framed network packet.
     *
     * Packet structure (12-byte header + payload):
     * - Byte 0: Header Flags (channel-specific)
     * - Bytes 1..4: Sequence Number (32-bit BE integer)
     * - Byte 5: Channel Type ID
     * - Bytes 6..11: Session / Channel context bytes (6 bytes)
     * - Bytes 12..N: Payload (U16 LE length prefix + UTF-8 message bytes + NUL byte + 4-byte LE channel)
     */
    fun encodePacket(message: String, channel: Int = 0): EncodedChatPacket {
        require(message.length <= MAX_PAYLOAD_SIZE) {
            "Message length exceeds maximum allowed limit of $MAX_PAYLOAD_SIZE characters"
        }

        val seq = getNextSequenceNumber()
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        val stringLen = messageBytes.size + 1 // including trailing NUL

        // Header (12 bytes) + String Payload (2 bytes len prefix + stringBytes + 1 NUL) + Channel (4 bytes LE)
        val totalSize = HEADER_SIZE + 2 + stringLen + 4
        val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN)

        // 12-byte Header Construction
        // Byte 0: Flags
        buffer.put(getHeaderFlags())

        // Bytes 1..4: Sequence Number in Big-Endian order
        buffer.put((seq shr 24).toByte())
        buffer.put((seq shr 16).toByte())
        buffer.put((seq shr 8).toByte())
        buffer.put(seq.toByte())

        // Byte 5: Channel Type ID
        buffer.put(getChannelTypeId())

        // Bytes 6..11: Session / Context bytes (6 bytes)
        val sessionBytes = getChannelSessionBytes()
        require(sessionBytes.size == 6) {
            "Channel session bytes must be exactly 6 bytes"
        }
        buffer.put(sessionBytes)

        // Payload
        // U16 LE length prefix for message string
        buffer.putShort(stringLen.toShort())
        buffer.put(messageBytes)
        buffer.put(0.toByte()) // NUL terminator

        // Channel ID (4 bytes LE)
        buffer.putInt(channel)

        return EncodedChatPacket(
            sequenceNumber = seq,
            headerFlags = getHeaderFlags(),
            channelTypeId = getChannelTypeId(),
            channel = channel,
            payload = buffer.array()
        )
    }
}

/**
 * Data class representing a fully encoded chat packet.
 */
data class EncodedChatPacket(
    val sequenceNumber: Int,
    val headerFlags: Byte,
    val channelTypeId: Byte,
    val channel: Int,
    val payload: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncodedChatPacket) return false
        return sequenceNumber == other.sequenceNumber &&
                headerFlags == other.headerFlags &&
                channelTypeId == other.channelTypeId &&
                channel == other.channel &&
                payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = sequenceNumber
        result = 31 * result + headerFlags
        result = 31 * result + channelTypeId
        result = 31 * result + channel
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
