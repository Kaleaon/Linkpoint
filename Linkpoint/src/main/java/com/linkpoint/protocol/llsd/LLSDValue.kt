package com.linkpoint.protocol.llsd

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * LLSD (Linden Lab Structured Data) value types
 * Based on the official Second Life LLSD specification
 */
sealed class LLSDValue {
    abstract fun toXML(): String
    abstract fun toBinary(): ByteArray
    
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
        val iso8601 = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(value)
        return "<date>$iso8601</date>"
    }
    
    override fun toBinary(): ByteArray {
        val seconds = value.time / 1000.0
        val buffer = ByteBuffer.allocate(9).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MARKER_DATE.code.toByte())
        buffer.putDouble(seconds)
        return buffer.array()
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
    
    fun getString(key: String): String? = (value[key] as? LLSDString)?.value
    fun getInt(key: String): Int? = (value[key] as? LLSDInteger)?.value
    fun getReal(key: String): Double? = (value[key] as? LLSDReal)?.value
    fun getBoolean(key: String): Boolean? = (value[key] as? LLSDBoolean)?.value
    fun getUUID(key: String): UUID? = (value[key] as? LLSDUUID)?.value
    fun getMap(key: String): LLSDMap? = value[key] as? LLSDMap
    fun getArray(key: String): LLSDArray? = value[key] as? LLSDArray
    
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
        val parts = mutableListOf<ByteArray>()
        parts.add(byteArrayOf(MARKER_MAP.code.toByte()))
        
        for ((k, v) in value) {
            // Key as 'k' + length + bytes
            val keyBytes = k.toByteArray(Charsets.UTF_8)
            val keyBuffer = ByteBuffer.allocate(5 + keyBytes.size).order(ByteOrder.BIG_ENDIAN)
            keyBuffer.put('k'.code.toByte())
            keyBuffer.putInt(keyBytes.size)
            keyBuffer.put(keyBytes)
            parts.add(keyBuffer.array())
            
            // Value
            parts.add(v.toBinary())
        }
        
        parts.add(byteArrayOf(MARKER_MAP_END.code.toByte()))
        
        return parts.fold(byteArrayOf()) { acc, bytes -> acc + bytes }
    }
}

data class LLSDArray(val value: MutableList<LLSDValue> = mutableListOf()) : LLSDValue() {
    operator fun get(index: Int): LLSDValue = value[index]
    fun add(llsdValue: LLSDValue) { value.add(llsdValue) }
    val size: Int get() = value.size
    
    override fun toXML(): String {
        val sb = StringBuilder("<array>")
        for (v in value) {
            sb.append(v.toXML())
        }
        sb.append("</array>")
        return sb.toString()
    }
    
    override fun toBinary(): ByteArray {
        val parts = mutableListOf<ByteArray>()
        parts.add(byteArrayOf(MARKER_ARRAY.code.toByte()))
        
        for (v in value) {
            parts.add(v.toBinary())
        }
        
        parts.add(byteArrayOf(MARKER_ARRAY_END.code.toByte()))
        
        return parts.fold(byteArrayOf()) { acc, bytes -> acc + bytes }
    }
}
