package com.linkpoint.slproto.llsd

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Linden Lab Structured Data (LLSD)
 * Base class for all LLSD types
 */
sealed class LLSD {
    /**
     * Convert to JSON representation
     */
    abstract fun toJson(): Any

    /**
     * Convert to XML representation
     */
    abstract fun toXml(): String

    companion object {
        /**
         * Parse LLSD from JSON
         */
        fun fromJson(json: Any): LLSD {
            return when (json) {
                is JSONObject -> {
                    val map = mutableMapOf<String, LLSD>()
                    for (key in json.keys()) {
                        map[key] = fromJson(json.get(key))
                    }
                    LLSDMap(map)
                }
                is JSONArray -> {
                    val list = mutableListOf<LLSD>()
                    for (i in 0 until json.length()) {
                        list.add(fromJson(json.get(i)))
                    }
                    LLSDArray(list)
                }
                is String -> LLSDString(json)
                is Int -> LLSDInteger(json)
                is Long -> LLSDInteger(json.toInt())
                is Double -> LLSDReal(json)
                is Float -> LLSDReal(json.toDouble())
                is Boolean -> LLSDBoolean(json)
                JSONObject.NULL -> LLSDUndefined
                else -> LLSDString(json.toString())
            }
        }

        /**
         * Parse LLSD from XML
         */
        fun fromXml(xml: String): LLSD {
            return try {
                val xmlParser = LLSDXmlParser()
                xmlParser.parse(xml)
            } catch (e: Exception) {
                e.printStackTrace()
                LLSDUndefined
            }
        }
    }
}

/**
 * LLSD Undefined type
 */
object LLSDUndefined : LLSD() {
    override fun toJson(): Any = JSONObject.NULL

    override fun toXml(): String = "<undef />"
}

/**
 * LLSD Boolean type
 */
data class LLSDBoolean(val value: Boolean) : LLSD() {
    override fun toJson(): Any = value

    override fun toXml(): String = "<boolean>${if (value) "true" else "false"}</boolean>"
}

/**
 * LLSD Integer type
 */
data class LLSDInteger(val value: Int) : LLSD() {
    override fun toJson(): Any = value

    override fun toXml(): String = "<integer>$value</integer>"
}

/**
 * LLSD Real (floating point) type
 */
data class LLSDReal(val value: Double) : LLSD() {
    override fun toJson(): Any = value

    override fun toXml(): String = "<real>$value</real>"
}

/**
 * LLSD String type
 */
data class LLSDString(val value: String) : LLSD() {
    override fun toJson(): Any = value

    override fun toXml(): String = "<string>$value</string>"
}

/**
 * LLSD UUID type
 */
data class LLSDUUID(val value: UUID) : LLSD() {
    override fun toJson(): Any = value.toString()

    override fun toXml(): String = "<uuid>$value</uuid>"
}

/**
 * LLSD Date type
 */
data class LLSDDate(val value: Date) : LLSD() {
    override fun toJson(): Any = value.time

    override fun toXml(): String = "<date>${value.time}</date>"
}

/**
 * LLSD URI type
 */
data class LLSDURI(val value: String) : LLSD() {
    override fun toJson(): Any = value

    override fun toXml(): String = "<uri>$value</uri>"
}

/**
 * LLSD Binary type
 */
data class LLSDBinary(val value: ByteArray) : LLSD() {
    override fun toJson(): Any = Base64.getEncoder().encodeToString(value)

    override fun toXml(): String = "<binary>${Base64.getEncoder().encodeToString(value)}</binary>"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLSDBinary) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int = value.contentHashCode()
}

/**
 * LLSD Array type
 */
data class LLSDArray(val value: MutableList<LLSD> = mutableListOf()) : LLSD() {
    override fun toJson(): Any {
        val array = JSONArray()
        for (item in value) {
            array.put(item.toJson())
        }
        return array
    }

    override fun toXml(): String {
        val sb = StringBuilder("<array>")
        for (item in value) {
            sb.append(item.toXml())
        }
        sb.append("</array>")
        return sb.toString()
    }

    operator fun get(index: Int): LLSD = value.getOrNull(index) ?: LLSDUndefined

    operator fun set(
        index: Int
        element: LLSD
    ) {
        if (index >= value.size) {
            // Expand array
            while (value.size <= index) {
                value.add(LLSDUndefined)
            }
        }
        value[index] = element
    }

    fun add(element: LLSD) = value.add(element)

    fun size(): Int = value.size
}

/**
 * LLSD Map type
 */
data class LLSDMap(val value: MutableMap<String, LLSD> = mutableMapOf()) : LLSD() {
    override fun toJson(): Any {
        val obj = JSONObject()
        for ((key, llsdValue) in value) {
            obj.put(key, llsdValue.toJson())
        }
        return obj
    }

    override fun toXml(): String {
        val sb = StringBuilder("<map>")
        for ((key, llsdValue) in value) {
            sb.append("<key>$key</key>")
            sb.append(llsdValue.toXml())
        }
        sb.append("</map>")
        return sb.toString()
    }

    operator fun get(key: String): LLSD = value[key] ?: LLSDUndefined

    operator fun set(
        key: String
        llsdValue: LLSD
    ) {
        value[key] = llsdValue
    }

    fun containsKey(key: String): Boolean = value.containsKey(key)

    fun keys(): Set<String> = value.keys
}

/**
 * LLSD helper functions
 */
object LLSDHelper {
    /**
     * Create LLSD from Kotlin value
     */
    fun from(value: Any?): LLSD {
        return when (value) {
            null -> LLSDUndefined
            is Boolean -> LLSDBoolean(value)
            is Int -> LLSDInteger(value)
            is Long -> LLSDInteger(value.toInt())
            is Float -> LLSDReal(value.toDouble())
            is Double -> LLSDReal(value)
            is String -> LLSDString(value)
            is UUID -> LLSDUUID(value)
            is Date -> LLSDDate(value)
            is ByteArray -> LLSDBinary(value)
            is List<*> -> {
                val array = LLSDArray()
                for (item in value) {
                    array.add(from(item))
                }
                array
            }
            is Map<*, *> -> {
                val map = LLSDMap()
                for ((k, v) in value) {
                    if (k is String) {
                        map[k] = from(v)
                    }
                }
                map
            }
            else -> LLSDString(value.toString())
        }
    }

    /**
     * Convert LLSD to Kotlin value
     */
    fun toKotlin(llsd: LLSD): Any? {
        return when (llsd) {
            is LLSDUndefined -> null
            is LLSDBoolean -> llsd.value
            is LLSDInteger -> llsd.value
            is LLSDReal -> llsd.value
            is LLSDString -> llsd.value
            is LLSDUUID -> llsd.value
            is LLSDDate -> llsd.value
            is LLSDURI -> llsd.value
            is LLSDBinary -> llsd.value
            is LLSDArray -> llsd.value.map { toKotlin(it) }
            is LLSDMap -> llsd.value.mapValues { toKotlin(it.value) }
        }
    }
}
