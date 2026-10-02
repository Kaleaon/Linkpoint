package com.linkpoint.voice

import android.util.Log

/**
 * Java delegation wrapper for shutting down legacy 32-bit Vivox engine.
 * Voice state management is fully delegated to Java VoiceManager and WebRtcVoiceSession.
 */
object VivoxVoiceEngine {
    private const val TAG = "VivoxVoiceEngine"
    private var isLibraryLoaded = false

    private fun logI(msg: String) {
        try { Log.i(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") }
    }
    private fun logW(msg: String) {
        try { Log.w(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") }
    }

    init {
        try {
            System.loadLibrary("linkpoint-j2k")
            isLibraryLoaded = true
        } catch (e: Throwable) {
            logW("Native library linkpoint-j2k not available for VivoxVoiceEngine: ${e.message}")
        }
    }

    /**
     * Shut down legacy 32-bit Vivox engine components.
     */
    fun shutdown() {
        logI("Shutting down legacy 32-bit Vivox components and delegating voice management to Java VoiceManager")
        if (isLibraryLoaded) {
            try {
                nativeShutdownVivox()
            } catch (e: Throwable) {
                logW("nativeShutdownVivox not available: ${e.message}")
            }
        }
    }

    fun isVivoxSupported(): Boolean {
        if (!isLibraryLoaded) return false
        return try {
            nativeIsVivoxSupported()
        } catch (e: Throwable) {
            false
        }
    }

    @JvmStatic
    private external fun nativeIsVivoxSupported(): Boolean

    @JvmStatic
    private external fun nativeShutdownVivox()
}
