package com.linkpoint.linden.llcorehttp

import java.util.TreeMap

class LLHttpHeaders {
    private val headers: TreeMap<String, MutableList<String>> =
        TreeMap(String.CASE_INSENSITIVE_ORDER)

    fun append(name: String, value: String) {
        headers.getOrPut(name) { mutableListOf() }.add(value)
    }

    fun set(name: String, value: String) {
        headers[name] = mutableListOf(value)
    }

    fun find(name: String): String? = headers[name]?.firstOrNull()

    fun findAll(name: String): List<String> = headers[name] ?: emptyList()

    fun remove(name: String): Boolean = headers.remove(name) != null

    fun contains(name: String): Boolean = name in headers

    fun toMap(): Map<String, String> = headers.mapValues { it.value.joinToString(", ") }

    fun toMultiMap(): Map<String, List<String>> = headers.toMap()

    fun clear() = headers.clear()

    fun isEmpty(): Boolean = headers.isEmpty()

    override fun toString(): String =
        headers.entries.joinToString("\n") { (k, v) -> "$k: ${v.joinToString(", ")}" }
}
