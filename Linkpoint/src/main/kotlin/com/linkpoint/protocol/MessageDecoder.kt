package com.linkpoint.protocol

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.SLMessageFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class MessageFrequency {
    HIGH,
    MEDIUM,
    LOW
}

object MessageDecoder {

    fun decode(buffer: ByteBuffer): SLMessage? {
        return try {
            if (!buffer.hasRemaining()) {
                return null
            }

            val (messageId, _) = readMessageId(buffer)
            val message = SLMessageFactory.CreateByID(messageId)
            if (message == null) {
                Debug.Log("Unknown message id: $messageId")
                return null
            }

            val originalOrder = buffer.order()
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            try {
                message.decode(buffer)
            } finally {
                buffer.order(originalOrder)
            }

            message
        } catch (e: Exception) {
            Debug.Log("Message decode error: ${e.message}")
            null
        }
    }

    private fun readMessageId(buffer: ByteBuffer): Pair<Int, MessageFrequency> {
        val first = buffer.get()
        if (first != 0xFF.toByte()) {
            return (first.toInt() and 0xFF) to MessageFrequency.HIGH
        }

        val second = buffer.get()
        if (second != 0xFF.toByte()) {
            val id = (second.toInt() and 0xFF) or 0xFF00
            return id to MessageFrequency.MEDIUM
        }

        val high = buffer.get()
        val low = buffer.get()
        val id = (-0x10000) or ((high.toInt() and 0xFF) shl 8) or (low.toInt() and 0xFF)
        return id to MessageFrequency.LOW
    }
}
