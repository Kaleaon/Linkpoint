package com.linkpoint.slproto.llsd

import android.util.Base64
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URI
import java.util.LinkedHashMap
import java.util.UUID

class LLSDXmlParser {
    private val factory = XmlPullParserFactory.newInstance()

    fun parse(xml: String): LLSD {
        val parser = factory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))

        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, "llsd")
        parser.nextTag()
        val value = parseValue(parser)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, "llsd")
        return value
    }

    private fun parseValue(parser: XmlPullParser): LLSD {
        parser.require(XmlPullParser.START_TAG, null, null)
        return when (parser.name) {
            "map" -> parseMap(parser)
            "array" -> parseArray(parser)
            "string" -> LLSDString(readText(parser))
            "integer" -> LLSDInteger(readText(parser).trim().toLongOrNull() ?: 0L)
            "real" -> LLSDReal(readText(parser).trim().toDoubleOrNull() ?: 0.0)
            "boolean" -> {
                val text = readText(parser).trim()
                LLSDBoolean(text == "1" || text.equals("true", ignoreCase = true))
            }
            "uuid" -> LLSDUUID(UUID.fromString(readText(parser).trim()))
            "binary" -> {
                val data = readText(parser).trim()
                if (data.isEmpty()) {
                    LLSDBinary(ByteArray(0))
                } else {
                    LLSDBinary(Base64.decode(data, Base64.DEFAULT))
                }
            }
            "date" -> LLSDDate(LLSD.parseDate(readText(parser)))
            "uri" -> LLSDURI(URI.create(readText(parser)))
            "undef" -> {
                consumeEndTag(parser, "undef")
                LLSDUndefined
            }
            else -> {
                skip(parser)
                LLSDUndefined
            }
        }
    }

    private fun parseMap(parser: XmlPullParser): LLSD {
        val map = LinkedHashMap<String, LLSD>()
        parser.next()
        while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == "map")) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "key") {
                val key = readText(parser)
                parser.nextTag()
                val value = parseValue(parser)
                map[key] = value
                parser.nextTag()
            } else {
                parser.next()
            }
        }
        return LLSDMap(map)
    }

    private fun parseArray(parser: XmlPullParser): LLSD {
        val list = mutableListOf<LLSD>()
        parser.next()
        while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == "array")) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                list += parseValue(parser)
                parser.nextTag()
            } else {
                parser.next()
            }
        }
        return LLSDArray(list)
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text ?: ""
            parser.nextTag()
        }
        return result
    }

    private fun skip(parser: XmlPullParser) {
        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> depth++
                XmlPullParser.END_TAG -> depth--
            }
        }
    }

    private fun consumeEndTag(parser: XmlPullParser, name: String) {
        if (parser.next() == XmlPullParser.END_TAG && parser.name == name) {
            return
        }
        while (!(parser.eventType == XmlPullParser.END_TAG && parser.name == name)) {
            parser.next()
        }
    }
}