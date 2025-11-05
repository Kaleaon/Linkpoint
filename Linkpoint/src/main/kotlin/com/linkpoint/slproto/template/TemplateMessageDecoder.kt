package com.linkpoint.slproto.template

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.types.LLVector4
import java.net.Inet4Address
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

internal object TemplateMessageDecoder {

    fun decode(template: TemplateMessage, buffer: ByteBuffer): SLMessage? {
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        return try {
            val blockData = mutableMapOf<String, MutableList<Map<String, Any?>>>()
            for (block in template.blocks) {
                val records = decodeBlock(buffer, block) ?: return null
                blockData[block.name] = records.toMutableList()
            }
            TemplateSLMessage(template, blockData)
        } catch (ex: Exception) {
            Debug.Log("Template decode failed for ${template.name}: ${ex.message}")
            null
        } finally {
            buffer.order(previousOrder)
        }
    }

    private fun decodeBlock(buffer: ByteBuffer, block: TemplateBlock): List<Map<String, Any?>>? {
        val repeatCount = when (block.type) {
            TemplateBlockType.SINGLE -> 1
            TemplateBlockType.MULTIPLE -> block.repeat ?: return null
            TemplateBlockType.VARIABLE -> {
                if (!buffer.hasRemaining()) return emptyList()
                buffer.get().toInt() and 0xFF
            }
        }

        val records = mutableListOf<Map<String, Any?>>()
        repeat(repeatCount) {
            val record = mutableMapOf<String, Any?>()
            for (field in block.fields) {
                val value = decodeField(buffer, field) ?: return null
                record[field.name] = value
            }
            records += record
        }
        return records
    }

    private fun decodeField(buffer: ByteBuffer, field: TemplateField): Any? {
        if (!buffer.hasRemaining()) return null
        return when (field.rawType.uppercase()) {
            "BOOL" -> buffer.get().toInt() != 0
            "U8" -> buffer.get().toInt() and 0xFF
            "U16" -> buffer.short.toInt() and 0xFFFF
            "U32" -> buffer.int.toLong() and 0xFFFFFFFFL
            "U64" -> buffer.long
            "S8" -> buffer.get()
            "S16" -> buffer.short
            "S32" -> buffer.int
            "S64" -> buffer.long
            "F32" -> buffer.float
            "F64" -> buffer.double
            "LLUUID" -> SLMessage.readUUID(buffer)
            "LLVECTOR3" -> LLVector3(buffer.float, buffer.float, buffer.float)
            "LLVECTOR3D" -> LLVector3d(buffer.double, buffer.double, buffer.double)
            "LLVECTOR4" -> LLVector4(buffer.float, buffer.float, buffer.float, buffer.float)
            "LLQUATERNION" -> LLQuaternion(buffer.float, buffer.float, buffer.float, buffer.float)
            "IPADDR" -> decodeIpAddress(buffer)
            "IPPORT" -> buffer.short.toInt() and 0xFFFF
            "VARIABLE" -> decodeVariable(buffer, field.param ?: 1)
            "FIXED" -> decodeFixed(buffer, field.param ?: 0)
            "BINARY" -> decodeFixed(buffer, field.param ?: 0)
            else -> decodeFallback(buffer, field)
        }
    }

    private fun decodeVariable(buffer: ByteBuffer, lengthBytes: Int): Any {
        val length = when (lengthBytes) {
            1 -> buffer.get().toInt() and 0xFF
            2 -> (buffer.get().toInt() and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)
            else -> buffer.get().toInt() and 0xFF
        }
        val data = ByteArray(length)
        buffer.get(data)
        return if (lengthBytes == 1) {
            String(data, StandardCharsets.UTF_8).trimEnd('\u0000')
        } else {
            data
        }
    }

    private fun decodeFixed(buffer: ByteBuffer, size: Int): ByteArray {
        val data = ByteArray(size)
        buffer.get(data)
        return data
    }

    private fun decodeFallback(buffer: ByteBuffer, field: TemplateField): Any? {
        // Default to treating field as unsigned byte with size parameter
        return if (field.param != null && field.param > 0) {
            decodeFixed(buffer, field.param)
        } else {
            buffer.get().toInt() and 0xFF
        }
    }

    private fun decodeIpAddress(buffer: ByteBuffer): String {
        val bytes = ByteArray(4)
        buffer.get(bytes)
        return try {
            Inet4Address.getByAddress(bytes).hostAddress ?: bytes.joinToString(".") { (it.toInt() and 0xFF).toString() }
        } catch (_: UnknownHostException) {
            bytes.joinToString(".") { (it.toInt() and 0xFF).toString() }
        }
    }
}
