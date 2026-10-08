package com.linkpoint.linden.llxml

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.ext.LexicalHandler
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

class XmlParser {
    var depth: Int = 0
        private set
    var lastError: String = ""
        private set

    fun parse(xml: String): XmlNode? = XmlNode.parse(xml)

    fun parseFile(path: String): XmlNode? = runCatching {
        parse(File(path).readText(Charsets.UTF_8))
    }.getOrElse { e ->
        lastError = e.message ?: "Unknown error"
        null
    }

    fun parseStream(input: java.io.InputStream): XmlNode? = runCatching {
        parse(input.reader(Charsets.UTF_8).readText())
    }.getOrElse { e ->
        lastError = e.message ?: "Unknown error"
        null
    }

    fun getErrorString(): String = lastError

    fun getCurrentLineNumber(): Int = -1

    fun getCurrentColumnNumber(): Int = -1

    // SAX-based incremental parse handler, mirrors LLXmlParser's virtual callback API.
    // Subclasses override the on* methods for event-driven processing.
    open class SaxHandler : DefaultHandler(), LexicalHandler {
        var depth: Int = 0
            private set
        var lastError: String = "no error"

        open fun onStartElement(name: String, attributes: Map<String, String>) {}
        open fun onEndElement(name: String) {}
        open fun onCharacterData(data: String) {}
        open fun onProcessingInstruction(target: String, data: String) {}
        open fun onComment(data: String) {}
        open fun onStartCdataSection() {}
        open fun onEndCdataSection() {}
        open fun onDefaultData(data: String) {}

        override fun startElement(uri: String, localName: String, qName: String, atts: Attributes) {
            val map = mutableMapOf<String, String>()
            for (i in 0 until atts.length) map[atts.getQName(i)] = atts.getValue(i)
            onStartElement(qName, map)
            depth++
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            depth--
            onEndElement(qName)
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            onCharacterData(String(ch, start, length))
        }

        override fun processingInstruction(target: String, data: String) {
            onProcessingInstruction(target, data)
        }

        // LexicalHandler methods for CDATA section tracking
        override fun startCDATA() {
            depth++
            onStartCdataSection()
        }

        override fun endCDATA() {
            onEndCdataSection()
            depth++
        }

        override fun comment(ch: CharArray, start: Int, length: Int) {
            onComment(String(ch, start, length))
        }

        override fun startDTD(name: String?, publicId: String?, systemId: String?) {}
        override fun endDTD() {}
        override fun startEntity(name: String?) {}
        override fun endEntity(name: String?) {}

        fun parseString(xml: String): Boolean = runCatching {
            val factory = SAXParserFactory.newInstance()
            val parser = factory.newSAXParser()
            val xmlReader = parser.xmlReader
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this)
            xmlReader.contentHandler = this
            xmlReader.parse(InputSource(StringReader(xml)))
            true
        }.getOrElse { e ->
            lastError = e.message ?: "Unknown error"
            false
        }

        fun parseFile(path: String): Boolean = runCatching {
            parseString(File(path).readText(Charsets.UTF_8))
        }.getOrElse { e ->
            lastError = e.message ?: "Unknown error"
            false
        }

        fun getErrorString(): String = lastError
        fun getCurrentLineNumber(): Int = -1
        fun getCurrentColumnNumber(): Int = -1
    }
}
