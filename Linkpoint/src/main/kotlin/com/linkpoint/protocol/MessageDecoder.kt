package com.linkpoint.protocol

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.SLMessageFactory
import com.linkpoint.slproto.template.MessageTemplateRegistry
import com.linkpoint.slproto.template.TemplateMessageDecoder
import java.lang.reflect.Method
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
            if (message != null) {
                decodePayload(message, buffer)
                return message
            }

            val template = MessageTemplateRegistry.getTemplateById(messageId)
            if (template != null) {
                val templated = TemplateMessageDecoder.decode(template, buffer)
                if (templated != null) {
                    return templated
                }
            }

            Debug.Log("Unknown message id: $messageId")
            null
        } catch (e: Exception) {
            Debug.Log("Message decode error: ${e.message}")
            null
        }
    }

    private fun decodePayload(message: SLMessage, buffer: ByteBuffer) {
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        try {
            val decodeMethod = findDeclaredMethod(message, "decode")
            if (decodeMethod != null && decodeMethod.declaringClass != SLMessage::class.java) {
                decodeMethod.isAccessible = true
                decodeMethod.invoke(message, buffer)
                return
            }

            val unpackMethod = findDeclaredMethod(message, "UnpackPayload")
            if (unpackMethod != null && unpackMethod.declaringClass != SLMessage::class.java) {
                unpackMethod.isAccessible = true
                unpackMethod.invoke(message, buffer)
                return
            }

            // Fall back to base implementation.
            message.decode(buffer)
        } finally {
            buffer.order(previousOrder)
        }
    }

    private fun findDeclaredMethod(message: SLMessage, name: String): Method? {
        return try {
            message::class.java.getDeclaredMethod(name, ByteBuffer::class.java)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    private fun readMessageId(buffer: ByteBuffer): Pair<Int, MessageFrequency> {
        val first = buffer.get().toInt()
        if (first != -1) {
            return (first and 0xFF) to MessageFrequency.HIGH
        }

        val second = buffer.get().toInt()
        if (second != -1) {
            val id = (second and 0xFF) or 0xFF00
            return id to MessageFrequency.MEDIUM
        }

        val high = buffer.get().toInt() and 0xFF
        val low = buffer.get().toInt() and 0xFF
        val id = (0xFFFF0000.toInt()) or (high shl 8) or low
        return id to MessageFrequency.LOW
    }
}
