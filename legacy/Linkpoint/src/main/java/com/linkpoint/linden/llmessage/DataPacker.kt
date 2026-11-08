package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector3
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class DataPacker {
    var passFlags: UInt = 0u
    protected var writeEnabled: Boolean = false

    abstract fun hasNext(): Boolean

    abstract fun packString(value: String, name: String): Boolean
    abstract fun unpackString(name: String): String?

    abstract fun packBinaryData(value: ByteArray, name: String): Boolean
    abstract fun unpackBinaryData(name: String): ByteArray?

    abstract fun packBinaryDataFixed(value: ByteArray, size: Int, name: String): Boolean
    abstract fun unpackBinaryDataFixed(size: Int, name: String): ByteArray?

    abstract fun packU8(value: UByte, name: String): Boolean
    abstract fun unpackU8(name: String): UByte?

    abstract fun packU16(value: UShort, name: String): Boolean
    abstract fun unpackU16(name: String): UShort?

    abstract fun packS16(value: Short, name: String): Boolean
    abstract fun unpackS16(name: String): Short?

    abstract fun packU32(value: UInt, name: String): Boolean
    abstract fun unpackU32(name: String): UInt?

    abstract fun packS32(value: Int, name: String): Boolean
    abstract fun unpackS32(name: String): Int?

    abstract fun packF32(value: Float, name: String): Boolean
    abstract fun unpackF32(name: String): Float?

    abstract fun packColor4(r: Float, g: Float, b: Float, a: Float, name: String): Boolean
    abstract fun unpackColor4(name: String): FloatArray?

    abstract fun packVector3(value: Vector3, name: String): Boolean
    abstract fun unpackVector3(name: String): Vector3?

    abstract fun packUUID(value: LLUUID, name: String): Boolean
    abstract fun unpackUUID(name: String): LLUUID?

    fun packFixed(value: Float, name: String, isSigned: Boolean, intBits: UInt, fracBits: UInt): Boolean {
        val unsignedBits = (intBits + fracBits).toInt()
        val totalBits = if (isSigned) unsignedBits + 1 else unsignedBits
        val maxVal = (1u shl intBits.toInt())
        val minVal = if (isSigned) -(maxVal.toInt()) else 0
        val clamped = value.coerceIn(minVal.toFloat(), maxVal.toFloat())
        var fixedVal = if (isSigned) clamped + maxVal.toFloat() else clamped
        fixedVal *= (1 shl fracBits.toInt()).toFloat()
        return when {
            totalBits <= 8 -> packU8(fixedVal.toUInt().toUByte(), name)
            totalBits <= 16 -> packU16(fixedVal.toUInt().toUShort(), name)
            totalBits <= 31 -> packU32(fixedVal.toUInt(), name)
            else -> false
        }
    }
}

class DataPackerBinaryBuffer(buffer: ByteArray) : DataPacker() {
    private val buf: ByteArray = buffer
    private var pos: Int = 0

    init { writeEnabled = true }

    val currentSize: Int get() = pos
    val bufferSize: Int get() = buf.size

    override fun hasNext(): Boolean = pos < buf.size

    fun reset() { pos = 0; writeEnabled = buf.isNotEmpty() }

    private fun verifyLength(needed: Int): Boolean {
        if (writeEnabled && pos > buf.size - needed) return false
        return true
    }

    private fun putBytes(bytes: ByteArray): Boolean {
        if (!verifyLength(bytes.size)) return false
        bytes.copyInto(buf, pos)
        pos += bytes.size
        return true
    }

    private fun getBytes(count: Int): ByteArray? {
        if (pos + count > buf.size) return null
        val out = buf.copyOfRange(pos, pos + count)
        pos += count
        return out
    }

    override fun packU8(value: UByte, name: String): Boolean = putBytes(byteArrayOf(value.toByte()))
    override fun unpackU8(name: String): UByte? = getBytes(1)?.get(0)?.toUByte()

    override fun packU16(value: UShort, name: String): Boolean {
        val bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value.toShort())
        return putBytes(bb.array())
    }
    override fun unpackU16(name: String): UShort? =
        getBytes(2)?.let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN).short.toUShort() }

    override fun packS16(value: Short, name: String): Boolean {
        val bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value)
        return putBytes(bb.array())
    }
    override fun unpackS16(name: String): Short? =
        getBytes(2)?.let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN).short }

    override fun packU32(value: UInt, name: String): Boolean {
        val bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value.toInt())
        return putBytes(bb.array())
    }
    override fun unpackU32(name: String): UInt? =
        getBytes(4)?.let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN).int.toUInt() }

    override fun packS32(value: Int, name: String): Boolean {
        val bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
        return putBytes(bb.array())
    }
    override fun unpackS32(name: String): Int? =
        getBytes(4)?.let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN).int }

    override fun packF32(value: Float, name: String): Boolean {
        val bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value)
        return putBytes(bb.array())
    }
    override fun unpackF32(name: String): Float? =
        getBytes(4)?.let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN).float }

    override fun packString(value: String, name: String): Boolean {
        val bytes = value.toByteArray(Charsets.UTF_8)
        if (!verifyLength(bytes.size + 1)) return false
        bytes.copyInto(buf, pos)
        pos += bytes.size
        buf[pos++] = 0
        return true
    }
    override fun unpackString(name: String): String? {
        val end = (pos until buf.size).firstOrNull { buf[it] == 0.toByte() } ?: -1
        if (end == -1 || end > buf.size) return null
        val s = String(buf, pos, end - pos, Charsets.UTF_8)
        pos = end + 1
        return s
    }

    override fun packBinaryData(value: ByteArray, name: String): Boolean {
        if (!verifyLength(2 + value.size)) return false
        packU16(value.size.toUShort(), name)
        return putBytes(value)
    }
    override fun unpackBinaryData(name: String): ByteArray? {
        val size = unpackU16(name)?.toInt() ?: return null
        return getBytes(size)
    }

    override fun packBinaryDataFixed(value: ByteArray, size: Int, name: String): Boolean {
        if (!verifyLength(size)) return false
        val src = if (value.size >= size) value.copyOf(size) else value + ByteArray(size - value.size)
        return putBytes(src)
    }
    override fun unpackBinaryDataFixed(size: Int, name: String): ByteArray? = getBytes(size)

    override fun packColor4(r: Float, g: Float, b: Float, a: Float, name: String): Boolean =
        packF32(r, name) && packF32(g, name) && packF32(b, name) && packF32(a, name)
    override fun unpackColor4(name: String): FloatArray? {
        val r = unpackF32(name) ?: return null
        val g = unpackF32(name) ?: return null
        val b = unpackF32(name) ?: return null
        val a = unpackF32(name) ?: return null
        return floatArrayOf(r, g, b, a)
    }

    override fun packVector3(value: Vector3, name: String): Boolean =
        packF32(value.x, name) && packF32(value.y, name) && packF32(value.z, name)
    override fun unpackVector3(name: String): Vector3? {
        val x = unpackF32(name) ?: return null
        val y = unpackF32(name) ?: return null
        val z = unpackF32(name) ?: return null
        return Vector3(x, y, z)
    }

    override fun packUUID(value: LLUUID, name: String): Boolean {
        val bytes = value.toBytes()
        return putBytes(bytes)
    }
    override fun unpackUUID(name: String): LLUUID? =
        getBytes(16)?.let { LLUUID.fromBytes(it) }
}

class DataPackerAsciiBuffer(buffer: CharArray) : DataPacker() {
    private val buf: CharArray = buffer
    private var pos: Int = 0
    var includeNames: Boolean = false

    init { writeEnabled = buffer.isNotEmpty() }

    val currentSize: Int get() = pos + 1
    val bufferSize: Int get() = buf.size

    override fun hasNext(): Boolean = currentSize < bufferSize

    fun reset() { pos = 0; writeEnabled = buf.isNotEmpty() }

    private fun writeToken(name: String, token: String): Boolean {
        val full = if (includeNames) "\t$name\t$token\n" else "$token\n"
        if (pos + full.length > buf.size) return false
        full.toCharArray().copyInto(buf, pos)
        pos += full.length
        return true
    }

    private fun readToken(name: String): String? {
        val str = String(buf, pos, buf.size - pos)
        val line = str.lines().firstOrNull() ?: return null
        pos += line.length + 1
        return if (includeNames) {
            val parts = line.split('\t')
            if (parts.size >= 3) parts[2] else null
        } else {
            line.trim()
        }
    }

    private fun parseSizedHexPayload(payload: String, expectedSize: Int? = null): ByteArray? {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) {
            return if (expectedSize == null || expectedSize == 0) ByteArray(0) else null
        }
        if (trimmed.length % 2 != 0) return null
        val actualSize = trimmed.length / 2
        if (expectedSize != null && actualSize < expectedSize) return null
        val targetSize = expectedSize ?: actualSize
        return ByteArray(targetSize) { index ->
            val off = index * 2
            trimmed.substring(off, off + 2).toIntOrNull(16)?.toByte() ?: return null
        }
    }

    override fun packU8(value: UByte, name: String) = writeToken(name, value.toInt().toString())
    override fun unpackU8(name: String) = readToken(name)?.toUByteOrNull()

    override fun packU16(value: UShort, name: String) = writeToken(name, value.toInt().toString())
    override fun unpackU16(name: String) = readToken(name)?.toUShortOrNull()

    override fun packS16(value: Short, name: String) = writeToken(name, value.toString())
    override fun unpackS16(name: String) = readToken(name)?.toShortOrNull()

    override fun packU32(value: UInt, name: String) = writeToken(name, value.toLong().toString())
    override fun unpackU32(name: String) = readToken(name)?.toUIntOrNull()

    override fun packS32(value: Int, name: String) = writeToken(name, value.toString())
    override fun unpackS32(name: String) = readToken(name)?.toIntOrNull()

    override fun packF32(value: Float, name: String) = writeToken(name, value.toString())
    override fun unpackF32(name: String) = readToken(name)?.toFloatOrNull()

    override fun packString(value: String, name: String) = writeToken(name, value)
    override fun unpackString(name: String) = readToken(name)

    override fun packBinaryData(value: ByteArray, name: String): Boolean {
        val hex = value.joinToString("") { "%02x".format(it) }
        return writeToken(name, "${value.size} $hex")
    }
    override fun unpackBinaryData(name: String): ByteArray? {
        val token = readToken(name) ?: return null
        val parts = token.split(' ', limit = 2)
        val size = parts[0].toIntOrNull() ?: return null
        if (parts.size < 2) return ByteArray(0)
        return parseSizedHexPayload(parts[1], size)
    }

    override fun packBinaryDataFixed(value: ByteArray, size: Int, name: String): Boolean {
        val hex = value.take(size).joinToString("") { "%02x".format(it) }
        return writeToken(name, hex)
    }
    override fun unpackBinaryDataFixed(size: Int, name: String): ByteArray? {
        val hex = readToken(name) ?: return null
        return parseSizedHexPayload(hex, size)
    }

    override fun packColor4(r: Float, g: Float, b: Float, a: Float, name: String) =
        writeToken(name, "$r $g $b $a")
    override fun unpackColor4(name: String): FloatArray? {
        val parts = readToken(name)?.split(' ') ?: return null
        if (parts.size < 4) return null
        return floatArrayOf(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat())
    }

    override fun packVector3(value: Vector3, name: String) =
        writeToken(name, "${value.x} ${value.y} ${value.z}")
    override fun unpackVector3(name: String): Vector3? {
        val parts = readToken(name)?.split(' ') ?: return null
        if (parts.size < 3) return null
        return Vector3(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat())
    }

    override fun packUUID(value: LLUUID, name: String) = writeToken(name, value.toString())
    override fun unpackUUID(name: String): LLUUID? =
        readToken(name)?.let { LLUUID.fromString(it) }
}

class DataPackerAsciiFile(private val stream: OutputStream, private val indent: Int = 2) : DataPacker() {
    private val writer = PrintWriter(stream)
    private val fields: MutableList<Pair<String, String>> = mutableListOf()
    private var readIndex: Int = 0

    init { writeEnabled = true }

    override fun hasNext(): Boolean = readIndex < fields.size

    private fun writeField(name: String, value: String) {
        fields.add(name to value)
        writer.println("${" ".repeat(indent)}$name\t$value")
        writer.flush()
    }

    private fun readField(name: String): String? {
        if (readIndex >= fields.size) return null
        val (fieldName, value) = fields[readIndex]
        if (name.isNotEmpty() && fieldName != name) return null
        readIndex++
        return value
    }

    private fun parseHexBytes(hex: String): ByteArray? {
        val cleanHex = hex.trim()
        if (cleanHex.length % 2 != 0) return null
        return try {
            ByteArray(cleanHex.length / 2) { idx ->
                cleanHex.substring(idx * 2, idx * 2 + 2).toInt(16).toByte()
            }
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun parseSizedHexPayload(payload: String, expectedSize: Int? = null): ByteArray? {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) {
            return if (expectedSize == null || expectedSize == 0) ByteArray(0) else null
        }
        if (trimmed.length % 2 != 0) return null
        val actualSize = trimmed.length / 2
        if (expectedSize != null && actualSize < expectedSize) return null
        val targetSize = expectedSize ?: actualSize
        return ByteArray(targetSize) { i ->
            val off = i * 2
            trimmed.substring(off, off + 2).toIntOrNull(16)?.toByte() ?: return null
        }
    }

    override fun packU8(value: UByte, name: String): Boolean { writeField(name, value.toInt().toString()); return true }
    override fun unpackU8(name: String): UByte? = readField(name)?.toUByteOrNull()

    override fun packU16(value: UShort, name: String): Boolean { writeField(name, value.toInt().toString()); return true }
    override fun unpackU16(name: String): UShort? = readField(name)?.toUShortOrNull()

    override fun packS16(value: Short, name: String): Boolean { writeField(name, value.toString()); return true }
    override fun unpackS16(name: String): Short? = readField(name)?.toShortOrNull()

    override fun packU32(value: UInt, name: String): Boolean { writeField(name, value.toLong().toString()); return true }
    override fun unpackU32(name: String): UInt? = readField(name)?.toUIntOrNull()

    override fun packS32(value: Int, name: String): Boolean { writeField(name, value.toString()); return true }
    override fun unpackS32(name: String): Int? = readField(name)?.toIntOrNull()

    override fun packF32(value: Float, name: String): Boolean { writeField(name, value.toString()); return true }
    override fun unpackF32(name: String): Float? = readField(name)?.toFloatOrNull()

    override fun packString(value: String, name: String): Boolean { writeField(name, value); return true }
    override fun unpackString(name: String): String? = readField(name)

    override fun packBinaryData(value: ByteArray, name: String): Boolean {
        writeField(name, "${value.size} ${value.joinToString("") { "%02x".format(it) }}")
        return true
    }
    override fun unpackBinaryData(name: String): ByteArray? {
        val token = readField(name) ?: return null
        val parts = token.split(' ', limit = 2)
        val expectedSize = parts.firstOrNull()?.toIntOrNull() ?: return null
        if (parts.size < 2) return if (expectedSize == 0) ByteArray(0) else null
        val bytes = parseHexBytes(parts[1]) ?: return null
        return if (bytes.size == expectedSize) bytes else null
    }

    override fun packBinaryDataFixed(value: ByteArray, size: Int, name: String): Boolean {
        val fixed = if (value.size >= size) {
            value.copyOf(size)
        } else {
            value + ByteArray(size - value.size)
        }
        writeField(name, fixed.joinToString("") { "%02x".format(it) })
        return true
    }
    override fun unpackBinaryDataFixed(size: Int, name: String): ByteArray? {
        val token = readField(name) ?: return null
        val bytes = parseHexBytes(token) ?: return null
        return if (bytes.size == size) bytes else null
    }

    override fun packColor4(r: Float, g: Float, b: Float, a: Float, name: String): Boolean {
        writeField(name, "$r $g $b $a"); return true
    }
    override fun unpackColor4(name: String): FloatArray? {
        val parts = readField(name)?.trim()?.split(Regex("\\s+")) ?: return null
        if (parts.size < 4) return null
        val r = parts[0].toFloatOrNull() ?: return null
        val g = parts[1].toFloatOrNull() ?: return null
        val b = parts[2].toFloatOrNull() ?: return null
        val a = parts[3].toFloatOrNull() ?: return null
        return floatArrayOf(r, g, b, a)
    }

    override fun packVector3(value: Vector3, name: String): Boolean {
        writeField(name, "${value.x} ${value.y} ${value.z}"); return true
    }
    override fun unpackVector3(name: String): Vector3? {
        val parts = readField(name)?.trim()?.split(Regex("\\s+")) ?: return null
        if (parts.size < 3) return null
        val x = parts[0].toFloatOrNull() ?: return null
        val y = parts[1].toFloatOrNull() ?: return null
        val z = parts[2].toFloatOrNull() ?: return null
        return Vector3(x, y, z)
    }

    override fun packUUID(value: LLUUID, name: String): Boolean { writeField(name, value.toString()); return true }
    override fun unpackUUID(name: String): LLUUID? = readField(name)?.let { LLUUID.fromString(it) }
}
