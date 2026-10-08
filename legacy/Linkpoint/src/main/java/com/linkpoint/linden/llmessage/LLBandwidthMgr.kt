package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLTimer

enum class BandwidthCategory(val index: Int) {
    RESEND(0), TEXTURE(1), OBJECT(2), ASSET(3), LAND(4), WIND(5), CLOUD(6);

    companion object { val SIZE = entries.size }
}

class LLBandwidthMgr {
    private inner class TokenBucket(private val bitsPerSec: Float) {
        private var tokens: Float = bitsPerSec
        private val timer = LLTimer()
        private val max: Float = bitsPerSec * 0.5f

        fun throttle(bytes: Int): Boolean {
            val elapsed = timer.getElapsedTimeAndResetF32()
            tokens = (tokens + elapsed * bitsPerSec).coerceAtMost(max)
            val needed = bytes * 8f
            if (tokens >= needed) { tokens -= needed; return true }
            return false
        }

        fun setRate(bps: Float) { tokens = bps }
    }

    private val buckets = Array(BandwidthCategory.SIZE) { i ->
        val defaultBps = when (BandwidthCategory.entries[i]) {
            BandwidthCategory.TEXTURE -> 220_000f
            BandwidthCategory.OBJECT  -> 220_000f
            BandwidthCategory.ASSET   -> 220_000f
            else                      -> 50_000f
        }
        TokenBucket(defaultBps)
    }

    fun throttle(bytes: Int, category: BandwidthCategory): Boolean =
        buckets[category.index].throttle(bytes)

    fun setRate(category: BandwidthCategory, bitsPerSec: Float) {
        buckets[category.index].setRate(bitsPerSec)
    }

    fun allowAll(bytes: Int, category: BandwidthCategory): Boolean = true
}
