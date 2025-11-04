// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Extension<M extends ExtendableMessageNano<M>, T>
{
    public static final int TYPE_BOOL = 8;
    public static final int TYPE_BYTES = 12;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_ENUM = 14;
    public static final int TYPE_FIXED32 = 7;
    public static final int TYPE_FIXED64 = 6;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_GROUP = 10;
    public static final int TYPE_INT32 = 5;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_MESSAGE = 11;
    public static final int TYPE_SFIXED32 = 15;
    public static final int TYPE_SFIXED64 = 16;
    public static final int TYPE_SINT32 = 17;
    public static final int TYPE_SINT64 = 18;
    public static final int TYPE_STRING = 9;
    public static final int TYPE_UINT32 = 13;
    public static final int TYPE_UINT64 = 4;
    protected final Class<T> clazz;
    protected final boolean repeated;
    public final int tag;
    protected final int type;
    
    private Extension(final int type, final Class<T> clazz, final int tag, final boolean repeated) {
        this.type = type;
        this.clazz = clazz;
        this.tag = tag;
        this.repeated = repeated;
    }
    
    @Deprecated
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(final int n, final Class<T> clazz, final int n2) {
        return new Extension<M, T>(n, clazz, n2, false);
    }
    
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(final int n, final Class<T> clazz, final long n2) {
        return new Extension<M, T>(n, clazz, (int)n2, false);
    }
    
    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createPrimitiveTyped(final int n, final Class<T> clazz, final long n2) {
        return new PrimitiveExtension<M, T>(n, clazz, (int)n2, false, 0, 0);
    }
    
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T[]> createRepeatedMessageTyped(final int n, final Class<T[]> clazz, final long n2) {
        return new Extension<M, T[]>(n, clazz, (int)n2, true);
    }
    
    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createRepeatedPrimitiveTyped(final int n, final Class<T> clazz, final long n2, final long n3, final long n4) {
        return new PrimitiveExtension<M, T>(n, clazz, (int)n2, true, (int)n3, (int)n4);
    }
    
    private T getRepeatedValueFrom(final List<UnknownFieldData> list) {
        final int n = 0;
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            final UnknownFieldData unknownFieldData = list.get(i);
            if (unknownFieldData.bytes.length != 0) {
                this.readDataInto(unknownFieldData, list2);
            }
        }
        final int size = list2.size();
        if (size != 0) {
            final T cast = this.clazz.cast(Array.newInstance(this.clazz.getComponentType(), size));
            for (int j = n; j < size; ++j) {
                Array.set(cast, j, list2.get(j));
            }
            return cast;
        }
        return null;
    }
    
    private T getSingularValueFrom(final List<UnknownFieldData> list) {
        if (!list.isEmpty()) {
            return this.clazz.cast(this.readData(CodedInputByteBufferNano.newInstance(list.get(list.size() - 1).bytes)));
        }
        return null;
    }
    
    protected int computeRepeatedSerializedSize(final Object o) {
        int n = 0;
        for (int length = Array.getLength(o), i = 0; i < length; ++i) {
            if (Array.get(o, i) != null) {
                n += this.computeSingularSerializedSize(Array.get(o, i));
            }
        }
        return n;
    }
    
    int computeSerializedSize(final Object o) {
        if (!this.repeated) {
            return this.computeSingularSerializedSize(o);
        }
        return this.computeRepeatedSerializedSize(o);
    }
    
    protected int computeSingularSerializedSize(final Object o) {
        final int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
        switch (this.type) {
            default: {
                throw new IllegalArgumentException("Unknown type " + this.type);
            }
            case 10: {
                return CodedOutputByteBufferNano.computeGroupSize(tagFieldNumber, (MessageNano)o);
            }
            case 11: {
                return CodedOutputByteBufferNano.computeMessageSize(tagFieldNumber, (MessageNano)o);
            }
        }
    }
    
    final T getValueFrom(final List<UnknownFieldData> list) {
        if (list != null) {
            T t;
            if (!this.repeated) {
                t = this.getSingularValueFrom(list);
            }
            else {
                t = this.getRepeatedValueFrom(list);
            }
            return t;
        }
        return null;
    }
    
    protected Object readData(final CodedInputByteBufferNano codedInputByteBufferNano) {
        if (this.repeated) {
            goto Label_0103;
        }
        final Class<T> clazz = this.clazz;
        try {
            switch (this.type) {
                default: {
                    throw new IllegalArgumentException("Unknown type " + this.type);
                }
                case 10: {
                    goto Label_0114;
                    goto Label_0114;
                }
                case 11: {
                    goto Label_0136;
                    goto Label_0136;
                }
            }
        }
        catch (final InstantiationException cause) {
            throw new IllegalArgumentException("Error creating instance of class " + clazz, cause);
        }
        catch (final IllegalAccessException cause2) {
            throw new IllegalArgumentException("Error creating instance of class " + clazz, cause2);
        }
        catch (final IOException cause3) {
            throw new IllegalArgumentException("Error reading extension field", cause3);
        }
    }
    
    protected void readDataInto(final UnknownFieldData unknownFieldData, final List<Object> list) {
        list.add(this.readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
    }
    
    protected void writeRepeatedData(final Object o, final CodedOutputByteBufferNano codedOutputByteBufferNano) {
        for (int length = Array.getLength(o), i = 0; i < length; ++i) {
            final Object value = Array.get(o, i);
            if (value != null) {
                this.writeSingularData(value, codedOutputByteBufferNano);
            }
        }
    }
    
    protected void writeSingularData(Object o, final CodedOutputByteBufferNano codedOutputByteBufferNano) {
        Label_0105: {
            try {
                codedOutputByteBufferNano.writeRawVarint32(this.tag);
                switch (this.type) {
                    default: {
                        o = new IllegalArgumentException("Unknown type " + this.type);
                        throw o;
                    }
                    case 10: {
                        break;
                    }
                    case 11: {
                        break Label_0105;
                    }
                }
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
            final MessageNano messageNano = (MessageNano)o;
            final int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
            codedOutputByteBufferNano.writeGroupNoTag(messageNano);
            codedOutputByteBufferNano.writeTag(tagFieldNumber, 4);
            return;
        }
        codedOutputByteBufferNano.writeMessageNoTag((MessageNano)o);
    }
    
    void writeTo(final Object o, final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (!this.repeated) {
            this.writeSingularData(o, codedOutputByteBufferNano);
        }
        else {
            this.writeRepeatedData(o, codedOutputByteBufferNano);
        }
    }
    
    private static class PrimitiveExtension<M extends ExtendableMessageNano<M>, T> extends Extension<M, T>
    {
        private final int nonPackedTag;
        private final int packedTag;
        
        public PrimitiveExtension(final int n, final Class<T> clazz, final int n2, final boolean b, final int nonPackedTag, final int packedTag) {
            super(n, clazz, n2, b, null);
            this.nonPackedTag = nonPackedTag;
            this.packedTag = packedTag;
        }
        
        private int computePackedDataSize(final Object o) {
            final int n = 0;
            final int n2 = 0;
            final int n3 = 0;
            final int n4 = 0;
            final int n5 = 0;
            final int n6 = 0;
            int n7 = 0;
            final int length = Array.getLength(o);
            int n8 = 0;
            Label_0149: {
                switch (this.type) {
                    default: {
                        throw new IllegalArgumentException("Unexpected non-packable type " + this.type);
                    }
                    case 8: {
                        n8 = length;
                        break;
                    }
                    case 2:
                    case 7:
                    case 15: {
                        n8 = length * 4;
                        break;
                    }
                    case 1:
                    case 6:
                    case 16: {
                        n8 = length * 8;
                        break;
                    }
                    case 5: {
                        int n9 = 0;
                        while (true) {
                            n8 = n7;
                            if (n9 >= length) {
                                break Label_0149;
                            }
                            final int computeInt32SizeNoTag = CodedOutputByteBufferNano.computeInt32SizeNoTag(Array.getInt(o, n9));
                            ++n9;
                            n7 += computeInt32SizeNoTag;
                        }
                        break;
                    }
                    case 17: {
                        int n10 = 0;
                        int n11 = n;
                        while (true) {
                            n8 = n11;
                            if (n10 >= length) {
                                break Label_0149;
                            }
                            final int computeSInt32SizeNoTag = CodedOutputByteBufferNano.computeSInt32SizeNoTag(Array.getInt(o, n10));
                            ++n10;
                            n11 += computeSInt32SizeNoTag;
                        }
                        break;
                    }
                    case 13: {
                        int n12 = 0;
                        int n13 = n2;
                        while (true) {
                            n8 = n13;
                            if (n12 >= length) {
                                break Label_0149;
                            }
                            final int computeUInt32SizeNoTag = CodedOutputByteBufferNano.computeUInt32SizeNoTag(Array.getInt(o, n12));
                            ++n12;
                            n13 += computeUInt32SizeNoTag;
                        }
                        break;
                    }
                    case 3: {
                        int n14 = 0;
                        int n15 = n3;
                        while (true) {
                            n8 = n15;
                            if (n14 >= length) {
                                break Label_0149;
                            }
                            final int computeInt64SizeNoTag = CodedOutputByteBufferNano.computeInt64SizeNoTag(Array.getLong(o, n14));
                            ++n14;
                            n15 += computeInt64SizeNoTag;
                        }
                        break;
                    }
                    case 18: {
                        int n16 = 0;
                        int n17 = n4;
                        while (true) {
                            n8 = n17;
                            if (n16 >= length) {
                                break Label_0149;
                            }
                            final int computeSInt64SizeNoTag = CodedOutputByteBufferNano.computeSInt64SizeNoTag(Array.getLong(o, n16));
                            ++n16;
                            n17 += computeSInt64SizeNoTag;
                        }
                        break;
                    }
                    case 4: {
                        int n18 = 0;
                        int n19 = n5;
                        while (true) {
                            n8 = n19;
                            if (n18 >= length) {
                                break Label_0149;
                            }
                            final int computeUInt64SizeNoTag = CodedOutputByteBufferNano.computeUInt64SizeNoTag(Array.getLong(o, n18));
                            ++n18;
                            n19 += computeUInt64SizeNoTag;
                        }
                        break;
                    }
                    case 14: {
                        int n20 = 0;
                        int n21 = n6;
                        while (true) {
                            n8 = n21;
                            if (n20 >= length) {
                                break Label_0149;
                            }
                            final int computeEnumSizeNoTag = CodedOutputByteBufferNano.computeEnumSizeNoTag(Array.getInt(o, n20));
                            ++n20;
                            n21 += computeEnumSizeNoTag;
                        }
                        break;
                    }
                }
            }
            return n8;
        }
        
        @Override
        protected int computeRepeatedSerializedSize(final Object o) {
            if (this.tag == this.nonPackedTag) {
                return super.computeRepeatedSerializedSize(o);
            }
            if (this.tag != this.packedTag) {
                throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
            }
            final int computePackedDataSize = this.computePackedDataSize(o);
            return computePackedDataSize + CodedOutputByteBufferNano.computeRawVarint32Size(computePackedDataSize) + CodedOutputByteBufferNano.computeRawVarint32Size(this.tag);
        }
        
        @Override
        protected final int computeSingularSerializedSize(final Object o) {
            final int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
            switch (this.type) {
                default: {
                    throw new IllegalArgumentException("Unknown type " + this.type);
                }
                case 1: {
                    return CodedOutputByteBufferNano.computeDoubleSize(tagFieldNumber, (double)o);
                }
                case 2: {
                    return CodedOutputByteBufferNano.computeFloatSize(tagFieldNumber, (float)o);
                }
                case 3: {
                    return CodedOutputByteBufferNano.computeInt64Size(tagFieldNumber, (long)o);
                }
                case 4: {
                    return CodedOutputByteBufferNano.computeUInt64Size(tagFieldNumber, (long)o);
                }
                case 5: {
                    return CodedOutputByteBufferNano.computeInt32Size(tagFieldNumber, (int)o);
                }
                case 6: {
                    return CodedOutputByteBufferNano.computeFixed64Size(tagFieldNumber, (long)o);
                }
                case 7: {
                    return CodedOutputByteBufferNano.computeFixed32Size(tagFieldNumber, (int)o);
                }
                case 8: {
                    return CodedOutputByteBufferNano.computeBoolSize(tagFieldNumber, (boolean)o);
                }
                case 9: {
                    return CodedOutputByteBufferNano.computeStringSize(tagFieldNumber, (String)o);
                }
                case 12: {
                    return CodedOutputByteBufferNano.computeBytesSize(tagFieldNumber, (byte[])o);
                }
                case 13: {
                    return CodedOutputByteBufferNano.computeUInt32Size(tagFieldNumber, (int)o);
                }
                case 14: {
                    return CodedOutputByteBufferNano.computeEnumSize(tagFieldNumber, (int)o);
                }
                case 15: {
                    return CodedOutputByteBufferNano.computeSFixed32Size(tagFieldNumber, (int)o);
                }
                case 16: {
                    return CodedOutputByteBufferNano.computeSFixed64Size(tagFieldNumber, (long)o);
                }
                case 17: {
                    return CodedOutputByteBufferNano.computeSInt32Size(tagFieldNumber, (int)o);
                }
                case 18: {
                    return CodedOutputByteBufferNano.computeSInt64Size(tagFieldNumber, (long)o);
                }
            }
        }
        
        @Override
        protected Object readData(final CodedInputByteBufferNano codedInputByteBufferNano) {
            try {
                return codedInputByteBufferNano.readPrimitiveField(this.type);
            }
            catch (final IOException cause) {
                throw new IllegalArgumentException("Error reading extension field", cause);
            }
        }
        
        @Override
        protected void readDataInto(final UnknownFieldData unknownFieldData, final List<Object> list) {
            Label_0036: {
                if (unknownFieldData.tag == this.nonPackedTag) {
                    break Label_0036;
                }
                while (true) {
                    final CodedInputByteBufferNano instance = CodedInputByteBufferNano.newInstance(unknownFieldData.bytes);
                    while (true) {
                        Label_0069: {
                            try {
                                instance.pushLimit(instance.readRawVarint32());
                                if (!instance.isAtEnd()) {
                                    break Label_0069;
                                }
                                return;
                                list.add(this.readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
                                return;
                            }
                            catch (final IOException cause) {
                                throw new IllegalArgumentException("Error reading extension field", cause);
                            }
                        }
                        list.add(this.readData(instance));
                        continue;
                    }
                }
            }
        }
        
        @Override
        protected void writeRepeatedData(Object o, final CodedOutputByteBufferNano codedOutputByteBufferNano) {
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            int n7 = 0;
            int n8 = 0;
            int n9 = 0;
            int n10 = 0;
            if (this.tag != this.nonPackedTag) {
                if (this.tag != this.packedTag) {
                    throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
                }
                final int length = Array.getLength(o);
                final int computePackedDataSize = this.computePackedDataSize(o);
                while (true) {
                    Label_0298: {
                        Label_0595: {
                            Label_0572: {
                                Label_0549: {
                                    Label_0526: {
                                        Label_0503: {
                                            Label_0480: {
                                                Label_0457: {
                                                    Label_0434: {
                                                        Label_0411: {
                                                            Label_0388: {
                                                                Label_0365: {
                                                                    Label_0342: {
                                                                        Label_0320: {
                                                                            try {
                                                                                codedOutputByteBufferNano.writeRawVarint32(this.tag);
                                                                                codedOutputByteBufferNano.writeRawVarint32(computePackedDataSize);
                                                                                switch (this.type) {
                                                                                    default: {
                                                                                        o = new IllegalArgumentException("Unpackable type " + this.type);
                                                                                        throw o;
                                                                                    }
                                                                                    case 8: {
                                                                                        break Label_0298;
                                                                                    }
                                                                                    case 7: {
                                                                                        break Label_0320;
                                                                                    }
                                                                                    case 15: {
                                                                                        break Label_0342;
                                                                                    }
                                                                                    case 2: {
                                                                                        break Label_0365;
                                                                                    }
                                                                                    case 6: {
                                                                                        break Label_0388;
                                                                                    }
                                                                                    case 16: {
                                                                                        break Label_0411;
                                                                                    }
                                                                                    case 1: {
                                                                                        break Label_0434;
                                                                                    }
                                                                                    case 5: {
                                                                                        break Label_0457;
                                                                                    }
                                                                                    case 17: {
                                                                                        break Label_0480;
                                                                                    }
                                                                                    case 13: {
                                                                                        break Label_0503;
                                                                                    }
                                                                                    case 3: {
                                                                                        break Label_0526;
                                                                                    }
                                                                                    case 18: {
                                                                                        break Label_0549;
                                                                                    }
                                                                                    case 4: {
                                                                                        break Label_0572;
                                                                                    }
                                                                                    case 14: {
                                                                                        break Label_0595;
                                                                                    }
                                                                                }
                                                                            }
                                                                            catch (final IOException cause) {
                                                                                throw new IllegalStateException(cause);
                                                                            }
                                                                            break Label_0285;
                                                                        }
                                                                        while (i < length) {
                                                                            codedOutputByteBufferNano.writeFixed32NoTag(Array.getInt(o, i));
                                                                            ++i;
                                                                        }
                                                                        return;
                                                                    }
                                                                    while (j < length) {
                                                                        codedOutputByteBufferNano.writeSFixed32NoTag(Array.getInt(o, j));
                                                                        ++j;
                                                                    }
                                                                    return;
                                                                }
                                                                while (k < length) {
                                                                    codedOutputByteBufferNano.writeFloatNoTag(Array.getFloat(o, k));
                                                                    ++k;
                                                                }
                                                                return;
                                                            }
                                                            while (l < length) {
                                                                codedOutputByteBufferNano.writeFixed64NoTag(Array.getLong(o, l));
                                                                ++l;
                                                            }
                                                            return;
                                                        }
                                                        while (n < length) {
                                                            codedOutputByteBufferNano.writeSFixed64NoTag(Array.getLong(o, n));
                                                            ++n;
                                                        }
                                                        return;
                                                    }
                                                    while (n2 < length) {
                                                        codedOutputByteBufferNano.writeDoubleNoTag(Array.getDouble(o, n2));
                                                        ++n2;
                                                    }
                                                    return;
                                                }
                                                while (n3 < length) {
                                                    codedOutputByteBufferNano.writeInt32NoTag(Array.getInt(o, n3));
                                                    ++n3;
                                                }
                                                return;
                                            }
                                            while (n4 < length) {
                                                codedOutputByteBufferNano.writeSInt32NoTag(Array.getInt(o, n4));
                                                ++n4;
                                            }
                                            return;
                                        }
                                        while (n5 < length) {
                                            codedOutputByteBufferNano.writeUInt32NoTag(Array.getInt(o, n5));
                                            ++n5;
                                        }
                                        return;
                                    }
                                    while (n6 < length) {
                                        codedOutputByteBufferNano.writeInt64NoTag(Array.getLong(o, n6));
                                        ++n6;
                                    }
                                    return;
                                }
                                while (n7 < length) {
                                    codedOutputByteBufferNano.writeSInt64NoTag(Array.getLong(o, n7));
                                    ++n7;
                                }
                                return;
                            }
                            while (n8 < length) {
                                codedOutputByteBufferNano.writeUInt64NoTag(Array.getLong(o, n8));
                                ++n8;
                            }
                            return;
                        }
                        while (n9 < length) {
                            codedOutputByteBufferNano.writeEnumNoTag(Array.getInt(o, n9));
                            ++n9;
                        }
                        return;
                        codedOutputByteBufferNano.writeBoolNoTag(Array.getBoolean(o, n10));
                        ++n10;
                    }
                    if (n10 < length) {
                        continue;
                    }
                    break;
                }
            }
            else {
                super.writeRepeatedData(o, codedOutputByteBufferNano);
            }
        }
        
        @Override
        protected final void writeSingularData(Object o, final CodedOutputByteBufferNano codedOutputByteBufferNano) {
            Label_0346: {
                Label_0332: {
                    Label_0318: {
                        Label_0304: {
                            Label_0290: {
                                Label_0276: {
                                    Label_0265: {
                                        Label_0254: {
                                            Label_0240: {
                                                Label_0226: {
                                                    Label_0212: {
                                                        Label_0198: {
                                                            Label_0184: {
                                                                Label_0170: {
                                                                    Label_0156: {
                                                                        try {
                                                                            codedOutputByteBufferNano.writeRawVarint32(this.tag);
                                                                            switch (this.type) {
                                                                                default: {
                                                                                    o = new StringBuilder();
                                                                                    throw new IllegalArgumentException(((StringBuilder)o).append("Unknown type ").append(this.type).toString());
                                                                                }
                                                                                case 1: {
                                                                                    break;
                                                                                }
                                                                                case 2: {
                                                                                    break Label_0156;
                                                                                }
                                                                                case 3: {
                                                                                    break Label_0170;
                                                                                }
                                                                                case 4: {
                                                                                    break Label_0184;
                                                                                }
                                                                                case 5: {
                                                                                    break Label_0198;
                                                                                }
                                                                                case 6: {
                                                                                    break Label_0212;
                                                                                }
                                                                                case 7: {
                                                                                    break Label_0226;
                                                                                }
                                                                                case 8: {
                                                                                    break Label_0240;
                                                                                }
                                                                                case 9: {
                                                                                    break Label_0254;
                                                                                }
                                                                                case 12: {
                                                                                    break Label_0265;
                                                                                }
                                                                                case 13: {
                                                                                    break Label_0276;
                                                                                }
                                                                                case 14: {
                                                                                    break Label_0290;
                                                                                }
                                                                                case 15: {
                                                                                    break Label_0304;
                                                                                }
                                                                                case 16: {
                                                                                    break Label_0318;
                                                                                }
                                                                                case 17: {
                                                                                    break Label_0332;
                                                                                }
                                                                                case 18: {
                                                                                    break Label_0346;
                                                                                }
                                                                            }
                                                                        }
                                                                        catch (final IOException cause) {
                                                                            throw new IllegalStateException(cause);
                                                                        }
                                                                        codedOutputByteBufferNano.writeDoubleNoTag((double)o);
                                                                        return;
                                                                    }
                                                                    codedOutputByteBufferNano.writeFloatNoTag((float)o);
                                                                    return;
                                                                }
                                                                codedOutputByteBufferNano.writeInt64NoTag((long)o);
                                                                return;
                                                            }
                                                            codedOutputByteBufferNano.writeUInt64NoTag((long)o);
                                                            return;
                                                        }
                                                        codedOutputByteBufferNano.writeInt32NoTag((int)o);
                                                        return;
                                                    }
                                                    codedOutputByteBufferNano.writeFixed64NoTag((long)o);
                                                    return;
                                                }
                                                codedOutputByteBufferNano.writeFixed32NoTag((int)o);
                                                return;
                                            }
                                            codedOutputByteBufferNano.writeBoolNoTag((boolean)o);
                                            return;
                                        }
                                        codedOutputByteBufferNano.writeStringNoTag((String)o);
                                        return;
                                    }
                                    codedOutputByteBufferNano.writeBytesNoTag((byte[])o);
                                    return;
                                }
                                codedOutputByteBufferNano.writeUInt32NoTag((int)o);
                                return;
                            }
                            codedOutputByteBufferNano.writeEnumNoTag((int)o);
                            return;
                        }
                        codedOutputByteBufferNano.writeSFixed32NoTag((int)o);
                        return;
                    }
                    codedOutputByteBufferNano.writeSFixed64NoTag((long)o);
                    return;
                }
                codedOutputByteBufferNano.writeSInt32NoTag((int)o);
                return;
            }
            codedOutputByteBufferNano.writeSInt64NoTag((long)o);
        }
    }
}
