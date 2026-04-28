package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector3

// Generic metadata about a decoded audio asset.
class AudioData(val id: LLUUID) {
    var hasLocalData: Boolean      = false
    var hasDecodedData: Boolean    = false
    var hasCompletedDecode: Boolean = false
    var hasDecodeFailed: Boolean   = false
    var hasWavLoadFailed: Boolean  = false

    // Set by the engine when this data is bound to a hardware buffer.
    var buffer: AudioBuffer? = null
}

// An audio source — owns one current and one queued AudioData,
// plus a channel assignment when it is actively playing.
open class AudioSource(
    val id: LLUUID,
    val ownerId: LLUUID,
    initialGain: Float = 1f,
    val type: Int = SoundFlags.TYPE_NONE,
    val sourceObjectId: LLUUID = LLUUID.NULL,
    val isTrigger: Boolean = true,
) {
    var gain: Float = initialGain.coerceIn(0f, 1f)
        set(value) { field = value.coerceIn(0f, 1f) }

    var priority: Float            = 0f
    var isMuted: Boolean           = false
    var isForcedPriority: Boolean  = false
    var loop: Boolean              = false
    var syncMaster: Boolean        = false
    var syncSlave: Boolean         = false
    var queueSounds: Boolean       = false
    var playedOnce: Boolean        = false
    var isCorrupted: Boolean       = false

    var positionGlobal: Vector3    = Vector3.ZERO
    var velocity: Vector3          = Vector3.ZERO

    var currentData: AudioData?    = null
    var queuedData: AudioData?     = null

    // Assigned by the engine when a hardware channel is acquired.
    var channel: AudioChannel?     = null

    val isDone: Boolean
        get() = !loop && playedOnce && channel == null

    open fun update() {}

    fun updatePriority() {
        priority = if (isForcedPriority) 1f else gain
    }

    fun stop() {
        channel?.cleanup()
        channel = null
        currentData = null
    }
}

// Base class for a single hardware playback channel.
abstract class AudioChannel {
    var currentSource: AudioSource? = null
    var currentBuffer: AudioBuffer? = null
    var secondaryGain: Float        = 1f
    var loopedThisFrame: Boolean    = false
    var waiting: Boolean            = false

    abstract fun play()
    abstract fun playSynced(other: AudioChannel)
    abstract fun cleanup()
    abstract fun isPlaying(): Boolean
    abstract fun update3DPosition()
    abstract fun updateLoop()

    open fun setSource(source: AudioSource?) {
        currentSource = source
    }

    open fun updateBuffer(): Boolean {
        val src = currentSource ?: return false
        val data = src.currentData ?: return false
        if (data.buffer == currentBuffer) return false
        currentBuffer = data.buffer
        return true
    }
}

// Engine-managed audio buffer holding decoded PCM data ready for hardware.
abstract class AudioBuffer {
    var inUse: Boolean              = false
    var audioData: AudioData?       = null

    abstract fun loadWav(filename: String): Boolean
    abstract fun getLength(): UInt
}
