// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.io.IOException;
import java.nio.ReadOnlyBufferException;
import java.nio.BufferOverflowException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public final class CodedOutputByteBufferNano
{
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    public static final int LITTLE_ENDIAN_64_SIZE = 8;
    private static final int MAX_UTF8_EXPANSION = 3;
    private final ByteBuffer buffer;
    
    private CodedOutputByteBufferNano(final ByteBuffer buffer) {
        (this.buffer = buffer).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private CodedOutputByteBufferNano(final byte[] array, final int offset, final int length) {
        this(ByteBuffer.wrap(array, offset, length));
    }
    
    public static int computeBoolSize(final int n, final boolean b) {
        return computeTagSize(n) + computeBoolSizeNoTag(b);
    }
    
    public static int computeBoolSizeNoTag(final boolean b) {
        return 1;
    }
    
    public static int computeBytesSize(final int n, final byte[] array) {
        return computeTagSize(n) + computeBytesSizeNoTag(array);
    }
    
    public static int computeBytesSizeNoTag(final byte[] array) {
        return computeRawVarint32Size(array.length) + array.length;
    }
    
    public static int computeDoubleSize(final int n, final double n2) {
        return computeTagSize(n) + computeDoubleSizeNoTag(n2);
    }
    
    public static int computeDoubleSizeNoTag(final double n) {
        return 8;
    }
    
    public static int computeEnumSize(final int n, final int n2) {
        return computeTagSize(n) + computeEnumSizeNoTag(n2);
    }
    
    public static int computeEnumSizeNoTag(final int n) {
        return computeRawVarint32Size(n);
    }
    
    static int computeFieldSize(final int n, final int i, final Object o) {
        switch (i) {
            default: {
                throw new IllegalArgumentException("Unknown type: " + i);
            }
            case 8: {
                return computeBoolSize(n, (boolean)o);
            }
            case 12: {
                return computeBytesSize(n, (byte[])o);
            }
            case 9: {
                return computeStringSize(n, (String)o);
            }
            case 2: {
                return computeFloatSize(n, (float)o);
            }
            case 1: {
                return computeDoubleSize(n, (double)o);
            }
            case 14: {
                return computeEnumSize(n, (int)o);
            }
            case 7: {
                return computeFixed32Size(n, (int)o);
            }
            case 5: {
                return computeInt32Size(n, (int)o);
            }
            case 13: {
                return computeUInt32Size(n, (int)o);
            }
            case 17: {
                return computeSInt32Size(n, (int)o);
            }
            case 15: {
                return computeSFixed32Size(n, (int)o);
            }
            case 3: {
                return computeInt64Size(n, (long)o);
            }
            case 4: {
                return computeUInt64Size(n, (long)o);
            }
            case 18: {
                return computeSInt64Size(n, (long)o);
            }
            case 6: {
                return computeFixed64Size(n, (long)o);
            }
            case 16: {
                return computeSFixed64Size(n, (long)o);
            }
            case 11: {
                return computeMessageSize(n, (MessageNano)o);
            }
            case 10: {
                return computeGroupSize(n, (MessageNano)o);
            }
        }
    }
    
    public static int computeFixed32Size(final int n, final int n2) {
        return computeTagSize(n) + computeFixed32SizeNoTag(n2);
    }
    
    public static int computeFixed32SizeNoTag(final int n) {
        return 4;
    }
    
    public static int computeFixed64Size(final int n, final long n2) {
        return computeTagSize(n) + computeFixed64SizeNoTag(n2);
    }
    
    public static int computeFixed64SizeNoTag(final long n) {
        return 8;
    }
    
    public static int computeFloatSize(final int n, final float n2) {
        return computeTagSize(n) + computeFloatSizeNoTag(n2);
    }
    
    public static int computeFloatSizeNoTag(final float n) {
        return 4;
    }
    
    public static int computeGroupSize(final int n, final MessageNano messageNano) {
        return computeTagSize(n) * 2 + computeGroupSizeNoTag(messageNano);
    }
    
    public static int computeGroupSizeNoTag(final MessageNano messageNano) {
        return messageNano.getSerializedSize();
    }
    
    public static int computeInt32Size(final int n, final int n2) {
        return computeTagSize(n) + computeInt32SizeNoTag(n2);
    }
    
    public static int computeInt32SizeNoTag(final int n) {
        if (n < 0) {
            return 10;
        }
        return computeRawVarint32Size(n);
    }
    
    public static int computeInt64Size(final int n, final long n2) {
        return computeTagSize(n) + computeInt64SizeNoTag(n2);
    }
    
    public static int computeInt64SizeNoTag(final long n) {
        return computeRawVarint64Size(n);
    }
    
    public static int computeMessageSize(final int n, final MessageNano messageNano) {
        return computeTagSize(n) + computeMessageSizeNoTag(messageNano);
    }
    
    public static int computeMessageSizeNoTag(final MessageNano messageNano) {
        final int serializedSize = messageNano.getSerializedSize();
        return serializedSize + computeRawVarint32Size(serializedSize);
    }
    
    public static int computeRawVarint32Size(final int n) {
        if ((n & 0xFFFFFF80) == 0x0) {
            return 1;
        }
        if ((n & 0xFFFFC000) == 0x0) {
            return 2;
        }
        if ((0xFFE00000 & n) == 0x0) {
            return 3;
        }
        if ((0xF0000000 & n) != 0x0) {
            return 5;
        }
        return 4;
    }
    
    public static int computeRawVarint64Size(final long n) {
        if ((0xFFFFFFFFFFFFFF80L & n) == 0x0L) {
            return 1;
        }
        if ((0xFFFFFFFFFFFFC000L & n) == 0x0L) {
            return 2;
        }
        if ((0xFFFFFFFFFFE00000L & n) == 0x0L) {
            return 3;
        }
        if ((0xFFFFFFFFF0000000L & n) == 0x0L) {
            return 4;
        }
        if ((0xFFFFFFF800000000L & n) == 0x0L) {
            return 5;
        }
        if ((0xFFFFFC0000000000L & n) == 0x0L) {
            return 6;
        }
        if ((0xFFFE000000000000L & n) == 0x0L) {
            return 7;
        }
        if ((0xFF00000000000000L & n) == 0x0L) {
            return 8;
        }
        if ((Long.MIN_VALUE & n) == 0x0L) {
            return 9;
        }
        return 10;
    }
    
    public static int computeSFixed32Size(final int n, final int n2) {
        return computeTagSize(n) + computeSFixed32SizeNoTag(n2);
    }
    
    public static int computeSFixed32SizeNoTag(final int n) {
        return 4;
    }
    
    public static int computeSFixed64Size(final int n, final long n2) {
        return computeTagSize(n) + computeSFixed64SizeNoTag(n2);
    }
    
    public static int computeSFixed64SizeNoTag(final long n) {
        return 8;
    }
    
    public static int computeSInt32Size(final int n, final int n2) {
        return computeTagSize(n) + computeSInt32SizeNoTag(n2);
    }
    
    public static int computeSInt32SizeNoTag(final int n) {
        return computeRawVarint32Size(encodeZigZag32(n));
    }
    
    public static int computeSInt64Size(final int n, final long n2) {
        return computeTagSize(n) + computeSInt64SizeNoTag(n2);
    }
    
    public static int computeSInt64SizeNoTag(final long n) {
        return computeRawVarint64Size(encodeZigZag64(n));
    }
    
    public static int computeStringSize(final int n, final String s) {
        return computeTagSize(n) + computeStringSizeNoTag(s);
    }
    
    public static int computeStringSizeNoTag(final String s) {
        final int encodedLength = encodedLength(s);
        return encodedLength + computeRawVarint32Size(encodedLength);
    }
    
    public static int computeTagSize(final int n) {
        return computeRawVarint32Size(WireFormatNano.makeTag(n, 0));
    }
    
    public static int computeUInt32Size(final int n, final int n2) {
        return computeTagSize(n) + computeUInt32SizeNoTag(n2);
    }
    
    public static int computeUInt32SizeNoTag(final int n) {
        return computeRawVarint32Size(n);
    }
    
    public static int computeUInt64Size(final int n, final long n2) {
        return computeTagSize(n) + computeUInt64SizeNoTag(n2);
    }
    
    public static int computeUInt64SizeNoTag(final long n) {
        return computeRawVarint64Size(n);
    }
    
    private static int encode(final CharSequence charSequence, final byte[] array, int i, int j) {
        final int n = 0;
        int length;
        int n2;
        char char1;
        for (length = charSequence.length(), n2 = i + j, j = n; j < length && j + i < n2; ++j) {
            char1 = charSequence.charAt(j);
            if (char1 >= '\u0080') {
                break;
            }
            array[i + j] = (byte)char1;
        }
        if (j != length) {
            i += j;
            while (j < length) {
                final char char2 = charSequence.charAt(j);
                Label_0199: {
                    if (char2 < '\u0080' && i < n2) {
                        final int n3 = i + 1;
                        array[i] = (byte)char2;
                        i = n3;
                    }
                    else if (char2 < '\u0800' && i <= n2 - 2) {
                        final int n4 = i + 1;
                        array[i] = (byte)(char2 >>> 6 | 0x3C0);
                        i = n4 + 1;
                        array[n4] = (byte)((char2 & '?') | 0x80);
                    }
                    else {
                        if (char2 < '\ud800' || '\udfff' < char2) {
                            if (i <= n2 - 3) {
                                final int n5 = i + 1;
                                array[i] = (byte)(char2 >>> 12 | 0x1E0);
                                final int n6 = n5 + 1;
                                array[n5] = (byte)((char2 >>> 6 & 0x3F) | 0x80);
                                i = n6 + 1;
                                array[n6] = (byte)((char2 & '?') | 0x80);
                                break Label_0199;
                            }
                        }
                        if (i > n2 - 4) {
                            throw new ArrayIndexOutOfBoundsException("Failed writing " + char2 + " at index " + i);
                        }
                        int n7;
                        if (j + 1 == charSequence.length()) {
                            n7 = j;
                        }
                        else {
                            ++j;
                            final char char3 = charSequence.charAt(j);
                            n7 = j;
                            if (Character.isSurrogatePair(char2, char3)) {
                                final int codePoint = Character.toCodePoint(char2, char3);
                                final int n8 = i + 1;
                                array[i] = (byte)(codePoint >>> 18 | 0xF0);
                                i = n8 + 1;
                                array[n8] = (byte)((codePoint >>> 12 & 0x3F) | 0x80);
                                final int n9 = i + 1;
                                array[i] = (byte)((codePoint >>> 6 & 0x3F) | 0x80);
                                i = n9 + 1;
                                array[n9] = (byte)((codePoint & 0x3F) | 0x80);
                                break Label_0199;
                            }
                        }
                        throw new IllegalArgumentException("Unpaired surrogate at index " + (n7 - 1));
                    }
                }
                ++j;
            }
            return i;
        }
        return i + length;
    }
    
    private static void encode(final CharSequence charSequence, final ByteBuffer byteBuffer) {
        if (!byteBuffer.isReadOnly()) {
            if (!byteBuffer.hasArray()) {
                encodeDirect(charSequence, byteBuffer);
            }
            else {
                try {
                    byteBuffer.position();
                }
                catch (final ArrayIndexOutOfBoundsException cause) {
                    final BufferOverflowException ex = new BufferOverflowException();
                    ex.initCause(cause);
                    throw ex;
                }
            }
            return;
        }
        throw new ReadOnlyBufferException();
    }
    
    private static void encodeDirect(final CharSequence charSequence, final ByteBuffer byteBuffer) {
        for (int i = 0; i < charSequence.length(); ++i) {
            final char char1 = charSequence.charAt(i);
            if (char1 >= '\u0080') {
                if (char1 >= '\u0800') {
                    if (char1 >= '\ud800' && '\udfff' >= char1) {
                        int n;
                        if (i + 1 == charSequence.length()) {
                            n = i;
                        }
                        else {
                            ++i;
                            final char char2 = charSequence.charAt(i);
                            n = i;
                            if (Character.isSurrogatePair(char1, char2)) {
                                final int codePoint = Character.toCodePoint(char1, char2);
                                byteBuffer.put((byte)(codePoint >>> 18 | 0xF0));
                                byteBuffer.put((byte)((codePoint >>> 12 & 0x3F) | 0x80));
                                byteBuffer.put((byte)((codePoint >>> 6 & 0x3F) | 0x80));
                                byteBuffer.put((byte)((codePoint & 0x3F) | 0x80));
                                continue;
                            }
                        }
                        throw new IllegalArgumentException("Unpaired surrogate at index " + (n - 1));
                    }
                    byteBuffer.put((byte)(char1 >>> 12 | 0x1E0));
                    byteBuffer.put((byte)((char1 >>> 6 & 0x3F) | 0x80));
                    byteBuffer.put((byte)((char1 & '?') | 0x80));
                }
                else {
                    byteBuffer.put((byte)(char1 >>> 6 | 0x3C0));
                    byteBuffer.put((byte)((char1 & '?') | 0x80));
                }
            }
            else {
                byteBuffer.put((byte)char1);
            }
        }
    }
    
    public static int encodeZigZag32(final int n) {
        return n << 1 ^ n >> 31;
    }
    
    public static long encodeZigZag64(final long n) {
        return n << 1 ^ n >> 63;
    }
    
    private static int encodedLength(final CharSequence charSequence) {
        int length;
        int n;
        for (length = charSequence.length(), n = 0; n < length && charSequence.charAt(n) < '\u0080'; ++n) {}
        int i;
        int n2;
        char char1;
        for (i = n, n2 = length; i < length; ++i, n2 += '\u007f' - char1 >>> 31) {
            char1 = charSequence.charAt(i);
            if (char1 >= '\u0800') {
                n2 += encodedLengthGeneral(charSequence, i);
                break;
            }
        }
        if (n2 >= length) {
            return n2;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (n2 + 4294967296L));
    }
    
    private static int encodedLengthGeneral(final CharSequence seq, int i) {
        final int length = seq.length();
        int n = 0;
        while (i < length) {
            final char char1 = seq.charAt(i);
            int n3;
            if (char1 >= '\u0800') {
                final int n2 = n + 2;
                if ('\ud800' > char1) {
                    n = n2;
                    n3 = i;
                }
                else {
                    n3 = i;
                    n = n2;
                    if (char1 <= '\udfff') {
                        if (Character.codePointAt(seq, i) < 65536) {
                            throw new IllegalArgumentException("Unpaired surrogate at index " + i);
                        }
                        n3 = i + 1;
                        n = n2;
                    }
                }
            }
            else {
                n += '\u007f' - char1 >>> 31;
                n3 = i;
            }
            i = n3 + 1;
        }
        return n;
    }
    
    public static CodedOutputByteBufferNano newInstance(final byte[] array) {
        return newInstance(array, 0, array.length);
    }
    
    public static CodedOutputByteBufferNano newInstance(final byte[] array, final int n, final int n2) {
        return new CodedOutputByteBufferNano(array, n, n2);
    }
    
    public void checkNoSpaceLeft() {
        if (this.spaceLeft() == 0) {
            return;
        }
        throw new IllegalStateException("Did not write as much data as expected.");
    }
    
    public int position() {
        return this.buffer.position();
    }
    
    public void reset() {
        this.buffer.clear();
    }
    
    public int spaceLeft() {
        return this.buffer.remaining();
    }
    
    public void writeBool(final int n, final boolean b) throws IOException {
        this.writeTag(n, 0);
        this.writeBoolNoTag(b);
    }
    
    public void writeBoolNoTag(final boolean b) throws IOException {
        int n = 0;
        if (b) {
            n = 1;
        }
        this.writeRawByte(n);
    }
    
    public void writeBytes(final int n, final byte[] array) throws IOException {
        this.writeTag(n, 2);
        this.writeBytesNoTag(array);
    }
    
    public void writeBytesNoTag(final byte[] array) throws IOException {
        this.writeRawVarint32(array.length);
        this.writeRawBytes(array);
    }
    
    public void writeDouble(final int n, final double n2) throws IOException {
        this.writeTag(n, 1);
        this.writeDoubleNoTag(n2);
    }
    
    public void writeDoubleNoTag(final double value) throws IOException {
        this.writeRawLittleEndian64(Double.doubleToLongBits(value));
    }
    
    public void writeEnum(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeEnumNoTag(n2);
    }
    
    public void writeEnumNoTag(final int n) throws IOException {
        this.writeRawVarint32(n);
    }
    
    void writeField(final int n, final int i, final Object o) throws IOException {
        switch (i) {
            default: {
                throw new IOException("Unknown type: " + i);
            }
            case 1: {
                this.writeDouble(n, (double)o);
                break;
            }
            case 2: {
                this.writeFloat(n, (float)o);
                break;
            }
            case 3: {
                this.writeInt64(n, (long)o);
                break;
            }
            case 4: {
                this.writeUInt64(n, (long)o);
                break;
            }
            case 5: {
                this.writeInt32(n, (int)o);
                break;
            }
            case 6: {
                this.writeFixed64(n, (long)o);
                break;
            }
            case 7: {
                this.writeFixed32(n, (int)o);
                break;
            }
            case 8: {
                this.writeBool(n, (boolean)o);
                break;
            }
            case 9: {
                this.writeString(n, (String)o);
                break;
            }
            case 12: {
                this.writeBytes(n, (byte[])o);
                break;
            }
            case 13: {
                this.writeUInt32(n, (int)o);
                break;
            }
            case 14: {
                this.writeEnum(n, (int)o);
                break;
            }
            case 15: {
                this.writeSFixed32(n, (int)o);
                break;
            }
            case 16: {
                this.writeSFixed64(n, (long)o);
                break;
            }
            case 17: {
                this.writeSInt32(n, (int)o);
                break;
            }
            case 18: {
                this.writeSInt64(n, (long)o);
                break;
            }
            case 11: {
                this.writeMessage(n, (MessageNano)o);
                break;
            }
            case 10: {
                this.writeGroup(n, (MessageNano)o);
                break;
            }
        }
    }
    
    public void writeFixed32(final int n, final int n2) throws IOException {
        this.writeTag(n, 5);
        this.writeFixed32NoTag(n2);
    }
    
    public void writeFixed32NoTag(final int n) throws IOException {
        this.writeRawLittleEndian32(n);
    }
    
    public void writeFixed64(final int n, final long n2) throws IOException {
        this.writeTag(n, 1);
        this.writeFixed64NoTag(n2);
    }
    
    public void writeFixed64NoTag(final long n) throws IOException {
        this.writeRawLittleEndian64(n);
    }
    
    public void writeFloat(final int n, final float n2) throws IOException {
        this.writeTag(n, 5);
        this.writeFloatNoTag(n2);
    }
    
    public void writeFloatNoTag(final float value) throws IOException {
        this.writeRawLittleEndian32(Float.floatToIntBits(value));
    }
    
    public void writeGroup(final int n, final MessageNano messageNano) throws IOException {
        this.writeTag(n, 3);
        this.writeGroupNoTag(messageNano);
        this.writeTag(n, 4);
    }
    
    public void writeGroupNoTag(final MessageNano messageNano) throws IOException {
        messageNano.writeTo(this);
    }
    
    public void writeInt32(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeInt32NoTag(n2);
    }
    
    public void writeInt32NoTag(final int n) throws IOException {
        if (n < 0) {
            this.writeRawVarint64(n);
        }
        else {
            this.writeRawVarint32(n);
        }
    }
    
    public void writeInt64(final int n, final long n2) throws IOException {
        this.writeTag(n, 0);
        this.writeInt64NoTag(n2);
    }
    
    public void writeInt64NoTag(final long n) throws IOException {
        this.writeRawVarint64(n);
    }
    
    public void writeMessage(final int n, final MessageNano messageNano) throws IOException {
        this.writeTag(n, 2);
        this.writeMessageNoTag(messageNano);
    }
    
    public void writeMessageNoTag(final MessageNano messageNano) throws IOException {
        this.writeRawVarint32(messageNano.getCachedSize());
        messageNano.writeTo(this);
    }
    
    public void writeRawByte(final byte b) throws IOException {
        if (this.buffer.hasRemaining()) {
            this.buffer.put(b);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawByte(final int n) throws IOException {
        this.writeRawByte((byte)n);
    }
    
    public void writeRawBytes(final byte[] array) throws IOException {
        this.writeRawBytes(array, 0, array.length);
    }
    
    public void writeRawBytes(final byte[] src, final int offset, final int length) throws IOException {
        if (this.buffer.remaining() < length) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.put(src, offset, length);
    }
    
    public void writeRawLittleEndian32(final int n) throws IOException {
        if (this.buffer.remaining() >= 4) {
            this.buffer.putInt(n);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawLittleEndian64(final long n) throws IOException {
        if (this.buffer.remaining() >= 8) {
            this.buffer.putLong(n);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawVarint32(int n) throws IOException {
        while ((n & 0xFFFFFF80) != 0x0) {
            this.writeRawByte((n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.writeRawByte(n);
    }
    
    public void writeRawVarint64(long n) throws IOException {
        while ((0xFFFFFFFFFFFFFF80L & n) != 0x0L) {
            this.writeRawByte(((int)n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.writeRawByte((int)n);
    }
    
    public void writeSFixed32(final int n, final int n2) throws IOException {
        this.writeTag(n, 5);
        this.writeSFixed32NoTag(n2);
    }
    
    public void writeSFixed32NoTag(final int n) throws IOException {
        this.writeRawLittleEndian32(n);
    }
    
    public void writeSFixed64(final int n, final long n2) throws IOException {
        this.writeTag(n, 1);
        this.writeSFixed64NoTag(n2);
    }
    
    public void writeSFixed64NoTag(final long n) throws IOException {
        this.writeRawLittleEndian64(n);
    }
    
    public void writeSInt32(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeSInt32NoTag(n2);
    }
    
    public void writeSInt32NoTag(final int n) throws IOException {
        this.writeRawVarint32(encodeZigZag32(n));
    }
    
    public void writeSInt64(final int n, final long n2) throws IOException {
        this.writeTag(n, 0);
        this.writeSInt64NoTag(n2);
    }
    
    public void writeSInt64NoTag(final long n) throws IOException {
        this.writeRawVarint64(encodeZigZag64(n));
    }
    
    public void writeString(final int n, final String s) throws IOException {
        this.writeTag(n, 2);
        this.writeStringNoTag(s);
    }
    
    public void writeStringNoTag(final String s) throws IOException {
        int computeRawVarint32Size;
        int position;
        try {
            computeRawVarint32Size = computeRawVarint32Size(s.length());
            if (computeRawVarint32Size != computeRawVarint32Size(s.length() * 3)) {
                this.writeRawVarint32(encodedLength(s));
                encode(s, this.buffer);
            }
            else {
                position = this.buffer.position();
                if (this.buffer.remaining() < computeRawVarint32Size) {
                    throw new OutOfSpaceException(computeRawVarint32Size + position, this.buffer.limit());
                }
                this.buffer.position();
                encode(s, this.buffer);
                final int position2 = this.buffer.position();
                this.buffer.position();
                this.writeRawVarint32(position2 - position - computeRawVarint32Size);
                this.buffer.position();
            }
            return;
        }
        catch (final BufferOverflowException cause) {
            final OutOfSpaceException ex = new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
            ex.initCause(cause);
            throw ex;
        }
        throw new OutOfSpaceException(computeRawVarint32Size + position, this.buffer.limit());
    }
    
    public void writeTag(final int n, final int n2) throws IOException {
        this.writeRawVarint32(WireFormatNano.makeTag(n, n2));
    }
    
    public void writeUInt32(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeUInt32NoTag(n2);
    }
    
    public void writeUInt32NoTag(final int n) throws IOException {
        this.writeRawVarint32(n);
    }
    
    public void writeUInt64(final int n, final long n2) throws IOException {
        this.writeTag(n, 0);
        this.writeUInt64NoTag(n2);
    }
    
    public void writeUInt64NoTag(final long n) throws IOException {
        this.writeRawVarint64(n);
    }
    
    public static class OutOfSpaceException extends IOException
    {
        private static final long serialVersionUID = -6947486886997889499L;
        
        OutOfSpaceException(final int i, final int j) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + j + ").");
        }
    }
}
