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
        if (first.toInt() != 0xFF) {
            return first.toInt() to MessageFrequency.HIGH
        }

        val second = buffer.get()
        if (second.toInt() != 0xFF) {
            val id = second.toInt() or 0xFF00
            return id to MessageFrequency.MEDIUM
        }

        val high = buffer.get().toInt()
        val low = buffer.get().toInt() and 0xFF
        val id = ((high shl 8) and 0xFF00) or 0xFF0000.toInt() or low
        return id to MessageFrequency.LOW
    }
}
