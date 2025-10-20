package com.linkpoint.slproto.llsd

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.StringReader
import java.util.UUID
import java.util.Date
import android.util.Base64

/**
 * LLSDXMLParser - Parse XML LLSD format
 * 
 * XML LLSD format example:
 * <llsd>
 *   <map>
 *     <key>name</key><string>value</string>
 *     <key>count</key><integer>42</integer>
 *   </map>
 * </llsd>
 * 
 * Based on Firestorm's LLSDXMLParser
 */
object LLSDXMLParser {
    
    /**
     * Parse LLSD from XML string
     */
    @Throws(LLSDXMLException::class)
    fun parse(xml: String): LLSD {
        return parse(StringReader(xml).buffered().inputStream())
    }
    
    /**
     * Parse LLSD from XML stream
     */
    @Throws(LLSDXMLException::class)
    fun parse(inputStream: InputStream): LLSD {
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(inputStream, "UTF-8")
            
            // Find <llsd> root element
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "llsd") {
                    // Parse content inside <llsd>
                    return parseElement(parser)
                }
                eventType = parser.next()
            }
            
            throw LLSDXMLException("No <llsd> root element found")
            
        } catch (e: Exception) {
            throw LLSDXMLException("XML parsing error: ${e.message}", e)
        }
    }
    
    /**
     * Parse a single LLSD element
     */
    @Throws(Exception::class)
    private fun parseElement(parser: XmlPullParser): LLSD {
        var eventType = parser.next()
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    return when (parser.name) {
                        "undef" -> LLSD()
                        
                        "boolean" -> {
                            val text = parser.nextText().trim()
                            LLSD(text == "true" || text == "1")
                        }
                        
                        "integer" -> {
                            val text = parser.nextText().trim()
                            LLSD(text.toIntOrNull() ?: 0)
                        }
                        
                        "real" -> {
                            val text = parser.nextText().trim()
                            LLSD(text.toDoubleOrNull() ?: 0.0)
                        }
                        
                        "string" -> {
                            val text = parser.nextText()
                            LLSD(text)
                        }
                        
                        "uuid" -> {
                            val text = parser.nextText().trim()
                            try {
                                LLSD(UUID.fromString(text))
                            } catch (e: Exception) {
                                LLSD(UUID(0, 0))
                            }
                        }
                        
                        "date" -> {
                            val text = parser.nextText().trim()
                            // Parse ISO 8601 date format
                            // For simplicity, using epoch seconds for now
                            val seconds = text.toDoubleOrNull() ?: 0.0
                            LLSD(Date((seconds * 1000).toLong()))
                        }
                        
                        "uri" -> {
                            val text = parser.nextText()
                            LLSD(text)
                        }
                        
                        "binary" -> {
                            val text = parser.nextText().trim()
                            // Binary data is Base64 encoded in XML
                            val bytes = try {
                                Base64.decode(text, Base64.DEFAULT)
                            } catch (e: Exception) {
                                ByteArray(0)
                            }
                            LLSD(bytes)
                        }
                        
                        "array" -> parseArray(parser)
                        
                        "map" -> parseMap(parser)
                        
                        else -> {
                            // Skip unknown element
                            skipElement(parser)
                            LLSD()
                        }
                    }
                }
                
                XmlPullParser.END_TAG -> {
                    // End of llsd element
                    return LLSD()
                }
            }
            
            eventType = parser.next()
        }
        
        return LLSD()
    }
    
    /**
     * Parse LLSD array
     */
    @Throws(Exception::class)
    private fun parseArray(parser: XmlPullParser): LLSD {
        val array = LLSD.emptyArray()
        var depth = 1  // We're inside <array>
        
        var eventType = parser.next()
        while (eventType != XmlPullParser.END_DOCUMENT && depth > 0) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "array") {
                        depth++
                    }
                    // Parse array element
                    val element = parseElement(parser)
                    array.add(element)
                }
                
                XmlPullParser.END_TAG -> {
                    if (parser.name == "array") {
                        depth--
                    }
                }
            }
            
            if (depth > 0) {
                eventType = parser.next()
            }
        }
        
        return array
    }
    
    /**
     * Parse LLSD map
     */
    @Throws(Exception::class)
    private fun parseMap(parser: XmlPullParser): LLSD {
        val map = LLSD.emptyMap()
        var depth = 1  // We're inside <map>
        var currentKey: String? = null
        
        var eventType = parser.next()
        while (eventType != XmlPullParser.END_DOCUMENT && depth > 0) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "map" -> depth++
                        
                        "key" -> {
                            currentKey = parser.nextText()
                        }
                        
                        else -> {
                            // Parse map value
                            if (currentKey != null) {
                                val value = parseElement(parser)
                                map[currentKey] = value
                                currentKey = null
                            }
                        }
                    }
                }
                
                XmlPullParser.END_TAG -> {
                    if (parser.name == "map") {
                        depth--
                    }
                }
            }
            
            if (depth > 0) {
                eventType = parser.next()
            }
        }
        
        return map
    }
    
    /**
     * Skip an XML element and all its children
     */
    @Throws(Exception::class)
    private fun skipElement(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        
        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> depth++
                XmlPullParser.END_TAG -> depth--
            }
        }
    }
}
