package lindenlab.llsd

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Lightweight LLSD XML parser based on the original Firestorm tooling.
 */
class LLSDParser @Throws(ParserConfigurationException::class) constructor() {

    private val documentBuilder: DocumentBuilder =
        DocumentBuilderFactory.newInstance().newDocumentBuilder()

    @Throws(IOException::class, LLSDException::class, SAXException::class)
    fun parse(stream: InputStream): LLSD {
        val document: Document = documentBuilder.parse(stream)
        val llsdNode = document.documentElement
            ?: throw LLSDException("Missing <llsd> root element.")
        if (!llsdNode.nodeName.equals("llsd", ignoreCase = true)) {
            throw LLSDException("Root element ${llsdNode.nodeName} is not <llsd>.")
        }
        val children = extractElements(llsdNode.childNodes)
        if (children.size != 1) {
            throw LLSDException("Expected single child under <llsd> node.")
        }
        return LLSD(parseNode(children[0]))
    }

    private fun extractElements(nodes: NodeList): List<Node> {
        val result = mutableListOf<Node>()
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                result.add(node)
            }
        }
        return result
    }

    @Throws(LLSDException::class)
    private fun parseNode(node: Node): Any? {
        val name = node.nodeName.lowercase()
        val children = node.childNodes

        return when (name) {
            "array" -> parseArray(children)
            "map" -> parseMap(children)
            "boolean" -> parseBoolean(children)
            "integer" -> parseInteger(children)
            "real" -> parseReal(children)
            "string" -> parseString(children)
            "uuid" -> parseUuid(children)
            "uri" -> parseUri(children)
            "date" -> parseDate(children)
            "binary" -> parseBinary(children)
            "undef" -> ""
            else -> throw LLSDException("Unsupported LLSD node <$name>")
        }
    }

    @Throws(LLSDException::class)
    private fun parseArray(children: NodeList): List<Any?> {
        val result = mutableListOf<Any?>()
        for (i in 0 until children.length) {
            val child = children.item(i)
            if (child.nodeType == Node.ELEMENT_NODE) {
                result.add(parseNode(child))
            }
        }
        return result
    }

    @Throws(LLSDException::class)
    private fun parseMap(children: NodeList): Map<String, Any?> {
        val trimmed = extractElements(children)
        if (trimmed.size % 2 != 0) {
            throw LLSDException("Map entries must be key/value pairs.")
        }
        val map = mutableMapOf<String, Any?>()
        var index = 0
        while (index < trimmed.size) {
            val keyNode = trimmed[index]
            val valueNode = trimmed[index + 1]
            if (!keyNode.nodeName.equals("key", ignoreCase = true)) {
                throw LLSDException("Expected <key> but found <${keyNode.nodeName}>.")
            }
            val key = keyNode.textContent ?: ""
            map[key] = parseNode(valueNode)
            index += 2
        }
        return map
    }

    @Throws(LLSDException::class)
    private fun parseBoolean(children: NodeList): Boolean {
        val text = childrenText(children)
        return text == "1" || text.equals("true", ignoreCase = true)
    }

    @Throws(LLSDException::class)
    private fun parseInteger(children: NodeList): Int {
        val text = childrenText(children)
        return if (text.isEmpty()) 0 else text.toIntOrNull()
            ?: throw LLSDException("Invalid integer value \"$text\".")
    }

    @Throws(LLSDException::class)
    private fun parseReal(children: NodeList): Double {
        val text = childrenText(children)
        if (text.equals("nan", ignoreCase = true)) return Double.NaN
        return if (text.isEmpty()) 0.0 else text.toDoubleOrNull()
            ?: throw LLSDException("Invalid real value \"$text\".")
    }

    private fun parseString(children: NodeList): String = childrenText(children)

    @Throws(LLSDException::class)
    private fun parseUuid(children: NodeList): UUID {
        val text = childrenText(children)
        if (text.isEmpty()) return UUID(0, 0)
        return try {
            UUID.fromString(text)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Invalid UUID \"$text\".", e)
        }
    }

    @Throws(LLSDException::class)
    private fun parseUri(children: NodeList): URI {
        val text = childrenText(children)
        return try {
            URI(text)
        } catch (e: Exception) {
            throw LLSDException("Invalid URI \"$text\".", e)
        }
    }

    @Throws(LLSDException::class)
    private fun parseDate(children: NodeList): Date {
        val text = childrenText(children)
        if (text.isEmpty()) return Date(0)
        val format = SimpleDateFormat(ISO8601_PATTERN).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return try {
            format.parse(text) ?: Date(0)
        } catch (e: Exception) {
            throw LLSDException("Invalid date \"$text\".", e)
        }
    }

    @Throws(LLSDException::class)
    private fun parseBinary(children: NodeList): ByteArray {
        val text = childrenText(children).trim()
        if (text.isEmpty()) return ByteArray(0)
        return try {
            java.util.Base64.getDecoder().decode(text)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Invalid base64 binary.", e)
        }
    }

    private fun childrenText(children: NodeList): String {
        val sb = StringBuilder()
        for (i in 0 until children.length) {
            val child = children.item(i)
            if (child.nodeType == Node.TEXT_NODE) {
                sb.append(child.nodeValue)
            }
        }
        return sb.toString()
    }

    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }
}
