package com.google.protobuf.nano;

import com.google.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Extension<M extends ExtendableMessageNano<M>, T> {
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

    /* loaded from: classes.dex */
    private static class PrimitiveExtension<M extends ExtendableMessageNano<M>, T> extends Extension<M, T> {
        private final int nonPackedTag;
        private final int packedTag;

        public PrimitiveExtension(int i, Class<T> cls, int i2, boolean z, int i3, int i4) {
            super(i, cls, i2, z);
            this.nonPackedTag = i3;
            this.packedTag = i4;
        }

        private int computePackedDataSize(Object obj) {
            int i = 0;
            int length = Array.getLength(obj);
            switch (this.type) {
                case 1:
                case 6:
                case 16:
                    return length * 8;
                case 2:
                case 7:
                case 15:
                    return length * 4;
                case 3:
                    int i2 = 0;
                    while (i2 < length) {
                        int computeInt64SizeNoTag = CodedOutputByteBufferNano.computeInt64SizeNoTag(Array.getLong(obj, i2)) + i;
                        i2++;
                        i = computeInt64SizeNoTag;
                    }
                    return i;
                case 4:
                    int i3 = 0;
                    while (i3 < length) {
                        int computeUInt64SizeNoTag = CodedOutputByteBufferNano.computeUInt64SizeNoTag(Array.getLong(obj, i3)) + i;
                        i3++;
                        i = computeUInt64SizeNoTag;
                    }
                    return i;
                case 5:
                    int i4 = 0;
                    while (i4 < length) {
                        int computeInt32SizeNoTag = CodedOutputByteBufferNano.computeInt32SizeNoTag(Array.getInt(obj, i4)) + i;
                        i4++;
                        i = computeInt32SizeNoTag;
                    }
                    return i;
                case 8:
                    return length;
                case 9:
                case 10:
                case 11:
                case 12:
                default:
                    throw new IllegalArgumentException("Unexpected non-packable type " + this.type);
                case 13:
                    int i5 = 0;
                    while (i5 < length) {
                        int computeUInt32SizeNoTag = CodedOutputByteBufferNano.computeUInt32SizeNoTag(Array.getInt(obj, i5)) + i;
                        i5++;
                        i = computeUInt32SizeNoTag;
                    }
                    return i;
                case 14:
                    int i6 = 0;
                    while (i6 < length) {
                        int computeEnumSizeNoTag = CodedOutputByteBufferNano.computeEnumSizeNoTag(Array.getInt(obj, i6)) + i;
                        i6++;
                        i = computeEnumSizeNoTag;
                    }
                    return i;
                case 17:
                    int i7 = 0;
                    while (i7 < length) {
                        int computeSInt32SizeNoTag = CodedOutputByteBufferNano.computeSInt32SizeNoTag(Array.getInt(obj, i7)) + i;
                        i7++;
                        i = computeSInt32SizeNoTag;
                    }
                    return i;
                case 18:
                    int i8 = 0;
                    while (i8 < length) {
                        int computeSInt64SizeNoTag = CodedOutputByteBufferNano.computeSInt64SizeNoTag(Array.getLong(obj, i8)) + i;
                        i8++;
                        i = computeSInt64SizeNoTag;
                    }
                    return i;
            }
        }

        @Override // com.google.protobuf.nano.Extension
        protected int computeRepeatedSerializedSize(Object obj) {
            if (this.tag != this.nonPackedTag) {
                if (this.tag != this.packedTag) {
                    throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
                }
                int computePackedDataSize = computePackedDataSize(obj);
                return computePackedDataSize + CodedOutputByteBufferNano.computeRawVarint32Size(computePackedDataSize) + CodedOutputByteBufferNano.computeRawVarint32Size(this.tag);
            }
            return super.computeRepeatedSerializedSize(obj);
        }

        @Override // com.google.protobuf.nano.Extension
        protected final int computeSingularSerializedSize(Object obj) {
            int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
            switch (this.type) {
                case 1:
                    return CodedOutputByteBufferNano.computeDoubleSize(tagFieldNumber, ((Double) obj).doubleValue());
                case 2:
                    return CodedOutputByteBufferNano.computeFloatSize(tagFieldNumber, ((Float) obj).floatValue());
                case 3:
                    return CodedOutputByteBufferNano.computeInt64Size(tagFieldNumber, ((Long) obj).longValue());
                case 4:
                    return CodedOutputByteBufferNano.computeUInt64Size(tagFieldNumber, ((Long) obj).longValue());
                case 5:
                    return CodedOutputByteBufferNano.computeInt32Size(tagFieldNumber, ((Integer) obj).intValue());
                case 6:
                    return CodedOutputByteBufferNano.computeFixed64Size(tagFieldNumber, ((Long) obj).longValue());
                case 7:
                    return CodedOutputByteBufferNano.computeFixed32Size(tagFieldNumber, ((Integer) obj).intValue());
                case 8:
                    return CodedOutputByteBufferNano.computeBoolSize(tagFieldNumber, ((Boolean) obj).booleanValue());
                case 9:
                    return CodedOutputByteBufferNano.computeStringSize(tagFieldNumber, (String) obj);
                case 10:
                case 11:
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
                case 12:
                    return CodedOutputByteBufferNano.computeBytesSize(tagFieldNumber, (byte[]) obj);
                case 13:
                    return CodedOutputByteBufferNano.computeUInt32Size(tagFieldNumber, ((Integer) obj).intValue());
                case 14:
                    return CodedOutputByteBufferNano.computeEnumSize(tagFieldNumber, ((Integer) obj).intValue());
                case 15:
                    return CodedOutputByteBufferNano.computeSFixed32Size(tagFieldNumber, ((Integer) obj).intValue());
                case 16:
                    return CodedOutputByteBufferNano.computeSFixed64Size(tagFieldNumber, ((Long) obj).longValue());
                case 17:
                    return CodedOutputByteBufferNano.computeSInt32Size(tagFieldNumber, ((Integer) obj).intValue());
                case 18:
                    return CodedOutputByteBufferNano.computeSInt64Size(tagFieldNumber, ((Long) obj).longValue());
            }
        }

        @Override // com.google.protobuf.nano.Extension
        protected Object readData(CodedInputByteBufferNano codedInputByteBufferNano) {
            try {
                return codedInputByteBufferNano.readPrimitiveField(this.type);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        @Override // com.google.protobuf.nano.Extension
        protected void readDataInto(UnknownFieldData unknownFieldData, List<Object> list) {
            if (unknownFieldData.tag == this.nonPackedTag) {
                list.add(readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
                return;
            }
            CodedInputByteBufferNano newInstance = CodedInputByteBufferNano.newInstance(unknownFieldData.bytes);
            try {
                newInstance.pushLimit(newInstance.readRawVarint32());
                while (!newInstance.isAtEnd()) {
                    list.add(readData(newInstance));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        @Override // com.google.protobuf.nano.Extension
        protected void writeRepeatedData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
            int i = 0;
            if (this.tag == this.nonPackedTag) {
                super.writeRepeatedData(obj, codedOutputByteBufferNano);
            } else if (this.tag != this.packedTag) {
                throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
            } else {
                int length = Array.getLength(obj);
                int computePackedDataSize = computePackedDataSize(obj);
                try {
                    codedOutputByteBufferNano.writeRawVarint32(this.tag);
                    codedOutputByteBufferNano.writeRawVarint32(computePackedDataSize);
                    switch (this.type) {
                        case 1:
                            while (i < length) {
                                codedOutputByteBufferNano.writeDoubleNoTag(Array.getDouble(obj, i));
                                i++;
                            }
                            return;
                        case 2:
                            while (i < length) {
                                codedOutputByteBufferNano.writeFloatNoTag(Array.getFloat(obj, i));
                                i++;
                            }
                            return;
                        case 3:
                            while (i < length) {
                                codedOutputByteBufferNano.writeInt64NoTag(Array.getLong(obj, i));
                                i++;
                            }
                            return;
                        case 4:
                            while (i < length) {
                                codedOutputByteBufferNano.writeUInt64NoTag(Array.getLong(obj, i));
                                i++;
                            }
                            return;
                        case 5:
                            while (i < length) {
                                codedOutputByteBufferNano.writeInt32NoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 6:
                            while (i < length) {
                                codedOutputByteBufferNano.writeFixed64NoTag(Array.getLong(obj, i));
                                i++;
                            }
                            return;
                        case 7:
                            while (i < length) {
                                codedOutputByteBufferNano.writeFixed32NoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 8:
                            while (i < length) {
                                codedOutputByteBufferNano.writeBoolNoTag(Array.getBoolean(obj, i));
                                i++;
                            }
                            return;
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        default:
                            throw new IllegalArgumentException("Unpackable type " + this.type);
                        case 13:
                            while (i < length) {
                                codedOutputByteBufferNano.writeUInt32NoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 14:
                            while (i < length) {
                                codedOutputByteBufferNano.writeEnumNoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 15:
                            while (i < length) {
                                codedOutputByteBufferNano.writeSFixed32NoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 16:
                            while (i < length) {
                                codedOutputByteBufferNano.writeSFixed64NoTag(Array.getLong(obj, i));
                                i++;
                            }
                            return;
                        case 17:
                            while (i < length) {
                                codedOutputByteBufferNano.writeSInt32NoTag(Array.getInt(obj, i));
                                i++;
                            }
                            return;
                        case 18:
                            while (i < length) {
                                codedOutputByteBufferNano.writeSInt64NoTag(Array.getLong(obj, i));
                                i++;
                            }
                            return;
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        @Override // com.google.protobuf.nano.Extension
        protected final void writeSingularData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
            try {
                codedOutputByteBufferNano.writeRawVarint32(this.tag);
                switch (this.type) {
                    case 1:
                        codedOutputByteBufferNano.writeDoubleNoTag(((Double) obj).doubleValue());
                        return;
                    case 2:
                        codedOutputByteBufferNano.writeFloatNoTag(((Float) obj).floatValue());
                        return;
                    case 3:
                        codedOutputByteBufferNano.writeInt64NoTag(((Long) obj).longValue());
                        return;
                    case 4:
                        codedOutputByteBufferNano.writeUInt64NoTag(((Long) obj).longValue());
                        return;
                    case 5:
                        codedOutputByteBufferNano.writeInt32NoTag(((Integer) obj).intValue());
                        return;
                    case 6:
                        codedOutputByteBufferNano.writeFixed64NoTag(((Long) obj).longValue());
                        return;
                    case 7:
                        codedOutputByteBufferNano.writeFixed32NoTag(((Integer) obj).intValue());
                        return;
                    case 8:
                        codedOutputByteBufferNano.writeBoolNoTag(((Boolean) obj).booleanValue());
                        return;
                    case 9:
                        codedOutputByteBufferNano.writeStringNoTag((String) obj);
                        return;
                    case 10:
                    case 11:
                    default:
                        throw new IllegalArgumentException("Unknown type " + this.type);
                    case 12:
                        codedOutputByteBufferNano.writeBytesNoTag((byte[]) obj);
                        return;
                    case 13:
                        codedOutputByteBufferNano.writeUInt32NoTag(((Integer) obj).intValue());
                        return;
                    case 14:
                        codedOutputByteBufferNano.writeEnumNoTag(((Integer) obj).intValue());
                        return;
                    case 15:
                        codedOutputByteBufferNano.writeSFixed32NoTag(((Integer) obj).intValue());
                        return;
                    case 16:
                        codedOutputByteBufferNano.writeSFixed64NoTag(((Long) obj).longValue());
                        return;
                    case 17:
                        codedOutputByteBufferNano.writeSInt32NoTag(((Integer) obj).intValue());
                        return;
                    case 18:
                        codedOutputByteBufferNano.writeSInt64NoTag(((Long) obj).longValue());
                        return;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private Extension(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.clazz = cls;
        this.tag = i2;
        this.repeated = z;
    }

    @Deprecated
    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int i, Class<T> cls, int i2) {
        return new Extension<>(i, cls, i2, false);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T> createMessageTyped(int i, Class<T> cls, long j) {
        return new Extension<>(i, cls, (int) j, false);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createPrimitiveTyped(int i, Class<T> cls, long j) {
        return new PrimitiveExtension(i, cls, (int) j, false, 0, 0);
    }

    public static <M extends ExtendableMessageNano<M>, T extends MessageNano> Extension<M, T[]> createRepeatedMessageTyped(int i, Class<T[]> cls, long j) {
        return new Extension<>(i, cls, (int) j, true);
    }

    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createRepeatedPrimitiveTyped(int i, Class<T> cls, long j, long j2, long j3) {
        return new PrimitiveExtension(i, cls, (int) j, true, (int) j2, (int) j3);
    }

    private T getRepeatedValueFrom(List<UnknownFieldData> list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            UnknownFieldData unknownFieldData = list.get(i);
            if (unknownFieldData.bytes.length != 0) {
                readDataInto(unknownFieldData, arrayList);
            }
        }
        int size = arrayList.size();
        if (size != 0) {
            T cast = this.clazz.cast(Array.newInstance(this.clazz.getComponentType(), size));
            for (int i2 = 0; i2 < size; i2++) {
                Array.set(cast, i2, arrayList.get(i2));
            }
            return cast;
        }
        return null;
    }

    private T getSingularValueFrom(List<UnknownFieldData> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.clazz.cast(readData(CodedInputByteBufferNano.newInstance(list.get(list.size() - 1).bytes)));
    }

    protected int computeRepeatedSerializedSize(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += computeSingularSerializedSize(Array.get(obj, i2));
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeSerializedSize(Object obj) {
        return !this.repeated ? computeSingularSerializedSize(obj) : computeRepeatedSerializedSize(obj);
    }

    protected int computeSingularSerializedSize(Object obj) {
        int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
        switch (this.type) {
            case 10:
                return CodedOutputByteBufferNano.computeGroupSize(tagFieldNumber, (MessageNano) obj);
            case 11:
                return CodedOutputByteBufferNano.computeMessageSize(tagFieldNumber, (MessageNano) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final T getValueFrom(List<UnknownFieldData> list) {
        if (list != null) {
            return !this.repeated ? getSingularValueFrom(list) : getRepeatedValueFrom(list);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected Object readData(CodedInputByteBufferNano codedInputByteBufferNano) {
        Class componentType = !this.repeated ? (Class<T>) this.clazz : this.clazz.getComponentType();
        try {
            switch (this.type) {
                case 10:
                    MessageNano messageNano = (MessageNano) componentType.newInstance();
                    codedInputByteBufferNano.readGroup(messageNano, WireFormatNano.getTagFieldNumber(this.tag));
                    return messageNano;
                case 11:
                    MessageNano messageNano2 = (MessageNano) componentType.newInstance();
                    codedInputByteBufferNano.readMessage(messageNano2);
                    return messageNano2;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading extension field", e);
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e2);
        } catch (InstantiationException e3) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e3);
        }
    }

    protected void readDataInto(UnknownFieldData unknownFieldData, List<Object> list) {
        list.add(readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
    }

    protected void writeRepeatedData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                writeSingularData(obj2, codedOutputByteBufferNano);
            }
        }
    }

    protected void writeSingularData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
        try {
            codedOutputByteBufferNano.writeRawVarint32(this.tag);
            switch (this.type) {
                case 10:
                    int tagFieldNumber = WireFormatNano.getTagFieldNumber(this.tag);
                    codedOutputByteBufferNano.writeGroupNoTag((MessageNano) obj);
                    codedOutputByteBufferNano.writeTag(tagFieldNumber, 4);
                    return;
                case 11:
                    codedOutputByteBufferNano.writeMessageNoTag((MessageNano) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeTo(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.repeated) {
            writeRepeatedData(obj, codedOutputByteBufferNano);
        } else {
            writeSingularData(obj, codedOutputByteBufferNano);
        }
    }
}
