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
    @JvmStatic
private LLSDNodeConstructor createArray = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return LLSDArray(xmlPullParser)
        }
    }
    @JvmStatic
private LLSDNodeConstructor createBinary = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDBinary(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createBoolean = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDBoolean(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createDate = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDDate(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createDouble = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDDouble(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createInt = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDInt(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createMap = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return LLSDMap(xmlPullParser)
        }
    }
    @JvmStatic
private LLSDNodeConstructor createString = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDString(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createURI = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDURI(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createUUID = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            return LLSDUUID(xmlPullParser.nextText())
        }
    }
    @JvmStatic
private LLSDNodeConstructor createUndef = LLSDNodeConstructor() {
         public fun createNodeFromXML(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
            xmlPullParser.nextTag()
            return LLSDUndefined()
        }
    }
    @JvmStatic
private Map<String, LLSDNodeConstructor> tagMap = HashMap(22)

    private interface LLSDNodeConstructor {
         fun createNodeFromXML(XmlPullParser xmlPullParser): LLSDNode) throws LLSDXMLException, XmlPullParserException, IOException
    }

    static {
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

    @JvmStatic
     fun parseNode(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        val name: String = xmlPullParser.getName()
        val lLSDNodeConstructor: LLSDNodeConstructor = tagMap.get(name)
        if (lLSDNodeConstructor != null) {
            return lLSDNodeConstructor.createNodeFromXML(xmlPullParser)
        }
        throw LLSDXMLException("Invalid tag name: " + name)
    }
}
