// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.io.IOException;

public final class CodedInputByteBufferNano
{
    private static final int DEFAULT_RECURSION_LIMIT = 64;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferStart;
    private int currentLimit;
    private int lastTag;
    private int recursionDepth;
    private int recursionLimit;
    private int sizeLimit;
    
    private CodedInputByteBufferNano(final byte[] buffer, final int n, final int n2) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.sizeLimit = 67108864;
        this.buffer = buffer;
        this.bufferStart = n;
        this.bufferSize = n + n2;
        this.bufferPos = n;
    }
    
    public static int decodeZigZag32(final int n) {
        return n >>> 1 ^ -(n & 0x1);
    }
    
    public static long decodeZigZag64(final long n) {
        return n >>> 1 ^ -(0x1L & n);
    }
    
    public static CodedInputByteBufferNano newInstance(final byte[] array) {
        return newInstance(array, 0, array.length);
    }
    
    public static CodedInputByteBufferNano newInstance(final byte[] array, final int n, final int n2) {
        return new CodedInputByteBufferNano(array, n, n2);
    }
    
    private void recomputeBufferSizeAfterLimit() {
        this.bufferSize += this.bufferSizeAfterLimit;
        final int bufferSize = this.bufferSize;
        if (bufferSize <= this.currentLimit) {
            this.bufferSizeAfterLimit = 0;
        }
        else {
            this.bufferSizeAfterLimit = bufferSize - this.currentLimit;
            this.bufferSize -= this.bufferSizeAfterLimit;
        }
    }
    
    public void checkLastTagWas(final int n) throws InvalidProtocolBufferNanoException {
        if (this.lastTag == n) {
            return;
        }
        throw InvalidProtocolBufferNanoException.invalidEndTag();
    }
    
    public int getBytesUntilLimit() {
        if (this.currentLimit != Integer.MAX_VALUE) {
            return this.currentLimit - this.bufferPos;
        }
        return -1;
    }
    
    public byte[] getData(final int n, final int n2) {
        if (n2 != 0) {
            final byte[] array = new byte[n2];
            System.arraycopy(this.buffer, this.bufferStart + n, array, 0, n2);
            return array;
        }
        return WireFormatNano.EMPTY_BYTES;
    }
    
    public int getPosition() {
        return this.bufferPos - this.bufferStart;
    }
    
    public boolean isAtEnd() {
        return this.bufferPos == this.bufferSize;
    }
    
    public void popLimit(final int currentLimit) {
        this.currentLimit = currentLimit;
        this.recomputeBufferSizeAfterLimit();
    }
    
    public int pushLimit(int currentLimit) throws InvalidProtocolBufferNanoException {
        if (currentLimit < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        currentLimit += this.bufferPos;
        final int currentLimit2 = this.currentLimit;
        if (currentLimit <= currentLimit2) {
            this.currentLimit = currentLimit;
            this.recomputeBufferSizeAfterLimit();
            return currentLimit2;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    
    public boolean readBool() throws IOException {
        boolean b = false;
        if (this.readRawVarint32() != 0) {
            b = true;
        }
        return b;
    }
    
    public byte[] readBytes() throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        if (rawVarint32 <= this.bufferSize - this.bufferPos && rawVarint32 > 0) {
            final byte[] array = new byte[rawVarint32];
            System.arraycopy(this.buffer, this.bufferPos, array, 0, rawVarint32);
            this.bufferPos += rawVarint32;
            return array;
        }
        if (rawVarint32 != 0) {
            return this.readRawBytes(rawVarint32);
        }
        return WireFormatNano.EMPTY_BYTES;
    }
    
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readRawLittleEndian64());
    }
    
    public int readEnum() throws IOException {
        return this.readRawVarint32();
    }
    
    public int readFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public long readFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readRawLittleEndian32());
    }
    
    public void readGroup(final MessageNano messageNano, final int n) throws IOException {
        if (this.recursionDepth < this.recursionLimit) {
            ++this.recursionDepth;
            messageNano.mergeFrom(this);
            this.checkLastTagWas(WireFormatNano.makeTag(n, 4));
            --this.recursionDepth;
            return;
        }
        throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
    }
    
    public int readInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public void readMessage(final MessageNano messageNano) throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        if (this.recursionDepth < this.recursionLimit) {
            final int pushLimit = this.pushLimit(rawVarint32);
            ++this.recursionDepth;
            messageNano.mergeFrom(this);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(pushLimit);
            return;
        }
        throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
    }
    
    Object readPrimitiveField(final int i) throws IOException {
        switch (i) {
            default: {
                throw new IllegalArgumentException("Unknown type " + i);
            }
            case 1: {
                return this.readDouble();
            }
            case 2: {
                return this.readFloat();
            }
            case 3: {
                return this.readInt64();
            }
            case 4: {
                return this.readUInt64();
            }
            case 5: {
                return this.readInt32();
            }
            case 6: {
                return this.readFixed64();
            }
            case 7: {
                return this.readFixed32();
            }
            case 8: {
                return this.readBool();
            }
            case 9: {
                return this.readString();
            }
            case 12: {
                return this.readBytes();
            }
            case 13: {
                return this.readUInt32();
            }
            case 14: {
                return this.readEnum();
            }
            case 15: {
                return this.readSFixed32();
            }
            case 16: {
                return this.readSFixed64();
            }
            case 17: {
                return this.readSInt32();
            }
            case 18: {
                return this.readSInt64();
            }
        }
    }
    
    public byte readRawByte() throws IOException {
        if (this.bufferPos != this.bufferSize) {
            return this.buffer[this.bufferPos++];
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    
    public byte[] readRawBytes(final int n) throws IOException {
        if (n < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        if (this.bufferPos + n > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        if (n > this.bufferSize - this.bufferPos) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        final byte[] array = new byte[n];
        System.arraycopy(this.buffer, this.bufferPos, array, 0, n);
        this.bufferPos += n;
        return array;
    }
    
    public int readRawLittleEndian32() throws IOException {
        return (this.readRawByte() & 0xFF) | (this.readRawByte() & 0xFF) << 8 | (this.readRawByte() & 0xFF) << 16 | (this.readRawByte() & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
        return ((long)this.readRawByte() & 0xFFL) << 8 | ((long)this.readRawByte() & 0xFFL) | ((long)this.readRawByte() & 0xFFL) << 16 | ((long)this.readRawByte() & 0xFFL) << 24 | ((long)this.readRawByte() & 0xFFL) << 32 | ((long)this.readRawByte() & 0xFFL) << 40 | ((long)this.readRawByte() & 0xFFL) << 48 | ((long)this.readRawByte() & 0xFFL) << 56;
    }
    
    public int readRawVarint32() throws IOException {
        int i = 0;
        final byte rawByte = this.readRawByte();
        if (rawByte < 0) {
            final int n = rawByte & 0x7F;
            final byte rawByte2 = this.readRawByte();
            int n5;
            if (rawByte2 < 0) {
                final int n2 = n | (rawByte2 & 0x7F) << 7;
                final byte rawByte3 = this.readRawByte();
                if (rawByte3 < 0) {
                    final int n3 = n2 | (rawByte3 & 0x7F) << 14;
                    final byte rawByte4 = this.readRawByte();
                    if (rawByte4 < 0) {
                        final byte rawByte5 = this.readRawByte();
                        final int n4 = n3 | (rawByte4 & 0x7F) << 21 | rawByte5 << 28;
                        if (rawByte5 < 0) {
                            while (i < 5) {
                                if (this.readRawByte() >= 0) {
                                    return n4;
                                }
                                ++i;
                            }
                            throw InvalidProtocolBufferNanoException.malformedVarint();
                        }
                        n5 = n4;
                    }
                    else {
                        n5 = (n3 | rawByte4 << 21);
                    }
                }
                else {
                    n5 = (n2 | rawByte3 << 14);
                }
            }
            else {
                n5 = (n | rawByte2 << 7);
            }
            return n5;
        }
        return rawByte;
    }
    
    public long readRawVarint64() throws IOException {
        int i = 0;
        long n = 0L;
        while (i < 64) {
            final byte rawByte = this.readRawByte();
            n |= (long)(rawByte & 0x7F) << i;
            if ((rawByte & 0x80) == 0x0) {
                return n;
            }
            i += 7;
        }
        throw InvalidProtocolBufferNanoException.malformedVarint();
    }
    
    public int readSFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
        return decodeZigZag32(this.readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
        return decodeZigZag64(this.readRawVarint64());
    }
    
    public String readString() throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        if (rawVarint32 <= this.bufferSize - this.bufferPos && rawVarint32 > 0) {
            final String s = new String(this.buffer, this.bufferPos, rawVarint32, InternalNano.UTF_8);
            this.bufferPos += rawVarint32;
            return s;
        }
        return new String(this.readRawBytes(rawVarint32), InternalNano.UTF_8);
    }
    
    public int readTag() throws IOException {
        if (this.isAtEnd()) {
            return this.lastTag = 0;
        }
        this.lastTag = this.readRawVarint32();
        if (this.lastTag != 0) {
            return this.lastTag;
        }
        throw InvalidProtocolBufferNanoException.invalidTag();
    }
    
    public int readUInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readUInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public void resetSizeCounter() {
    }
    
    public void rewindToPosition(final int n) {
        if (n > this.bufferPos - this.bufferStart) {
            throw new IllegalArgumentException("Position " + n + " is beyond current " + (this.bufferPos - this.bufferStart));
        }
        if (n >= 0) {
            this.bufferPos = this.bufferStart + n;
            return;
        }
        throw new IllegalArgumentException("Bad position " + n);
    }
    
    public int setRecursionLimit(final int n) {
        if (n >= 0) {
            final int recursionLimit = this.recursionLimit;
            this.recursionLimit = n;
            return recursionLimit;
        }
        throw new IllegalArgumentException("Recursion limit cannot be negative: " + n);
    }
    
    public int setSizeLimit(final int n) {
        if (n >= 0) {
            final int sizeLimit = this.sizeLimit;
            this.sizeLimit = n;
            return sizeLimit;
        }
        throw new IllegalArgumentException("Size limit cannot be negative: " + n);
    }
    
    public boolean skipField(final int n) throws IOException {
        switch (WireFormatNano.getTagWireType(n)) {
            default: {
                throw InvalidProtocolBufferNanoException.invalidWireType();
            }
            case 0: {
                this.readInt32();
                return true;
            }
            case 1: {
                this.readRawLittleEndian64();
                return true;
            }
            case 2: {
                this.skipRawBytes(this.readRawVarint32());
                return true;
            }
            case 3: {
                this.skipMessage();
                this.checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(n), 4));
                return true;
            }
            case 4: {
                return false;
            }
            case 5: {
                this.readRawLittleEndian32();
                return true;
            }
        }
    }
    
    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = this.readTag();
        } while (tag != 0 && this.skipField(tag));
    }
    
    public void skipRawBytes(final int n) throws IOException {
        if (n < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        if (this.bufferPos + n > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        if (n > this.bufferSize - this.bufferPos) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        this.bufferPos += n;
    }
}
