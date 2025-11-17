package lindenlab.llsd

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.net.URI
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.TimeZone
import java.util.UUID

/**
 * Minimal LLSD document wrapper with XML serialisation support.
 */
class LLSD(val content: Any?) {

    @Throws(IOException::class, LLSDException::class)
    fun serialise(writer: Writer, charset: String = "UTF-8") {
        writer.write("<?xml version=\"1.0\" encoding=\"$charset\"?>\n")
        writer.write("<llsd>\n")
        if (content == null) {
            writer.write("<undef/>\n")
        } else {
            serialiseElement(writer, content)
        }
        writer.write("</llsd>\n")
    }

    override fun toString(): String {
        val writer = StringWriter()
        return try {
            serialise(writer)
            writer.toString()
        } catch (e: Exception) {
            "Unable to serialise LLSD: ${e.message}"
        }
    }

    @Throws(IOException::class, LLSDException::class)
    private fun serialiseElement(writer: Writer, node: Any?) {
        val dateFormat = SimpleDateFormat(ISO8601_PATTERN).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val decimalFormat = DecimalFormat("#0.0#")

        when (node) {
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val map = node as Map<String, Any?>
                writer.write("<map>\n")
                map.forEach { (key, value) ->
                    writer.write("\t<key>${encodeXML(key)}</key>\n\t")
                    serialiseElement(writer, value)
                }
                writer.write("</map>\n")
            }
            is List<*> -> {
                writer.write("<array>\n")
                node.forEach { child ->
                    writer.write("\t")
                    serialiseElement(writer, child)
                }
                writer.write("</array>\n")
            }
            is Boolean -> writer.write("<boolean>$node</boolean>\n")
            is Int -> writer.write("<integer>$node</integer>\n")
            is Double -> {
                if (node.isNaN()) writer.write("<real>nan</real>\n")
                else writer.write("<real>${decimalFormat.format(node)}</real>\n")
            }
            is Float -> {
                if (node.isNaN()) writer.write("<real>nan</real>\n")
                else writer.write("<real>${decimalFormat.format(node)}</real>\n")
            }
            is UUID -> writer.write("<uuid>$node</uuid>\n")
            is String -> writer.write("<string>${encodeXML(node)}</string>\n")
            is Date -> writer.write("<date>${dateFormat.format(node)}</date>\n")
            is URI -> writer.write("<uri>${encodeXML(node.toString())}</uri>\n")
            is ByteArray -> {
                val encoded = Base64.getEncoder().encodeToString(node)
                writer.write("<binary>$encoded</binary>\n")
            }
            is LLSDUndefined -> {
                val tag = when (node) {
                    LLSDUndefined.BINARY -> "binary"
                    LLSDUndefined.BOOLEAN -> "boolean"
                    LLSDUndefined.DATE -> "date"
                    LLSDUndefined.INTEGER -> "integer"
                    LLSDUndefined.REAL -> "real"
                    LLSDUndefined.STRING -> "string"
                    LLSDUndefined.URI -> "uri"
                    LLSDUndefined.UUID -> "uuid"
                }
                writer.write("<$tag><undef /></$tag>\n")
            }
            null -> writer.write("<undef/>\n")
            else -> throw LLSDException("Unsupported LLSD type: ${node::class.java.name}")
        }
    }

    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        fun encodeXML(text: String?): String {
            if (text == null) return "null"
            if (text.isEmpty()) return text

            val sb = StringBuilder()
            text.forEach { ch ->
                when (ch) {
                    '<' -> sb.append("&lt;")
                    '>' -> sb.append("&gt;")
                    '&' -> sb.append("&amp;")
                    '"' -> sb.append("&quot;")
                    '\'' -> sb.append("&apos;")
                    else -> sb.append(ch)
                }
            }
            return sb.toString()
        }
    }
}
