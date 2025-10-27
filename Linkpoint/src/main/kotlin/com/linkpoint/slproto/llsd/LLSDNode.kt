package com.linkpoint.slproto.llsd

import android.util.Xml
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
    private const val /* synthetic */ IntArray f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = null

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m195getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues
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
        f114comlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues = iArr
        return iArr
    }

    @JvmStatic
     fun fromAny(inputStream: InputStream, str: String) throws LLSDXMLException {
        try {
            val bufferedInputStream: BufferedInputStream = BufferedInputStream(inputStream, 65536)
            switch (m195getcomlumiyaviewerlumiyaslprotohttpsLLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal()]) {
                case 1:
                    return fromBinary(DataInputStream(bufferedInputStream))
                case 2:
                    return parseXML(bufferedInputStream, "UTF-8")
                default:
                    throw LLSDXMLException("Unknown content type")
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @JvmStatic
    com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode fromBinary(java.io.DataInputStream r6) throws com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException {
        /*
            r0 = 0
        L_0x0001:
            val r1: Byte = r6.readByte()     // Catch:{ IOException -> 0x0026 }
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
            val r1: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            r0.<init>((Int) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x0052:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble     // Catch:{ IOException -> 0x0026 }
            val r2: Double = r6.readDouble()     // Catch:{ IOException -> 0x0026 }
            r0.<init>((Double) r2)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x005c:
            val r0: Long = r6.readLong()     // Catch:{ IOException -> 0x0026 }
            val r2: Long = r6.readLong()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID r4 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID     // Catch:{ IOException -> 0x0026 }
            java.util.UUID r5 = java.util.UUID     // Catch:{ IOException -> 0x0026 }
            r5.<init>(r0, r2)     // Catch:{ IOException -> 0x0026 }
            r4.<init>((java.util.UUID) r5)     // Catch:{ IOException -> 0x0026 }
            return r4
        L_0x006f:
            val r0: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            val r0: ByteArray = Byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary     // Catch:{ IOException -> 0x0026 }
            r1.<init>((ByteArray) r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x007e:
            val r0: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            if (r0 != 0) goto L_0x008d
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = ""
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x008d:
            val r0: ByteArray = Byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString     // Catch:{ IOException -> 0x0026 }
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)     // Catch:{ IOException -> 0x0026 }
            r1.<init>(r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x009c:
            val r0: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            if (r0 != 0) goto L_0x00ab
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = ""
            r0.<init>((java.lang.String) r1)     // Catch:{ IOException -> 0x0026 }
            return r0
        L_0x00ab:
            val r0: ByteArray = Byte[r0]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r0)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI r1 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI     // Catch:{ IOException -> 0x0026 }
            java.lang.String r0 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r0)     // Catch:{ IOException -> 0x0026 }
            r1.<init>((java.lang.String) r0)     // Catch:{ IOException -> 0x0026 }
            return r1
        L_0x00ba:
            val r0: Double = r6.readDouble()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate     // Catch:{ IOException -> 0x0026 }
            java.util.Date r3 = java.util.Date     // Catch:{ IOException -> 0x0026 }
            r4 = 4652007308841189376(0x408f400000000000, Double:1000.0)
            val r0: Double = r0 * r4
            val r0: Long = java.lang.Math.round(r0)     // Catch:{ IOException -> 0x0026 }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0026 }
            r2.<init>((java.util.Date) r3)     // Catch:{ IOException -> 0x0026 }
            return r2
        L_0x00d3:
            val r1: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray r2 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray     // Catch:{ IOException -> 0x0026 }
            r2.<init>()     // Catch:{ IOException -> 0x0026 }
        L_0x00dc:
            if (r0 >= r1) goto L_0x00e8
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r3 = fromBinary(r6)     // Catch:{ IOException -> 0x0026 }
            r2.add(r3)     // Catch:{ IOException -> 0x0026 }
            val r0: Int = r0 + 1
            goto L_0x00dc
        L_0x00e8:
            val r0: Byte = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r1 = 93
            if (r0 == r1) goto L_0x00f9
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Array terminator expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x00f9:
            return r2
        L_0x00fa:
            val r1: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            java.util.HashMap r2 = java.util.HashMap     // Catch:{ IOException -> 0x0026 }
            r2.<init>(r1)     // Catch:{ IOException -> 0x0026 }
        L_0x0103:
            if (r0 >= r1) goto L_0x012d
            val r3: Byte = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r4 = 107(0x6b, Float:1.5E-43)
            if (r3 == r4) goto L_0x0116
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Map key expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x0116:
            val r3: Int = r6.readInt()     // Catch:{ IOException -> 0x0026 }
            val r3: ByteArray = Byte[r3]     // Catch:{ IOException -> 0x0026 }
            r6.readFully(r3)     // Catch:{ IOException -> 0x0026 }
            java.lang.String r3 = com.lumiyaviewer.lumiya.slproto.SLMessage.stringFromVariableUTF(r3)     // Catch:{ IOException -> 0x0026 }
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r4 = fromBinary(r6)     // Catch:{ IOException -> 0x0026 }
            r2.put(r3, r4)     // Catch:{ IOException -> 0x0026 }
            val r0: Int = r0 + 1
            goto L_0x0103
        L_0x012d:
            com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap r0 = com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap     // Catch:{ IOException -> 0x0026 }
            r0.<init>((java.util.Map<java.lang.String, com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode>) r2)     // Catch:{ IOException -> 0x0026 }
            val r1: Byte = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r2 = 125(0x7d, Float:1.75E-43)
            if (r1 == r2) goto L_0x0143
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException r0 = com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException     // Catch:{ IOException -> 0x0026 }
            java.lang.String r1 = "Map terminator expected"
            r0.<init>(r1)     // Catch:{ IOException -> 0x0026 }
            throw r0     // Catch:{ IOException -> 0x0026 }
        L_0x0143:
            return r0
        L_0x0144:
            val r1: Byte = r6.readByte()     // Catch:{ IOException -> 0x0026 }
            r2 = 62
            if (r1 == r2) goto L_0x0001
            goto L_0x0144
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode.fromBinary(java.io.DataInputStream):com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode")
    }

    @JvmStatic
     fun fromBinaryFile(file: File) throws LLSDXMLException {
        DataInputStream dataInputStream
        try {
            dataInputStream = DataInputStream(FileInputStream(file))
            try {
                val fromBinary: LLSDNode = fromBinary(dataInputStream)
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close()
                    } catch (IOException e) {
                        val lLSDXMLException: LLSDXMLException = LLSDXMLException(e.getMessage())
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

    @JvmStatic
     fun parseXML(inputStream: InputStream, str: String) throws LLSDXMLException {
        try {
            val newPullParser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
            newPullParser.setInput(inputStream, str)
            newPullParser.nextTag()
            newPullParser.require(2, (String) null, "llsd")
            newPullParser.nextTag()
            val parseNode: LLSDNode = LLSDNodeFactory.parseNode(newPullParser)
            newPullParser.nextTag()
            newPullParser.require(3, (String) null, "llsd")
            return parseNode
        } catch (XmlPullParserException e) {
            Debug.Log("XmlPullParserException: " + e.getMessage())
            e.printStackTrace()
            val lLSDXMLException: LLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (IOException e2) {
            throw LLSDXMLException("Input stream error")
        }
    }

     public fun asBinary() throws LLSDValueTypeException {
        throw LLSDValueTypeException("binary", this)
    }

     public fun asBoolean() throws LLSDValueTypeException {
        throw LLSDValueTypeException("Boolean", this)
    }

     public fun asDate() throws LLSDValueTypeException {
        throw LLSDValueTypeException("date", this)
    }

     public fun asDouble() throws LLSDValueTypeException {
        throw LLSDValueTypeException("real", this)
    }

     public fun asInt() throws LLSDValueTypeException {
        throw LLSDValueTypeException("integer", this)
    }

     public fun asLong() throws LLSDValueTypeException {
        throw LLSDValueTypeException("Long", this)
    }

     public fun asString() throws LLSDValueTypeException {
        throw LLSDValueTypeException("string", this)
    }

     public fun asURI() throws LLSDValueTypeException {
        throw LLSDValueTypeException("uri", this)
    }

     public fun asUUID() throws LLSDValueTypeException {
        throw LLSDValueTypeException("uuid", this)
    }

     public fun byIndex(i: Int) throws LLSDException {
        throw LLSDValueTypeException("array", this)
    }

     public fun byKey(str: String) throws LLSDException {
        throw LLSDValueTypeException("map", this)
    }

     public fun getCount() throws LLSDException {
        throw LLSDValueTypeException("array", this)
    }

     public fun isBinary(): Boolean {
        return this instanceof LLSDBinary
    }

     public fun isBoolean(): Boolean {
        return this instanceof LLSDBoolean
    }

     public fun isDate(): Boolean {
        return this instanceof LLSDDate
    }

     public fun isDouble(): Boolean {
        return this instanceof LLSDDouble
    }

     public fun isInt(): Boolean {
        return this instanceof LLSDInt
    }

     public fun isLong(): Boolean {
        return this instanceof LLSDInt
    }

     public fun isString(): Boolean {
        return this instanceof LLSDString
    }

     public fun isURI(): Boolean {
        return this instanceof LLSDURI
    }

     public fun isUUID(): Boolean {
        return this instanceof LLSDUUID
    }

     public fun keyExists(str: String) throws LLSDException {
        throw LLSDValueTypeException("map", this)
    }

     public fun serializeToXML() throws IOException {
        val newSerializer: XmlSerializer = Xml.newSerializer()
        val stringWriter: StringWriter = StringWriter()
        newSerializer.setOutput(stringWriter)
        newSerializer.startTag("", "llsd")
        toXML(newSerializer)
        newSerializer.endTag("", "llsd")
        newSerializer.endDocument()
        return stringWriter.toString()
    }

    fun serializeToXML(outputStream: OutputStream, str: String) throws IOException {
        val newSerializer: XmlSerializer = Xml.newSerializer()
        newSerializer.setOutput(outputStream, str)
        newSerializer.startTag("", "llsd")
        toXML(newSerializer)
        newSerializer.endTag("", "llsd")
        newSerializer.endDocument()
    }

    public abstract Unit toBinary(DataOutputStream dataOutputStream) throws IOException

    public <T> T toObject(Class<? : T> cls) throws LLSDException {
        throw LLSDException("Cannot deserialize " + getClass().getName())
    }

    public abstract Unit toXML(XmlSerializer xmlSerializer) throws IOException
}
