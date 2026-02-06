package com.lumiyaviewer.lumiya.slproto.llsd

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
import java.io.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

object LLSDNodeFactory {
    private val tagMap: Map<String, LLSDNodeConstructor> = mapOf(
        "undef" to LLSDNodeConstructor { parser ->
            parser.nextTag()
            LLSDUndefined()
        },
        "boolean" to LLSDNodeConstructor { parser -> LLSDBoolean(parser.nextText()) },
        "integer" to LLSDNodeConstructor { parser -> LLSDInt(parser.nextText()) },
        "real" to LLSDNodeConstructor { parser -> LLSDDouble(parser.nextText()) },
        "uuid" to LLSDNodeConstructor { parser -> LLSDUUID(parser.nextText()) },
        "string" to LLSDNodeConstructor { parser -> LLSDString(parser.nextText()) },
        "date" to LLSDNodeConstructor { parser -> LLSDDate(parser.nextText()) },
        "uri" to LLSDNodeConstructor { parser -> LLSDURI(parser.nextText()) },
        "binary" to LLSDNodeConstructor { parser -> LLSDBinary(parser.nextText()) },
        "array" to LLSDNodeConstructor { parser -> LLSDArray(parser) },
        "map" to LLSDNodeConstructor { parser -> LLSDMap(parser) }
    )

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    fun parseNode(parser: XmlPullParser): LLSDNode {
        val name = parser.name
        val constructor = tagMap[name]
            ?: throw LLSDXMLException("Invalid tag name: $name")
        return constructor.createNodeFromXML(parser)
    }

    fun interface LLSDNodeConstructor {
        @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(parser: XmlPullParser): LLSDNode
    }
}
