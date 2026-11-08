package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector3

class LLAudioSource(
    val id: LLUUID,
    var position: Vector3 = Vector3.ZERO,
    var velocity: Vector3 = Vector3.ZERO,
    var gain: Float = 1f,
    var loop: Boolean = false,
    var ambient: Boolean = false
) {
    private val pendingBuffers: ArrayDeque<LLAudioBuffer> = ArrayDeque()
    private var currentBuffer: LLAudioBuffer? = null
    var isPlaying: Boolean = false
    var isMuted: Boolean = false

    fun queueBuffer(buffer: LLAudioBuffer) {
        pendingBuffers.addLast(buffer)
    }

    fun dequeueBuffer(): LLAudioBuffer? {
        currentBuffer = if (pendingBuffers.isNotEmpty()) pendingBuffers.removeFirst() else null
        return currentBuffer
    }

    fun getCurrentBuffer(): LLAudioBuffer? = currentBuffer

    fun hasPendingBuffers(): Boolean = pendingBuffers.isNotEmpty()

    fun play() { isPlaying = true }
    fun stop() { isPlaying = false; currentBuffer = null; pendingBuffers.clear() }
    fun pause() { isPlaying = false }

    fun getEffectiveGain(): Float = if (isMuted) 0f else gain
}
