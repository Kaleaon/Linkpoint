// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.rawbuffers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class DirectByteBuffer
{
    private ByteBuffer buf;
    
    static {
        System.loadLibrary("rawbuf");
    }
    
    public DirectByteBuffer(final int i) {
        this.buf = this.allocate(i);
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder());
            return;
        }
        throw new OutOfMemoryError("DirectByteBuffer: Failed to allocate " + i + " bytes");
    }
    
    public DirectByteBuffer(final DirectByteBuffer directByteBuffer) {
        this.buf = this.allocate(directByteBuffer.asByteBuffer().capacity());
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder());
            this.copyFrom(0, directByteBuffer, 0, directByteBuffer.asByteBuffer().capacity());
            return;
        }
        throw new OutOfMemoryError("DirectByteBuffer: Failed to allocate " + directByteBuffer.asByteBuffer().capacity() + " bytes");
    }
    
    private native ByteBuffer allocate(final int p0);
    
    private native void copyByteArray(final ByteBuffer p0, final int p1, final byte[] p2, final int p3, final int p4);
    
    private native void copyFloatArray(final ByteBuffer p0, final int p1, final float[] p2, final int p3, final int p4);
    
    private native void copyPart(final ByteBuffer p0, final ByteBuffer p1, final int p2, final int p3, final int p4);
    
    private native void copyShortArray(final ByteBuffer p0, final int p1, final short[] p2, final int p3, final int p4);
    
    private native void copyShortArrayOffset(final ByteBuffer p0, final int p1, final short[] p2, final int p3, final int p4, final short p5);
    
    private native void release(final ByteBuffer p0);
    
    public static int zeroDecode(final byte[] array, final int i, final int j, final byte[] array2, int zeroDecodeArray, final int n) throws IndexOutOfBoundsException {
        zeroDecodeArray = zeroDecodeArray(array, i, j, array2, zeroDecodeArray, n);
        if (zeroDecodeArray >= 0) {
            return zeroDecodeArray;
        }
        throw new IndexOutOfBoundsException("zeroDecode: out of dest buffer, destStart " + Integer.toString(i) + " destMaxLen " + Integer.toString(j));
    }
    
    private static native int zeroDecodeArray(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5);
    
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
    
    public void copyFrom(final int n, final DirectByteBuffer directByteBuffer, final int n2, final int n3) {
        this.copyPart(this.buf, directByteBuffer.buf, n, n2, n3);
    }
    
    public void copyFromFloat(final int n, final DirectByteBuffer directByteBuffer, final int n2, final int n3) {
        this.copyPart(this.buf, directByteBuffer.buf, n * 4, n2 * 4, n3 * 4);
    }
    
    public void copyFromShort(final int n, final DirectByteBuffer directByteBuffer, final int n2, final int n3) {
        this.copyPart(this.buf, directByteBuffer.buf, n * 2, n2 * 2, n3 * 2);
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.buf != null) {
            this.release(this.buf);
            this.buf = null;
        }
        super.finalize();
    }
    
    public int getCapacity() {
        return this.buf.capacity();
    }
    
    public float getFloat(final int n) {
        return this.buf.getFloat(n * 4);
    }
    
    public short getShort(final int n) {
        return this.buf.getShort(n * 2);
    }
    
    public void loadFromByteArray(final int i, final byte[] array, final int n, final int n2) {
        final int j = i + n2;
        if (i >= 0 && i <= this.buf.capacity() && j >= 0 && j <= this.buf.capacity()) {
            this.copyByteArray(this.buf, i, array, n, n2);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf.capacity(), i, j));
    }
    
    public void loadFromFloatArray(final int n, final float[] array, final int n2, final int n3) {
        final int i = n * 4;
        final int j = (n + n3) * 4;
        if (i >= 0 && i <= this.buf.capacity() && j >= 0 && j <= this.buf.capacity()) {
            this.copyFloatArray(this.buf, n, array, n2, n3);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf.capacity(), i, j));
    }
    
    public void loadFromShortArray(final int n, final short[] array, final int n2, final int n3) {
        final int i = n * 2;
        final int j = (n + n3) * 2;
        if (i >= 0 && i <= this.buf.capacity() && j >= 0 && j <= this.buf.capacity()) {
            this.copyShortArray(this.buf, n, array, n2, n3);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf.capacity(), i, j));
    }
    
    public void loadFromShortArrayOffset(final int n, final short[] array, final int n2, final int n3, final short n4) {
        final int i = n * 2;
        final int j = (n + n3) * 2;
        if (i >= 0 && i <= this.buf.capacity() && j >= 0 && j <= this.buf.capacity()) {
            this.copyShortArrayOffset(this.buf, n, array, n2, n3, n4);
            return;
        }
        throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf.capacity(), i, j));
    }
    
    public int position() {
        return this.buf.position();
    }
    
    public Buffer position(final int n) {
        return this.buf.position();
    }
    
    public int positionFloat() {
        return this.buf.position() / 4;
    }
    
    public Buffer positionFloat(final int n) {
        return this.buf.position();
    }
    
    public Buffer positionShort(final int n) {
        return this.buf.position();
    }
    
    public void putFloat(final float n) {
        this.buf.putFloat(n);
    }
    
    public void putFloat(final int n, final float n2) {
        this.buf.putFloat(n * 4, n2);
    }
    
    public void putRawFloat(final int n, final float n2) {
        this.buf.putFloat(n, n2);
    }
    
    public void putRawInt(final int n, final int n2) {
        this.buf.putInt(n, n2);
    }
    
    public void putShort(final short n) {
        this.buf.putShort(n);
    }
    
    public void read(final InputStream inputStream) throws IOException {
        final ByteBuffer byteBuffer = this.asByteBuffer();
        final byte[] array = new byte[byteBuffer.capacity()];
        inputStream.read(array);
        final int position = byteBuffer.position();
        byteBuffer.position();
        byteBuffer.put(array);
        byteBuffer.position();
    }
}
