// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDNodeType, LLSDXMLException, LLSDValueTypeException, LLSDNode

public class LLSDStreamingParser
{
    public static interface LLSDContentHandler
    {

        public abstract LLSDContentHandler onArrayBegin(String s)
            throws LLSDXMLException;

        public abstract void onArrayEnd(String s)
            throws LLSDXMLException;

        public abstract LLSDContentHandler onMapBegin(String s)
            throws LLSDXMLException;

        public abstract void onMapEnd(String s)
            throws LLSDXMLException, InterruptedException;

        public abstract void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException;
    }

    public static class LLSDDefaultContentHandler
        implements LLSDContentHandler
    {

        public LLSDContentHandler onArrayBegin(String s)
            throws LLSDXMLException
        {
            return new LLSDDefaultContentHandler();
        }

        public void onArrayEnd(String s)
            throws LLSDXMLException
        {
        }

        public LLSDContentHandler onMapBegin(String s)
            throws LLSDXMLException
        {
            return new LLSDDefaultContentHandler();
        }

        public void onMapEnd(String s)
            throws LLSDXMLException, InterruptedException
        {
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
        }

        public LLSDDefaultContentHandler()
        {
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues[];
    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues[];

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues = ai;
        return ai;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues;
        }
        int ai[] = new int[LLSDNodeType.values().length];
        try
        {
            ai[LLSDNodeType.llsdArray.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror12) { }
        try
        {
            ai[LLSDNodeType.llsdBinary.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror11) { }
        try
        {
            ai[LLSDNodeType.llsdBoolean.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror10) { }
        try
        {
            ai[LLSDNodeType.llsdDate.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[LLSDNodeType.llsdDouble.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[LLSDNodeType.llsdInteger.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[LLSDNodeType.llsdKey.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[LLSDNodeType.llsdMap.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[LLSDNodeType.llsdRoot.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[LLSDNodeType.llsdString.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[LLSDNodeType.llsdURI.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[LLSDNodeType.llsdUUID.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[LLSDNodeType.llsdUndef.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues = ai;
        return ai;
    }

    public LLSDStreamingParser()
    {
    }

    public static void parseAny(InputStream inputstream, String s, LLSDContentHandler llsdcontenthandler)
        throws LLSDXMLException
    {
        inputstream = new BufferedInputStream(inputstream, 0x10000);
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(inputstream, s).ordinal()];
        JVM INSTR tableswitch 1 2: default 84
    //                   1 44
    //                   2 76;
           goto _L1 _L2 _L3
_L2:
        parseBinary(new DataInputStream(inputstream), llsdcontenthandler);
        return;
_L3:
        try
        {
            parseXML(inputstream, "UTF-8", llsdcontenthandler);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            s = new LLSDXMLException("I/O error");
            s.initCause(inputstream);
            throw s;
        }
_L1:
    }

    public static void parseBinary(DataInputStream datainputstream, LLSDContentHandler llsdcontenthandler)
        throws LLSDXMLException
    {
        try
        {
            parseBinaryNode(1, null, datainputstream, llsdcontenthandler);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (DataInputStream datainputstream)
        {
            llsdcontenthandler = new LLSDXMLException("Invalid value type");
        }
        // Misplaced declaration of an exception variable
        catch (DataInputStream datainputstream)
        {
            llsdcontenthandler = new LLSDXMLException("Interrupted");
            llsdcontenthandler.initCause(datainputstream);
            throw llsdcontenthandler;
        }
        // Misplaced declaration of an exception variable
        catch (DataInputStream datainputstream)
        {
            llsdcontenthandler = new LLSDXMLException("I/O error");
            llsdcontenthandler.initCause(datainputstream);
            throw llsdcontenthandler;
        }
        llsdcontenthandler.initCause(datainputstream);
        throw llsdcontenthandler;
    }

    private static void parseBinaryNode(int i, String s, DataInputStream datainputstream, LLSDContentHandler llsdcontenthandler)
        throws LLSDXMLException, LLSDValueTypeException, InterruptedException, IOException
    {
        do
        {
            if (i <= 0)
            {
                break;
            }
            byte byte0 = datainputstream.readByte();
            switch (byte0)
            {
            default:
                throw new LLSDXMLException((new StringBuilder()).append("Unknown LLSD element 0x").append(Integer.toHexString(byte0)).toString());

            case 33: // '!'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDUndefined());
                i--;
                break;

            case 49: // '1'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDBoolean(true));
                i--;
                break;

            case 48: // '0'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDBoolean(false));
                i--;
                break;

            case 105: // 'i'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDInt(datainputstream.readInt()));
                i--;
                break;

            case 114: // 'r'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDDouble(datainputstream.readDouble()));
                i--;
                break;

            case 117: // 'u'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDUUID(new UUID(datainputstream.readLong(), datainputstream.readLong())));
                i--;
                break;

            case 98: // 'b'
                byte abyte0[] = new byte[datainputstream.readInt()];
                datainputstream.readFully(abyte0);
                llsdcontenthandler.onPrimitiveValue(s, new LLSDBinary(abyte0));
                i--;
                break;

            case 115: // 's'
                int j = datainputstream.readInt();
                if (j == 0)
                {
                    llsdcontenthandler.onPrimitiveValue(s, new LLSDString(""));
                } else
                {
                    byte abyte1[] = new byte[j];
                    datainputstream.readFully(abyte1);
                    llsdcontenthandler.onPrimitiveValue(s, new LLSDString(SLMessage.stringFromVariableUTF(abyte1)));
                }
                i--;
                break;

            case 108: // 'l'
                int k = datainputstream.readInt();
                if (k == 0)
                {
                    llsdcontenthandler.onPrimitiveValue(s, new LLSDURI(""));
                } else
                {
                    byte abyte2[] = new byte[k];
                    datainputstream.readFully(abyte2);
                    llsdcontenthandler.onPrimitiveValue(s, new LLSDURI(SLMessage.stringFromVariableUTF(abyte2)));
                }
                i--;
                break;

            case 100: // 'd'
                llsdcontenthandler.onPrimitiveValue(s, new LLSDDate(new Date(Math.round(datainputstream.readDouble() * 1000D))));
                i--;
                break;

            case 91: // '['
                int l = datainputstream.readInt();
                LLSDContentHandler llsdcontenthandler3 = llsdcontenthandler.onArrayBegin(s);
                LLSDContentHandler llsdcontenthandler1 = llsdcontenthandler3;
                if (llsdcontenthandler3 == null)
                {
                    llsdcontenthandler1 = llsdcontenthandler;
                }
                parseBinaryNode(l, null, datainputstream, llsdcontenthandler1);
                if (datainputstream.readByte() != 93)
                {
                    throw new LLSDXMLException("Array terminator expected");
                }
                llsdcontenthandler1.onMapEnd(s);
                i--;
                break;

            case 123: // '{'
                int j1 = datainputstream.readInt();
                LLSDContentHandler llsdcontenthandler4 = llsdcontenthandler.onMapBegin(s);
                LLSDContentHandler llsdcontenthandler2 = llsdcontenthandler4;
                if (llsdcontenthandler4 == null)
                {
                    llsdcontenthandler2 = llsdcontenthandler;
                }
                for (int i1 = 0; i1 < j1; i1++)
                {
                    if (datainputstream.readByte() != 107)
                    {
                        throw new LLSDXMLException("Map key expected");
                    }
                    byte abyte3[] = new byte[datainputstream.readInt()];
                    datainputstream.readFully(abyte3);
                    parseBinaryNode(1, SLMessage.stringFromVariableUTF(abyte3), datainputstream, llsdcontenthandler2);
                }

                if (datainputstream.readByte() != 125)
                {
                    throw new LLSDXMLException("Map terminator expected");
                }
                llsdcontenthandler2.onMapEnd(s);
                i--;
                break;

            case 60: // '<'
                while (datainputstream.readByte() != 62) ;
                break;

            case 10: // '\n'
                break;
            }
        } while (true);
    }

    public static void parseXML(InputStream inputstream, String s, LLSDContentHandler llsdcontenthandler)
        throws LLSDXMLException
    {
        try
        {
            XmlPullParser xmlpullparser = XmlPullParserFactory.newInstance().newPullParser();
            xmlpullparser.setInput(inputstream, s);
            xmlpullparser.nextTag();
            xmlpullparser.require(2, null, "llsd");
            xmlpullparser.nextTag();
            parseXMLNode(null, xmlpullparser, llsdcontenthandler);
            xmlpullparser.require(3, null, "llsd");
            return;
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            Debug.Log((new StringBuilder()).append("XmlPullParserException: ").append(inputstream.getMessage()).toString());
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            throw new LLSDXMLException("Input stream error");
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            inputstream.printStackTrace();
            s = new LLSDXMLException("Malformed XML");
            s.initCause(inputstream);
            throw s;
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            inputstream.printStackTrace();
            s = new LLSDXMLException("Interrupted");
            s.initCause(inputstream);
            throw s;
        }
        inputstream.printStackTrace();
        s = new LLSDXMLException("Malformed XML");
        s.initCause(inputstream);
        throw s;
    }

    private static void parseXMLNode(String s, XmlPullParser xmlpullparser, LLSDContentHandler llsdcontenthandler)
        throws LLSDXMLException, XmlPullParserException, IOException, LLSDValueTypeException, InterruptedException
    {
        Object obj = xmlpullparser.getName();
        LLSDNodeType llsdnodetype = LLSDNodeType.byTag(((String) (obj)));
        if (llsdnodetype == null)
        {
            throw new LLSDXMLException((new StringBuilder()).append("Unknown tag: ").append(((String) (obj))).toString());
        }
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_llsd_2D_LLSDNodeTypeSwitchesValues()[llsdnodetype.ordinal()])
        {
        default:
            return;

        case 6: // '\006'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDInt(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 3: // '\003'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDBoolean(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 10: // '\n'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDString(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 5: // '\005'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDDouble(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 12: // '\f'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDUUID(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 2: // '\002'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDBinary(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 4: // '\004'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDDate(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 11: // '\013'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDURI(xmlpullparser.nextText()));
            xmlpullparser.nextTag();
            return;

        case 13: // '\r'
            llsdcontenthandler.onPrimitiveValue(s, new LLSDUndefined());
            xmlpullparser.nextTag();
            return;

        case 9: // '\t'
            throw new LLSDXMLException((new StringBuilder()).append("Unexpected tag: ").append(((String) (obj))).toString());

        case 1: // '\001'
            obj = llsdcontenthandler.onArrayBegin(s);
            xmlpullparser.nextTag();
            if (obj != null)
            {
                llsdcontenthandler = ((LLSDContentHandler) (obj));
            }
            for (; xmlpullparser.getEventType() != 3; parseXMLNode(null, xmlpullparser, llsdcontenthandler)) { }
            llsdcontenthandler.onArrayEnd(s);
            xmlpullparser.nextTag();
            return;

        case 8: // '\b'
            obj = llsdcontenthandler.onMapBegin(s);
            xmlpullparser.nextTag();
            if (obj != null)
            {
                llsdcontenthandler = ((LLSDContentHandler) (obj));
            }
            for (; xmlpullparser.getEventType() != 3; parseXMLNode(((String) (obj)), xmlpullparser, llsdcontenthandler))
            {
                obj = xmlpullparser.getName();
                if (!((String) (obj)).equalsIgnoreCase("key"))
                {
                    throw new LLSDXMLException((new StringBuilder()).append("Unexpected tag: ").append(((String) (obj))).toString());
                }
                obj = xmlpullparser.nextText();
                xmlpullparser.nextTag();
            }

            llsdcontenthandler.onMapEnd(s);
            xmlpullparser.nextTag();
            return;

        case 7: // '\007'
            throw new LLSDXMLException((new StringBuilder()).append("Unexpected tag: ").append(((String) (obj))).toString());
        }
    }
}
