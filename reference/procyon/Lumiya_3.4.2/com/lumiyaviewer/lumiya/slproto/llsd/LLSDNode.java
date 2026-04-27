// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd;

import java.io.DataOutputStream;
import java.io.OutputStream;
import org.xmlpull.v1.XmlSerializer;
import java.io.Writer;
import java.io.StringWriter;
import android.util.Xml;
import java.net.URI;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.lumiyaviewer.lumiya.Debug;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.File;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate;
import java.util.Date;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector;

public abstract class LLSDNode
{
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues() {
        if (LLSDNode.-com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues != null) {
            return LLSDNode.-com-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues;
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
    
    public static LLSDNode fromAny(final InputStream in, final String s) throws LLSDXMLException {
        BufferedInputStream in2;
        try {
            in2 = new BufferedInputStream(in, 65536);
            switch (-getcom-lumiyaviewer-lumiya-slproto-https-LLSDContentTypeDetector$LLSDContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(in2, s).ordinal()]) {
                default: {
                    throw new LLSDXMLException("Unknown content type");
                }
                case 1: {
                    break;
                }
                case 2: {
                    return parseXML(in2, "UTF-8");
                }
            }
        }
        catch (final IOException cause) {
            final LLSDXMLException ex = new LLSDXMLException("I/O error");
            ex.initCause(cause);
            throw ex;
        }
        return fromBinary(new DataInputStream(in2));
    }
    
    public static LLSDNode fromBinary(final DataInputStream dataInputStream) throws LLSDXMLException {
        final int n = 0;
        int i = 0;
    Label_0004_Outer:
        while (true) {
            Label_0477: {
                Label_0418: {
                    Label_0343: {
                        Label_0300: {
                            Label_0276: {
                            Label_0193:
                                while (true) {
                                    Label_0594: {
                                        try {
                                            while (true) {
                                                final byte byte1 = dataInputStream.readByte();
                                                switch (byte1) {
                                                    case 10: {
                                                        continue Label_0004_Outer;
                                                    }
                                                    default: {
                                                        throw new LLSDXMLException("Unknown LLSD element 0x" + Integer.toHexString(byte1));
                                                    }
                                                    case 33: {
                                                        break Label_0193;
                                                    }
                                                    case 49: {
                                                        return new LLSDBoolean(true);
                                                    }
                                                    case 48: {
                                                        return new LLSDBoolean(false);
                                                    }
                                                    case 105: {
                                                        return new LLSDInt(dataInputStream.readInt());
                                                    }
                                                    case 114: {
                                                        return new LLSDDouble(dataInputStream.readDouble());
                                                    }
                                                    case 117: {
                                                        return new LLSDUUID(new UUID(dataInputStream.readLong(), dataInputStream.readLong()));
                                                    }
                                                    case 98: {
                                                        break Label_0276;
                                                    }
                                                    case 115: {
                                                        break Label_0300;
                                                    }
                                                    case 108: {
                                                        break Label_0343;
                                                    }
                                                    case 100: {
                                                        return new LLSDDate(new Date(Math.round(dataInputStream.readDouble() * 1000.0)));
                                                    }
                                                    case 91: {
                                                        break Label_0418;
                                                    }
                                                    case 123: {
                                                        break Label_0477;
                                                    }
                                                    case 60: {
                                                        break Label_0594;
                                                    }
                                                }
                                            }
                                        }
                                        catch (final IOException cause) {
                                            final LLSDXMLException ex = new LLSDXMLException(cause.getMessage());
                                            ex.initCause(cause);
                                            throw ex;
                                        }
                                        return new LLSDUndefined();
                                    }
                                    while (dataInputStream.readByte() != 62) {}
                                    continue;
                                }
                                return new LLSDUndefined();
                            }
                            final byte[] b = new byte[dataInputStream.readInt()];
                            dataInputStream.readFully(b);
                            return new LLSDBinary(b);
                        }
                        final int int1 = dataInputStream.readInt();
                        if (int1 == 0) {
                            return new LLSDString("");
                        }
                        final byte[] b2 = new byte[int1];
                        dataInputStream.readFully(b2);
                        return new LLSDString(SLMessage.stringFromVariableUTF(b2));
                    }
                    final int int2 = dataInputStream.readInt();
                    if (int2 == 0) {
                        return new LLSDURI("");
                    }
                    final byte[] b3 = new byte[int2];
                    dataInputStream.readFully(b3);
                    return new LLSDURI(SLMessage.stringFromVariableUTF(b3));
                }
                final int int3 = dataInputStream.readInt();
                final LLSDArray llsdArray = new LLSDArray();
                while (i < int3) {
                    llsdArray.add(fromBinary(dataInputStream));
                    ++i;
                }
                if (dataInputStream.readByte() != 93) {
                    throw new LLSDXMLException("Array terminator expected");
                }
                return llsdArray;
            }
            final int int4 = dataInputStream.readInt();
            final HashMap hashMap = new HashMap(int4);
            for (int j = n; j < int4; ++j) {
                if (dataInputStream.readByte() != 107) {
                    throw new LLSDXMLException("Map key expected");
                }
                final byte[] b4 = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(b4);
                hashMap.put((Object)SLMessage.stringFromVariableUTF(b4), (Object)fromBinary(dataInputStream));
            }
            final LLSDMap llsdMap = new LLSDMap((Map<String, LLSDNode>)hashMap);
            if (dataInputStream.readByte() != 125) {
                throw new LLSDXMLException("Map terminator expected");
            }
            return llsdMap;
        }
    }
    
    public static LLSDNode fromBinaryFile(final File p0) throws LLSDXMLException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_1       
        //     4: new             Ljava/io/FileInputStream;
        //     7: astore_2       
        //     8: aload_2        
        //     9: aload_0        
        //    10: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/File;)V
        //    13: aload_1        
        //    14: aload_2        
        //    15: invokespecial   java/io/DataInputStream.<init>:(Ljava/io/InputStream;)V
        //    18: aload_1        
        //    19: invokestatic    com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.fromBinary:(Ljava/io/DataInputStream;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    22: astore_0       
        //    23: aload_1        
        //    24: ifnull          31
        //    27: aload_1        
        //    28: invokevirtual   java/io/DataInputStream.close:()V
        //    31: aload_0        
        //    32: areturn        
        //    33: astore_2       
        //    34: aconst_null    
        //    35: astore_0       
        //    36: aload_0        
        //    37: ifnull          44
        //    40: aload_0        
        //    41: invokevirtual   java/io/DataInputStream.close:()V
        //    44: aload_2        
        //    45: athrow         
        //    46: astore_2       
        //    47: new             Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDXMLException;
        //    50: dup            
        //    51: aload_2        
        //    52: invokevirtual   java/io/IOException.getMessage:()Ljava/lang/String;
        //    55: invokespecial   com/lumiyaviewer/lumiya/slproto/llsd/LLSDXMLException.<init>:(Ljava/lang/String;)V
        //    58: astore_0       
        //    59: aload_0        
        //    60: aload_2        
        //    61: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDXMLException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //    64: pop            
        //    65: aload_0        
        //    66: athrow         
        //    67: astore_2       
        //    68: aload_1        
        //    69: astore_0       
        //    70: goto            36
        //    Exceptions:
        //  throws com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      18     33     36     Any
        //  18     23     67     73     Any
        //  27     31     46     67     Ljava/io/IOException;
        //  40     44     46     67     Ljava/io/IOException;
        //  44     46     46     67     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0031:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static LLSDNode parseXML(final InputStream inputStream, final String s) throws LLSDXMLException {
        try {
            final XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
            pullParser.setInput(inputStream, s);
            pullParser.nextTag();
            pullParser.require(2, (String)null, "llsd");
            pullParser.nextTag();
            final LLSDNode node = LLSDNodeFactory.parseNode(pullParser);
            pullParser.nextTag();
            pullParser.require(3, (String)null, "llsd");
            return node;
        }
        catch (final IOException ex) {
            throw new LLSDXMLException("Input stream error");
        }
        catch (final XmlPullParserException cause) {
            Debug.Log("XmlPullParserException: " + cause.getMessage());
            cause.printStackTrace();
            final LLSDXMLException ex2 = new LLSDXMLException("Malformed XML");
            ex2.initCause((Throwable)cause);
            throw ex2;
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
    
    public LLSDNode byIndex(final int n) throws LLSDException {
        throw new LLSDValueTypeException("array", this);
    }
    
    public LLSDNode byKey(final String s) throws LLSDException {
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
    
    public boolean keyExists(final String s) throws LLSDException {
        throw new LLSDValueTypeException("map", this);
    }
    
    public String serializeToXML() throws IOException {
        final XmlSerializer serializer = Xml.newSerializer();
        final StringWriter output = new StringWriter();
        serializer.setOutput((Writer)output);
        serializer.startTag("", "llsd");
        this.toXML(serializer);
        serializer.endTag("", "llsd");
        serializer.endDocument();
        return output.toString();
    }
    
    public void serializeToXML(final OutputStream outputStream, final String s) throws IOException {
        final XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, s);
        serializer.startTag("", "llsd");
        this.toXML(serializer);
        serializer.endTag("", "llsd");
        serializer.endDocument();
    }
    
    public abstract void toBinary(final DataOutputStream p0) throws IOException;
    
    public <T> T toObject(final Class<? extends T> clazz) throws LLSDException {
        throw new LLSDException("Cannot deserialize " + this.getClass().getName());
    }
    
    public abstract void toXML(final XmlSerializer p0) throws IOException;
}
