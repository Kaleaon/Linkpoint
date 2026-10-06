package com.linkpoint.assets.mesh

import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDParser
import java.util.concurrent.atomic.AtomicLong

/**
 * Result of decoding a binary LLSD mesh header.
 *
 * @property map Parsed LLSD map containing LOD metadata and mesh headers.
 * @property headerEnd Consumed byte boundary offset in the original payload where the header ends.
 */
data class HeaderResult(
    val map: LLSDMap,
    val headerEnd: Int
)

/**
 * Decodes binary LLSD headers and calculates exact consumed byte boundaries.
 */
class MeshHeaderDecoder {

    private val parseFailures = AtomicLong(0)
    private val parseSuccesses = AtomicLong(0)

    /**
     * Decodes the binary LLSD header from [data].
     * Computes exact header boundary including magic prefix offset if present.
     *
     * @param data Binary payload containing the LLSD mesh header at offset 0.
     * @return [HeaderResult] on success or null on parse failure.
     */
    fun decodeHeader(data: ByteArray): HeaderResult? {
        if (data.isEmpty()) {
            parseFailures.incrementAndGet()
            return null
        }

        val magicOffset = calculateMagicHeaderOffset(data)
        val payload = if (magicOffset > 0) {
            if (magicOffset >= data.size) {
                parseFailures.incrementAndGet()
                return null
            }
            data.copyOfRange(magicOffset, data.size)
        } else {
            data
        }

        val (value, consumed) = LLSDParser.parseBinaryAndConsumed(payload)
        if (consumed <= 0 || value !is LLSDMap) {
            parseFailures.incrementAndGet()
            return null
        }

        parseSuccesses.incrementAndGet()
        val totalHeaderEnd = magicOffset + consumed
        return HeaderResult(value, totalHeaderEnd)
    }

    /**
     * Returns the magic header prefix byte count if present.
     */
    private fun calculateMagicHeaderOffset(data: ByteArray): Int {
        val magic = "<?llsd/binary?>".toByteArray(Charsets.US_ASCII)
        if (data.size < magic.size) return 0
        for (i in magic.indices) {
            if (data[i] != magic[i]) return 0
        }
        var idx = magic.size
        if (idx < data.size && data[idx] == '\r'.code.toByte()) idx++
        if (idx < data.size && data[idx] == '\n'.code.toByte()) idx++
        return idx
    }

    /** Returns total number of failed header decode attempts. */
    fun getParseFailureCount(): Long = parseFailures.get()

    /** Returns total number of successful header decode attempts. */
    fun getParseSuccessCount(): Long = parseSuccesses.get()
}
