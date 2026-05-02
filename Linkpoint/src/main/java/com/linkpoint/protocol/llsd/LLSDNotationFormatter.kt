package com.linkpoint.protocol.llsd

import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Locale
import java.util.TimeZone

/**
 * LLSD notation serializer modeled after secondlife/python-llsd
 * (serde_notation.py). This fills the previous gap where Linkpoint could parse
 * notation but could not emit it.
 */
object LLSDNotationFormatter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun format(value: LLSDValue, includeHeader: Boolean = false): String {
        val body = writeValue(value)
        return if (includeHeader) "<?llsd/notation?>\n$body" else body
    }

    private fun writeValue(value: LLSDValue): String = when (value) {
        LLSDUndefined -> "!"
        is LLSDBoolean -> if (value.value) "true" else "false"
        is LLSDInteger -> "i${value.value}"
        is LLSDReal -> "r${formatReal(value.value)}"
        is LLSDUUID -> "u${value.value}"
        is LLSDString -> quoteAndEscape(value.value)
        is LLSDBinary -> "b64\"${Base64.getEncoder().encodeToString(value.value)}\""
        is LLSDDate -> "d\"${dateFormat.format(value.value)}\""
        is LLSDURI -> "l${quoteAndEscape(value.value)}"
        is LLSDMap -> value.value.entries.joinToString(prefix = "{", postfix = "}", separator = ",") {
            "${quoteAndEscape(it.key)}:${writeValue(it.value)}"
        }
        is LLSDArray -> value.value.joinToString(prefix = "[", postfix = "]", separator = ",") { writeValue(it) }
    }

    private fun quoteAndEscape(text: String): String {
        val escaped = buildString(text.length + 8) {
            text.forEach { ch ->
                when (ch) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(ch)
                }
            }
        }
        return "\"$escaped\""
    }

    private fun formatReal(value: Double): String {
        return when {
            value.isNaN() -> "NaN"
            value == Double.POSITIVE_INFINITY -> "Inf"
            value == Double.NEGATIVE_INFINITY -> "-Inf"
            else -> value.toString()
        }
    }
}
