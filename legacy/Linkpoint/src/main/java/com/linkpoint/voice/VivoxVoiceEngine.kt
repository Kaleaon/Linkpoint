package com.linkpoint.voice

import android.util.Log

/**
 * Pure Kotlin delegation object for shutting down legacy 32-bit Vivox engine checks.
 * Voice state management is fully delegated to Java VoiceManager and WebRtcVoiceSession.
 */
object VivoxVoiceEngine {
    private const val TAG = "VivoxVoiceEngine"

    private fun logI(msg: String) {
        try { Log.i(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") }
    }

    /**
     * Shut down legacy 32-bit Vivox engine components purely in Kotlin memory.
     */
    fun shutdown() {
        logI("Shutting down legacy 32-bit Vivox components and delegating voice management to Java VoiceManager")
    }

    /**
     * Returns false as legacy Vivox is permanently disabled in favor of WebRTC voice pipeline.
     */
    fun isVivoxSupported(): Boolean {
        return false
    }
}
