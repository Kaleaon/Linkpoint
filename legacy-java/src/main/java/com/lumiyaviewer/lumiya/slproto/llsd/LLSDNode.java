package com.lumiyaviewer.lumiya.slproto.llsd;

import android.util.Xml;
import com.google.common.logging.nano.Vr;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector;
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
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public abstract class LLSDNode {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f114x653d09df = null;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m205x70385c83() {
        if (f114x653d09df != null) {
            return f114x653d09df;
        }
        int[] iArr = new int[LLSDContentTypeDetector.LLSDContentType.valuesCustom().length];
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        f114x653d09df = iArr;
        return iArr;
    }

    public static LLSDNode fromAny(InputStream inputStream, String str) throws LLSDXMLException {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 65536);
            switch (m205x70385c83()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal()]) {
                case 1:
                    return fromBinary(new DataInputStream(bufferedInputStream));
                case 2:
                    return parseXML(bufferedInputStream, "UTF-8");
                default:
                    throw new LLSDXMLException("Unknown content type");
            }
        } catch (IOException e) {
            LLSDXMLException lLSDXMLException = new LLSDXMLException("I/O error");
            lLSDXMLException.initCause(e);
            throw lLSDXMLException;
        }
    }

    public static LLSDNode fromBinary(DataInputStream dataInputStream) throws LLSDXMLException {
        int i = 0;
        while (true) {
            try {
                byte readByte = dataInputStream.readByte();
                switch (readByte) {
                    case 10:
                        break;
                    case 33:
                        return new LLSDUndefined();
                    case 48:
                        return new LLSDBoolean(false);
                    case 49:
                        return new LLSDBoolean(true);
                    case 60:
                        do {
                        } while (dataInputStream.readByte() != 62);
                    case 91:
                        int readInt = dataInputStream.readInt();
                        LLSDArray lLSDArray = new LLSDArray();
                        while (i < readInt) {
                            lLSDArray.add(fromBinary(dataInputStream));
                            i++;
                        }
                        if (dataInputStream.readByte() != 93) {
                            throw new LLSDXMLException("Array terminator expected");
                        }
                        return lLSDArray;
                    case 98:
                        byte[] bArr = new byte[dataInputStream.readInt()];
                        dataInputStream.readFully(bArr);
                        return new LLSDBinary(bArr);
                    case 100:
                        return new LLSDDate(new Date(Math.round(dataInputStream.readDouble() * 1000.0d)));
                    case 105:
                        return new LLSDInt(dataInputStream.readInt());
                    case 108:
                        int readInt2 = dataInputStream.readInt();
                        if (readInt2 == 0) {
                            return new LLSDURI("");
                        }
                        byte[] bArr2 = new byte[readInt2];
                        dataInputStream.readFully(bArr2);
                        return new LLSDURI(SLMessage.stringFromVariableUTF(bArr2));
                    case 114:
                        return new LLSDDouble(dataInputStream.readDouble());
                    case 115:
                        int readInt3 = dataInputStream.readInt();
                        if (readInt3 == 0) {
                            return new LLSDString("");
                        }
                        byte[] bArr3 = new byte[readInt3];
                        dataInputStream.readFully(bArr3);
                        return new LLSDString(SLMessage.stringFromVariableUTF(bArr3));
                    case 117:
                        return new LLSDUUID(new UUID(dataInputStream.readLong(), dataInputStream.readLong()));
                    case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND /* 123 */:
                        int readInt4 = dataInputStream.readInt();
                        HashMap hashMap = new HashMap(readInt4);
                        while (i < readInt4) {
                            if (dataInputStream.readByte() != 107) {
                                throw new LLSDXMLException("Map key expected");
                            }
                            byte[] bArr4 = new byte[dataInputStream.readInt()];
                            dataInputStream.readFully(bArr4);
                            hashMap.put(SLMessage.stringFromVariableUTF(bArr4), fromBinary(dataInputStream));
                            i++;
                        }
                        LLSDMap lLSDMap = new LLSDMap(hashMap);
                        if (dataInputStream.readByte() != 125) {
                            throw new LLSDXMLException("Map terminator expected");
                        }
                        return lLSDMap;
                    default:
                        throw new LLSDXMLException("Unknown LLSD element 0x" + Integer.toHexString(readByte));
                }
            } catch (IOException e) {
                LLSDXMLException lLSDXMLException = new LLSDXMLException(e.getMessage());
                lLSDXMLException.initCause(e);
                throw lLSDXMLException;
            }
        }
    }

    public static LLSDNode fromBinaryFile(File file) throws LLSDXMLException {
        DataInputStream dataInputStream;
        try {
            try {
                dataInputStream = new DataInputStream(new FileInputStream(file));
            } catch (Throwable th) {
                th = th;
                dataInputStream = null;
            }
            try {
                LLSDNode fromBinary = fromBinary(dataInputStream);
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                return fromBinary;
            } catch (Throwable th2) {
                th = th2;
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                throw th;
            }
        } catch (IOException e) {
            LLSDXMLException lLSDXMLException = new LLSDXMLException(e.getMessage());
            lLSDXMLException.initCause(e);
            throw lLSDXMLException;
        }
    }

    public static LLSDNode parseXML(InputStream inputStream, String str) throws LLSDXMLException {
        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(inputStream, str);
            newPullParser.nextTag();
            newPullParser.require(2, null, "llsd");
            newPullParser.nextTag();
            LLSDNode parseNode = LLSDNodeFactory.parseNode(newPullParser);
            newPullParser.nextTag();
            newPullParser.require(3, null, "llsd");
            return parseNode;
        } catch (IOException e) {
            throw new LLSDXMLException("Input stream error");
        } catch (XmlPullParserException e2) {
            Debug.Log("XmlPullParserException: " + e2.getMessage());
            e2.printStackTrace();
            LLSDXMLException lLSDXMLException = new LLSDXMLException("Malformed XML");
            lLSDXMLException.initCause(e2);
            throw lLSDXMLException;
        }
    }

    public byte[] asBinary() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("binary", this);
    }

    public boolean asBoolean() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("boolean", this);
    }

    public Date asDate() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("date", this);
    }

    public double asDouble() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("real", this);
    }

    public int asInt() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("integer", this);
    }

    public long asLong() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("long", this);
    }

    public String asString() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("string", this);
    }

    public URI asURI() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("uri", this);
    }

    public UUID asUUID() throws LLSDValueTypeException {
        throw new LLSDValueTypeException("uuid", this);
    }

    public LLSDNode byIndex(int i) throws LLSDException {
        throw new LLSDValueTypeException("array", this);
    }

    public LLSDNode byKey(String str) throws LLSDException {
        throw new LLSDValueTypeException("map", this);
    }

    public int getCount() throws LLSDException {
        throw new LLSDValueTypeException("array", this);
    }

    public boolean isBinary() {
        return this instanceof LLSDBinary;
    }

    public boolean isBoolean() {
        return this instanceof LLSDBoolean;
    }

    public boolean isDate() {
        return this instanceof LLSDDate;
    }

    public boolean isDouble() {
        return this instanceof LLSDDouble;
    }

    public boolean isInt() {
        return this instanceof LLSDInt;
    }

    public boolean isLong() {
        return this instanceof LLSDInt;
    }

    public boolean isString() {
        return this instanceof LLSDString;
    }

    public boolean isURI() {
        return this instanceof LLSDURI;
    }

    public boolean isUUID() {
        return this instanceof LLSDUUID;
    }

    public boolean keyExists(String str) throws LLSDException {
        throw new LLSDValueTypeException("map", this);
    }

    public String serializeToXML() throws IOException {
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        newSerializer.setOutput(stringWriter);
        newSerializer.startTag("", "llsd");
        toXML(newSerializer);
        newSerializer.endTag("", "llsd");
        newSerializer.endDocument();
        return stringWriter.toString();
    }

    public void serializeToXML(OutputStream outputStream, String str) throws IOException {
        XmlSerializer newSerializer = Xml.newSerializer();
        newSerializer.setOutput(outputStream, str);
        newSerializer.startTag("", "llsd");
        toXML(newSerializer);
        newSerializer.endTag("", "llsd");
        newSerializer.endDocument();
    }

    public abstract void toBinary(DataOutputStream dataOutputStream) throws IOException;

    public <T> T toObject(Class<? extends T> cls) throws LLSDException {
        throw new LLSDException("Cannot deserialize " + getClass().getName());
    }

    public abstract void toXML(XmlSerializer xmlSerializer) throws IOException;
}
