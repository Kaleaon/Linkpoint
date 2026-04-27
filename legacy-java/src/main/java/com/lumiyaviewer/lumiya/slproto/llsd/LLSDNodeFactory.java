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

/* loaded from: classes.dex */
public class LLSDNodeFactory {
    private static Map<String, LLSDNodeConstructor> tagMap = new HashMap(22);
    private static LLSDNodeConstructor createUndef = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.1
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            xmlPullParser.nextTag();
            return new LLSDUndefined();
        }
    };
    private static LLSDNodeConstructor createBoolean = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.2
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDBoolean(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createInt = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.3
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDInt(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createDouble = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.4
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDDouble(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createUUID = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.5
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDUUID(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createString = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.6
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDString(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createDate = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.7
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDDate(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createURI = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.8
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDURI(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createBinary = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.9
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            return new LLSDBinary(xmlPullParser.nextText());
        }
    };
    private static LLSDNodeConstructor createArray = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.10
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return new LLSDArray(xmlPullParser);
        }
    };
    private static LLSDNodeConstructor createMap = new LLSDNodeConstructor() { // from class: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.11
        @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory.LLSDNodeConstructor
        public LLSDNode createNodeFromXML(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
            return new LLSDMap(xmlPullParser);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface LLSDNodeConstructor {
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
        if (lLSDNodeConstructor == null) {
            throw new LLSDXMLException("Invalid tag name: " + name);
        }
        return lLSDNodeConstructor.createNodeFromXML(xmlPullParser);
    }
}
