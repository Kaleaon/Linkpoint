package com.linkpoint.slproto.llsd

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * LLSD XML Parser
 * Handles parsing of LLSD XML format
 */
class LLSDXmlParser {
    private val parserFactory = XmlPullParserFactory.newInstance()
    
    fun parse(xml: String): LLSD {
        val parser = parserFactory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))
        
        return parseLLSD(parser)
    }
    
    private fun parseLLSD(parser: XmlPullParser): LLSD {
        var eventType = parser.eventType
        var llsdValue: LLSD? = null
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "llsd" -> {
                            // Root element, continue parsing children
                        }
                        "map" -> {
                            llsdValue = parseMap(parser)
                        }
                        "array" -> {
                            llsdValue = parseArray(parser)
                        }
                        "string" -> {
                            llsdValue = LLSDString(parser.nextText())
                        }
                        "integer" -> {
                            llsdValue = LLSDInteger(parser.nextText().toIntOrNull() ?: 0)
                        }
                        "real" -> {
                            llsdValue = LLSDReal(parser.nextText().toDoubleOrNull() ?: 0.0)
                        }
                        "boolean" -> {
                            val text = parser.nextText()
                            llsdValue = LLSDBoolean(text == "1" || text.equals("true", ignoreCase = true))
                        }
                        "uuid" -> {
                            llsdValue = LLSDUUID(UUID.fromString(parser.nextText()))
                        }
                        "binary" -> {
                            llsdValue = LLSDBinary(parser.nextText().toByteArray())
                        }
                        "date" -> {
                            llsdValue = LLSDDate(parser.nextText())
                        }
                        "uri" -> {
                            llsdValue = LLSDURI(parser.nextText())
                        }
                        "undef" -> {
                            llsdValue = LLSDUndefined
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        
        return llsdValue ?: LLSDUndefined
    }
    
    private fun parseMap(parser: XmlPullParser): LLSDMap {
        val map = mutableMapOf<String, LLSD>()
        var currentKey: String? = null
        
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "key" -> {
                            currentKey = parser.nextText()
                        }
                        else -> {
                            if (currentKey != null) {
                                val value = parseLLSDValue(parser, parser.name)
                                map[currentKey] = value
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "map") {
                        break
                    }
                }
            }
            eventType = parser.next()
        }
        
        return LLSDMap(map)
    }
    
    private fun parseArray(parser: XmlPullParser): LLSDArray {
        val array = mutableListOf<LLSD>()
        
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name != "array") {
                        val value = parseLLSDValue(parser, parser.name)
                        array.add(value)
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "array") {
                        break
                    }
                }
            }
            eventType = parser.next()
        }
        
        return LLSDArray(array)
    }
    
    private fun parseLLSDValue(parser: XmlPullParser, tagName: String): LLSD {
        return when (tagName) {
            "map" -> parseMap(parser)
            "array" -> parseArray(parser)
            "string" -> LLSDString(parser.nextText())
            "integer" -> LLSDInteger(parser.nextText().toIntOrNull() ?: 0)
            "real" -> LLSDReal(parser.nextText().toDoubleOrNull() ?: 0.0)
            "boolean" -> {
                val text = parser.nextText()
                LLSDBoolean(text == "1" || text.equals("true", ignoreCase = true))
            }
            "uuid" -> LLSDUUID(UUID.fromString(parser.nextText()))
            "binary" -> LLSDBinary(parser.nextText().toByteArray())
            "date" -> LLSDDate(parser.nextText())
            "uri" -> LLSDURI(parser.nextText())
            "undef" -> LLSDUndefined
            else -> LLSDUndefined
        }
    }
}