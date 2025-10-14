package com.lumiyaviewer.lumiya.slproto.llsd

import android.util.Xml
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.StringWriter
import java.net.URI
import java.util.Date
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

abstract class LLSDNode {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = null

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] m195getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues
        }
        Int[] iArr = Int[LLSDContentTypeDetector.LLSDContentType.values().length]
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = iArr
        return iArr
    }

    LLSDNode fromAny(InputStream inputStream, String str) throws LLSDXMLException {
        try {
            BufferedInputStream bufferedInputStream = BufferedInputStream(inputStream, 65536)
            switch (m195getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal()]) {
                case 1:
                    return fromBinary(DataInputStream(bufferedInputStream))
                case 2:
                    return parseXML(bufferedInputStream, "UTF-8")
                default:
                    throw LLSDXMLException("Unknown content type")
            }
        } catch (IOException e) {
            LLSDXMLException lLSDXMLException = LLSDXMLException("I/O error")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        }
        LLSDXMLException lLSDXMLException2 = LLSDXMLException("I/O error")
        lLSDXMLException2.initCause(e)
        throw lLSDXMLException2
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode fromBinary(java.io.DataInputStream r6) throws com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException {
        /*
            r0 = 0
        L_0x0001:
            byte r1 = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            switch(r1) {
                case 10: goto L_0x0001
                case 33: goto L_0x0034
                case 48: goto L_0x0041
                case 49: goto L_0x003a
                case 60: goto L_0x0144
                case 91: goto L_0x00d3
                case 98: goto L_0x006f
                case 100: goto L_0x00ba
                case 105: goto L_0x0048
                case 108: goto L_0x009c
                case 114: goto L_0x0052
                case 115: goto L_0x007e
                case 117: goto L_0x005c
                case 123: goto L_0x00fa
                default: goto L_0x0008
            }     // Catch:{ IOException -> 0x0026 }
        L_0x0008:
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.StringBuilder r2 = java.lang.StringBuilder     // Catch:{ IOException -> 0x0026 }
            r2.<init>()     // Catch:{ IOException -> 0x0026 }
            java.lang.String r3 = "Unknown LLSD element 0x"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = java.lang.Integer.toHexString(r1)     // Catch:{ IOException -> 0x0026 }
            java.lang.StringBuilder r1 = r2.append(r1)     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0026 }
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x0026:
            r0 = move-exception
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r1 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
            java.lang.String r2 = r0.getMessage()
            r1.<init>(r2)
            r1.initCause(r0)
            throw r1
        L_0x0034:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined     // Catch:{ IOException -> 0x0026 }
            r0.<init>()     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x003a:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean     // Catch:{ IOException -> 0x0026 }
            r1 = 1
            r0.<init>((Boolean) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x0041:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean     // Catch:{ IOException -> 0x0026 }
            r1 = 0
            r0.<init>((Boolean) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x0048:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt     // Catch:{ IOException -> 0x0026 }
            Int r1 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            r0.<init>((Int) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x0052:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble     // Catch:{ IOException -> 0x0026 }
            double r2 = r6.readDouble()     // Catch:{ IOException -> 0x0026 }
            r0.<init>((double) r2)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x005c:
            Long r0 = r6.readLong()     // Catch:{ IOException -> 0x0026 }
            Long r2 = r6.readLong()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID r4 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID     // Catch:{ IOException -> 0x0026 }
            java.util.UUID r5 = java.util.UUID     // Catch:{ IOException -> 0x0026 }
            r5.<init>(r0, r2)     // Catch:{ IOException -> 0x0026 }
            r4.<init>((java.util.UUID) r5)     // Catch:{ IOException -> 0x0026 }
            return r4
        L_0x006f:
            Int r0 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            byte[] r0 = byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary     // Catch:{ IOException -> 0x0026 }
            r1.<init>((byte[]) r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x007e:
            Int r0 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            if (r0 != 0) goto L_0x008d
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = ""
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x008d:
            byte[] r0 = byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString     // Catch:{ IOException -> 0x0026 }
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)     // Catch:{ IOException -> 0x0026 }
            r1.<init>(r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x009c:
            Int r0 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            if (r0 != 0) goto L_0x00ab
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = ""
            r0.<init>((java.lang.String) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x00ab:
            byte[] r0 = byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI     // Catch:{ IOException -> 0x0026 }
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)     // Catch:{ IOException -> 0x0026 }
            r1.<init>((java.lang.String) r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x00ba:
            double r0 = r6.readDouble()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate     // Catch:{ IOException -> 0x0026 }
            java.util.Date r3 = java.util.Date     // Catch:{ IOException -> 0x0026 }
            r4 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r0 = r0 * r4
            Long r0 = java.lang.Math.round(r0)     // Catch:{ IOException -> 0x0026 }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0026 }
            r2.<init>((java.util.Date) r3)     // Catch:{ IOException -> 0x0026 }
            return r2
        L_0x00d3:
            Int r1 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray     // Catch:{ IOException -> 0x0026 }
            r2.<init>()     // Catch:{ IOException -> 0x0026 }
        L_0x00dc:
            if (r0 >= r1) goto L_0x00e8
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r3 = fromBinary(r6)     // Catch:{ IOException -> 0x0026 }
            r2.add(r3)     // Catch:{ IOException -> 0x0026 }
            Int r0 = r0 + 1
            goto L_0x00dc
        L_0x00e8:
            byte r0 = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r1 = 93
            if (r0 == r1) goto L_0x00f9
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Array terminator expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x00f9:
            return r2
        L_0x00fa:
            Int r1 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            java.util.HashMap r2 = java.util.HashMap     // Catch:{ IOException -> 0x0026 }
            r2.<init>(r1)     // Catch:{ IOException -> 0x0026 }
        L_0x0103:
            if (r0 >= r1) goto L_0x012d
            byte r3 = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r4 = 107(0x6b, float:1.5E-43)
            if (r3 == r4) goto L_0x0116
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Map key expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x0116:
            Int r3 = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            byte[] r3 = byte[r3]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r3)     // Catch:{ IOException -> 0x0026 }
            java.lang.String r3 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r3)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r4 = fromBinary(r6)     // Catch:{ IOException -> 0x0026 }
            r2.put(r3, r4)     // Catch:{ IOException -> 0x0026 }
            Int r0 = r0 + 1
            goto L_0x0103
        L_0x012d:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap     // Catch:{ IOException -> 0x0026 }
            r0.<init>((java.util.Map<java.lang.String, com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode>) r2)     // Catch:{ IOException -> 0x0026 }
            byte r1 = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r2 = 125(0x7d, float:1.75E-43)
            if (r1 == r2) goto L_0x0143
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Map terminator expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x0143:
            return r0
        L_0x0144:
            byte r1 = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r2 = 62
            if (r1 == r2) goto L_0x0001
            goto L_0x0144
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode.fromBinary(java.io.DataInputStream):com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode")
    }

    LLSDNode fromBinaryFile(File file) throws LLSDXMLException {
        DataInputStream dataInputStream
        try {
            dataInputStream = DataInputStream(FileInputStream(file))
            try {
                LLSDNode fromBinary = fromBinary(dataInputStream)
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close()
                    } catch (IOException e) {
                        LLSDXMLException lLSDXMLException = LLSDXMLException(e.getMessage())
                        lLSDXMLException.initCause(e)
                        throw lLSDXMLException
                    }
                }
                return fromBinary
            } catch (Throwable th) {
                th = th
            }
        } catch (Throwable th2) {
            th = th2
            dataInputStream = null
            if (dataInputStream != null) {
                dataInputStream.close()
            }
            throw th
        }
    }

    LLSDNode parseXML(InputStream inputStream, String str) throws LLSDXMLException {
        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser()
            newPullParser.setInput(inputStream, str)
            newPullParser.nextTag()
            newPullParser.require(2, (String) null, "llsd")
            newPullParser.nextTag()
            LLSDNode parseNode = LLSDNodeFactory.parseNode(newPullParser)
            newPullParser.nextTag()
            newPullParser.require(3, (String) null, "llsd")
            return parseNode
        } catch (XmlPullParserException e) {
            Debug.Log("XmlPullParserException: " + e.getMessage())
            e.printStackTrace()
            LLSDXMLException lLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (IOException e2) {
            throw LLSDXMLException("Input stream error")
        }
    }

    byte[] asBinary() throws LLSDValueTypeException {
        throw LLSDValueTypeException("binary", this)
    }

    Boolean asBoolean() throws LLSDValueTypeException {
        throw LLSDValueTypeException("Boolean", this)
    }

    Date asDate() throws LLSDValueTypeException {
        throw LLSDValueTypeException("date", this)
    }

    double asDouble() throws LLSDValueTypeException {
        throw LLSDValueTypeException("real", this)
    }

    Int asInt() throws LLSDValueTypeException {
        throw LLSDValueTypeException("integer", this)
    }

    Long asLong() throws LLSDValueTypeException {
        throw LLSDValueTypeException("Long", this)
    }

    String asString() throws LLSDValueTypeException {
        throw LLSDValueTypeException("string", this)
    }

    URI asURI() throws LLSDValueTypeException {
        throw LLSDValueTypeException("uri", this)
    }

    UUID asUUID() throws LLSDValueTypeException {
        throw LLSDValueTypeException("uuid", this)
    }

    LLSDNode byIndex(Int i) throws LLSDException {
        throw LLSDValueTypeException("array", this)
    }

    LLSDNode byKey(String str) throws LLSDException {
        throw LLSDValueTypeException("map", this)
    }

    Int getCount() throws LLSDException {
        throw LLSDValueTypeException("array", this)
    }

    Boolean isBinary() {
        return this instanceof LLSDBinary
    }

    Boolean isBoolean() {
        return this instanceof LLSDBoolean
    }

    Boolean isDate() {
        return this instanceof LLSDDate
    }

    Boolean isDouble() {
        return this instanceof LLSDDouble
    }

    Boolean isInt() {
        return this instanceof LLSDInt
    }

    Boolean isLong() {
        return this instanceof LLSDInt
    }

    Boolean isString() {
        return this instanceof LLSDString
    }

    Boolean isURI() {
        return this instanceof LLSDURI
    }

    Boolean isUUID() {
        return this instanceof LLSDUUID
    }

    Boolean keyExists(String str) throws LLSDException {
        throw LLSDValueTypeException("map", this)
    }

    String serializeToXML() throws IOException {
        XmlSerializer newSerializer = Xml.newSerializer()
        StringWriter stringWriter = StringWriter()
        newSerializer.setOutput(stringWriter)
        newSerializer.startTag("", "llsd")
        toXML(newSerializer)
        newSerializer.endTag("", "llsd")
        newSerializer.endDocument()
        return stringWriter.toString()
    }

    Unit serializeToXML(OutputStream outputStream, String str) throws IOException {
        XmlSerializer newSerializer = Xml.newSerializer()
        newSerializer.setOutput(outputStream, str)
        newSerializer.startTag("", "llsd")
        toXML(newSerializer)
        newSerializer.endTag("", "llsd")
        newSerializer.endDocument()
    }

    abstract Unit toBinary(DataOutputStream dataOutputStream) throws IOException

    <T> T toObject(Class<? : T> cls) throws LLSDException {
        throw LLSDException("Cannot deserialize " + getClass().getName())
    }

    abstract Unit toXML(XmlSerializer xmlSerializer) throws IOException
}
