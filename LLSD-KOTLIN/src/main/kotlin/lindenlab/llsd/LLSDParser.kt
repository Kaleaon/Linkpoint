/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*
import java.util.logging.Logger
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * A parser for LLSD (Linden Lab Structured Data) in its traditional XML format.
 *
 * This class is responsible for taking an [InputStream] containing an
 * LLSD XML document and converting it into a Java object representation, which is
 * then wrapped in an [LLSD] object. It uses the standard Java XML parsing
 * APIs (DOM) to process the document.
 *
 * The parser correctly interprets the XML tags that correspond to LLSD data
 * types (e.g., `<integer>`, `<string>`, `<map>`, `<array>`)
 * and converts their contents into the appropriate Java types.
 *
 * @see LLSD
 * @see [LLSD XML Specification](http://wiki.secondlife.com/wiki/LLSD#XML_Serialization)
 */
class LLSDParser @Throws(ParserConfigurationException::class) constructor() {
    
    /**
     * Document builder used to parse the replies
     */
    private val documentBuilder: DocumentBuilder
    
    init {
        val factory = DocumentBuilderFactory.newInstance()
        documentBuilder = factory.newDocumentBuilder()
    }
    
    /**
     * Helper method to filter a [NodeList], returning only nodes that are
     * actual elements.
     *
     * @param nodes The list of nodes to filter.
     * @return A list containing only the `ELEMENT_NODE` instances from the input.
     */
    private fun extractElements(nodes: NodeList): List<Node> {
        val trimmedNodes = mutableListOf<Node>()
        
        for (nodeIdx in 0 until nodes.length) {
            val node = nodes.item(nodeIdx)
            if (node.nodeType == Node.ELEMENT_NODE) {
                trimmedNodes.add(node)
            }
        }
        
        return trimmedNodes
    }
    
    /**
     * Parses an LLSD document from an XML input stream.
     *
     * This is the main entry point for parsing an XML-based LLSD document. It
     * reads the stream, builds a DOM tree, and then traverses the tree to
     * construct the corresponding LLSD object structure.
     *
     * @param xmlFile The input stream containing the XML LLSD data.
     * @return An [LLSD] object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading the stream.
     * @throws LLSDException if the document is not valid LLSD (e.g., incorrect
     *                       structure or invalid data formats).
     * @throws SAXException  if a general XML parsing error occurs.
     */
    @Throws(IOException::class, LLSDException::class, SAXException::class)
    fun parse(xmlFile: InputStream): LLSD {
        val document: Document = documentBuilder.parse(xmlFile)
        val llsdNode = document.documentElement
            ?: throw LLSDException("Outer-most tag for LLSD missing.")
        
        if (!llsdNode.nodeName.equals("llsd", ignoreCase = true)) {
            throw LLSDException("Outer-most tag for LLSD is \"${llsdNode.nodeName}\" instead of \"llsd\".")
        }
        
        val childNodesTrimmed = extractElements(llsdNode.childNodes)
        if (childNodesTrimmed.isEmpty()) {
            LOGGER.warning("Empty LLSD tag found. This might indicate a malformed or empty response.")
            return LLSD(null)
        }
        
        if (childNodesTrimmed.size > 1) {
            throw LLSDException("Expected only one subelement for element <llsd>.")
        }
        
        val llsdContents = parseNode(childNodesTrimmed[0])
        return LLSD(llsdContents)
    }
    
    @Throws(LLSDException::class)
    private fun parseArray(nodeList: NodeList): List<Any?> {
        val value = mutableListOf<Any?>()
        
        for (nodeIdx in 0 until nodeList.length) {
            val node = nodeList.item(nodeIdx)
            
            if (node.nodeType == Node.ELEMENT_NODE) {
                value.add(parseNode(node))
            }
        }
        
        return value
    }
    
    @Throws(LLSDException::class)
    private fun parseBoolean(elementContents: String): Boolean {
        return elementContents == "1" || elementContents.equals("true", ignoreCase = true)
    }
    
    @Throws(LLSDException::class)
    private fun parseDate(elementContents: String): Date {
        if (elementContents.isEmpty()) {
            return Date(0)
        }
        
        return try {
            Date.from(Instant.parse(elementContents))
        } catch (e: DateTimeParseException) {
            throw LLSDException("Unable to parse LLSD date value, received \"$elementContents\".", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseInteger(elementContents: String): Int {
        if (elementContents.isEmpty()) {
            return 0
        }
        
        return try {
            elementContents.toInt()
        } catch (e: NumberFormatException) {
            throw LLSDException("Unable to parse LLSD integer value, received \"$elementContents\".", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseMap(nodeList: NodeList): Map<String, Any?> {
        val trimmedNodes = extractElements(nodeList)
        val valueMap = mutableMapOf<String, Any?>()
        
        if (trimmedNodes.size % 2 != 0) {
            throw LLSDException("Unable to parse LLSD map as it has odd number of nodes: $nodeList")
        }
        
        var nodeIdx = 0
        while (nodeIdx < trimmedNodes.size) {
            val keyNode = trimmedNodes[nodeIdx]
            val valueNode = trimmedNodes[nodeIdx + 1]
            var key: String? = null
            
            val keyChildren = keyNode.childNodes
            for (keyNodeIdx in 0 until keyChildren.length) {
                val textNode = keyChildren.item(keyNodeIdx)
                when (textNode.nodeType) {
                    Node.TEXT_NODE -> key = textNode.nodeValue
                    else -> throw LLSDException("Unexpected node \"${textNode.nodeName}\" found while parsing key for map.")
                }
            }
            
            val value = parseNode(valueNode)
            if (key == null) {
                throw LLSDException("Key for map cannot be null.")
            }
            valueMap[key] = value
            
            nodeIdx += 2
        }
        
        return valueMap
    }
    
    /**
     * Recursively parses a single DOM node and converts it into an LLSD value.
     *
     * This method is the core of the parser's logic. It examines the node's name
     * to determine the LLSD data type and then calls the appropriate helper
     * method to parse its content.
     *
     * @param node The DOM node to parse.
     * @return A Java object representing the parsed LLSD value.
     * @throws LLSDException if the node is of an unknown or invalid type.
     */
    @Throws(LLSDException::class)
    private fun parseNode(node: Node): Any? {
        var isUndefined = false
        val childNodes = node.childNodes
        val nodeName = node.nodeName.lowercase()
        
        // Handle compound types (array and map)
        if (nodeName == "array") {
            return parseArray(childNodes)
        } else if (nodeName == "map") {
            return parseMap(childNodes)
        }
        
        val nodeText = StringBuilder()
        for (nodeIdx in 0 until childNodes.length) {
            val childNode = childNodes.item(nodeIdx) ?: continue
            
            when (childNode.nodeType) {
                Node.TEXT_NODE -> {
                    childNode.nodeValue?.let { nodeText.append(it) }
                }
                Node.ELEMENT_NODE -> {
                    if (childNode.nodeName == "undef") {
                        isUndefined = true
                    }
                }
            }
        }
        
        // Handle binary after nodeText is built
        if (nodeName == "binary") {
            if (isUndefined) {
                return LLSDUndefined.BINARY
            }
            val base64Content = nodeText.toString().trim()
            if (base64Content.isEmpty()) {
                return ByteArray(0) // Empty binary data
            }
            return try {
                Base64.getDecoder().decode(base64Content)
            } catch (e: IllegalArgumentException) {
                throw LLSDException("Invalid base64 binary data: $base64Content", e)
            }
        }
        
        return when (nodeName) {
            "undef" -> null
            "boolean" -> if (isUndefined) LLSDUndefined.BOOLEAN else parseBoolean(nodeText.toString())
            "date" -> if (isUndefined) LLSDUndefined.DATE else parseDate(nodeText.toString())
            "integer" -> if (isUndefined) LLSDUndefined.INTEGER else parseInteger(nodeText.toString())
            "real" -> if (isUndefined) LLSDUndefined.REAL else parseReal(nodeText.toString())
            "string" -> if (isUndefined) LLSDUndefined.STRING else parseString(nodeText.toString())
            "uri" -> if (isUndefined) LLSDUndefined.URI else parseURI(nodeText.toString())
            "uuid" -> if (isUndefined) LLSDUndefined.UUID else parseUUID(nodeText.toString())
            else -> throw LLSDException("Encountered unexpected node \"${node.nodeName}\".")
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseReal(elementContents: String): Double {
        if (elementContents.isEmpty()) {
            return 0.0
        }
        
        return when (elementContents.lowercase(java.util.Locale.ROOT)) {
            "nan" -> Double.NaN
            "inf", "infinity" -> Double.POSITIVE_INFINITY
            "-inf", "-infinity" -> Double.NEGATIVE_INFINITY
            else -> try {
                elementContents.toDouble()
            } catch (e: NumberFormatException) {
                throw LLSDException("Unable to parse LLSD real value, received \"$elementContents\".", e)
            }
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseString(elementContents: String): String {
        return elementContents
    }
    
    @Throws(LLSDException::class)
    private fun parseURI(elementContents: String): URI {
        return try {
            URI(elementContents)
        } catch (e: java.net.URISyntaxException) {
            throw LLSDException("Unable to parse LLSD URI value, received \"$elementContents\".", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseUUID(elementContents: String): UUID {
        if (elementContents.isEmpty()) {
            return UUID(0L, 0L)
        }
        
        return try {
            UUID.fromString(elementContents)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Unable to parse LLSD UUID value, received \"$elementContents\".", e)
        }
    }
    
    companion object {
        private val LOGGER = Logger.getLogger(LLSDParser::class.java.name)
    }
}
