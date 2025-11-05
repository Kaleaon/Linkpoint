package com.linkpoint.slproto

import com.linkpoint.protocol.MessageDecoder
import java.nio.ByteBuffer
import java.util.ArrayList

private const val FLAG_MASK = 0xFF

/**
 * Represents a Second Life UDP packet with header metadata, optional ACKs, and
 * an encoded message payload.
 */
class SLPacket private constructor(
    val header: SLPacketHeader,
    val message: SLMessage,
    val ackIds: List<Int>
) {

    /**
     * Write this packet into the provided [ByteBuffer]. The buffer must be
     * cleared before calling this function and will be ready for writing to the
     * network after it returns (position set to end of packet data).
     */
    fun writeTo(buffer: ByteBuffer) {
        val clampedAcks = if (ackIds.size > MAX_ACKS_PER_PACKET) {
            ackIds.subList(0, MAX_ACKS_PER_PACKET)
        } else {
            ackIds
        }

        buffer.put(header.flagsWithAck(clampedAcks.isNotEmpty()))
        buffer.putInt(header.sequenceNumber)
        buffer.put(header.offset)

        if (clampedAcks.isNotEmpty()) {
            buffer.put(clampedAcks.size.toByte())
            for (ack in clampedAcks) {
                buffer.putInt(ack)
            }
        }

        message.encode(buffer)
    }

    companion object {
        private const val HEADER_MIN_BYTES = 6
        private const val MAX_ACKS_PER_PACKET = 255

        /**
         * Create a packet ready to be sent over the network.
         */
        fun outgoing(
            message: SLMessage,
            sequenceNumber: Int,
            isResent: Boolean = false,
            ackIds: List<Int> = emptyList(),
            offset: Byte = 0
        ): SLPacket {
            val clampedAcks = if (ackIds.size > MAX_ACKS_PER_PACKET) {
                ackIds.subList(0, MAX_ACKS_PER_PACKET)
            } else {
                ackIds
            }

            val header = SLPacketHeader.forMessage(
                message = message,
                sequenceNumber = sequenceNumber,
                isResent = isResent,
                hasAck = clampedAcks.isNotEmpty(),
                offset = offset
            )

            return SLPacket(header, message, clampedAcks.toList())
        }

        /**
         * Decode a packet from the provided [ByteBuffer]. The buffer position must
         * point to the beginning of the packet (flags byte). On success this will
         * advance the buffer position to the end of the message payload.
         */
        fun decode(
            buffer: ByteBuffer,
            decoder: (ByteBuffer) -> SLMessage?
        ): DecodedPacket? {
            if (buffer.remaining() < HEADER_MIN_BYTES) {
                return null
            }

            val flags = buffer.get()
            val sequence = buffer.getInt()
            val offset = buffer.get()

            val ackIds = if ((flags.toInt() and SLMessage.LL_ACK_FLAG.toInt()) != 0) {
                if (!buffer.hasRemaining()) {
                    return null
                }

                val count = buffer.get().toInt() and FLAG_MASK
                val ids = ArrayList<Int>(count)
                for (i in 0 until count) {
                    if (buffer.remaining() < Int.SIZE_BYTES) {
                        return null
                    }
                    ids.add(buffer.getInt())
                }
                ids
            } else {
                emptyList()
            }

            val payloadSlice = buffer.slice().asReadOnlyBuffer()
            val message = decoder(buffer)

            val header = SLPacketHeader(flags, sequence, offset)
            return DecodedPacket(header, ackIds, message, payloadSlice)
        }

        /**
         * Convenience overload that uses the standard [MessageDecoder].
         */
        fun decode(buffer: ByteBuffer): DecodedPacket? {
            return decode(buffer) { MessageDecoder.decode(it) }
        }
    }
}

/**
 * Header metadata for an SL packet.
 */
data class SLPacketHeader(
    val flags: Byte,
    val sequenceNumber: Int,
    val offset: Byte = 0
) {
    val isReliable: Boolean
        get() = ((flags.toInt() and FLAG_MASK) and SLMessage.LL_RELIABLE_FLAG.toInt()) != 0

    val isResent: Boolean
        get() = ((flags.toInt() and FLAG_MASK) and SLMessage.LL_RESENT_FLAG.toInt()) != 0

    val hasAck: Boolean
        get() = ((flags.toInt() and FLAG_MASK) and SLMessage.LL_ACK_FLAG.toInt()) != 0

    fun flagsWithAck(hasAck: Boolean): Byte {
        val flagInt = flags.toInt() and FLAG_MASK
        return if (hasAck) {
            ((flagInt or SLMessage.LL_ACK_FLAG.toInt()) and FLAG_MASK).toByte()
        } else {
            ((flagInt and SLMessage.LL_ACK_FLAG.toInt().inv()) and FLAG_MASK).toByte()
        }
    }

    companion object {
        fun forMessage(
            message: SLMessage,
            sequenceNumber: Int,
            isResent: Boolean = false,
            hasAck: Boolean = false,
            offset: Byte = 0
        ): SLPacketHeader {
            var flags = 0
            if (message.isReliable) {
                flags = flags or SLMessage.LL_RELIABLE_FLAG.toInt()
            }
            if (isResent) {
                flags = flags or SLMessage.LL_RESENT_FLAG.toInt()
            }
            if (hasAck) {
                flags = flags or SLMessage.LL_ACK_FLAG.toInt()
            }

            return SLPacketHeader((flags and FLAG_MASK).toByte(), sequenceNumber, offset)
        }
    }
}

/**
 * Result of decoding a packet from the network.
 */
data class DecodedPacket(
    val header: SLPacketHeader,
    val ackIds: List<Int>,
    val message: SLMessage?,
    val payload: ByteBuffer
)
