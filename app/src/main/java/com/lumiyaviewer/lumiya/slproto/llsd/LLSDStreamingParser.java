package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.Debug;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LLSDStreamingParser {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = null;

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues = null;

    public interface LLSDContentHandler {
        LLSDContentHandler onArrayBegin(String str) throws LLSDXMLException;

        void onArrayEnd(String str) throws LLSDXMLException;

        LLSDContentHandler onMapBegin(String str) throws LLSDXMLException;

        void onMapEnd(String str) throws LLSDXMLException, InterruptedException;

        void onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException;
    }

    public static class LLSDDefaultContentHandler implements LLSDContentHandler {
        public LLSDContentHandler onArrayBegin(String str) throws LLSDXMLException {
            return new LLSDDefaultContentHandler();
        }

        public void onArrayEnd(String str) throws LLSDXMLException {
        }

        public LLSDContentHandler onMapBegin(String str) throws LLSDXMLException {
            return new LLSDDefaultContentHandler();
        }

        public void onMapEnd(String str) throws LLSDXMLException, InterruptedException {
        }

        public void onPrimitiveValue(String str, LLSDNode lLSDNode) throws LLSDXMLException, LLSDValueTypeException {
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m196getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues;
        }
        int[] iArr = new int[LLSDContentTypeDetector.LLSDContentType.values().length];
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m197getcomlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues() {
        if (f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues != null) {
            return f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues;
        }
        int[] iArr = new int[LLSDNodeType.values().length];
        try {
            iArr[LLSDNodeType.llsdArray.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDNodeType.llsdBinary.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[LLSDNodeType.llsdBoolean.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[LLSDNodeType.llsdDate.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[LLSDNodeType.llsdDouble.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[LLSDNodeType.llsdInteger.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[LLSDNodeType.llsdKey.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[LLSDNodeType.llsdMap.ordinal()] = 8;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[LLSDNodeType.llsdRoot.ordinal()] = 9;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[LLSDNodeType.llsdString.ordinal()] = 10;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[LLSDNodeType.llsdURI.ordinal()] = 11;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[LLSDNodeType.llsdUUID.ordinal()] = 12;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[LLSDNodeType.llsdUndef.ordinal()] = 13;
        } catch (NoSuchFieldError e13) {
        }
        f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues = iArr;
        return iArr;
    }

    public static void parseAny(InputStream inputStream, String str, LLSDContentHandler lLSDContentHandler) throws LLSDXMLException {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 65536);
            switch (m196getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal()]) {
                case 1:
                    parseBinary(new DataInputStream(bufferedInputStream), lLSDContentHandler);
                    return;
                case 2:
                    parseXML(bufferedInputStream, "UTF-8", lLSDContentHandler);
                    return;
                default:
                    return;
            }
        } catch (IOException e) {
            LLSDXMLException lLSDXMLException = new LLSDXMLException("I/O error");
            lLSDXMLException.initCause(e);
            throw lLSDXMLException;
        }
        LLSDXMLException lLSDXMLException2 = new LLSDXMLException("I/O error");
        lLSDXMLException2.initCause(e);
        throw lLSDXMLException2;
    }

    public static void parseBinary(DataInputStream dataInputStream, LLSDContentHandler lLSDContentHandler) throws LLSDXMLException {
        try {
            parseBinaryNode(1, (String) null, dataInputStream, lLSDContentHandler);
        } catch (LLSDValueTypeException e) {
            LLSDXMLException lLSDXMLException = new LLSDXMLException("Invalid value type");
            lLSDXMLException.initCause(e);
            throw lLSDXMLException;
        } catch (InterruptedException e2) {
            LLSDXMLException lLSDXMLException2 = new LLSDXMLException("Interrupted");
            lLSDXMLException2.initCause(e2);
            throw lLSDXMLException2;
        } catch (IOException e3) {
            LLSDXMLException lLSDXMLException3 = new LLSDXMLException("I/O error");
            lLSDXMLException3.initCause(e3);
            throw lLSDXMLException3;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x017b, code lost:
        r0 = r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseBinaryNode(int r10, java.lang.String r11, java.io.DataInputStream r12, com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler r13) throws com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException, com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException, java.lang.InterruptedException, java.io.IOException {
        /*
            r9 = 1
            r8 = 0
            r3 = 0
            r1 = r10
        L_0x0004:
            if (r1 <= 0) goto L_0x0181
            byte r0 = r12.readByte()
            switch(r0) {
                case 10: goto L_0x017e;
                case 33: goto L_0x002b;
                case 48: goto L_0x0042;
                case 49: goto L_0x0037;
                case 60: goto L_0x0173;
                case 91: goto L_0x0103;
                case 98: goto L_0x0083;
                case 100: goto L_0x00e4;
                case 105: goto L_0x004d;
                case 108: goto L_0x00bd;
                case 114: goto L_0x005c;
                case 115: goto L_0x0097;
                case 117: goto L_0x006b;
                case 123: goto L_0x0129;
                default: goto L_0x000d;
            }
        L_0x000d:
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r1 = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown LLSD element 0x"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r0 = java.lang.Integer.toHexString(r0)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        L_0x002b:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
            r0.<init>()
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
        L_0x0035:
            r1 = r0
            goto L_0x0004
        L_0x0037:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
            r0.<init>((boolean) r9)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0042:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
            r0.<init>((boolean) r3)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x004d:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
            int r2 = r12.readInt()
            r0.<init>((int) r2)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x005c:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
            double r4 = r12.readDouble()
            r0.<init>((double) r4)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x006b:
            long r4 = r12.readLong()
            long r6 = r12.readLong()
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
            java.util.UUID r2 = new java.util.UUID
            r2.<init>(r4, r6)
            r0.<init>((java.util.UUID) r2)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0083:
            int r0 = r12.readInt()
            byte[] r0 = new byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary r2 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
            r2.<init>((byte[]) r0)
            r13.onPrimitiveValue(r11, r2)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0097:
            int r0 = r12.readInt()
            if (r0 != 0) goto L_0x00ab
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
            java.lang.String r2 = ""
            r0.<init>(r2)
            r13.onPrimitiveValue(r11, r0)
        L_0x00a8:
            int r0 = r1 + -1
            goto L_0x0035
        L_0x00ab:
            byte[] r0 = new byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r2 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)
            r2.<init>(r0)
            r13.onPrimitiveValue(r11, r2)
            goto L_0x00a8
        L_0x00bd:
            int r0 = r12.readInt()
            if (r0 != 0) goto L_0x00d2
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
            java.lang.String r2 = ""
            r0.<init>((java.lang.String) r2)
            r13.onPrimitiveValue(r11, r0)
        L_0x00ce:
            int r0 = r1 + -1
            goto L_0x0035
        L_0x00d2:
            byte[] r0 = new byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r2 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)
            r2.<init>((java.lang.String) r0)
            r13.onPrimitiveValue(r11, r2)
            goto L_0x00ce
        L_0x00e4:
            double r4 = r12.readDouble()
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate r0 = new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
            java.util.Date r2 = new java.util.Date
            r6 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r4 = r4 * r6
            long r4 = java.lang.Math.round(r4)
            r2.<init>(r4)
            r0.<init>((java.util.Date) r2)
            r13.onPrimitiveValue(r11, r0)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0103:
            int r2 = r12.readInt()
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler r0 = r13.onArrayBegin(r11)
            if (r0 != 0) goto L_0x010e
            r0 = r13
        L_0x010e:
            parseBinaryNode(r2, r8, r12, r0)
            byte r2 = r12.readByte()
            r4 = 93
            if (r2 == r4) goto L_0x0122
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Array terminator expected"
            r0.<init>(r1)
            throw r0
        L_0x0122:
            r0.onMapEnd(r11)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0129:
            int r4 = r12.readInt()
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler r0 = r13.onMapBegin(r11)
            if (r0 != 0) goto L_0x0134
            r0 = r13
        L_0x0134:
            r2 = r3
        L_0x0135:
            if (r2 >= r4) goto L_0x015b
            byte r5 = r12.readByte()
            r6 = 107(0x6b, float:1.5E-43)
            if (r5 == r6) goto L_0x0148
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Map key expected"
            r0.<init>(r1)
            throw r0
        L_0x0148:
            int r5 = r12.readInt()
            byte[] r5 = new byte[r5]
            r12.readFully(r5)
            java.lang.String r5 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r5)
            parseBinaryNode(r9, r5, r12, r0)
            int r2 = r2 + 1
            goto L_0x0135
        L_0x015b:
            byte r2 = r12.readByte()
            r4 = 125(0x7d, float:1.75E-43)
            if (r2 == r4) goto L_0x016c
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Map terminator expected"
            r0.<init>(r1)
            throw r0
        L_0x016c:
            r0.onMapEnd(r11)
            int r0 = r1 + -1
            goto L_0x0035
        L_0x0173:
            byte r0 = r12.readByte()
            r2 = 62
            if (r0 != r2) goto L_0x0173
            r0 = r1
            goto L_0x0035
        L_0x017e:
            r0 = r1
            goto L_0x0035
        L_0x0181:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.parseBinaryNode(int, java.lang.String, java.io.DataInputStream, com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler):void");
    }

    public static void parseXML(InputStream inputStream, String str, LLSDContentHandler lLSDContentHandler) throws LLSDXMLException {
        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(inputStream, str);
            newPullParser.nextTag();
            newPullParser.require(2, (String) null, "llsd");
            newPullParser.nextTag();
            parseXMLNode((String) null, newPullParser, lLSDContentHandler);
            newPullParser.require(3, (String) null, "llsd");
        } catch (XmlPullParserException e) {
            Debug.Log("XmlPullParserException: " + e.getMessage());
            e.printStackTrace();
            LLSDXMLException lLSDXMLException = new LLSDXMLException("Malformed XML");
            lLSDXMLException.initCause(e);
            throw lLSDXMLException;
        } catch (IOException e2) {
            throw new LLSDXMLException("Input stream error");
        } catch (LLSDValueTypeException e3) {
            e3.printStackTrace();
            LLSDXMLException lLSDXMLException2 = new LLSDXMLException("Malformed XML");
            lLSDXMLException2.initCause(e3);
            throw lLSDXMLException2;
        } catch (InterruptedException e4) {
            e4.printStackTrace();
            LLSDXMLException lLSDXMLException3 = new LLSDXMLException("Interrupted");
            lLSDXMLException3.initCause(e4);
            throw lLSDXMLException3;
        }
    }

    private static void parseXMLNode(String str, XmlPullParser xmlPullParser, LLSDContentHandler lLSDContentHandler) throws LLSDXMLException, XmlPullParserException, IOException, LLSDValueTypeException, InterruptedException {
        String name = xmlPullParser.getName();
        LLSDNodeType byTag = LLSDNodeType.byTag(name);
        if (byTag == null) {
            throw new LLSDXMLException("Unknown tag: " + name);
        }
        switch (m197getcomlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues()[byTag.ordinal()]) {
            case 1:
                LLSDContentHandler onArrayBegin = lLSDContentHandler.onArrayBegin(str);
                xmlPullParser.nextTag();
                if (onArrayBegin != null) {
                    lLSDContentHandler = onArrayBegin;
                }
                while (xmlPullParser.getEventType() != 3) {
                    parseXMLNode((String) null, xmlPullParser, lLSDContentHandler);
                }
                lLSDContentHandler.onArrayEnd(str);
                xmlPullParser.nextTag();
                return;
            case 2:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDBinary(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 3:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDBoolean(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 4:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDDate(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 5:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDDouble(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 6:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDInt(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 7:
                throw new LLSDXMLException("Unexpected tag: " + name);
            case 8:
                LLSDContentHandler onMapBegin = lLSDContentHandler.onMapBegin(str);
                xmlPullParser.nextTag();
                if (onMapBegin != null) {
                    lLSDContentHandler = onMapBegin;
                }
                while (xmlPullParser.getEventType() != 3) {
                    String name2 = xmlPullParser.getName();
                    if (!name2.equalsIgnoreCase("key")) {
                        throw new LLSDXMLException("Unexpected tag: " + name2);
                    }
                    String nextText = xmlPullParser.nextText();
                    xmlPullParser.nextTag();
                    parseXMLNode(nextText, xmlPullParser, lLSDContentHandler);
                }
                lLSDContentHandler.onMapEnd(str);
                xmlPullParser.nextTag();
                return;
            case 9:
                throw new LLSDXMLException("Unexpected tag: " + name);
            case 10:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDString(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 11:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDURI(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 12:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDUUID(xmlPullParser.nextText()));
                xmlPullParser.nextTag();
                return;
            case 13:
                lLSDContentHandler.onPrimitiveValue(str, new LLSDUndefined());
                xmlPullParser.nextTag();
                return;
            default:
                return;
        }
    }
}
