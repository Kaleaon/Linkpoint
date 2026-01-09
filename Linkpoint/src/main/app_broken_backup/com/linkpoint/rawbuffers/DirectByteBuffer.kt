package com.linkpoint.rawbuffers

import java.io.IOException
import java.io.InputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class DirectByteBuffer {
    private ByteBuffer buf

    {
        System.loadLibrary("rawbuf")
    }

    DirectByteBuffer(Int i) {
        this.buf = allocate(i)
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder())
            return
        }
        throw OutOfMemoryError("DirectByteBuffer: Failed to allocate " + i + " bytes")
    }

    DirectByteBuffer(DirectByteBuffer directByteBuffer) {
        this.buf = allocate(directByteBuffer.asByteBuffer().capacity())
        if (this.buf != null) {
            this.buf.order(ByteOrder.nativeOrder())
            copyFrom(0, directByteBuffer, 0, directByteBuffer.asByteBuffer().capacity())
            return
        }
        throw OutOfMemoryError("DirectByteBuffer: Failed to allocate " + directByteBuffer.asByteBuffer().capacity() + " bytes")
    }

    private native ByteBuffer allocate(Int i)

    private native Unit copyByteArray(ByteBuffer byteBuffer, Int i, ByteArray bArr, Int i2, Int i3)

    private native Unit copyFloatArray(ByteBuffer byteBuffer, Int i, FloatArray fArr, Int i2, Int i3)

    private native Unit copyPart(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, Int i, Int i2, Int i3)

    private native Unit copyShortArray(ByteBuffer byteBuffer, Int i, ShortArray sArr, Int i2, Int i3)

    private native Unit copyShortArrayOffset(ByteBuffer byteBuffer, Int i, ShortArray sArr, Int i2, Int i3, Short s)

    private native Unit release(ByteBuffer byteBuffer)

    @Throws(IndexOutOfBoundsException::class)

    fun zeroDecode(ByteArray bArr, Int i, Int i2, ByteArray bArr2, Int i3, Int i4): Int {
        Int zeroDecodeArray = zeroDecodeArray(bArr, i, i2, bArr2, i3, i4)
        if (zeroDecodeArray >= 0) {
            return zeroDecodeArray
        }
        throw IndexOutOfBoundsException("zeroDecode: out of dest buffer, destStart " + Int.toString(i) + " destMaxLen " + Int.toString(i2))
    }

    private native Int zeroDecodeArray(ByteArray bArr, Int i, Int i2, ByteArray bArr2, Int i3, Int i4)

    fun asByteBuffer(): ByteBuffer {
        return this.buf
    }

    fun asFloatBuffer(): FloatBuffer {
        return this.buf.asFloatBuffer()
    }

    fun asIntBuffer(): IntBuffer {
        return this.buf.asIntBuffer()
    }

    fun asShortBuffer(): ShortBuffer {
        return this.buf.asShortBuffer()
    }

    fun copyFrom(Int i, DirectByteBuffer directByteBuffer, Int i2, Int i3): Unit {
        copyPart(this.buf, directByteBuffer.buf, i, i2, i3)
    }

    fun copyFromFloat(Int i, DirectByteBuffer directByteBuffer, Int i2, Int i3): Unit {
        copyPart(this.buf, directByteBuffer.buf, i * 4, i2 * 4, i3 * 4)
    }

    fun copyFromShort(Int i, DirectByteBuffer directByteBuffer, Int i2, Int i3): Unit {
        copyPart(this.buf, directByteBuffer.buf, i * 2, i2 * 2, i3 * 2)
    }

    /* access modifiers changed from: protected */
    @Throws(Throwable::class)
    fun finalize() {
        if (this.buf != null) {
            release(this.buf)
            this.buf = null
        }
        super.finalize()
    }

    fun getCapacity(): Int {
        return this.buf.capacity()
    }

    fun getFloat(Int i): Float {
        return this.buf.getFloat(i * 4)
    }

    fun getShort(Int i): Short {
        return this.buf.getShort(i * 2)
    }

    fun loadFromByteArray(Int i, ByteArray bArr, Int i2, Int i3): Unit {
        Int i4 = i + i3
        if (i >= 0 && i <= this.buf.capacity() && i4 >= 0 && i4 <= this.buf.capacity()) {
            copyByteArray(this.buf, i, bArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", Any[]{Int.valueOf(this.buf.capacity()), Int.valueOf(i), Int.valueOf(i4)}))
    }

    fun loadFromFloatArray(Int i, FloatArray fArr, Int i2, Int i3): Unit {
        Int i4 = i * 4
        Int i5 = (i + i3) * 4
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyFloatArray(this.buf, i, fArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", Any[]{Int.valueOf(this.buf.capacity()), Int.valueOf(i4), Int.valueOf(i5)}))
    }

    fun loadFromShortArray(Int i, ShortArray sArr, Int i2, Int i3): Unit {
        Int i4 = i * 2
        Int i5 = (i + i3) * 2
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyShortArray(this.buf, i, sArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", Any[]{Int.valueOf(this.buf.capacity()), Int.valueOf(i4), Int.valueOf(i5)}))
    }

    fun loadFromShortArrayOffset(Int i, ShortArray sArr, Int i2, Int i3, Short s): Unit {
        Int i4 = i * 2
        Int i5 = (i + i3) * 2
        if (i4 >= 0 && i4 <= this.buf.capacity() && i5 >= 0 && i5 <= this.buf.capacity()) {
            copyShortArrayOffset(this.buf, i, sArr, i2, i3, s)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", Any[]{Int.valueOf(this.buf.capacity()), Int.valueOf(i4), Int.valueOf(i5)}))
    }

    fun position(): Int {
        return this.buf.position()
    }

    fun position(Int i): Buffer {
        return this.buf.position(i)
    }

    fun positionFloat(): Int {
        return this.buf.position() / 4
    }

    fun positionFloat(Int i): Buffer {
        return this.buf.position(i * 4)
    }

    fun positionShort(Int i): Buffer {
        return this.buf.position(i * 2)
    }

    fun putFloat(Float f): Unit {
        this.buf.putFloat(f)
    }

    fun putFloat(Int i, Float f): Unit {
        this.buf.putFloat(i * 4, f)
    }

    fun putRawFloat(Int i, Float f): Unit {
        this.buf.putFloat(i, f)
    }

    fun putRawInt(Int i, Int i2): Unit {
        this.buf.putInt(i, i2)
    }

    fun putShort(Short s): Unit {
        this.buf.putShort(s)
    }

    @Throws(IOException::class)

    fun read(InputStream inputStream) {
        ByteBuffer asByteBuffer = asByteBuffer()
        ByteArray bArr = Byte[asByteBuffer.capacity()]
        inputStream.read(bArr)
        Int position = asByteBuffer.position()
        asByteBuffer.position(0)
        asByteBuffer.put(bArr)
        asByteBuffer.position(position)
    }
}
