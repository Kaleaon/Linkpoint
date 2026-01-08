package com.lumiyaviewer.lumiya.utils

import java.io.DataInput
import java.io.DataInputStream
import java.io.EOFException
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

class LittleEndianDataInputStream(inStream: InputStream) : FilterInputStream(inStream), DataInput {

    private val dataIn = DataInputStream(inStream)
    private val buffer = ByteArray(8)

    @Throws(IOException::class)
    override fun readFully(b: ByteArray) {
        dataIn.readFully(b)
    }

    @Throws(IOException::class)
    override fun readFully(b: ByteArray, off: Int, len: Int) {
        dataIn.readFully(b, off, len)
    }

    @Throws(IOException::class)
    override fun skipBytes(n: Int): Int {
        return dataIn.skipBytes(n)
    }

    @Throws(IOException::class)
    override fun readBoolean(): Boolean {
        return dataIn.readBoolean()
    }

    @Throws(IOException::class)
    override fun readByte(): Byte {
        return dataIn.readByte()
    }

    @Throws(IOException::class)
    override fun readUnsignedByte(): Int {
        return dataIn.readUnsignedByte()
    }

    @Throws(IOException::class)
    override fun readShort(): Short {
        val b1 = read()
        val b2 = read()
        if ((b1 or b2) < 0) throw EOFException()
        return (b1 or (b2 shl 8)).toShort()
    }

    @Throws(IOException::class)
    override fun readUnsignedShort(): Int {
        val b1 = read()
        val b2 = read()
        if ((b1 or b2) < 0) throw EOFException()
        return b1 or (b2 shl 8)
    }

    @Throws(IOException::class)
    override fun readChar(): Char {
        val b1 = read()
        val b2 = read()
        if ((b1 or b2) < 0) throw EOFException()
        return (b1 or (b2 shl 8)).toChar()
    }

    @Throws(IOException::class)
    override fun readInt(): Int {
        val b1 = read()
        val b2 = read()
        val b3 = read()
        val b4 = read()
        if ((b1 or b2 or b3 or b4) < 0) throw EOFException()
        return b1 or (b2 shl 8) or (b3 shl 16) or (b4 shl 24)
    }

    @Throws(IOException::class)
    override fun readLong(): Long {
        readFully(buffer, 0, 8)
        return (buffer[0].toLong() and 0xFF) or
                ((buffer[1].toLong() and 0xFF) shl 8) or
                ((buffer[2].toLong() and 0xFF) shl 16) or
                ((buffer[3].toLong() and 0xFF) shl 24) or
                ((buffer[4].toLong() and 0xFF) shl 32) or
                ((buffer[5].toLong() and 0xFF) shl 40) or
                ((buffer[6].toLong() and 0xFF) shl 48) or
                ((buffer[7].toLong() and 0xFF) shl 56)
    }

    @Throws(IOException::class)
    override fun readFloat(): Float {
        return Float.fromBits(readInt())
    }

    @Throws(IOException::class)
    override fun readDouble(): Double {
        return Double.fromBits(readLong())
    }

    @Throws(IOException::class)
    override fun readLine(): String? {
        throw UnsupportedOperationException("readLine is not supported")
    }

    @Throws(IOException::class)
    override fun readUTF(): String {
        return dataIn.readUTF()
    }

    @Throws(IOException::class)
    fun readZeroTerminatedString(): String {
        val sb = StringBuilder()
        while (true) {
            val b = read()
            if (b == -1 || b == 0) {
                break
            }
            sb.append(b.toChar())
        }
        return sb.toString()
    }
}
