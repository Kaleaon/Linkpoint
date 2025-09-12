// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDXMLException, LLSDNode

public class LLSDNodeFactory
{
    private static interface LLSDNodeConstructor
    {

        public abstract LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
            throws LLSDXMLException, XmlPullParserException, IOException;
    }


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
    private static Map tagMap;

    public LLSDNodeFactory()
    {
    }

    public static LLSDNode parseNode(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException, LLSDXMLException
    {
        String s = xmlpullparser.getName();
        LLSDNodeConstructor llsdnodeconstructor = (LLSDNodeConstructor)tagMap.get(s);
        if (llsdnodeconstructor == null)
        {
            throw new LLSDXMLException((new StringBuilder()).append("Invalid tag name: ").append(s).toString());
        } else
        {
            return llsdnodeconstructor.createNodeFromXML(xmlpullparser);
        }
    }

    static 
    {
        tagMap = new HashMap(22);
        createUndef = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                xmlpullparser.nextTag();
                return new LLSDUndefined();
            }

        };
        createBoolean = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDBoolean(xmlpullparser.nextText());
            }

        };
        createInt = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDInt(xmlpullparser.nextText());
            }

        };
        createDouble = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDDouble(xmlpullparser.nextText());
            }

        };
        createUUID = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDUUID(xmlpullparser.nextText());
            }

        };
        createString = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDString(xmlpullparser.nextText());
            }

        };
        createDate = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDDate(xmlpullparser.nextText());
            }

        };
        createURI = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDURI(xmlpullparser.nextText());
            }

        };
        createBinary = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws XmlPullParserException, IOException
            {
                return new LLSDBinary(xmlpullparser.nextText());
            }

        };
        createArray = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws LLSDXMLException, XmlPullParserException, IOException
            {
                return new LLSDArray(xmlpullparser);
            }

        };
        createMap = new LLSDNodeConstructor() {

            public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
                throws LLSDXMLException, XmlPullParserException, IOException
            {
                return new LLSDMap(xmlpullparser);
            }

        };
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
}
