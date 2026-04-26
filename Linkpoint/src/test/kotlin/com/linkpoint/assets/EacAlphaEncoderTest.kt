package com.linkpoint.assets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [EacAlphaEncoder]. These are pure-Kotlin tests — no
 * Android framework, no Filament — so they run on the host JVM in
 * `:testStableDebugUnitTest`. They exercise the encoder against the
 * Khronos-spec EAC decoder reimplemented inline in [decodeEac]; if
 * encoder + decoder ever drift, the round-trip assertions will catch
 * it before any device-only path does.
 */
class EacAlphaEncoderTest {

    @Test
    fun `single-block constant alpha round-trips exactly`() {
        // A constant 4×4 alpha block should decode to the exact source value
        // for every interesting alpha (transparent, opaque, mid-grey).
        for (target in intArrayOf(0, 64, 128, 192, 255)) {
            val src = ByteArray(16) { target.toByte() }
            val encoded = EacAlphaEncoder.encode(src, width = 4, height = 4)
            assertEquals("encoded length", 8, encoded.size)
            val decoded = decodeEac(encoded, width = 4, height = 4)
            for (p in 0 until 16) {
                assertEquals("pixel $p (target=$target)", target, decoded[p].toInt() and 0xFF)
            }
        }
    }

    @Test
    fun `full alpha gradient is reasonably approximated`() {
        // 8×8 ramp: alpha = (x + y * 8) * 4, capped at 255.
        val w = 8
        val h = 8
        val src = ByteArray(w * h) { i ->
            val x = i % w
            val y = i / w
            ((x + y * w) * 4).coerceAtMost(255).toByte()
        }
        val encoded = EacAlphaEncoder.encode(src, w, h)
        assertEquals("encoded length", (w / 4) * (h / 4) * 8, encoded.size)
        val decoded = decodeEac(encoded, w, h)

        // Per-pixel error budget: spec-conformant block encoders typically
        // achieve <10 absolute units on smooth ramps. Use a generous bound
        // so the test isn't fragile to small encoder tweaks while still
        // catching any catastrophic regression (e.g. a swapped index byte).
        var maxErr = 0
        var sumSqErr = 0L
        for (p in src.indices) {
            val s = src[p].toInt() and 0xFF
            val d = decoded[p].toInt() and 0xFF
            val e = (s - d).let { if (it < 0) -it else it }
            if (e > maxErr) maxErr = e
            sumSqErr += (e * e).toLong()
        }
        val rms = kotlin.math.sqrt(sumSqErr.toDouble() / src.size)
        assertTrue("RMS error $rms should be ≤ 12", rms <= 12.0)
        assertTrue("max per-pixel error $maxErr should be ≤ 32", maxErr <= 32)
    }

    @Test
    fun `width and height must be multiples of 4`() {
        val src = ByteArray(5 * 4) { 0 }
        try {
            EacAlphaEncoder.encode(src, width = 5, height = 4)
            fail("expected IllegalArgumentException for non-multiple-of-4 width")
        } catch (_: IllegalArgumentException) {
            // pass
        }
    }

    /**
     * Reference EAC decoder. Reads the 8-byte block layout that
     * `EacAlphaEncoder.writeBlock` produces and returns the per-pixel
     * alpha array. Keeps the test self-contained (no Android, no
     * external deps) and double-checks the encoder against the
     * Khronos formula directly.
     */
    private fun decodeEac(encoded: ByteArray, width: Int, height: Int): ByteArray {
        val tables = arrayOf(
            intArrayOf(-3, -6, -9, -15, 2, 5, 8, 14),
            intArrayOf(-3, -7, -10, -13, 2, 6, 9, 12),
            intArrayOf(-2, -5, -8, -13, 1, 4, 7, 12),
            intArrayOf(-2, -4, -6, -13, 1, 3, 5, 12),
            intArrayOf(-3, -6, -8, -12, 2, 5, 7, 11),
            intArrayOf(-3, -7, -9, -11, 2, 6, 8, 10),
            intArrayOf(-4, -7, -8, -11, 3, 6, 7, 10),
            intArrayOf(-3, -5, -8, -11, 2, 4, 7, 10),
            intArrayOf(-2, -6, -8, -10, 1, 5, 7, 9),
            intArrayOf(-2, -5, -8, -10, 1, 4, 7, 9),
            intArrayOf(-2, -4, -8, -10, 1, 3, 7, 9),
            intArrayOf(-2, -5, -7, -10, 1, 4, 6, 9),
            intArrayOf(-3, -4, -7, -10, 2, 3, 6, 9),
            intArrayOf(-1, -2, -3, -10, 0, 1, 2, 9),
            intArrayOf(-4, -6, -8, -9, 3, 5, 7, 8),
            intArrayOf(-3, -5, -7, -9, 2, 4, 6, 8),
        )
        val blocksX = width / 4
        val blocksY = height / 4
        val out = ByteArray(width * height)
        for (by in 0 until blocksY) {
            for (bx in 0 until blocksX) {
                val off = (by * blocksX + bx) * 8
                val base = encoded[off].toInt() and 0xFF
                val mt = encoded[off + 1].toInt() and 0xFF
                val multiplier = (mt shr 4) and 0x0F
                val tableIdx = mt and 0x0F
                val table = tables[tableIdx]
                // Pack 6 index bytes back into a 48-bit big-endian value.
                var bits = 0L
                for (i in 0 until 6) {
                    bits = (bits shl 8) or (encoded[off + 2 + i].toLong() and 0xFFL)
                }
                for (p in 0 until 16) {
                    val idx = ((bits ushr ((15 - p) * 3)) and 0x7L).toInt()
                    val decoded = (base + multiplier * table[idx]).coerceIn(0, 255)
                    val row = p / 4
                    val col = p % 4
                    out[(by * 4 + row) * width + (bx * 4 + col)] = decoded.toByte()
                }
            }
        }
        return out
    }

    private fun fail(msg: String): Nothing = throw AssertionError(msg)
}
