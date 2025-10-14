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
import java.util.HashMap
import java.util.Map
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class LLSDNodeFactory {
    private LLSDNodeConstructor createArray = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return LLSDArray(xmlPullParser)
        }
    }
    private LLSDNodeConstructor createBinary = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDBinary(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createBoolean = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDBoolean(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createDate = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDDate(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createDouble = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDDouble(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createInt = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDInt(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createMap = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return LLSDMap(xmlPullParser)
        }
    }
    private LLSDNodeConstructor createString = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDString(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createURI = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDURI(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createUUID = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return LLSDUUID(xmlPullParser.nextText())
        }
    }
    private LLSDNodeConstructor createUndef = LLSDNodeConstructor() {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            xmlPullParser.nextTag()
            return LLSDUndefined()
        }
    }
    private Map<String, LLSDNodeConstructor> tagMap = HashMap(22)

    private interface LLSDNodeConstructor {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException
    }

    {
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

    LLSDNode parseNode(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        String name = xmlPullParser.getName()
        LLSDNodeConstructor lLSDNodeConstructor = tagMap.get(name)
        if (lLSDNodeConstructor != null) {
            return lLSDNodeConstructor.createNodeFromXML(xmlPullParser)
        }
        throw LLSDXMLException("Invalid tag name: " + name)
    }
}
