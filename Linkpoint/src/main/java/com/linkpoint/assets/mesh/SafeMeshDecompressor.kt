package com.linkpoint.assets.mesh

import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicLong

/**
 * Thrown when decompressed stream size exceeds configured byte quota.
 */
class QuotaExceededException(message: String) : RuntimeException(message)

/**
 * Thrown when stream decompression stalls without making progress.
 */
class StreamStallException(message: String) : RuntimeException(message)

/**
 * Snapshot of decompression metrics.
 */
data class DecompressorTelemetry(
    val decompressionCount: Long,
    val totalCompressedBytes: Long,
    val totalDecompressedBytes: Long,
    val decompressionRatio: Double,
    val quotaExceededCount: Long,
    val stallCount: Long
)

/**
 * Bounded zlib decompressor for mesh streams with memory quotas and stall monitoring.
 *
 * @property maxQuotaBytes Maximum permitted output size in bytes (default 32 MB).
 */
class SafeMeshDecompressor(
    val maxQuotaBytes: Long = DEFAULT_MAX_QUOTA_BYTES
) {
    companion object {
        /** Default decompression quota: 32 MB */
        const val DEFAULT_MAX_QUOTA_BYTES: Long = 32 * 1024 * 1024L

        /** Low-memory profile quota: 32 MB */
        const val LOW_MEMORY_QUOTA_BYTES: Long = 32 * 1024 * 1024L

        /** Buffer size per inflation iteration: 16 KB */
        private const val BUFFER_SIZE: Int = 16 * 1024

        /** Maximum consecutive zero-byte inflation cycles before declaring a stall */
        private const val MAX_STALL_CYCLES: Int = 100
    }

    private val totalCompressedBytes = AtomicLong(0)
    private val totalDecompressedBytes = AtomicLong(0)
    private val decompressionCount = AtomicLong(0)
    private val quotaExceededCount = AtomicLong(0)
    private val stallCount = AtomicLong(0)

    /**
     * Inflates compressed zlib payload [data] within configured [maxQuotaBytes].
     * Uses 16 KB chunk buffers and detects inflation stalls.
     *
     * @param data Compressed byte array.
     * @return Decompressed byte array.
     * @throws QuotaExceededException if output exceeds [maxQuotaBytes].
     * @throws StreamStallException if inflater stalls repeatedly.
     */
    fun decompress(data: ByteArray): ByteArray {
        val inflater = java.util.zip.Inflater()
        try {
            inflater.setInput(data)
            val buffer = ByteArray(BUFFER_SIZE)
            val resultStream = ByteArrayOutputStream(maxOf(data.size * 4, 1024))
            var totalOutputBytes = 0L
            var stallCycles = 0

            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0) {
                    stallCycles++
                    if (stallCycles >= MAX_STALL_CYCLES || inflater.needsInput()) {
                        if (!inflater.finished()) {
                            stallCount.incrementAndGet()
                            throw StreamStallException(
                                "Decompression stalled after $stallCycles consecutive zero-byte reads (compressed size: ${data.size})"
                            )
                        }
                        break
                    }
                } else {
                    stallCycles = 0
                    totalOutputBytes += count
                    if (totalOutputBytes > maxQuotaBytes) {
                        quotaExceededCount.incrementAndGet()
                        throw QuotaExceededException(
                            "Decompressed stream size ($totalOutputBytes bytes) exceeded configured quota ($maxQuotaBytes bytes)"
                        )
                    }
                    resultStream.write(buffer, 0, count)
                }
            }

            val result = resultStream.toByteArray()
            decompressionCount.incrementAndGet()
            totalCompressedBytes.addAndGet(data.size.toLong())
            totalDecompressedBytes.addAndGet(result.size.toLong())
            return result
        } finally {
            inflater.end()
        }
    }

    /**
     * Returns a snapshot of decompression metrics.
     */
    fun getTelemetry(): DecompressorTelemetry {
        val compressed = totalCompressedBytes.get()
        val decompressed = totalDecompressedBytes.get()
        val ratio = if (compressed > 0) decompressed.toDouble() / compressed.toDouble() else 0.0
        return DecompressorTelemetry(
            decompressionCount = decompressionCount.get(),
            totalCompressedBytes = compressed,
            totalDecompressedBytes = decompressed,
            decompressionRatio = ratio,
            quotaExceededCount = quotaExceededCount.get(),
            stallCount = stallCount.get()
        )
    }
}
