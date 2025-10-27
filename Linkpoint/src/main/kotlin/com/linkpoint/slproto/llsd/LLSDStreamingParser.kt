package com.linkpoint.slproto.llsd

import com.linkpoint.Debug
import com.linkpoint.slproto.https.LLSDContentTypeDetector
import com.linkpoint.slproto.llsd.types.LLSDBinary
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDDate
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDURI
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

class LLSDStreamingParser {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = null

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues = null

    interface LLSDContentHandler {
         fun onArrayBegin(String str): LLSDContentHandler) throws LLSDXMLException

         fun onArrayEnd(str: String) throws LLSDXMLException

         fun onMapBegin(String str): LLSDContentHandler) throws LLSDXMLException

         fun onMapEnd(str: String) throws LLSDXMLException, InterruptedException

         fun onPrimitiveValue(str: String, lLSDNode: LLSDNode) throws LLSDXMLException, LLSDValueTypeException
    }

    @JvmStatic
    class LLSDDefaultContentHandler : LLSDContentHandler {
         public fun onArrayBegin(str: String) throws LLSDXMLException {
            return LLSDDefaultContentHandler()
        }

        fun onArrayEnd(str: String) throws LLSDXMLException {
        }

         public fun onMapBegin(str: String) throws LLSDXMLException {
            return LLSDDefaultContentHandler()
        }

        fun onMapEnd(str: String) throws LLSDXMLException, InterruptedException {
        }

        fun onPrimitiveValue(str: String, lLSDNode: LLSDNode) throws LLSDXMLException, LLSDValueTypeException {
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m196getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues
        }
        val iArr: IntArray = Int[LLSDContentTypeDetector.LLSDContentType.values().length]
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        f115comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = iArr
        return iArr
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-llsd-LLSDNodeTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m197getcomlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues() {
        if (f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues != null) {
            return f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues
        }
        val iArr: IntArray = Int[LLSDNodeType.values().length]
        try {
            iArr[LLSDNodeType.llsdArray.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDNodeType.llsdBinary.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[LLSDNodeType.llsdBoolean.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[LLSDNodeType.llsdDate.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[LLSDNodeType.llsdDouble.ordinal()] = 5
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[LLSDNodeType.llsdInteger.ordinal()] = 6
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[LLSDNodeType.llsdKey.ordinal()] = 7
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[LLSDNodeType.llsdMap.ordinal()] = 8
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[LLSDNodeType.llsdRoot.ordinal()] = 9
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[LLSDNodeType.llsdString.ordinal()] = 10
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[LLSDNodeType.llsdURI.ordinal()] = 11
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[LLSDNodeType.llsdUUID.ordinal()] = 12
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[LLSDNodeType.llsdUndef.ordinal()] = 13
        } catch (NoSuchFieldError e13) {
        }
        f116comlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues = iArr
        return iArr
    }

    @JvmStatic
     fun parseAny(inputStream: InputStream, str: String, lLSDContentHandler: LLSDContentHandler) throws LLSDXMLException {
        try {
            val bufferedInputStream: BufferedInputStream = BufferedInputStream(inputStream, 65536)
            switch (m196getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal()]) {
                case 1:
                    parseBinary(DataInputStream(bufferedInputStream), lLSDContentHandler)
                    return
                case 2:
                    parseXML(bufferedInputStream, "UTF-8", lLSDContentHandler)
                    return
                default:
                    return
            }
        } catch (IOException e) {
            val lLSDXMLException: LLSDXMLException = LLSDXMLException("I/O error")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        }
        val lLSDXMLException2: LLSDXMLException = LLSDXMLException("I/O error")
        lLSDXMLException2.initCause(e)
        throw lLSDXMLException2
    }

    @JvmStatic
     fun parseBinary(dataInputStream: DataInputStream, lLSDContentHandler: LLSDContentHandler) throws LLSDXMLException {
        try {
            parseBinaryNode(1, (String) null, dataInputStream, lLSDContentHandler)
        } catch (LLSDValueTypeException e) {
            val lLSDXMLException: LLSDXMLException = LLSDXMLException("Invalid value type")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (InterruptedException e2) {
            val lLSDXMLException2: LLSDXMLException = LLSDXMLException("Interrupted")
            lLSDXMLException2.initCause(e2)
            throw lLSDXMLException2
        } catch (IOException e3) {
            val lLSDXMLException3: LLSDXMLException = LLSDXMLException("I/O error")
            lLSDXMLException3.initCause(e3)
            throw lLSDXMLException3
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x017b, code lost:
        r0 = r1
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @JvmStatic
 private fun parseBinaryNode(r10: Int, java.lang.String r11, java.io.DataInputStream r12, com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler r13) throws com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException, com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException, java.lang.InterruptedException, java.io.IOException {
        /*
            r9 = 1
            r8 = 0
            r3 = 0
            r1 = r10
        L_0x0004:
            if (r1 <= 0) goto L_0x0181
            val r0: Byte = r12.readByte()
            switch(r0) {
                case 10: goto L_0x017e
                case 33: goto L_0x002b
                case 48: goto L_0x0042
                case 49: goto L_0x0037
                case 60: goto L_0x0173
                case 91: goto L_0x0103
                case 98: goto L_0x0083
                case 100: goto L_0x00e4
                case 105: goto L_0x004d
                case 108: goto L_0x00bd
                case 114: goto L_0x005c
                case 115: goto L_0x0097
                case 117: goto L_0x006b
                case 123: goto L_0x0129
                default: goto L_0x000d
            }
        L_0x000d:
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r1 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.StringBuilder r2 = java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown LLSD element 0x"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r0 = java.lang.Integer.toHexString(r0)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        L_0x002b:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
            r0.<init>()
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
        L_0x0035:
            r1 = r0
            goto L_0x0004
        L_0x0037:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
            r0.<init>((Boolean) r9)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0042:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
            r0.<init>((Boolean) r3)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x004d:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
            val r2: Int = r12.readInt()
            r0.<init>((Int) r2)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x005c:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
            val r4: Double = r12.readDouble()
            r0.<init>((Double) r4)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x006b:
            val r4: Long = r12.readLong()
            val r6: Long = r12.readLong()
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
            java.util.UUID r2 = java.util.UUID
            r2.<init>(r4, r6)
            r0.<init>((java.util.UUID) r2)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0083:
            val r0: Int = r12.readInt()
            val r0: ByteArray = Byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
            r2.<init>((ByteArray) r0)
            r13.onPrimitiveValue(r11, r2)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0097:
            val r0: Int = r12.readInt()
            if (r0 != 0) goto L_0x00ab
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
            java.lang.String r2 = ""
            r0.<init>(r2)
            r13.onPrimitiveValue(r11, r0)
        L_0x00a8:
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x00ab:
            val r0: ByteArray = Byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)
            r2.<init>(r0)
            r13.onPrimitiveValue(r11, r2)
            goto L_0x00a8
        L_0x00bd:
            val r0: Int = r12.readInt()
            if (r0 != 0) goto L_0x00d2
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
            java.lang.String r2 = ""
            r0.<init>((java.lang.String) r2)
            r13.onPrimitiveValue(r11, r0)
        L_0x00ce:
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x00d2:
            val r0: ByteArray = Byte[r0]
            r12.readFully(r0)
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)
            r2.<init>((java.lang.String) r0)
            r13.onPrimitiveValue(r11, r2)
            goto L_0x00ce
        L_0x00e4:
            val r4: Double = r12.readDouble()
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
            java.util.Date r2 = java.util.Date
            r6 = 4652007308841189376(0x408f400000000000, Double:1000.0)
            val r4: Double = r4 * r6
            val r4: Long = java.lang.Math.round(r4)
            r2.<init>(r4)
            r0.<init>((java.util.Date) r2)
            r13.onPrimitiveValue(r11, r0)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0103:
            val r2: Int = r12.readInt()
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler r0 = r13.onArrayBegin(r11)
            if (r0 != 0) goto L_0x010e
            r0 = r13
        L_0x010e:
            parseBinaryNode(r2, r8, r12, r0)
            val r2: Byte = r12.readByte()
            r4 = 93
            if (r2 == r4) goto L_0x0122
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Array terminator expected"
            r0.<init>(r1)
            throw r0
        L_0x0122:
            r0.onMapEnd(r11)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0129:
            val r4: Int = r12.readInt()
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler r0 = r13.onMapBegin(r11)
            if (r0 != 0) goto L_0x0134
            r0 = r13
        L_0x0134:
            r2 = r3
        L_0x0135:
            if (r2 >= r4) goto L_0x015b
            val r5: Byte = r12.readByte()
            r6 = 107(0x6b, Float:1.5E-43)
            if (r5 == r6) goto L_0x0148
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Map key expected"
            r0.<init>(r1)
            throw r0
        L_0x0148:
            val r5: Int = r12.readInt()
            val r5: ByteArray = Byte[r5]
            r12.readFully(r5)
            java.lang.String r5 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r5)
            parseBinaryNode(r9, r5, r12, r0)
            val r2: Int = r2 + 1
            goto L_0x0135
        L_0x015b:
            val r2: Byte = r12.readByte()
            r4 = 125(0x7d, Float:1.75E-43)
            if (r2 == r4) goto L_0x016c
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r1 = "Map terminator expected"
            r0.<init>(r1)
            throw r0
        L_0x016c:
            r0.onMapEnd(r11)
            val r0: Int = r1 + -1
            goto L_0x0035
        L_0x0173:
            val r0: Byte = r12.readByte()
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
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.parseBinaryNode(Int, java.lang.String, java.io.DataInputStream, com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser$LLSDContentHandler):Unit")
    }

    @JvmStatic
     fun parseXML(inputStream: InputStream, str: String, lLSDContentHandler: LLSDContentHandler) throws LLSDXMLException {
        try {
            val newPullParser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
            newPullParser.setInput(inputStream, str)
            newPullParser.nextTag()
            newPullParser.require(2, (String) null, "llsd")
            newPullParser.nextTag()
            parseXMLNode((String) null, newPullParser, lLSDContentHandler)
            newPullParser.require(3, (String) null, "llsd")
        } catch (XmlPullParserException e) {
            Debug.Log("XmlPullParserException: " + e.getMessage())
            e.printStackTrace()
            val lLSDXMLException: LLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (IOException e2) {
            throw LLSDXMLException("Input stream error")
        } catch (LLSDValueTypeException e3) {
            e3.printStackTrace()
            val lLSDXMLException2: LLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException2.initCause(e3)
            throw lLSDXMLException2
        } catch (InterruptedException e4) {
            e4.printStackTrace()
            val lLSDXMLException3: LLSDXMLException = LLSDXMLException("Interrupted")
            lLSDXMLException3.initCause(e4)
            throw lLSDXMLException3
        }
    }

    @JvmStatic
 private fun parseXMLNode(str: String, xmlPullParser: XmlPullParser, lLSDContentHandler: LLSDContentHandler) throws LLSDXMLException, XmlPullParserException, IOException, LLSDValueTypeException, InterruptedException {
        val name: String = xmlPullParser.getName()
        val byTag: LLSDNodeType = LLSDNodeType.byTag(name)
        if (byTag == null) {
            throw LLSDXMLException("Unknown tag: " + name)
        }
        switch (m197getcomlumiyaviewerlumiyaslprotollsdLLSDNodeTypeSwitchesValues()[byTag.ordinal()]) {
            case 1:
                val onArrayBegin: LLSDContentHandler = lLSDContentHandler.onArrayBegin(str)
                xmlPullParser.nextTag()
                if (onArrayBegin != null) {
                    lLSDContentHandler = onArrayBegin
                }
                while (xmlPullParser.getEventType() != 3) {
                    parseXMLNode((String) null, xmlPullParser, lLSDContentHandler)
                }
                lLSDContentHandler.onArrayEnd(str)
                xmlPullParser.nextTag()
                return
            case 2:
                lLSDContentHandler.onPrimitiveValue(str, LLSDBinary(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 3:
                lLSDContentHandler.onPrimitiveValue(str, LLSDBoolean(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 4:
                lLSDContentHandler.onPrimitiveValue(str, LLSDDate(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 5:
                lLSDContentHandler.onPrimitiveValue(str, LLSDDouble(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 6:
                lLSDContentHandler.onPrimitiveValue(str, LLSDInt(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 7:
                throw LLSDXMLException("Unexpected tag: " + name)
            case 8:
                val onMapBegin: LLSDContentHandler = lLSDContentHandler.onMapBegin(str)
                xmlPullParser.nextTag()
                if (onMapBegin != null) {
                    lLSDContentHandler = onMapBegin
                }
                while (xmlPullParser.getEventType() != 3) {
                    val name2: String = xmlPullParser.getName()
                    if (!name2.equalsIgnoreCase("key")) {
                        throw LLSDXMLException("Unexpected tag: " + name2)
                    }
                    val nextText: String = xmlPullParser.nextText()
                    xmlPullParser.nextTag()
                    parseXMLNode(nextText, xmlPullParser, lLSDContentHandler)
                }
                lLSDContentHandler.onMapEnd(str)
                xmlPullParser.nextTag()
                return
            case 9:
                throw LLSDXMLException("Unexpected tag: " + name)
            case 10:
                lLSDContentHandler.onPrimitiveValue(str, LLSDString(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 11:
                lLSDContentHandler.onPrimitiveValue(str, LLSDURI(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 12:
                lLSDContentHandler.onPrimitiveValue(str, LLSDUUID(xmlPullParser.nextText()))
                xmlPullParser.nextTag()
                return
            case 13:
                lLSDContentHandler.onPrimitiveValue(str, LLSDUndefined())
                xmlPullParser.nextTag()
                return
            default:
                return
        }
    }
}
