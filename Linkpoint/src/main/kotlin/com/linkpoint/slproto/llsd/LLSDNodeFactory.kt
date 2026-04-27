package com.linkpoint.slproto.llsd

import com.linkpoint.slproto.llsd.types.LLSDArray
import com.linkpoint.slproto.llsd.types.LLSDBinary
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDDate
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDURI
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import java.io.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

object LLSDNodeFactory {

    private interface Builder {
        @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
        fun fromXml(parser: XmlPullParser): LLSDNode
    }

    private val constructors: Map<String, Builder> = mapOf(
        "undef" to Builder { parser ->
            parser.nextTag()
            LLSDUndefined()
        },
        "boolean" to Builder { parser -> LLSDBoolean(parser.nextText()) },
        "integer" to Builder { parser -> LLSDInt(parser.nextText()) },
        "real" to Builder { parser -> LLSDDouble(parser.nextText()) },
        "uuid" to Builder { parser -> LLSDUUID(parser.nextText()) },
        "string" to Builder { parser -> LLSDString(parser.nextText()) },
        "date" to Builder { parser -> LLSDDate(parser.nextText()) },
        "uri" to Builder { parser -> LLSDURI(parser.nextText()) },
        "binary" to Builder { parser -> LLSDBinary(parser.nextText()) },
        "array" to Builder { parser -> LLSDArray(parser) },
        "map" to Builder { parser -> LLSDMap(parser) }
    )

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    fun parseNode(parser: XmlPullParser): LLSDNode {
        val name = parser.name
        val builder = constructors[name] ?: throw LLSDXMLException("Invalid tag name: $name")
        return builder.fromXml(parser)
    }
}
