package com.linkpoint.protocol.binary

import java.nio.ByteBuffer
import java.nio.ByteOrder

object SLPacketFlags {
    const val ZEROCODED: Int = 0x80
    const val RELIABLE: Int = 0x40
    const val RESENT: Int = 0x20
    const val APPENDED_ACKS: Int = 0x10
}

enum class SLPacketFrequency { HIGH, MEDIUM, LOW, FIXED }

data class SLPacketHeader(
    val flags: Int,
    val sequenceNumber: Long,
    val extraBytesCount: Int,
    val messageId: Int,
    val frequency: SLPacketFrequency,
    val extraData: ByteArray,
    val acks: List<Long>,
    val bodyOffset: Int
) {
    val isZerocoded: Boolean get() = (flags and SLPacketFlags.ZEROCODED) != 0
    val isReliable: Boolean get() = (flags and SLPacketFlags.RELIABLE) != 0
    val isResent: Boolean get() = (flags and SLPacketFlags.RESENT) != 0
    val hasAppendedAcks: Boolean get() = (flags and SLPacketFlags.APPENDED_ACKS) != 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SLPacketHeader) return false
        return flags == other.flags && sequenceNumber == other.sequenceNumber &&
                extraBytesCount == other.extraBytesCount && messageId == other.messageId &&
                frequency == other.frequency && extraData.contentEquals(other.extraData) &&
                acks == other.acks && bodyOffset == other.bodyOffset
    }

    override fun hashCode(): Int {
        var result = flags
        result = 31 * result + sequenceNumber.hashCode()
        result = 31 * result + extraBytesCount
        result = 31 * result + messageId
        result = 31 * result + frequency.hashCode()
        result = 31 * result + extraData.contentHashCode()
        result = 31 * result + acks.hashCode()
        result = 31 * result + bodyOffset
        return result
    }
}

object SLPacketCodec {
    fun zeroDecompress(src: ByteArray, headerSize: Int = 6): ByteArray {
        if (src.size <= headerSize) return src.copyOf()
        val out = mutableListOf<Byte>()
        for (i in 0 until headerSize) {
            out.add(src[i])
        }
        var i = headerSize
        while (i < src.size) {
            val b = src[i]
            if (b == 0.toByte()) {
                if (i + 1 < src.size) {
                    val count = src[i + 1].toInt() and 0xFF
                    for (c in 0 until count) {
                        out.add(0.toByte())
                    }
                    i += 2
                } else {
                    out.add(0.toByte())
                    i++
                }
            } else {
                out.add(b)
                i++
            }
        }
        return out.toByteArray()
    }

    fun parseHeader(packetBytes: ByteArray): SLPacketHeader {
        require(packetBytes.size >= 7) { "Packet length too short for header: ${packetBytes.size}" }
        val buffer = ByteBuffer.wrap(packetBytes).order(ByteOrder.BIG_ENDIAN)

        val flags = buffer.get().toInt() and 0xFF
        val seq = buffer.int.toLong() and 0xFFFFFFFFL
        val extraLen = buffer.get().toInt() and 0xFF

        require(6 + extraLen < packetBytes.size) { "Extra header length exceeds packet size" }

        val extraData = ByteArray(extraLen)
        buffer.get(extraData)

        val first = buffer.get().toInt() and 0xFF
        val freq: SLPacketFrequency
        val msgId: Int

        if (first != 0xFF) {
            freq = SLPacketFrequency.HIGH
            msgId = first
        } else {
            val second = buffer.get().toInt() and 0xFF
            if (second != 0xFF) {
                freq = SLPacketFrequency.MEDIUM
                msgId = second
            } else {
                val high = buffer.get().toInt() and 0xFF
                if (high == 0xFF) {
                    freq = SLPacketFrequency.FIXED
                    msgId = buffer.get().toInt() and 0xFF
                } else {
                    val low = buffer.get().toInt() and 0xFF
                    freq = SLPacketFrequency.LOW
                    msgId = (high shl 8) or low
                }
            }
        }

        val acks = mutableListOf<Long>()
        val bodyOffset = buffer.position()

        if ((flags and SLPacketFlags.APPENDED_ACKS) != 0 && packetBytes.isNotEmpty()) {
            val ackCount = packetBytes.last().toInt() and 0xFF
            val acksStart = packetBytes.size - 1 - (ackCount * 4)
            if (acksStart >= bodyOffset) {
                val ackBuffer = ByteBuffer.wrap(packetBytes, acksStart, ackCount * 4).order(ByteOrder.BIG_ENDIAN)
                for (i in 0 until ackCount) {
                    acks.add(ackBuffer.int.toLong() and 0xFFFFFFFFL)
                }
            }
        }

        return SLPacketHeader(
            flags = flags,
            sequenceNumber = seq,
            extraBytesCount = extraLen,
            messageId = msgId,
            frequency = freq,
            extraData = extraData,
            acks = acks,
            bodyOffset = bodyOffset
        )
    }
}
