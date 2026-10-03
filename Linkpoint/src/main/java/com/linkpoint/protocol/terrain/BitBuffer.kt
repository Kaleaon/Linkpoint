package com.linkpoint.protocol.terrain

import java.nio.ByteBuffer

/**
 * Bit-level buffer reader for terrain patch decompression.
 * 
 * Based on the reference viewer's BitBuffer implementation for reading DCT-compressed
 * terrain heightmap data from the Second Life protocol.
 */
class BitBuffer(data: ByteArray) {
    private val buffer: ByteBuffer = ByteBuffer.wrap(data)
    private var bytePos = 0
    private var bitPos = 0
    
    /**
     * Read specified number of bits from the buffer.
     * Bits are read MSB first within each byte.
     */
    fun getBits(numBits: Int): Int {
        val output = ByteArray(4)
        var outputIdx = 0
        var bitsWritten = 0
        var remaining = numBits
        
        while (remaining > 0) {
            val toRead = if (remaining > 8) {
                remaining -= 8
                8
            } else {
                val r = remaining
                remaining = 0
                r
            }
            
            repeat(toRead) {
                output[outputIdx] = (output[outputIdx].toInt() shl 1).toByte()
                
                if (bytePos < buffer.limit()) {
                    val byte = buffer.get(bytePos).toInt() and 0xFF
                    if ((byte and (128 shr bitPos)) != 0) {
                        output[outputIdx] = (output[outputIdx].toInt() or 1).toByte()
                    }
                }
                
                bitPos++
                bitsWritten++
                
                if (bitPos >= 8) {
                    bitPos = 0
                    bytePos++
                }
                
                if (bitsWritten >= 8) {
                    outputIdx++
                    bitsWritten = 0
                }
            }
        }
        
        return (output[0].toInt() and 0xFF) or
               ((output[1].toInt() and 0xFF) shl 8) or
               ((output[2].toInt() and 0xFF) shl 16) or
               ((output[3].toInt() and 0xFF) shl 24)
    }
    
    /**
     * Read a 32-bit float from the buffer.
     */
    fun getFloat(): Float {
        return Float.fromBits(getBits(32))
    }
    
    /**
     * Check if we've reached end of buffer.
     */
    fun isEOF(): Boolean {
        return bytePos >= buffer.limit()
    }
}
