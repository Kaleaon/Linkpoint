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
import java.util.HashMap
import java.util.Map
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class LLSDNodeFactory {
    private LLSDNodeConstructor createArray = LLSDNodeConstructor() {
        @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDArray(xmlPullParser)
        }
    }
    private LLSDNodeConstructor createBinary = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDBinary(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createBoolean = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDBoolean(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createDate = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDDate(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createDouble = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDDouble(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createInt = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDInt(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createMap = LLSDNodeConstructor() {
        @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDMap(xmlPullParser)
        }
    }
    private LLSDNodeConstructor createString = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDString(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createURI = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDURI(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createUUID = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            return LLSDUUID(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createUndef = LLSDNodeConstructor() {
        @Throws(XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
            xmlPullParser.nextTag()
            return LLSDUndefined()
        }
    }
    private Map<String, LLSDNodeConstructor> tagMap = HashMap(22)

    private interface LLSDNodeConstructor {
        @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException
    }::class)
        fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode {
        tagMap.put("undef", createUndef)
        tagMap.put("Boolean", createBoolean)
        tagMap.put("integer", createInt)
        tagMap.put("real", createDouble)
        tagMap.put("uuid", createUUID)
        tagMap.put("string", createString)
        tagMap.put("date", createDate)
        tagMap.put("uri", createURI)
        tagMap.put("binary", createBinary)
        tagMap.put("array", createArray)
        tagMap.put("map", createMap)
    }

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)

    fun parseNode(XmlPullParser xmlPullParser): LLSDNode {
        var name: String = xmlPullParser.getName()
        LLSDNodeConstructor lLSDNodeConstructor = tagMap.get(name)
        if (lLSDNodeConstructor != null) {
            return lLSDNodeConstructor.createNodeFromXML(xmlPullParser)
        }
        throw LLSDXMLException("Invalid tag name: " + name)
    }
}
