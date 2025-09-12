package com.lumiyaviewer.rawbuffers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class DirectByteBuffer {
    private ByteBuffer buf;

    static {
        System.loadLibrary("rawbuf");
    }

    public DirectByteBuffer(int i) {
        this.buf = allocate(i);
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder());
            return;
        }
        throw new OutOfMemoryError("DirectByteBuffer: Failed to allocate " + i + " bytes");
    }

    public DirectByteBuffer(DirectByteBuffer directByteBuffer) {
        this.buf = allocate(directByteBuffer.asByteBuffer().capacity());
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder());
            copyFrom(0, directByteBuffer, 0, directByteBuffer.asByteBuffer().capacity());
            return;
        }
        throw new OutOfMemoryError("DirectByteBuffer: Failed to allocate " + directByteBuffer.asByteBuffer().capacity() + " bytes");
    }

    private native ByteBuffer allocate(int i);

    private native void copyByteArray(ByteBuffer byteBuffer, int i, byte[] bArr, int i2, int i3);

    private native void copyFloatArray(ByteBuffer byteBuffer, int i, float[] fArr, int i2, int i3);

    private native void copyPart(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, int i2, int i3);

    private native void copyShortArray(ByteBuffer byteBuffer, int i, short[] sArr, int i2, int i3);

    private native void copyShortArrayOffset(ByteBuffer byteBuffer, int i, short[] sArr, int i2, int i3, short s);

    private native void release(ByteBuffer byteBuffer);

    public static int zeroDecode(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) throws IndexOutOfBoundsException {
        int zeroDecodeArray = zeroDecodeArray(bArr, i, i2, bArr2, i3, i4);
        if (zeroDecodeArray >= 0) {
            return zeroDecodeArray;
        }
        throw new IndexOutOfBoundsException("zeroDecode: out of dest buffer, destStart " + Integer.toString(i) + " destMaxLen " + Integer.toString(i2));
    }

    private static native int zeroDecodeArray(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4);

    public ByteBuffer asByteBuffer() {
        return this.buf;
    }

    public FloatBuffer asFloatBuffer() {
        return this.buf.asFloatBuffer();
    }

    public IntBuffer asIntBuffer() {
        return this.buf.asIntBuffer();
    }

    public ShortBuffer asShortBuffer() {
        return this.buf.asShortBuffer();
    }

    public void copyFrom(int i, DirectByteBuffer directByteBuffer, int i2, int i3) {
        copyPart(this.buf, directByteBuffer.buf, i, i2, i3);
    }

    public void copyFromFloat(int i, DirectByteBuffer directByteBuffer, int i2, int i3) {
        copyPart(this.buf, directByteBuffer.buf, i * 4, i2 * 4, i3 * 4);
    }

    public void copyFromShort(int i, DirectByteBuffer directByteBuffer, int i2, int i3) {
        copyPart(this.buf, directByteBuffer.buf, i * 2, i2 * 2, i3 * 2);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        if (this.buf != null) {
            release(this.buf);
            this.buf = null;
        }
        super.finalize();
    }

    public int getCapacity() {
        return this.buf.capacity();
    }

    public float getFloat(int i) {
        return this.buf.getFloat(i * 4);
    }

    public short getShort(int i) {
        return this.buf.getShort(i * 2);
    }

    public void loadFromByteArray(int i, byte[] bArr, int i2, int i3) {
        int i4 = i + i3;
        if (i >= 0 && i <= this.buf.capacity() && i4 >= 0 && i4 <= this.buf.capacity()) {
            copyByteArray(this.buf, i, bArr, i2, i3);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[]{Integer.valueOf(this.buf.capacity()), Integer.valueOf(i), Integer.valueOf(i4)}));
    }

    public void loadFromFloatArray(int i, float[] fArr, int i2, int i3) {
        int i4 = i * 4;
        int i5 = (i + i3) * 4;
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyFloatArray(this.buf, i, fArr, i2, i3);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[]{Integer.valueOf(this.buf.capacity()), Integer.valueOf(i4), Integer.valueOf(i5)}));
    }

    public void loadFromShortArray(int i, short[] sArr, int i2, int i3) {
        int i4 = i * 2;
        int i5 = (i + i3) * 2;
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyShortArray(this.buf, i, sArr, i2, i3);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[]{Integer.valueOf(this.buf.capacity()), Integer.valueOf(i4), Integer.valueOf(i5)}));
    }

    public void loadFromShortArrayOffset(int i, short[] sArr, int i2, int i3, short s) {
        int i4 = i * 2;
        int i5 = (i + i3) * 2;
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyShortArrayOffset(this.buf, i, sArr, i2, i3, s);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[]{Integer.valueOf(this.buf.capacity()), Integer.valueOf(i4), Integer.valueOf(i5)}));
    }

    public int position() {
        return this.buf.position();
    }

    public Buffer position(int i) {
        return this.buf.position(i);
    }

    public int positionFloat() {
        return this.buf.position() / 4;
    }

    public Buffer positionFloat(int i) {
        return this.buf.position(i * 4);
    }

    public Buffer positionShort(int i) {
        return this.buf.position(i * 2);
    }

    public void putFloat(float f) {
        this.buf.putFloat(f);
    }

    public void putFloat(int i, float f) {
        this.buf.putFloat(i * 4, f);
    }

    public void putRawFloat(int i, float f) {
        this.buf.putFloat(i, f);
    }

    public void putRawInt(int i, int i2) {
        this.buf.putInt(i, i2);
    }

    public void putShort(short s) {
        this.buf.putShort(s);
    }

    public void read(InputStream inputStream) throws IOException {
        ByteBuffer asByteBuffer = asByteBuffer();
        byte[] bArr = new byte[asByteBuffer.capacity()];
        inputStream.read(bArr);
        int position = asByteBuffer.position();
        asByteBuffer.position(0);
        asByteBuffer.put(bArr);
        asByteBuffer.position(position);
    }
}
