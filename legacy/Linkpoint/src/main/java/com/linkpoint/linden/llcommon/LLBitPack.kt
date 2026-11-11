package com.linkpoint.linden.llcommon

private const val MAX_DATA_BITS = 8

/**
 * Bit-level packer / unpacker, direct port of LLBitPack.
 *
 * [buffer] is the backing store.  [maxSize] is the maximum number of bytes
 * that may be written into it.  The class tracks [bufferSize] (bytes
 * committed to the buffer), [loadSize] (bits currently held in the
 * accumulator [load]), and [totalBits] (total bits packed so far).
 */
class BitPack(val buffer: ByteArray, val maxSize: Int) {
    var bufferSize: Int = 0
        private set
    var totalBits: Int = 0
        private set

    private var load: Int = 0       // accumulator byte (up to 8 bits)
    private var loadSize: Int = 0   // how many valid bits are in [load]

    fun resetBitPacking() {
        load = 0
        loadSize = 0
        totalBits = 0
        bufferSize = 0
    }

    /**
     * Pack [numBits] bits from [data] into the buffer.
     * Each byte in [data] contributes its top [dsize] bits (where dsize <= 8),
     * matching the C++ left-shift-before-pack convention.
     */
    fun bitPack(data: ByteArray, numBits: Int) {
        var remaining = numBits
        var srcIdx = 0

        while (remaining > 0) {
            val dsize = if (remaining > MAX_DATA_BITS) MAX_DATA_BITS else remaining
            remaining -= dsize

            var dataByte = data[srcIdx++].toInt() and 0xFF
            // align the meaningful bits to the MSB
            dataByte = (dataByte shl (MAX_DATA_BITS - dsize)) and 0xFF

            repeat(dsize) {
                if (loadSize == MAX_DATA_BITS) {
                    check(bufferSize < maxSize) { "bufferSize exceeding maxSize!" }
                    buffer[bufferSize++] = load.toByte()
                    loadSize = 0
                    load = 0
                }
                load = ((load shl 1) or (dataByte ushr (MAX_DATA_BITS - 1))) and 0xFF
                dataByte = (dataByte shl 1) and 0xFF
                loadSize++
                totalBits++
            }
        }
    }

    /**
     * Unpack [numBits] bits from the buffer into [outBuffer].
     * Each byte in [outBuffer] receives up to 8 bits, MSB first.
     */
    fun bitUnpack(outBuffer: ByteArray, numBits: Int) {
        var remaining = numBits
        var dstIdx = 0

        while (remaining > 0) {
            val dsize = if (remaining > MAX_DATA_BITS) MAX_DATA_BITS else remaining
            remaining -= dsize

            var retval = 0
            repeat(dsize) {
                if (loadSize == 0) {
                    check(bufferSize <= maxSize) { "bufferSize exceeding maxSize" }
                    load = buffer[bufferSize++].toInt() and 0xFF
                    loadSize = MAX_DATA_BITS
                }
                retval = ((retval shl 1) or (load ushr (MAX_DATA_BITS - 1))) and 0xFF
                load = (load shl 1) and 0xFF
                loadSize--
            }
            outBuffer[dstIdx++] = retval.toByte()
        }
    }

    /** Flush any partially-filled accumulator byte to the buffer. */
    fun flush(): Int {
        if (loadSize > 0) {
            load = (load shl (MAX_DATA_BITS - loadSize)) and 0xFF
            check(bufferSize < maxSize) { "bufferSize exceeding maxSize!" }
            buffer[bufferSize++] = load.toByte()
            loadSize = 0
        }
        return bufferSize
    }
}
