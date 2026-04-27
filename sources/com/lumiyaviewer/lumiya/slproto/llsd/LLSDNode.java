// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;

import android.util.Xml;
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
import java.util.Map;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDXMLException, LLSDNodeFactory, LLSDValueTypeException, LLSDException

public abstract class LLSDNode
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues[];

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

    public LLSDNode()
    {
    }

    public static LLSDNode fromAny(InputStream inputstream, String s)
        throws LLSDXMLException
    {
        inputstream = new BufferedInputStream(inputstream, 0x10000);
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_https_2D_LLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(inputstream, s).ordinal()];
        JVM INSTR tableswitch 1 2: default 94
    //                   1 73
    //                   2 85;
           goto _L1 _L2 _L3
_L1:
        throw new LLSDXMLException("Unknown content type");
        inputstream;
        s = new LLSDXMLException("I/O error");
        s.initCause(inputstream);
        throw s;
_L2:
        return fromBinary(new DataInputStream(inputstream));
_L3:
        inputstream = parseXML(inputstream, "UTF-8");
        return inputstream;
    }

    public static LLSDNode fromBinary(DataInputStream datainputstream)
        throws LLSDXMLException
    {
        int i;
        int j;
        j = 0;
        i = 0;
_L26:
        int k;
        try
        {
            k = datainputstream.readByte();
        }
        // Misplaced declaration of an exception variable
        catch (DataInputStream datainputstream)
        {
            LLSDXMLException llsdxmlexception = new LLSDXMLException(datainputstream.getMessage());
            llsdxmlexception.initCause(datainputstream);
            throw llsdxmlexception;
        }
        k;
        JVM INSTR lookupswitch 14: default 564
    //                   10: 5
    //                   33: 188
    //                   48: 205
    //                   49: 196
    //                   60: 548
    //                   91: 388
    //                   98: 261
    //                   100: 362
    //                   105: 214
    //                   108: 322
    //                   114: 226
    //                   115: 282
    //                   117: 238
    //                   123: 442;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14 _L15
_L2:
        continue; /* Loop/switch isn't completed */
_L1:
        throw new LLSDXMLException((new StringBuilder()).append("Unknown LLSD element 0x").append(Integer.toHexString(k)).toString());
_L3:
        return new LLSDUndefined();
_L5:
        return new LLSDBoolean(true);
_L4:
        return new LLSDBoolean(false);
_L10:
        return new LLSDInt(datainputstream.readInt());
_L12:
        return new LLSDDouble(datainputstream.readDouble());
_L14:
        return new LLSDUUID(new UUID(datainputstream.readLong(), datainputstream.readLong()));
_L8:
        byte abyte0[] = new byte[datainputstream.readInt()];
        datainputstream.readFully(abyte0);
        return new LLSDBinary(abyte0);
_L13:
        i = datainputstream.readInt();
        if (i != 0) goto _L17; else goto _L16
_L16:
        return new LLSDString("");
_L17:
        byte abyte1[] = new byte[i];
        datainputstream.readFully(abyte1);
        return new LLSDString(SLMessage.stringFromVariableUTF(abyte1));
_L11:
        i = datainputstream.readInt();
        if (i != 0) goto _L19; else goto _L18
_L18:
        return new LLSDURI("");
_L19:
        byte abyte2[] = new byte[i];
        datainputstream.readFully(abyte2);
        return new LLSDURI(SLMessage.stringFromVariableUTF(abyte2));
_L9:
        return new LLSDDate(new Date(Math.round(datainputstream.readDouble() * 1000D)));
_L7:
        Object obj;
        j = datainputstream.readInt();
        obj = new LLSDArray();
_L21:
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        ((LLSDArray) (obj)).add(fromBinary(datainputstream));
        i++;
        if (true) goto _L21; else goto _L20
_L20:
        if (datainputstream.readByte() != 93)
        {
            throw new LLSDXMLException("Array terminator expected");
        }
        break; /* Loop/switch isn't completed */
_L15:
        k = datainputstream.readInt();
        obj = new HashMap(k);
        i = j;
_L23:
        if (i >= k)
        {
            break; /* Loop/switch isn't completed */
        }
        if (datainputstream.readByte() != 107)
        {
            throw new LLSDXMLException("Map key expected");
        }
        byte abyte3[] = new byte[datainputstream.readInt()];
        datainputstream.readFully(abyte3);
        ((Map) (obj)).put(SLMessage.stringFromVariableUTF(abyte3), fromBinary(datainputstream));
        i++;
        if (true) goto _L23; else goto _L22
_L22:
        obj = new LLSDMap(((Map) (obj)));
        if (datainputstream.readByte() != 125)
        {
            throw new LLSDXMLException("Map terminator expected");
        }
          goto _L24
_L6:
        do
        {
            k = datainputstream.readByte();
        } while (k != 62);
        if (true) goto _L26; else goto _L25
_L25:
        return ((LLSDNode) (obj));
_L24:
        return ((LLSDNode) (obj));
    }

    public static LLSDNode fromBinaryFile(File file)
        throws LLSDXMLException
    {
        Object obj = new DataInputStream(new FileInputStream(file));
        file = fromBinary(((DataInputStream) (obj)));
        if (obj != null)
        {
            try
            {
                ((DataInputStream) (obj)).close();
            }
            // Misplaced declaration of an exception variable
            catch (File file)
            {
                obj = new LLSDXMLException(file.getMessage());
                ((LLSDXMLException) (obj)).initCause(file);
                throw obj;
            }
            return file;
        } else
        {
            return file;
        }
_L2:
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_39;
        }
        ((DataInputStream) (obj)).close();
        throw file;
        file;
        continue; /* Loop/switch isn't completed */
        file;
        obj = null;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public static LLSDNode parseXML(InputStream inputstream, String s)
        throws LLSDXMLException
    {
        try
        {
            XmlPullParser xmlpullparser = XmlPullParserFactory.newInstance().newPullParser();
            xmlpullparser.setInput(inputstream, s);
            xmlpullparser.nextTag();
            xmlpullparser.require(2, null, "llsd");
            xmlpullparser.nextTag();
            inputstream = LLSDNodeFactory.parseNode(xmlpullparser);
            xmlpullparser.nextTag();
            xmlpullparser.require(3, null, "llsd");
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            Debug.Log((new StringBuilder()).append("XmlPullParserException: ").append(inputstream.getMessage()).toString());
            inputstream.printStackTrace();
            s = new LLSDXMLException("Malformed XML");
            s.initCause(inputstream);
            throw s;
        }
        // Misplaced declaration of an exception variable
        catch (InputStream inputstream)
        {
            throw new LLSDXMLException("Input stream error");
        }
        return inputstream;
    }

    public byte[] asBinary()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("binary", this);
    }

    public boolean asBoolean()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("boolean", this);
    }

    public Date asDate()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("date", this);
    }

    public double asDouble()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("real", this);
    }

    public int asInt()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("integer", this);
    }

    public long asLong()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("long", this);
    }

    public String asString()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("string", this);
    }

    public URI asURI()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("uri", this);
    }

    public UUID asUUID()
        throws LLSDValueTypeException
    {
        throw new LLSDValueTypeException("uuid", this);
    }

    public LLSDNode byIndex(int i)
        throws LLSDException
    {
        throw new LLSDValueTypeException("array", this);
    }

    public LLSDNode byKey(String s)
        throws LLSDException
    {
        throw new LLSDValueTypeException("map", this);
    }

    public int getCount()
        throws LLSDException
    {
        throw new LLSDValueTypeException("array", this);
    }

    public boolean isBinary()
    {
        return this instanceof LLSDBinary;
    }

    public boolean isBoolean()
    {
        return this instanceof LLSDBoolean;
    }

    public boolean isDate()
    {
        return this instanceof LLSDDate;
    }

    public boolean isDouble()
    {
        return this instanceof LLSDDouble;
    }

    public boolean isInt()
    {
        return this instanceof LLSDInt;
    }

    public boolean isLong()
    {
        return this instanceof LLSDInt;
    }

    public boolean isString()
    {
        return this instanceof LLSDString;
    }

    public boolean isURI()
    {
        return this instanceof LLSDURI;
    }

    public boolean isUUID()
    {
        return this instanceof LLSDUUID;
    }

    public boolean keyExists(String s)
        throws LLSDException
    {
        throw new LLSDValueTypeException("map", this);
    }

    public String serializeToXML()
        throws IOException
    {
        XmlSerializer xmlserializer = Xml.newSerializer();
        StringWriter stringwriter = new StringWriter();
        xmlserializer.setOutput(stringwriter);
        xmlserializer.startTag("", "llsd");
        toXML(xmlserializer);
        xmlserializer.endTag("", "llsd");
        xmlserializer.endDocument();
        return stringwriter.toString();
    }

    public void serializeToXML(OutputStream outputstream, String s)
        throws IOException
    {
        XmlSerializer xmlserializer = Xml.newSerializer();
        xmlserializer.setOutput(outputstream, s);
        xmlserializer.startTag("", "llsd");
        toXML(xmlserializer);
        xmlserializer.endTag("", "llsd");
        xmlserializer.endDocument();
    }

    public abstract void toBinary(DataOutputStream dataoutputstream)
        throws IOException;

    public Object toObject(Class class1)
        throws LLSDException
    {
        throw new LLSDException((new StringBuilder()).append("Cannot deserialize ").append(getClass().getName()).toString());
    }

    public abstract void toXML(XmlSerializer xmlserializer)
        throws IOException;
}
