package com.linkpoint.linden.llcommon

sealed class LLSD {
    object Undefined : LLSD()
    data class LLSDBoolean(val value: Boolean) : LLSD()
    data class LLSDInteger(val value: Int) : LLSD()
    data class LLSDReal(val value: Double) : LLSD()
    data class LLSDString(val value: String) : LLSD()
    data class LLSDUUID(val value: LLUUID) : LLSD()
    data class LLSDDate(val value: LLDate) : LLSD()
    data class LLSDURI(val value: LLURI) : LLSD()
    data class LLSDBinary(val value: ByteArray) : LLSD() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is LLSDBinary) return false
            return value.contentEquals(other.value)
        }
        override fun hashCode(): Int = value.contentHashCode()
    }
    data class LLSDMap(val value: Map<String, LLSD>) : LLSD()
    data class LLSDArray(val value: List<LLSD>) : LLSD()

    fun isUndefined(): Boolean = this is Undefined

    fun isDefined(): Boolean = this !is Undefined

    fun asBoolean(): Boolean = when (this) {
        is Undefined -> false
        is LLSDBoolean -> value
        is LLSDInteger -> value != 0
        is LLSDReal -> value != 0.0
        is LLSDString -> value.isNotEmpty()
        is LLSDUUID -> value.notNull()
        is LLSDDate -> value.notNull()
        is LLSDURI -> value.asString().isNotEmpty()
        is LLSDBinary -> value.isNotEmpty()
        is LLSDMap -> value.isNotEmpty()
        is LLSDArray -> value.isNotEmpty()
    }

    fun asInt(): Int = when (this) {
        is Undefined -> 0
        is LLSDBoolean -> if (value) 1 else 0
        is LLSDInteger -> value
        is LLSDReal -> value.toInt()
        is LLSDString -> value.toIntOrNull() ?: 0
        else -> 0
    }

    fun asReal(): Double = when (this) {
        is Undefined -> 0.0
        is LLSDBoolean -> if (value) 1.0 else 0.0
        is LLSDInteger -> value.toDouble()
        is LLSDReal -> value
        is LLSDString -> value.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }

    fun asString(): String = when (this) {
        is Undefined -> ""
        is LLSDBoolean -> if (value) "true" else "false"
        is LLSDInteger -> value.toString()
        is LLSDReal -> value.toString()
        is LLSDString -> value
        is LLSDUUID -> value.toString()
        is LLSDDate -> value.toISOString()
        is LLSDURI -> value.asString()
        is LLSDBinary -> java.util.Base64.getEncoder().encodeToString(value)
        is LLSDMap -> ""
        is LLSDArray -> ""
    }

    fun asUUID(): LLUUID = when (this) {
        is LLSDUUID -> value
        is LLSDString -> LLUUID.fromString(value) ?: LLUUID.NULL
        else -> LLUUID.NULL
    }

    fun asDate(): LLDate = when (this) {
        is LLSDDate -> value
        is LLSDString -> LLDate.fromISOString(value) ?: LLDate.NULL
        else -> LLDate.NULL
    }

    fun asURI(): LLURI = when (this) {
        is LLSDURI -> value
        is LLSDString -> LLURI.fromString(value)
        else -> LLURI()
    }

    fun asBinary(): ByteArray = when (this) {
        is LLSDBinary -> value
        is LLSDString -> runCatching { java.util.Base64.getDecoder().decode(value) }.getOrElse { byteArrayOf() }
        else -> byteArrayOf()
    }

    fun size(): Int = when (this) {
        is LLSDMap -> value.size
        is LLSDArray -> value.size
        else -> 0
    }

    operator fun get(key: String): LLSD = when (this) {
        is LLSDMap -> value[key] ?: Undefined
        else -> Undefined
    }

    operator fun get(index: Int): LLSD = when (this) {
        is LLSDArray -> if (index in value.indices) value[index] else Undefined
        else -> Undefined
    }

    fun has(key: String): Boolean = this is LLSDMap && value.containsKey(key)

    fun asFloat(): Float = asReal().toFloat()
    fun asInteger(): Int = asInt()

    companion object {
        fun of(value: Boolean): LLSD = LLSDBoolean(value)
        fun of(value: Int): LLSD = LLSDInteger(value)
        fun of(value: Double): LLSD = LLSDReal(value)
        fun of(value: String): LLSD = LLSDString(value)
        fun of(value: LLUUID): LLSD = LLSDUUID(value)
        fun of(value: LLDate): LLSD = LLSDDate(value)
        fun of(value: LLURI): LLSD = LLSDURI(value)
        fun of(value: ByteArray): LLSD = LLSDBinary(value)
        fun ofMap(value: Map<String, LLSD> = kotlin.collections.emptyMap()): LLSD = LLSDMap(value)
        fun ofArray(value: List<LLSD> = kotlin.collections.emptyList()): LLSD = LLSDArray(value)
        fun emptyMap(): LLSD = LLSDMap(mapOf())
        fun emptyArray(): LLSD = LLSDArray(listOf())

        fun integer(v: Int): LLSD = LLSDInteger(v)
        fun real(v: Double): LLSD = LLSDReal(v)
        fun string(v: String): LLSD = LLSDString(v)
        fun uuid(v: LLUUID): LLSD = LLSDUUID(v)
        fun bool(v: Boolean): LLSD = LLSDBoolean(v)
        fun array(vararg items: LLSD): LLSD = LLSDArray(items.toList())
        fun map(vararg pairs: Pair<String, LLSD>): LLSD = LLSDMap(mapOf(*pairs))
    }
}
