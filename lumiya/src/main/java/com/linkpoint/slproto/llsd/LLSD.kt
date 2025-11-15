package com.linkpoint.slproto.llsd

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.LinkedHashMap
import java.util.UUID

/**
 * Modern LLSD model (Libremetaverse compatible).
 *
 * Provides strongly typed nodes that can convert between JSON, XML, and Kotlin primitives.
 */
sealed interface LLSD {
    fun toJson(): Any?
    fun toXml(): String

    companion object {
        private val ISO_FORMATTERS =
            listOf(
                DateTimeFormatter.ISO_INSTANT,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            )

        fun fromJson(json: Any?): LLSD =
            when (json) {
                null, JSONObject.NULL -> LLSDUndefined
                is JSONObject -> {
                    val map = LinkedHashMap<String, LLSD>()
                    for (key in json.keys()) {
                        map[key] = fromJson(json.get(key))
                    }
                    LLSDMap(map)
                }
                is JSONArray -> {
                    val list = mutableListOf<LLSD>()
                    for (i in 0 until json.length()) {
                        list += fromJson(json.get(i))
                    }
                    LLSDArray(list)
                }
                is Boolean -> LLSDBoolean(json)
                is Int -> LLSDInteger(json.toLong())
                is Long -> LLSDInteger(json)
                is Number -> LLSDReal(json.toDouble())
                is UUID -> LLSDUUID(json)
                is Date -> LLSDDate(json)
                is String -> {
                    val trimmed = json.trim()
                    if (trimmed.equals("true", ignoreCase = true) || trimmed.equals("false", ignoreCase = true)) {
                        LLSDBoolean(trimmed.equals("true", ignoreCase = true))
                    } else {
                        LLSDString(json)
                    }
                }
                is ByteArray -> LLSDBinary(json)
                else -> LLSDString(json.toString())
            }

        fun fromXml(xml: String): LLSD = LLSDXmlParser().parse(xml)

        internal fun parseDate(value: String): Date {
            val trimmed = value.trim()
            if (trimmed.isEmpty()) return Date(0)

            ISO_FORMATTERS.forEach { formatter ->
                try {
                    val temporal = formatter.parse(trimmed)
                    val instant =
                        when (temporal) {
                            is Instant -> temporal
                            else -> OffsetDateTime.from(temporal).toInstant()
                        }
                    return Date.from(instant)
                } catch (_: Exception) {
                }
            }

            trimmed.toDoubleOrNull()?.let { seconds ->
                return Date((seconds * 1000).toLong())
            }

            return Date(0)
        }
    }
}

object LLSDUndefined : LLSD {
    override fun toJson(): Any? = JSONObject.NULL
    override fun toXml(): String = "<undef/>"
}

data class LLSDBoolean(val value: Boolean) : LLSD {
    override fun toJson(): Any = value
    override fun toXml(): String = "<boolean>${if (value) "true" else "false"}</boolean>"
}

data class LLSDInteger(val value: Long) : LLSD {
    constructor(value: Int) : this(value.toLong())

    override fun toJson(): Any = value
    override fun toXml(): String = "<integer>$value</integer>"
}

data class LLSDReal(val value: Double) : LLSD {
    constructor(value: Float) : this(value.toDouble())

    override fun toJson(): Any = value
    override fun toXml(): String = "<real>$value</real>"
}

data class LLSDString(val value: String) : LLSD {
    override fun toJson(): Any = value
    override fun toXml(): String = "<string>${escapeXml(value)}</string>"
}

data class LLSDUUID(val value: UUID) : LLSD {
    override fun toJson(): Any = value.toString()
    override fun toXml(): String = "<uuid>${value}</uuid>"
}

data class LLSDURI(val value: URI) : LLSD {
    constructor(value: String) : this(URI.create(value))

    override fun toJson(): Any = value.toString()
    override fun toXml(): String = "<uri>${escapeXml(value.toString())}</uri>"
}

data class LLSDDate(val value: Date) : LLSD {
    override fun toJson(): Any = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(value.time))
    override fun toXml(): String = "<date>${DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(value.time))}</date>"
}

data class LLSDBinary(val value: ByteArray) : LLSD {
    override fun toJson(): Any = Base64.encodeToString(value, Base64.NO_WRAP)
    override fun toXml(): String = "<binary>${Base64.encodeToString(value, Base64.NO_WRAP)}</binary>"

    override fun equals(other: Any?): Boolean =
        other is LLSDBinary && value.contentEquals(other.value)

    override fun hashCode(): Int = value.contentHashCode()
}

data class LLSDArray(val value: MutableList<LLSD> = mutableListOf()) : LLSD {
    constructor(elements: Collection<LLSD>) : this(elements.toMutableList())

    override fun toJson(): Any {
        val array = JSONArray()
        value.forEach { array.put(it.toJson()) }
        return array
    }

    override fun toXml(): String = buildString {
        append("<array>")
        value.forEach { append(it.toXml()) }
        append("</array>")
    }

    operator fun get(index: Int): LLSD = value.getOrNull(index) ?: LLSDUndefined

    fun add(element: LLSD) {
        value += element
    }
}

data class LLSDMap(val value: LinkedHashMap<String, LLSD> = LinkedHashMap()) : LLSD {
    constructor(initial: Map<String, LLSD>) : this(LinkedHashMap(initial))

    override fun toJson(): Any {
        val obj = JSONObject()
        value.forEach { (key, node) -> obj.put(key, node.toJson()) }
        return obj
    }

    override fun toXml(): String = buildString {
        append("<map>")
        value.forEach { (key, node) ->
            append("<key>${escapeXml(key)}</key>")
            append(node.toXml())
        }
        append("</map>")
    }

    operator fun get(key: String): LLSD = value[key] ?: LLSDUndefined
    operator fun set(key: String, node: LLSD) {
        value[key] = node
    }

    fun put(key: String, node: LLSD) = set(key, node)
}

object LLSDHelper {
    fun from(value: Any?): LLSD =
        when (value) {
            null -> LLSDUndefined
            is LLSD -> value
            is Boolean -> LLSDBoolean(value)
            is Int -> LLSDInteger(value.toLong())
            is Long -> LLSDInteger(value)
            is Float -> LLSDReal(value.toDouble())
            is Double -> LLSDReal(value)
            is UUID -> LLSDUUID(value)
            is URI -> LLSDURI(value)
            is Date -> LLSDDate(value)
            is ByteArray -> LLSDBinary(value.copyOf())
            is String -> LLSDString(value)
            is List<*> -> {
                val list = LLSDArray()
                value.forEach { list.add(from(it)) }
                list
            }
            is Map<*, *> -> {
                val map = LLSDMap()
                value.forEach { (key, entryValue) ->
                    if (key is String) {
                        map[key] = from(entryValue)
                    }
                }
                map
            }
            else -> LLSDString(value.toString())
        }

    fun toKotlin(node: LLSD): Any? =
        when (node) {
            is LLSDUndefined -> null
            is LLSDBoolean -> node.value
            is LLSDInteger -> node.value
            is LLSDReal -> node.value
            is LLSDString -> node.value
            is LLSDUUID -> node.value
            is LLSDURI -> node.value
            is LLSDDate -> node.value
            is LLSDBinary -> node.value
            is LLSDArray -> node.value.map { toKotlin(it) }
            is LLSDMap -> node.value.mapValues { toKotlin(it.value) }
        }
}

private fun escapeXml(input: String): String =
    input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
