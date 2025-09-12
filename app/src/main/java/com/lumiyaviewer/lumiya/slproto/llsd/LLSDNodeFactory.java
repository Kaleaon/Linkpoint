package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LLSDNodeFactory {
    private static LLSDNodeConstructor createArray = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return new LLSDArray(xmlPullParser);
        }
    };
    private static LLSDNodeConstructor createBinary = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDBinary(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createBoolean = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDBoolean(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createDate = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDDate(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createDouble = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDDouble(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createInt = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDInt(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createMap = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return new LLSDMap(xmlPullParser);
        }
    };
    private static LLSDNodeConstructor createString = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDString(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createURI = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDURI(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createUUID = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDUUID(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createUndef = new LLSDNodeConstructor() {
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            xmlPullParser.nextTag();
            return new LLSDUndefined();
        }
    };
    private static Map<String, LLSDNodeConstructor> tagMap = new HashMap(22);

    private interface LLSDNodeConstructor {
        LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException;
    }

    static {
        tagMap.put("undef", createUndef);
        tagMap.put("boolean", createBoolean);
        tagMap.put("integer", createInt);
        tagMap.put("real", createDouble);
        tagMap.put("uuid", createUUID);
        tagMap.put("string", createString);
        tagMap.put("date", createDate);
        tagMap.put("uri", createURI);
        tagMap.put("binary", createBinary);
        tagMap.put("array", createArray);
        tagMap.put("map", createMap);
    }

    public static LLSDNode parseNode(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        String name = xmlPullParser.getName();
        LLSDNodeConstructor lLSDNodeConstructor = tagMap.get(name);
        if (lLSDNodeConstructor != null) {
            return lLSDNodeConstructor.createNodeFromXML(xmlPullParser);
        }
        throw new LLSDXMLException("Invalid tag name: " + name);
    }
}
