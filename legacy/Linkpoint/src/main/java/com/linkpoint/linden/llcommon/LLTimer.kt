package com.linkpoint.linden.llcommon

class LLTimer {

    private var startNanos: Long = System.nanoTime()
    private var expiryNanos: Long = Long.MAX_VALUE
    private var started: Boolean = true

    fun start() {
        reset()
        started = true
    }

    fun stop() {
        started = false
    }

    fun reset() {
        startNanos = System.nanoTime()
    }

    fun getElapsedTimeF32(): Float = ((System.nanoTime() - startNanos) / 1_000_000_000.0).toFloat()

    fun getElapsedTimeF64(): Double = (System.nanoTime() - startNanos) / 1_000_000_000.0

    fun getElapsedTimeAndResetF32(): Float {
        val elapsed = getElapsedTimeF32()
        reset()
        return elapsed
    }

    fun getElapsedTimeAndResetF64(): Double {
        val elapsed = getElapsedTimeF64()
        reset()
        return elapsed
    }

    fun getRemainingTimeF32(): Float {
        val remaining = (expiryNanos - System.nanoTime()) / 1_000_000_000.0f
        return if (remaining < 0f) 0f else remaining
    }

    fun setTimerExpirySec(secs: Double) {
        expiryNanos = System.nanoTime() + (secs * 1_000_000_000.0).toLong()
    }

    fun hasExpired(): Boolean = System.nanoTime() >= expiryNanos

    fun checkExpirationAndReset(expiration: Float): Boolean {
        if (hasExpired()) {
            setTimerExpirySec(expiration.toDouble())
            reset()
            return true
        }
        return false
    }

    fun getStarted(): Boolean = started

    companion object {
        @JvmStatic
        var sTimer: LLTimer? = LLTimer()

        @JvmStatic
        fun getTotalSeconds(): Double = System.currentTimeMillis() / 1_000.0

        @JvmStatic
        fun getTotalTime(): Long = System.currentTimeMillis() * 1_000L

        @JvmStatic
        fun getElapsedSeconds(): Double =
            sTimer?.getElapsedTimeF64() ?: 0.0

        @JvmStatic
        fun knifeCPU() {}
    }
}

class LLFrameTimer {

    private var startNanos: Long = frameTimeNanos
    private var expiryNanos: Long = Long.MAX_VALUE

    fun reset() {
        startNanos = frameTimeNanos
    }

    fun getElapsedSeconds(): Double = (frameTimeNanos - startNanos) / 1_000_000_000.0

    fun setTimerExpirySec(secs: Double) {
        expiryNanos = frameTimeNanos + (secs * 1_000_000_000.0).toLong()
    }

    fun hasExpired(): Boolean = frameTimeNanos >= expiryNanos

    companion object {
        @Volatile private var frameTimeNanos: Long = System.nanoTime()
        @Volatile private var frameCount: Long = 0L

        @JvmStatic
        fun update() {
            frameTimeNanos = System.nanoTime()
            frameCount++
        }

        @JvmStatic
        fun getFrameTime(): Double = frameTimeNanos / 1_000_000_000.0

        @JvmStatic
        fun getFrameCount(): Long = frameCount
    }
}
