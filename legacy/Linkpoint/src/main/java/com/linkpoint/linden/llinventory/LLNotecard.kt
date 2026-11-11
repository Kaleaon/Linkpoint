package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLUUID

class LLNotecard(
    var subject: String = "",
    var body: String = "",
    val embeddedItems: MutableList<LLUUID> = mutableListOf()
) {
    companion object {
        private const val VERSION_MAGIC = "Linden text version 2"
        private const val EMBEDDED_MAGIC = "{"

        fun deserialize(data: String): LLNotecard? {
            val lines = data.lines()
            if (lines.isEmpty()) return null
            val nc = LLNotecard()
            var i = 0
            while (i < lines.size) {
                val line = lines[i].trim()
                when {
                    line.startsWith("Text length") -> {
                        val bodyStart = i + 1
                        val sb = StringBuilder()
                        for (j in bodyStart until lines.size) sb.append(lines[j]).append('\n')
                        nc.body = sb.toString().trimEnd('\n', '}')
                        break
                    }
                    line.startsWith("embedded_count") -> {
                        val count = line.substringAfterLast(' ').trim().toIntOrNull() ?: 0
                        repeat(count) { nc.embeddedItems.add(LLUUID.generate()) }
                    }
                }
                i++
            }
            return nc
        }
    }

    fun serialize(): String = buildString {
        appendLine(VERSION_MAGIC)
        appendLine("{")
        if (embeddedItems.isNotEmpty()) {
            appendLine("embedded_count ${embeddedItems.size}")
            for (id in embeddedItems) appendLine("  $id")
        }
        val bodyBytes = body.toByteArray(Charsets.UTF_8)
        appendLine("Text length ${bodyBytes.size}")
        append(body)
        appendLine("}")
    }
}
