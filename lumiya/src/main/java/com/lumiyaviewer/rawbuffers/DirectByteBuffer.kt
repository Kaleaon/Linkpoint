package com.lumiyaviewer.rawbuffers

import java.io.IOException
import java.io.InputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class DirectByteBuffer {
    private var buf: ByteBuffer?

    init {
        try {
            System.loadLibrary("rawbuf")
        } catch (e: UnsatisfiedLinkError) {
            // Ignore for now if library is missing in this environment
        }
    }

    constructor(i: Int) {
        this.buf = allocate(i)
        if (this.buf != null) {
            this.buf!!.order(ByteOrder.nativeOrder())
            return
        }
        throw OutOfMemoryError("DirectByteBuffer: Failed to allocate " + i + " bytes")
    }

    constructor(directByteBuffer: DirectByteBuffer) {
        this.buf = allocate(directByteBuffer.asByteBuffer().capacity())
        if (this.buf != null) {
            this.buf!!.order(ByteOrder.nativeOrder())
            copyFrom(0, directByteBuffer, 0, directByteBuffer.asByteBuffer().capacity())
            return
        }
        throw OutOfMemoryError("DirectByteBuffer: Failed to allocate " + directByteBuffer.asByteBuffer().capacity() + " bytes")
    }

    // Stub native method
    private fun allocate(i: Int): ByteBuffer {
        return ByteBuffer.allocateDirect(i)
    }

    private external fun copyByteArray(byteBuffer: ByteBuffer, i: Int, bArr: ByteArray, i2: Int, i3: Int)

    private external fun copyFloatArray(byteBuffer: ByteBuffer, i: Int, fArr: FloatArray, i2: Int, i3: Int)

    private external fun copyPart(byteBuffer: ByteBuffer, byteBuffer2: ByteBuffer, i: Int, i2: Int, i3: Int)

    private external fun copyShortArray(byteBuffer: ByteBuffer, i: Int, sArr: ShortArray, i2: Int, i3: Int)

    private external fun copyShortArrayOffset(byteBuffer: ByteBuffer, i: Int, sArr: ShortArray, i2: Int, i3: Int, s: Short)

    private fun release(byteBuffer: ByteBuffer) {
        // No-op in Java, reliant on GC or native release if implemented
    }

    @Throws(IndexOutOfBoundsException::class)
    fun zeroDecode(bArr: ByteArray, i: Int, i2: Int, bArr2: ByteArray, i3: Int, i4: Int): Int {
        val zeroDecodeArray = zeroDecodeArray(bArr, i, i2, bArr2, i3, i4)
        if (zeroDecodeArray >= 0) {
            return zeroDecodeArray
        }
        throw IndexOutOfBoundsException("zeroDecode: out of dest buffer, destStart " + i + " destMaxLen " + i2)
    }

    private external fun zeroDecodeArray(bArr: ByteArray, i: Int, i2: Int, bArr2: ByteArray, i3: Int, i4: Int): Int

    fun asByteBuffer(): ByteBuffer {
        return this.buf!!
    }

    fun asFloatBuffer(): FloatBuffer {
        return this.buf!!.asFloatBuffer()
    }

    fun asIntBuffer(): IntBuffer {
        return this.buf!!.asIntBuffer()
    }

    fun asShortBuffer(): ShortBuffer {
        return this.buf!!.asShortBuffer()
    }

    fun copyFrom(i: Int, directByteBuffer: DirectByteBuffer, i2: Int, i3: Int) {
        // Fallback implementation if native fails or for stub
        val src = directByteBuffer.buf!!.duplicate()
        src.position(i2)
        src.limit(i2 + i3)
        val dst = this.buf!!.duplicate()
        dst.position(i)
        dst.put(src)
        // copyPart(this.buf!!, directByteBuffer.buf!!, i, i2, i3)
    }

    fun copyFromFloat(i: Int, directByteBuffer: DirectByteBuffer, i2: Int, i3: Int) {
        copyPart(this.buf!!, directByteBuffer.buf!!, i * 4, i2 * 4, i3 * 4)
    }

    fun copyFromShort(i: Int, directByteBuffer: DirectByteBuffer, i2: Int, i3: Int) {
        copyPart(this.buf!!, directByteBuffer.buf!!, i * 2, i2 * 2, i3 * 2)
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        if (this.buf != null) {
            release(this.buf!!)
            this.buf = null
        }
    }

    fun getCapacity(): Int {
        return this.buf!!.capacity()
    }

    fun getFloat(i: Int): Float {
        return this.buf!!.getFloat(i * 4)
    }

    fun getShort(i: Int): Short {
        return this.buf!!.getShort(i * 2)
    }

    fun loadFromByteArray(i: Int, bArr: ByteArray, i2: Int, i3: Int) {
        val i4 = i + i3
        if (i >= 0 && i <= this.buf!!.capacity() && i4 >= 0 && i4 <= this.buf!!.capacity()) {
            // Fallback
            val dst = this.buf!!.duplicate()
            dst.position(i)
            dst.put(bArr, i2, i3)
            // copyByteArray(this.buf!!, i, bArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf!!.capacity(), i, i4))
    }

    fun loadFromFloatArray(i: Int, fArr: FloatArray, i2: Int, i3: Int) {
        val i4 = i * 4
        val i5 = (i + i3) * 4
        if (i4 >= 0 && i4 <= this.buf!!.capacity() && i5 >= 0 && i5 <= this.buf!!.capacity()) {
            copyFloatArray(this.buf!!, i, fArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf!!.capacity(), i4, i5))
    }

    fun loadFromShortArray(i: Int, sArr: ShortArray, i2: Int, i3: Int) {
        val i4 = i * 2
        val i5 = (i + i3) * 2
        if (i4 >= 0 && i4 <= this.buf!!.capacity() && i5 >= 0 && i5 <= this.buf!!.capacity()) {
            copyShortArray(this.buf!!, i, sArr, i2, i3)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf!!.capacity(), i4, i5))
    }

    fun loadFromShortArrayOffset(i: Int, sArr: ShortArray, i2: Int, i3: Int, s: Short) {
        val i4 = i * 2
        val i5 = (i + i3) * 2
        if (i4 >= 0 && i4 <= this.buf!!.capacity() && i5 >= 0 && i5 <= this.buf!!.capacity()) {
            copyShortArrayOffset(this.buf!!, i, sArr, i2, i3, s)
            return
        }
        throw IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", this.buf!!.capacity(), i4, i5))
    }

    fun position(): Int {
        return this.buf!!.position()
    }

    fun position(i: Int): Buffer {
        return this.buf!!.position(i)
    }

    fun positionFloat(): Int {
        return this.buf!!.position() / 4
    }

    fun positionFloat(i: Int): Buffer {
        return this.buf!!.position(i * 4)
    }

    fun positionShort(i: Int): Buffer {
        return this.buf!!.position(i * 2)
    }

    fun putFloat(f: Float) {
        this.buf!!.putFloat(f)
    }

    fun putFloat(i: Int, f: Float) {
        this.buf!!.putFloat(i * 4, f)
    }

    fun putRawFloat(i: Int, f: Float) {
        this.buf!!.putFloat(i, f)
    }

    fun putRawInt(i: Int, i2: Int) {
        this.buf!!.putInt(i, i2)
    }

    fun putShort(s: Short) {
        this.buf!!.putShort(s)
    }

    @Throws(IOException::class)
    fun read(inputStream: InputStream) {
        val asByteBuffer = asByteBuffer()
        val bArr = ByteArray(asByteBuffer.capacity())
        inputStream.read(bArr)
        val position = asByteBuffer.position()
        asByteBuffer.position(0)
        asByteBuffer.put(bArr)
        asByteBuffer.position(position)
    }
}
