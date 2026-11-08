package com.linkpoint.protocol.llsd

import kotlin.random.Random
import org.junit.Test

/**
 * Fuzz-style entrypoint for LLSD binary parser hardening.
 *
 * Kept intentionally deterministic so it can run in CI.
 */
class LLSDParserFuzzTargetTest {
    @Test
    fun fuzzBinaryParserDeterministicCorpus() {
        val seeds = intArrayOf(0, 1, 7, 42, 99, 777, 1024, 4096, 65_535)
        for (seed in seeds) {
            val random = Random(seed)
            repeat(250) {
                val size = random.nextInt(0, 2048)
                val input = ByteArray(size) { random.nextInt(0, 256).toByte() }
                LLSDParser.parseBinary(input)
            }
        }
    }
}
