package com.linkpoint.linden.llcommon

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object LLString {

    fun trimHead(s: String): String = s.trimStart()

    fun trimTail(s: String): String = s.trimEnd()

    fun trim(s: String): String = s.trim()

    fun toUpper(s: String): String = s.uppercase(Locale.ROOT)

    fun toLower(s: String): String = s.lowercase(Locale.ROOT)

    fun isValidIndex(s: String, i: Int): Boolean = s.isNotEmpty() && i in 0..s.length

    fun truncate(s: String, maxLen: Int): String =
        if (s.length <= maxLen) s else s.substring(0, maxLen)

    fun containsNonprintable(s: String): Boolean = s.any { it.code < 32 || it.code == 127 }

    fun stripNonprintable(s: String): String =
        s.filter { it.code >= 32 && it.code != 127 }

    fun compareInsensitive(a: String, b: String): Int =
        a.lowercase(Locale.ROOT).compareTo(b.lowercase(Locale.ROOT))

    fun startsWith(s: String, prefix: String, caseSensitive: Boolean = true): Boolean {
        if (s.isEmpty() || prefix.isEmpty()) return false
        return if (caseSensitive) s.startsWith(prefix)
        else s.lowercase(Locale.ROOT).startsWith(prefix.lowercase(Locale.ROOT))
    }

    fun endsWith(s: String, suffix: String, caseSensitive: Boolean = true): Boolean {
        if (s.isEmpty() || suffix.isEmpty()) return false
        return if (caseSensitive) s.endsWith(suffix)
        else s.lowercase(Locale.ROOT).endsWith(suffix.lowercase(Locale.ROOT))
    }

    // Replaces [KEY] placeholders with the corresponding value from args.
    fun format(fmt: String, args: Map<String, String>): String {
        var result = fmt
        for ((key, value) in args) {
            result = result.replace("[$key]", value)
        }
        return result
    }

    fun replaceAll(s: String, search: String, replace: String): String =
        s.replace(search, replace)

    fun tokenize(s: String, delimiters: String): List<String> {
        if (s.isEmpty()) return emptyList()
        val result = mutableListOf<String>()
        val delimSet = delimiters.toSet()
        val sb = StringBuilder()
        for (ch in s) {
            if (ch in delimSet) {
                if (sb.isNotEmpty()) {
                    result.add(sb.toString())
                    sb.clear()
                }
            } else {
                sb.append(ch)
            }
        }
        if (sb.isNotEmpty()) result.add(sb.toString())
        return result
    }

    // Wraps s in double-quotes, escaping any embedded double-quotes with backslash.
    fun quote(s: String): String {
        if (s.startsWith('"') && s.endsWith('"') && s.length >= 2) return s
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"'
    }

    // Strips surrounding double-quotes and unescapes internal sequences.
    fun unquote(s: String): String {
        if (s.startsWith('"') && s.endsWith('"') && s.length >= 2) {
            return s.substring(1, s.length - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
        }
        return s
    }

    fun toBoolean(s: String): Boolean {
        return when (s.trim().lowercase(Locale.ROOT)) {
            "true", "1", "yes" -> true
            else -> false
        }
    }

    fun formatNumber(n: Double, decimalDigits: Int): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = if (decimalDigits > 0) {
            "#,##0." + "0".repeat(decimalDigits)
        } else {
            "#,##0"
        }
        return DecimalFormat(pattern, symbols).format(n)
    }
}

fun trimHead(s: String): String = LLString.trimHead(s)
fun trimTail(s: String): String = LLString.trimTail(s)
fun trim(s: String): String = LLString.trim(s)
fun toUpper(s: String): String = LLString.toUpper(s)
fun toLower(s: String): String = LLString.toLower(s)
fun isValidIndex(s: String, i: Int): Boolean = LLString.isValidIndex(s, i)
fun truncate(s: String, maxLen: Int): String = LLString.truncate(s, maxLen)
fun containsNonprintable(s: String): Boolean = LLString.containsNonprintable(s)
fun stripNonprintable(s: String): String = LLString.stripNonprintable(s)
fun compareInsensitive(a: String, b: String): Int = LLString.compareInsensitive(a, b)
fun startsWith(s: String, prefix: String, caseSensitive: Boolean = true): Boolean =
    LLString.startsWith(s, prefix, caseSensitive)
fun endsWith(s: String, suffix: String, caseSensitive: Boolean = true): Boolean =
    LLString.endsWith(s, suffix, caseSensitive)
fun format(fmt: String, args: Map<String, String>): String = LLString.format(fmt, args)
fun replaceAll(s: String, search: String, replace: String): String =
    LLString.replaceAll(s, search, replace)
fun tokenize(s: String, delimiters: String): List<String> = LLString.tokenize(s, delimiters)
fun quote(s: String): String = LLString.quote(s)
fun unquote(s: String): String = LLString.unquote(s)
fun toBoolean(s: String): Boolean = LLString.toBoolean(s)
fun formatNumber(n: Double, decimalDigits: Int): String = LLString.formatNumber(n, decimalDigits)
