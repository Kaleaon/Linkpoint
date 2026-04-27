package com.google.protobuf.nano;

import com.google.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
                default:
                    throw new IllegalArgumentException("Unexpected non-packable type " + this.type);
            }
        }

        /* access modifiers changed from: protected */
        public int computeRepeatedSerializedSize(Object obj) {
            if (this.tag == this.nonPackedTag) {
                return Extension.super.computeRepeatedSerializedSize(obj);
            }
            if (this.tag != this.packedTag) {
                throw new IllegalArgumentException("Unexpected repeated extension tag " + this.tag + ", unequal to both non-packed variant " + this.nonPackedTag + " and packed variant " + this.packedTag);
            }
            int computePackedDataSize = computePackedDataSize(obj);
            return computePackedDataSize + CodedOutputByteBufferNano.computeRawVarint32Size(computePackedDataSize) + CodedOutputByteBufferNano.computeRawVarint32Size(this.tag);
        }

        /* access modifiers changed from: protected */
        public final int computeSingularSerializedSize(Object obj) {
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
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        }

        /* access modifiers changed from: protected */
        public Object readData(CodedInputByteBufferNano codedInputByteBufferNano) {
            try {
                return codedInputByteBufferNano.readPrimitiveField(this.type);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }
        }

        /* access modifiers changed from: protected */
        public void readDataInto(UnknownFieldData unknownFieldData, List<Object> list) {
            if (unknownFieldData.tag != this.nonPackedTag) {
                CodedInputByteBufferNano newInstance = CodedInputByteBufferNano.newInstance(unknownFieldData.bytes);
                try {
                    newInstance.pushLimit(newInstance.readRawVarint32());
                    while (!newInstance.isAtEnd()) {
                        list.add(readData(newInstance));
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error reading extension field", e);
                }
            } else {
                list.add(readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x008b, code lost:
            r6.writeFixed32NoTag(java.lang.reflect.Array.getInt(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0094, code lost:
            if (r0 < r1) goto L_0x008b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0097, code lost:
            r6.writeSFixed32NoTag(java.lang.reflect.Array.getInt(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a0, code lost:
            if (r0 < r1) goto L_0x0097;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a3, code lost:
            r6.writeFloatNoTag(java.lang.reflect.Array.getFloat(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x00ac, code lost:
            if (r0 < r1) goto L_0x00a3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00af, code lost:
            r6.writeFixed64NoTag(java.lang.reflect.Array.getLong(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b8, code lost:
            if (r0 < r1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bb, code lost:
            r6.writeSFixed64NoTag(java.lang.reflect.Array.getLong(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c4, code lost:
            if (r0 < r1) goto L_0x00bb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c7, code lost:
            r6.writeDoubleNoTag(java.lang.reflect.Array.getDouble(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d0, code lost:
            if (r0 < r1) goto L_0x00c7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d4, code lost:
            r6.writeInt32NoTag(java.lang.reflect.Array.getInt(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00dd, code lost:
            if (r0 < r1) goto L_0x00d4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00e1, code lost:
            r6.writeSInt32NoTag(java.lang.reflect.Array.getInt(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00ea, code lost:
            if (r0 < r1) goto L_0x00e1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ee, code lost:
            r6.writeUInt32NoTag(java.lang.reflect.Array.getInt(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f7, code lost:
            if (r0 < r1) goto L_0x00ee;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00fb, code lost:
            r6.writeInt64NoTag(java.lang.reflect.Array.getLong(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x0104, code lost:
            if (r0 < r1) goto L_0x00fb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0108, code lost:
            r6.writeSInt64NoTag(java.lang.reflect.Array.getLong(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x0111, code lost:
            if (r0 < r1) goto L_0x0108;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x0115, code lost:
            r6.writeUInt64NoTag(java.lang.reflect.Array.getLong(r5, r0));
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x011e, code lost:
            if (r0 < r1) goto L_0x0115;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x0122, code lost:
            r6.writeEnumNoTag(java.lang.reflect.Array.getInt(r5, r0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x0129, code lost:
            r0 = r0 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x012b, code lost:
            if (r0 < r1) goto L_0x0122;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:75:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:83:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void writeRepeatedData(java.lang.Object r5, com.google.protobuf.nano.CodedOutputByteBufferNano r6) {
            /*
                r4 = this;
                r0 = 0
                int r1 = r4.tag
                int r2 = r4.nonPackedTag
                if (r1 == r2) goto L_0x0043
                int r1 = r4.tag
                int r2 = r4.packedTag
                if (r1 == r2) goto L_0x0047
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Unexpected repeated extension tag "
                java.lang.StringBuilder r1 = r1.append(r2)
                int r2 = r4.tag
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = ", unequal to both non-packed variant "
                java.lang.StringBuilder r1 = r1.append(r2)
                int r2 = r4.nonPackedTag
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = " and packed variant "
                java.lang.StringBuilder r1 = r1.append(r2)
                int r2 = r4.packedTag
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x0043:
                com.google.protobuf.nano.Extension.super.writeRepeatedData(r5, r6)
            L_0x0046:
                return
            L_0x0047:
                int r1 = java.lang.reflect.Array.getLength(r5)
                int r2 = r4.computePackedDataSize(r5)
                int r3 = r4.tag     // Catch:{ IOException -> 0x0078 }
                r6.writeRawVarint32(r3)     // Catch:{ IOException -> 0x0078 }
                r6.writeRawVarint32(r2)     // Catch:{ IOException -> 0x0078 }
                int r2 = r4.type     // Catch:{ IOException -> 0x0078 }
                switch(r2) {
                    case 1: goto L_0x00d0;
                    case 2: goto L_0x00ac;
                    case 3: goto L_0x0104;
                    case 4: goto L_0x011e;
                    case 5: goto L_0x00dd;
                    case 6: goto L_0x00b8;
                    case 7: goto L_0x0094;
                    case 8: goto L_0x0088;
                    case 9: goto L_0x005c;
                    case 10: goto L_0x005c;
                    case 11: goto L_0x005c;
                    case 12: goto L_0x005c;
                    case 13: goto L_0x00f7;
                    case 14: goto L_0x012b;
                    case 15: goto L_0x00a0;
                    case 16: goto L_0x00c4;
                    case 17: goto L_0x00ea;
                    case 18: goto L_0x0111;
                    default: goto L_0x005c;
                }     // Catch:{ IOException -> 0x0078 }
            L_0x005c:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ IOException -> 0x0078 }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0078 }
                r1.<init>()     // Catch:{ IOException -> 0x0078 }
                java.lang.String r2 = "Unpackable type "
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0078 }
                int r2 = r4.type     // Catch:{ IOException -> 0x0078 }
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IOException -> 0x0078 }
                java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0078 }
                r0.<init>(r1)     // Catch:{ IOException -> 0x0078 }
                throw r0     // Catch:{ IOException -> 0x0078 }
            L_0x0078:
                r0 = move-exception
                java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
                r1.<init>(r0)
                throw r1
            L_0x007f:
                boolean r2 = java.lang.reflect.Array.getBoolean(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeBoolNoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x0088:
                if (r0 < r1) goto L_0x007f
                goto L_0x0046
            L_0x008b:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeFixed32NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x0094:
                if (r0 < r1) goto L_0x008b
                goto L_0x0046
            L_0x0097:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeSFixed32NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00a0:
                if (r0 < r1) goto L_0x0097
                goto L_0x0046
            L_0x00a3:
                float r2 = java.lang.reflect.Array.getFloat(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeFloatNoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00ac:
                if (r0 < r1) goto L_0x00a3
                goto L_0x0046
            L_0x00af:
                long r2 = java.lang.reflect.Array.getLong(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeFixed64NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00b8:
                if (r0 < r1) goto L_0x00af
                goto L_0x0046
            L_0x00bb:
                long r2 = java.lang.reflect.Array.getLong(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeSFixed64NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00c4:
                if (r0 < r1) goto L_0x00bb
                goto L_0x0046
            L_0x00c7:
                double r2 = java.lang.reflect.Array.getDouble(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeDoubleNoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00d0:
                if (r0 < r1) goto L_0x00c7
                goto L_0x0046
            L_0x00d4:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeInt32NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00dd:
                if (r0 < r1) goto L_0x00d4
                goto L_0x0046
            L_0x00e1:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeSInt32NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00ea:
                if (r0 < r1) goto L_0x00e1
                goto L_0x0046
            L_0x00ee:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeUInt32NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x00f7:
                if (r0 < r1) goto L_0x00ee
                goto L_0x0046
            L_0x00fb:
                long r2 = java.lang.reflect.Array.getLong(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeInt64NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x0104:
                if (r0 < r1) goto L_0x00fb
                goto L_0x0046
            L_0x0108:
                long r2 = java.lang.reflect.Array.getLong(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeSInt64NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x0111:
                if (r0 < r1) goto L_0x0108
                goto L_0x0046
            L_0x0115:
                long r2 = java.lang.reflect.Array.getLong(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeUInt64NoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x011e:
                if (r0 < r1) goto L_0x0115
                goto L_0x0046
            L_0x0122:
                int r2 = java.lang.reflect.Array.getInt(r5, r0)     // Catch:{ IOException -> 0x0078 }
                r6.writeEnumNoTag(r2)     // Catch:{ IOException -> 0x0078 }
                int r0 = r0 + 1
            L_0x012b:
                if (r0 < r1) goto L_0x0122
                goto L_0x0046
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.nano.Extension.PrimitiveExtension.writeRepeatedData(java.lang.Object, com.google.protobuf.nano.CodedOutputByteBufferNano):void");
        }

        /* access modifiers changed from: protected */
        public final void writeSingularData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
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
                    default:
                        throw new IllegalArgumentException("Unknown type " + this.type);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            throw new IllegalStateException(e);
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
        if (size == 0) {
            return null;
        }
        T cast = this.clazz.cast(Array.newInstance(this.clazz.getComponentType(), size));
        for (int i2 = 0; i2 < size; i2++) {
            Array.set(cast, i2, arrayList.get(i2));
        }
        return cast;
    }

    private T getSingularValueFrom(List<UnknownFieldData> list) {
        if (!list.isEmpty()) {
            return this.clazz.cast(readData(CodedInputByteBufferNano.newInstance(list.get(list.size() - 1).bytes)));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int computeRepeatedSerializedSize(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += computeSingularSerializedSize(Array.get(obj, i2));
            }
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public int computeSerializedSize(Object obj) {
        return !this.repeated ? computeSingularSerializedSize(obj) : computeRepeatedSerializedSize(obj);
    }

    /* access modifiers changed from: protected */
    public int computeSingularSerializedSize(Object obj) {
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

    /* access modifiers changed from: package-private */
    public final T getValueFrom(List<UnknownFieldData> list) {
        if (list != null) {
            return !this.repeated ? getSingularValueFrom(list) : getRepeatedValueFrom(list);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Object readData(CodedInputByteBufferNano codedInputByteBufferNano) {
        Class<T> componentType = !this.repeated ? this.clazz : this.clazz.getComponentType();
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
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e2);
        } catch (IOException e3) {
            throw new IllegalArgumentException("Error reading extension field", e3);
        }
    }

    /* access modifiers changed from: protected */
    public void readDataInto(UnknownFieldData unknownFieldData, List<Object> list) {
        list.add(readData(CodedInputByteBufferNano.newInstance(unknownFieldData.bytes)));
    }

    /* access modifiers changed from: protected */
    public void writeRepeatedData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                writeSingularData(obj2, codedOutputByteBufferNano);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void writeSingularData(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) {
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
        throw new IllegalStateException(e);
    }

    /* access modifiers changed from: package-private */
    public void writeTo(Object obj, CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (!this.repeated) {
            writeSingularData(obj, codedOutputByteBufferNano);
        } else {
            writeRepeatedData(obj, codedOutputByteBufferNano);
        }
    }
}
