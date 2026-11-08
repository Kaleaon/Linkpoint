package com.linkpoint.voice

import android.util.Log

/**
 * JNI audio bridge passing decoded raw PCM audio packets from Java AudioTrack sinks /
 * WebRTC audio receivers into SpatialAudioEngine for 3D spatial matrix calculations.
 */
open class SpatialAudioBridge(
    sampleRate: Int = 48000,
    maxRingBufferFrames: Int = 1440 // ~30ms at 48kHz
) {
    companion object {
        private const val TAG = "SpatialAudioBridge"
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
                logI("Native library linkpoint-j2k loaded for SpatialAudioBridge")
            } catch (e: Throwable) {
                logW("Native library linkpoint-j2k not available for SpatialAudioBridge: ${e.message}")
            }
        }
    }

    private var nativeHandle: Long = 0

    init {
        if (isLibraryLoaded) {
            try {
                nativeHandle = nativeInit(sampleRate, maxRingBufferFrames)
            } catch (e: Throwable) {
                logW("nativeInit failed: ${e.message}")
                nativeHandle = 0L
            }
        }
    }

    /**
     * Process raw decoded PCM audio from Java AudioTrack sink through JNI native SpatialAudioEngine.
     * Returns the number of output samples processed into outputPcm buffer.
     */
    fun processPcmChunk(
        inputPcm: ShortArray,
        inputSamples: Int,
        channels: Int,
        outputPcm: ShortArray
    ): Int {
        if (nativeHandle != 0L && isLibraryLoaded) {
            try {
                return nativeProcessPcmBuffer(
                    nativeHandle,
                    inputPcm,
                    inputSamples,
                    channels,
                    outputPcm,
                    outputPcm.size
                )
            } catch (e: Throwable) {
                logW("nativeProcessPcmBuffer failed: ${e.message}")
            }
        }
        // Fallback: copy input to output directly if native engine is unavailable
        val copyLength = minOf(inputPcm.size, outputPcm.size)
        System.arraycopy(inputPcm, 0, outputPcm, 0, copyLength)
        return copyLength
    }

    open fun updateListener(
        x: Float, y: Float, z: Float,
        lookX: Float = 0f, lookY: Float = 1f, lookZ: Float = 0f,
        upX: Float = 0f, upY: Float = 0f, upZ: Float = 1f
    ) {
        if (nativeHandle != 0L && isLibraryLoaded) {
            try {
                nativeUpdateListenerPosition(nativeHandle, x, y, z, lookX, lookY, lookZ, upX, upY, upZ)
            } catch (e: Throwable) {
                logW("nativeUpdateListenerPosition failed: ${e.message}")
            }
        }
    }

    open fun updateSource(x: Float, y: Float, z: Float) {
        if (nativeHandle != 0L && isLibraryLoaded) {
            try {
                nativeUpdateSourcePosition(nativeHandle, x, y, z)
            } catch (e: Throwable) {
                logW("nativeUpdateSourcePosition failed: ${e.message}")
            }
        }
    }

    val latencyMs: Float
        get() {
            if (nativeHandle != 0L && isLibraryLoaded) {
                try {
                    return nativeGetLatencyMs(nativeHandle)
                } catch (e: Throwable) {
                    logW("nativeGetLatencyMs failed: ${e.message}")
                }
            }
            return 0.0f
        }

    fun release() {
        if (nativeHandle != 0L && isLibraryLoaded) {
            try {
                nativeRelease(nativeHandle)
            } catch (e: Throwable) {
                logW("nativeRelease failed: ${e.message}")
            } finally {
                nativeHandle = 0L
            }
        }
    }

    // Native JNI functions
    private external fun nativeInit(sampleRate: Int, maxRingBufferFrames: Int): Long
    private external fun nativeProcessPcmBuffer(
        handle: Long,
        inputBuffer: ShortArray,
        inputSamples: Int,
        channels: Int,
        outputBuffer: ShortArray,
        maxOutputSamples: Int
    ): Int
    private external fun nativeUpdateListenerPosition(
        handle: Long,
        x: Float, y: Float, z: Float,
        lookX: Float, lookY: Float, lookZ: Float,
        upX: Float, upY: Float, upZ: Float
    )
    private external fun nativeUpdateSourcePosition(handle: Long, x: Float, y: Float, z: Float)
    private external fun nativeGetLatencyMs(handle: Long): Float
    private external fun nativeRelease(handle: Long)
}
