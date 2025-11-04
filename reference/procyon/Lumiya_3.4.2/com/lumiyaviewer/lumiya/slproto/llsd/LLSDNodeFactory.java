// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import org.xmlpull.v1.XmlPullParser;
import java.util.HashMap;
import java.util.Map;

public class LLSDNodeFactory
{
    private static LLSDNodeConstructor createArray;
    private static LLSDNodeConstructor createBinary;
    private static LLSDNodeConstructor createBoolean;
    private static LLSDNodeConstructor createDate;
    private static LLSDNodeConstructor createDouble;
    private static LLSDNodeConstructor createInt;
    private static LLSDNodeConstructor createMap;
    private static LLSDNodeConstructor createString;
    private static LLSDNodeConstructor createURI;
    private static LLSDNodeConstructor createUUID;
    private static LLSDNodeConstructor createUndef;
    private static Map<String, LLSDNodeConstructor> tagMap;
    
    static {
        LLSDNodeFactory.tagMap = new HashMap<String, LLSDNodeConstructor>(22);
        LLSDNodeFactory.createUndef = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                xmlPullParser.nextTag();
                return new LLSDUndefined();
            }
        };
        LLSDNodeFactory.createBoolean = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDBoolean(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createInt = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDInt(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createDouble = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDDouble(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createUUID = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDUUID(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createString = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDString(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createDate = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDDate(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createURI = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDURI(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createBinary = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
                return new LLSDBinary(xmlPullParser.nextText());
            }
        };
        LLSDNodeFactory.createArray = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
                return new LLSDArray(xmlPullParser);
            }
        };
        LLSDNodeFactory.createMap = (LLSDNodeConstructor)new LLSDNodeConstructor() {
            @Override
            public LLSDNode createNodeFromXML(final XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
                return new LLSDMap(xmlPullParser);
            }
        };
        LLSDNodeFactory.tagMap.put("undef", LLSDNodeFactory.createUndef);
        LLSDNodeFactory.tagMap.put("boolean", LLSDNodeFactory.createBoolean);
        LLSDNodeFactory.tagMap.put("integer", LLSDNodeFactory.createInt);
        LLSDNodeFactory.tagMap.put("real", LLSDNodeFactory.createDouble);
        LLSDNodeFactory.tagMap.put("uuid", LLSDNodeFactory.createUUID);
        LLSDNodeFactory.tagMap.put("string", LLSDNodeFactory.createString);
        LLSDNodeFactory.tagMap.put("date", LLSDNodeFactory.createDate);
        LLSDNodeFactory.tagMap.put("uri", LLSDNodeFactory.createURI);
        LLSDNodeFactory.tagMap.put("binary", LLSDNodeFactory.createBinary);
        LLSDNodeFactory.tagMap.put("array", LLSDNodeFactory.createArray);
        LLSDNodeFactory.tagMap.put("map", LLSDNodeFactory.createMap);
    }
    
    public static LLSDNode parseNode(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        final String name = xmlPullParser.getName();
        final LLSDNodeConstructor llsdNodeConstructor = LLSDNodeFactory.tagMap.get(name);
        if (llsdNodeConstructor == null) {
            throw new LLSDXMLException("Invalid tag name: " + name);
        }
        return llsdNodeConstructor.createNodeFromXML(xmlPullParser);
    }
    
    private interface LLSDNodeConstructor
    {
        LLSDNode createNodeFromXML(final XmlPullParser p0) throws LLSDXMLException, XmlPullParserException, IOException;
    }
}
