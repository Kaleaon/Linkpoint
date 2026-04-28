package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector3

enum class AudioType(val id: Int) {
    NONE(0),
    SFX(1),
    UI(2),
    AMBIENT(3);

    companion object {
        private val byId = entries.associateBy { it.id }
        fun fromId(id: Int): AudioType = byId[id] ?: NONE
    }
}

enum class AudioPlayState {
    STOPPED, PLAYING, PAUSED
}

data class SoundData(
    val audioId: LLUUID,
    val ownerId: LLUUID,
    val gain: Float,
    val type: Int = SoundFlags.TYPE_NONE,
    val posGlobal: Vector3 = Vector3.ZERO,
)

abstract class AudioEngine {
    // ---- state ---------------------------------------------------------------

    @set:JvmName("setMutedProp")
    protected var muted: Boolean      = false
    @set:JvmName("setMasterGainProp")
    protected var masterGain: Float   = 1f
    @set:JvmName("setInternalGainProp")
    protected var internalGain: Float = 1f
    protected var enableWind: Boolean = false
    var maxWindGain: Float            = 1f

    protected val secondaryGain: FloatArray = FloatArray(AudioType.entries.size) { 1f }

    protected val allSources: MutableMap<LLUUID, AudioSource> = LinkedHashMap()
    protected val allData: MutableMap<LLUUID, AudioData>      = LinkedHashMap()
    protected val channels: Array<AudioChannel?> = arrayOfNulls(SoundFlags.MAX_CHANNELS)
    protected val buffers: Array<AudioBuffer?>   = arrayOfNulls(SoundFlags.MAX_BUFFERS)

    // ---- lifecycle -----------------------------------------------------------

    open fun init(numChannels: Int, outputDevice: String): Boolean = true
    open fun shutdown() {
        allSources.clear()
        allData.clear()
    }

    abstract fun getDriverName(verbose: Boolean): String

    // ---- per-frame -----------------------------------------------------------

    open fun idle(listenerPos: Vector3) {
        updateChannels()
    }

    open fun updateChannels() {}

    // ---- listener ------------------------------------------------------------

    open fun setListener(pos: Vector3, vel: Vector3, up: Vector3, at: Vector3) {
        setListenerPos(pos)
        setListenerVelocity(vel)
        orientListener(up, at)
    }

    open fun setListenerPos(pos: Vector3) {}
    open fun setListenerVelocity(vel: Vector3) {}
    open fun orientListener(up: Vector3, at: Vector3) {}
    open fun getListenerPos(): Vector3 = Vector3.ZERO

    // ---- gain ----------------------------------------------------------------

    fun setMasterGain(gain: Float) {
        masterGain = gain.coerceIn(0f, 1f)
        setInternalGain(if (muted) 0f else masterGain)
    }

    fun setMuted(muted: Boolean) {
        this.muted = muted
        setInternalGain(if (muted) 0f else masterGain)
    }

    fun setSecondaryGain(type: Int, gain: Float) {
        if (type in secondaryGain.indices) secondaryGain[type] = gain.coerceIn(0f, 1f)
    }

    fun getSecondaryGain(type: Int): Float =
        if (type in secondaryGain.indices) secondaryGain[type] else 1f

    protected abstract fun setInternalGain(gain: Float)

    // ---- wind ----------------------------------------------------------------

    open fun isWindEnabled(): Boolean = enableWind
    open fun enableWind(enabled: Boolean) { enableWind = enabled }
    abstract fun updateWind(direction: Vector3, cameraHeightAboveWater: Float)

    // ---- sound triggering ----------------------------------------------------

    fun triggerSound(
        soundId: LLUUID,
        ownerId: LLUUID,
        gain: Float,
        type: Int = SoundFlags.TYPE_NONE,
        pos: Vector3 = Vector3.ZERO,
    ) {
        triggerSound(SoundData(soundId, ownerId, gain, type, pos))
    }

    fun triggerSound(data: SoundData) {
        if (muted) return
        val source = AudioSource(
            id = LLUUID.generate(),
            ownerId = data.ownerId,
            initialGain = data.gain,
            type = data.type,
            isTrigger = true,
        ).also {
            it.positionGlobal = data.posGlobal
        }
        addAudioSource(source)
        val audioData = getAudioData(data.audioId)
        source.currentData = audioData
        source.channel = getFreeChannel(source.priority)
        source.channel?.setSource(source)
    }

    fun preloadSound(id: LLUUID): Boolean {
        getAudioData(id)
        return true
    }

    // ---- source management ---------------------------------------------------

    fun addAudioSource(source: AudioSource) {
        allSources[source.id] = source
    }

    fun cleanupAudioSource(source: AudioSource) {
        source.stop()
        allSources.remove(source.id)
    }

    fun findAudioSource(sourceId: LLUUID): AudioSource? = allSources[sourceId]

    fun getAudioData(audioId: LLUUID): AudioData =
        allData.getOrPut(audioId) { AudioData(audioId) }

    fun removeAudioData(audioId: LLUUID) {
        allData.remove(audioId)
    }

    // ---- channel/buffer management -------------------------------------------

    fun getFreeBuffer(): AudioBuffer? {
        return buffers.firstOrNull { it != null && !it.inUse }
    }

    fun getFreeChannel(priority: Float): AudioChannel? {
        val free = channels.firstOrNull { it != null && it.currentSource == null }
        if (free != null) return free
        // evict the channel whose source has the lowest priority
        return channels
            .filter { it?.currentSource != null }
            .minByOrNull { it!!.currentSource!!.priority }
            ?.also { it.cleanup() }
    }

    fun cleanupBuffer(buffer: AudioBuffer) {
        buffer.inUse = false
        buffer.audioData = null
    }

    // ---- abstract engine hooks -----------------------------------------------

    protected abstract fun createBuffer(): AudioBuffer
    protected abstract fun createChannel(): AudioChannel

    // ---- doppler / rolloff ---------------------------------------------------

    open fun setDopplerFactor(factor: Float) {}
    open fun getDopplerFactor(): Float = 1f
    open fun setRolloffFactor(factor: Float) {}
    open fun getRolloffFactor(): Float = 1f
}
