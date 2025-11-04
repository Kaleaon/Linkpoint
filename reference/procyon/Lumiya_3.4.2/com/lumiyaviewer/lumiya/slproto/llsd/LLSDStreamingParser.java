// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.lumiyaviewer.lumiya.Debug;
import org.xmlpull.v1.XmlPullParserFactory;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate;
import java.util.Date;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector;

public class LLSDStreamingParser
{
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (LLSDStreamingParser.-com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return LLSDStreamingParser.-com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues = new int[LLSDContentTypeDetector.LLSDContentType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2;
                    return -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues;
                }
                catch (final NoSuchFieldError noSuchFieldError) {}
            }
            catch (final NoSuchFieldError noSuchFieldError2) {
                continue;
            }
            break;
        }
    }
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues() {
        if (LLSDStreamingParser.-com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues != null) {
            return LLSDStreamingParser.-com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues = new int[LLSDNodeType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdArray.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdBinary.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdBoolean.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdDate.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdDouble.ordinal()] = 5;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdInteger.ordinal()] = 6;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdKey.ordinal()] = 7;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdMap.ordinal()] = 8;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdRoot.ordinal()] = 9;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdString.ordinal()] = 10;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdURI.ordinal()] = 11;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdUUID.ordinal()] = 12;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues[LLSDNodeType.llsdUndef.ordinal()] = 13;
                                                                return -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues;
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError2) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError3) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError4) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError5) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError6) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError7) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError8) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError9) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError10) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError11) {}
                }
                catch (final NoSuchFieldError noSuchFieldError12) {}
            }
            catch (final NoSuchFieldError noSuchFieldError13) {
                continue;
            }
            break;
        }
    }
    
    public static void parseAny(final InputStream in, final String s, final LLSDContentHandler llsdContentHandler) throws LLSDXMLException {
        while (true) {
            BufferedInputStream in2 = null;
            Label_0081: {
                try {
                    in2 = new BufferedInputStream(in, 65536);
                    switch (-getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(in2, s).ordinal()]) {
                        case 1: {
                            parseBinary(new DataInputStream(in2), llsdContentHandler);
                            break;
                        }
                        case 2: {
                            break Label_0081;
                        }
                    }
                    return;
                }
                catch (final IOException cause) {
                    final LLSDXMLException ex = new LLSDXMLException("I/O error");
                    ex.initCause(cause);
                    throw ex;
                }
            }
            parseXML(in2, "UTF-8", llsdContentHandler);
        }
    }
    
    public static void parseBinary(final DataInputStream dataInputStream, final LLSDContentHandler llsdContentHandler) throws LLSDXMLException {
        try {
            parseBinaryNode(1, null, dataInputStream, llsdContentHandler);
        }
        catch (final IOException cause) {
            final LLSDXMLException ex = new LLSDXMLException("I/O error");
            ex.initCause(cause);
            throw ex;
        }
        catch (final InterruptedException cause2) {
            final LLSDXMLException ex2 = new LLSDXMLException("Interrupted");
            ex2.initCause(cause2);
            throw ex2;
        }
        catch (final LLSDValueTypeException cause3) {
            final LLSDXMLException ex3 = new LLSDXMLException("Invalid value type");
            ex3.initCause(cause3);
            throw ex3;
        }
    }
    
    private static void parseBinaryNode(int i, final String s, final DataInputStream dataInputStream, final LLSDContentHandler llsdContentHandler) throws LLSDXMLException, LLSDValueTypeException, InterruptedException, IOException {
        while (i > 0) {
            final byte byte1 = dataInputStream.readByte();
            switch (byte1) {
                default: {
                    throw new LLSDXMLException("Unknown LLSD element 0x" + Integer.toHexString(byte1));
                }
                case 33: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDUndefined());
                    --i;
                    continue;
                }
                case 49: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDBoolean(true));
                    --i;
                    continue;
                }
                case 48: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDBoolean(false));
                    --i;
                    continue;
                }
                case 105: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDInt(dataInputStream.readInt()));
                    --i;
                    continue;
                }
                case 114: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDDouble(dataInputStream.readDouble()));
                    --i;
                    continue;
                }
                case 117: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDUUID(new UUID(dataInputStream.readLong(), dataInputStream.readLong())));
                    --i;
                    continue;
                }
                case 98: {
                    final byte[] b = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(b);
                    llsdContentHandler.onPrimitiveValue(s, new LLSDBinary(b));
                    --i;
                    continue;
                }
                case 115: {
                    final int int1 = dataInputStream.readInt();
                    if (int1 == 0) {
                        llsdContentHandler.onPrimitiveValue(s, new LLSDString(""));
                    }
                    else {
                        final byte[] b2 = new byte[int1];
                        dataInputStream.readFully(b2);
                        llsdContentHandler.onPrimitiveValue(s, new LLSDString(SLMessage.stringFromVariableUTF(b2)));
                    }
                    --i;
                    continue;
                }
                case 108: {
                    final int int2 = dataInputStream.readInt();
                    if (int2 == 0) {
                        llsdContentHandler.onPrimitiveValue(s, new LLSDURI(""));
                    }
                    else {
                        final byte[] b3 = new byte[int2];
                        dataInputStream.readFully(b3);
                        llsdContentHandler.onPrimitiveValue(s, new LLSDURI(SLMessage.stringFromVariableUTF(b3)));
                    }
                    --i;
                    continue;
                }
                case 100: {
                    llsdContentHandler.onPrimitiveValue(s, new LLSDDate(new Date(Math.round(dataInputStream.readDouble() * 1000.0))));
                    --i;
                    continue;
                }
                case 91: {
                    final int int3 = dataInputStream.readInt();
                    LLSDContentHandler onArrayBegin;
                    if ((onArrayBegin = llsdContentHandler.onArrayBegin(s)) == null) {
                        onArrayBegin = llsdContentHandler;
                    }
                    parseBinaryNode(int3, null, dataInputStream, onArrayBegin);
                    if (dataInputStream.readByte() != 93) {
                        throw new LLSDXMLException("Array terminator expected");
                    }
                    onArrayBegin.onMapEnd(s);
                    --i;
                    continue;
                }
                case 123: {
                    final int int4 = dataInputStream.readInt();
                    LLSDContentHandler onMapBegin;
                    if ((onMapBegin = llsdContentHandler.onMapBegin(s)) == null) {
                        onMapBegin = llsdContentHandler;
                    }
                    for (int j = 0; j < int4; ++j) {
                        if (dataInputStream.readByte() != 107) {
                            throw new LLSDXMLException("Map key expected");
                        }
                        final byte[] b4 = new byte[dataInputStream.readInt()];
                        dataInputStream.readFully(b4);
                        parseBinaryNode(1, SLMessage.stringFromVariableUTF(b4), dataInputStream, onMapBegin);
                    }
                    if (dataInputStream.readByte() != 125) {
                        throw new LLSDXMLException("Map terminator expected");
                    }
                    onMapBegin.onMapEnd(s);
                    --i;
                    continue;
                }
                case 60: {
                    while (dataInputStream.readByte() != 62) {}
                    continue;
                }
                case 10: {
                    continue;
                }
            }
        }
    }
    
    public static void parseXML(final InputStream inputStream, final String s, final LLSDContentHandler llsdContentHandler) throws LLSDXMLException {
        try {
            final XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
            pullParser.setInput(inputStream, s);
            pullParser.nextTag();
            pullParser.require(2, (String)null, "llsd");
            pullParser.nextTag();
            parseXMLNode(null, pullParser, llsdContentHandler);
            pullParser.require(3, (String)null, "llsd");
        }
        catch (final InterruptedException cause) {
            cause.printStackTrace();
            final LLSDXMLException ex = new LLSDXMLException("Interrupted");
            ex.initCause(cause);
            throw ex;
        }
        catch (final LLSDValueTypeException cause2) {
            cause2.printStackTrace();
            final LLSDXMLException ex2 = new LLSDXMLException("Malformed XML");
            ex2.initCause(cause2);
            throw ex2;
        }
        catch (final IOException ex3) {
            throw new LLSDXMLException("Input stream error");
        }
        catch (final XmlPullParserException cause3) {
            Debug.Log("XmlPullParserException: " + cause3.getMessage());
            cause3.printStackTrace();
            final LLSDXMLException ex4 = new LLSDXMLException("Malformed XML");
            ex4.initCause((Throwable)cause3);
            throw ex4;
        }
    }
    
    private static void parseXMLNode(final String s, final XmlPullParser xmlPullParser, LLSDContentHandler llsdContentHandler) throws LLSDXMLException, XmlPullParserException, IOException, LLSDValueTypeException, InterruptedException {
        final String name = xmlPullParser.getName();
        final LLSDNodeType byTag = LLSDNodeType.byTag(name);
        if (byTag == null) {
            throw new LLSDXMLException("Unknown tag: " + name);
        }
        switch (-getcom-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues()[byTag.ordinal()]) {
            case 6: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDInt(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 3: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDBoolean(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 10: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDString(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 5: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDDouble(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 12: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDUUID(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 2: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDBinary(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 4: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDDate(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 11: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDURI(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                break;
            }
            case 13: {
                llsdContentHandler.onPrimitiveValue(s, new LLSDUndefined());
                xmlPullParser.nextTag();
                break;
            }
            case 9: {
                throw new LLSDXMLException("Unexpected tag: " + name);
            }
            case 1: {
                final LLSDContentHandler onArrayBegin = llsdContentHandler.onArrayBegin(s);
                xmlPullParser.nextTag();
                if (onArrayBegin != null) {
                    llsdContentHandler = onArrayBegin;
                }
                while (xmlPullParser.getEventType() != 3) {
                    parseXMLNode(null, xmlPullParser, llsdContentHandler);
                }
                llsdContentHandler.onArrayEnd(s);
                xmlPullParser.nextTag();
                break;
            }
            case 8: {
                final LLSDContentHandler onMapBegin = llsdContentHandler.onMapBegin(s);
                xmlPullParser.nextTag();
                if (onMapBegin != null) {
                    llsdContentHandler = onMapBegin;
                }
                while (xmlPullParser.getEventType() != 3) {
                    final String name2 = xmlPullParser.getName();
                    if (!name2.equalsIgnoreCase("key")) {
                        throw new LLSDXMLException("Unexpected tag: " + name2);
                    }
                    final String nextText = xmlPullParser.nextText();
                    xmlPullParser.nextTag();
                    parseXMLNode(nextText, xmlPullParser, llsdContentHandler);
                }
                llsdContentHandler.onMapEnd(s);
                xmlPullParser.nextTag();
                break;
            }
            case 7: {
                throw new LLSDXMLException("Unexpected tag: " + name);
            }
        }
    }
    
    public interface LLSDContentHandler
    {
        LLSDContentHandler onArrayBegin(final String p0) throws LLSDXMLException;
        
        void onArrayEnd(final String p0) throws LLSDXMLException;
        
        LLSDContentHandler onMapBegin(final String p0) throws LLSDXMLException;
        
        void onMapEnd(final String p0) throws LLSDXMLException, InterruptedException;
        
        void onPrimitiveValue(final String p0, final LLSDNode p1) throws LLSDXMLException, LLSDValueTypeException;
    }
    
    public static class LLSDDefaultContentHandler implements LLSDContentHandler
    {
        @Override
        public LLSDContentHandler onArrayBegin(final String s) throws LLSDXMLException {
            return new LLSDDefaultContentHandler();
        }
        
        @Override
        public void onArrayEnd(final String s) throws LLSDXMLException {
        }
        
        @Override
        public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
            return new LLSDDefaultContentHandler();
        }
        
        @Override
        public void onMapEnd(final String s) throws LLSDXMLException, InterruptedException {
        }
        
        @Override
        public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
        }
    }
}
