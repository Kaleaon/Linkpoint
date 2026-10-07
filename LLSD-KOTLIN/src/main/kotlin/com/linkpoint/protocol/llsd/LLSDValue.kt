package com.linkpoint.protocol.llsd

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * LLSD (Linden Lab Structured Data) value types.
 * Based on the official Second Life LLSD specification:
 * https://wiki.secondlife.com/wiki/LLSD
 */
sealed class LLSDValue {
    abstract fun toXML(): String
    abstract fun toBinary(): ByteArray

    fun toNotation(includeHeader: Boolean = false): String =
        LLSDNotationFormatter.format(this, includeHeader)

    companion object {
        // Binary format markers
        const val MARKER_UNDEF = '!'
        const val MARKER_TRUE = '1'
        const val MARKER_FALSE = '0'
        const val MARKER_INTEGER = 'i'
        const val MARKER_REAL = 'r'
        const val MARKER_UUID = 'u'
        const val MARKER_STRING = 's'
        const val MARKER_BINARY = 'b'
        const val MARKER_DATE = 'd'
        const val MARKER_URI = 'l'
        const val MARKER_MAP = '{'
        const val MARKER_MAP_END = '}'
        const val MARKER_ARRAY = '['
        const val MARKER_ARRAY_END = ']'
    }
}

object LLSDUndefined : LLSDValue() {
    override fun toXML() = "<undef />"
    override fun toBinary() = byteArrayOf(MARKER_UNDEF.code.toByte())
}

data class LLSDBoolean(val value: Boolean) : LLSDValue() {
    override fun toXML() = if (value) "<boolean>true</boolean>" else "<boolean>false</boolean>"
    override fun toBinary() = byteArrayOf(
        if (value) MARKER_TRUE.code.toByte() else MARKER_FALSE.code.toByte()
    )
}

data class LLSDInteger(val value: Int) : LLSDValue() {
    override fun toXML() = "<integer>$value</integer>"
    override fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_INTEGER.code.toByte())
        buffer.putInt(value)
        return buffer.array()
    }
}

data class LLSDReal(val value: Double) : LLSDValue() {
    override fun toXML() = "<real>$value</real>"
    override fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(9).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_REAL.code.toByte())
        buffer.putDouble(value)
        return buffer.array()
    }
}

data class LLSDUUID(val value: UUID) : LLSDValue() {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))

    override fun toXML() = "<uuid>$value</uuid>"
    override fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(17).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_UUID.code.toByte())
        buffer.putLong(value.mostSignificantBits)
        buffer.putLong(value.leastSignificantBits)
        return buffer.array()
    }

    companion object {
        val ZERO = LLSDUUID(UUID(0, 0))
    }
}

data class LLSDString(val value: String) : LLSDValue() {
    override fun toXML(): String {
        val escaped = value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        return "<string>$escaped</string>"
    }

    override fun toBinary(): ByteArray {
        val bytes = value.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(5 + bytes.size).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_STRING.code.toByte())
        buffer.putInt(bytes.size)
        buffer.put(bytes)
        return buffer.array()
    }
}

data class LLSDBinary(val value: ByteArray) : LLSDValue() {
    override fun toXML(): String {
        val base64 = Base64.getEncoder().encodeToString(value)
        return "<binary>$base64</binary>"
    }

    override fun toBinary(): ByteArray {
        val buffer = ByteBuffer.allocate(5 + value.size).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_BINARY.code.toByte())
        buffer.putInt(value.size)
        buffer.put(value)
        return buffer.array()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLSDBinary) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode() = value.contentHashCode()
}

data class LLSDDate(val value: Date) : LLSDValue() {
    constructor(timestamp: Long) : this(Date(timestamp))

    override fun toXML(): String {
        val iso8601 = ISO_8601_FORMATTER.format(value.toInstant())
        return "<date>$iso8601</date>"
    }

    override fun toBinary(): ByteArray {
        val seconds = value.time / 1000.0
        // Date payload is LITTLE-endian per the python-llsd v1.2.4
        // reference implementation (`struct.pack("<d", seconds)`).
        // This is the lone exception in LLSD binary — every other
        // numeric (Integer, Real, length prefixes, UUIDs) is
        // network-byte-order. Don't "fix" it to BE: the reference
        // viewer + python-llsd + libremetaverse all emit LE for dates,
        // and so do every cross-implementation conformance fixture.
        val buffer = ByteBuffer.allocate(9)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_DATE.code.toByte())
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.putDouble(seconds)
        return buffer.array()
    }

    companion object {
        private val ISO_8601_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
    }
}

data class LLSDURI(val value: String) : LLSDValue() {
    override fun toXML() = "<uri>$value</uri>"

    override fun toBinary(): ByteArray {
        val bytes = value.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(5 + bytes.size).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_URI.code.toByte())
        buffer.putInt(bytes.size)
        buffer.put(bytes)
        return buffer.array()
    }
}

data class LLSDMap(val value: MutableMap<String, LLSDValue> = mutableMapOf()) : LLSDValue() {
    operator fun get(key: String): LLSDValue? = value[key]
    operator fun set(key: String, llsdValue: LLSDValue) { value[key] = llsdValue }

    fun getString(key: String): String? = when (val entry = value[key]) {
        is LLSDString -> entry.value
        is LLSDURI -> entry.value
        else -> null
    }
    fun getStringOrEmpty(key: String): String = getString(key) ?: ""
    fun getIntOrZero(key: String): Int = getInt(key) ?: 0
    fun getRealOrZero(key: String): Double = getReal(key) ?: 0.0
    fun getBooleanOrFalse(key: String): Boolean = getBoolean(key) ?: false
    fun getUUIDString(key: String): String? = getUUID(key)?.toString()
    fun getInt(key: String): Int? = (value[key] as? LLSDInteger)?.value
    fun getLong(key: String): Long? = (value[key] as? LLSDInteger)?.value?.toLong()
    fun getReal(key: String): Double? = (value[key] as? LLSDReal)?.value
    fun getBoolean(key: String): Boolean? = (value[key] as? LLSDBoolean)?.value
    fun getUUID(key: String): UUID? = (value[key] as? LLSDUUID)?.value
    fun getMap(key: String): LLSDMap? = value[key] as? LLSDMap
    fun getArray(key: String): LLSDArray? = value[key] as? LLSDArray
    fun getMapOrEmpty(key: String): LLSDMap = getMap(key) ?: LLSDMap()
    fun getArrayOrEmpty(key: String): LLSDArray = getArray(key) ?: LLSDArray()

    override fun toXML(): String {
        val sb = StringBuilder("<map>")
        for ((k, v) in value) {
            sb.append("<key>$k</key>")
            sb.append(v.toXML())
        }
        sb.append("</map>")
        return sb.toString()
    }

    override fun toBinary(): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(MARKER_MAP.code)
        // LLSD binary spec mandates a 4-byte BE element count after the
        // map/array open marker. Emitting it makes round-trips match the
        // canonical fixture bytes; previously we wrote `{ ... }` without
        // the count, which the parser then mis-interpreted as 4 bytes
        // of payload (each null byte read as MARKER_UNDEF), breaking
        // every map/array round-trip.
        ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value.size).array()
            .also { out.write(it) }

        for ((k, v) in value) {
            val keyBytes = k.toByteArray(Charsets.UTF_8)
            val keyBuffer = ByteBuffer.allocate(5 + keyBytes.size).order(ByteOrder.BIG_ENDIAN)
            keyBuffer.put('k'.code.toByte())
            keyBuffer.putInt(keyBytes.size)
            keyBuffer.put(keyBytes)
            out.write(keyBuffer.array())
            out.write(v.toBinary())
        }

        out.write(MARKER_MAP_END.code)
        return out.toByteArray()
    }
}

data class LLSDArray(val value: MutableList<LLSDValue> = mutableListOf()) : LLSDValue() {
    operator fun get(index: Int): LLSDValue = value[index]
    fun add(llsdValue: LLSDValue) { value.add(llsdValue) }
    val size: Int get() = value.size
    fun asStringList(): List<String> = value.map { it.toString() }

    override fun toXML(): String {
        val sb = StringBuilder("<array>")
        for (v in value) {
            sb.append(v.toXML())
        }
        sb.append("</array>")
        return sb.toString()
    }

    override fun toBinary(): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(MARKER_ARRAY.code)
        // 4-byte BE element count — required by the LLSD binary spec.
        // See LLSDMap.toBinary for the matching map serialisation.
        ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value.size).array()
            .also { out.write(it) }

        for (v in value) {
            out.write(v.toBinary())
        }

        out.write(MARKER_ARRAY_END.code)
        return out.toByteArray()
    }
}

fun LLSDValue.toObject(): Any? = when (this) {
    LLSDUndefined -> ""
    is LLSDBoolean -> value
    is LLSDInteger -> value
    is LLSDReal -> value
    is LLSDUUID -> value
    is LLSDString -> value
    is LLSDBinary -> value
    is LLSDDate -> value
    is LLSDURI -> try { java.net.URI(value) } catch (_: Exception) { value }
    is LLSDMap -> java.util.HashMap(value.mapValues { it.value.toObject() })
    is LLSDArray -> java.util.ArrayList(value.map { it.toObject() })
}

fun LLSDValue.toLlsd(): lindenlab.llsd.LLSD = lindenlab.llsd.LLSD(toObject())

fun LLSDValue.toJSON(): String = when (this) {
    LLSDUndefined -> "null"
    is LLSDBoolean -> if (value) "true" else "false"
    is LLSDInteger -> "$value"
    is LLSDReal -> when {
        value.isNaN() -> "\"NaN\""
        value == Double.POSITIVE_INFINITY -> "\"Infinity\""
        value == Double.NEGATIVE_INFINITY -> "\"-Infinity\""
        else -> "$value"
    }
    is LLSDUUID -> "{\"i\":\"$value\"}"
    is LLSDString -> "\"${escapeJson(value)}\""
    is LLSDBinary -> "{\"b\":\"${Base64.getEncoder().encodeToString(value)}\"}"
    is LLSDDate -> {
        val iso = java.time.format.DateTimeFormatter.ISO_INSTANT.withZone(java.time.ZoneId.of("UTC")).format(value.toInstant())
        "{\"d\":\"$iso\"}"
    }
    is LLSDURI -> "{\"u\":\"${escapeJson(value)}\"}"
    is LLSDMap -> {
        val entries = value.entries.joinToString(",") { (k, v) ->
            "\"${escapeJson(k)}\":${v.toJSON()}"
        }
        "{$entries}"
    }
    is LLSDArray -> {
        val items = value.joinToString(",") { it.toJSON() }
        "[$items]"
    }
}

private fun escapeJson(s: String): String = s
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t")

fun llsdOf(obj: Any?): LLSDValue = when (obj) {
    null -> LLSDUndefined
    is Boolean -> LLSDBoolean(obj)
    is Int -> LLSDInteger(obj)
    is Long -> LLSDInteger(obj.toInt())
    is Float -> LLSDReal(obj.toDouble())
    is Double -> LLSDReal(obj)
    is UUID -> LLSDUUID(obj)
    is String -> LLSDString(obj)
    is ByteArray -> LLSDBinary(obj)
    is Date -> LLSDDate(obj)
    is java.time.Instant -> LLSDDate(Date.from(obj))
    is java.net.URI -> LLSDURI(obj.toString())
    is Map<*, *> -> LLSDMap(obj.mapKeys { it.key.toString() }.mapValues { llsdOf(it.value) }.toMutableMap())
    is List<*> -> LLSDArray(obj.map { llsdOf(it) }.toMutableList())
    is LLSDValue -> obj
    is lindenlab.llsd.LLSD -> llsdOf(obj.content)
    else -> LLSDString(obj.toString())
}
