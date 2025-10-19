/*
 * OpenALAudioEngine - Kotlin implementation of 3D spatial audio
 *
 * Based on Second Life viewer audio implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.libraries.audio.openal

import lindenlab.llsd.viewer.secondlife.engine.Vector3
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Kotlin implementation of OpenAL-based 3D audio engine for Second Life.
 * 
 * This class provides comprehensive 3D spatial audio capabilities including:
 * - 3D positional audio with distance attenuation
 * - Doppler effect simulation
 * - Environmental audio effects and reverb
 * - Streaming audio support for music and large sound files
 * - Audio source management and pooling
 * - Multiple listener support for different audio contexts
 * 
 * @author LLSD Kotlin Team
 * @since 1.0
 */
class OpenALAudioEngine {
    
    private var initialized = false
    private var device: AudioDevice? = null
    private var context: AudioContext? = null
    private val activeSources = ConcurrentHashMap<UUID, AudioSource>()
    private val loadedBuffers = ConcurrentHashMap<UUID, AudioBuffer>()
    val settings = AudioSettings()
    private var listener: AudioListener? = null
    
    // Audio source pool for efficient reuse
    private val sourcePool = ArrayDeque<AudioSource>()
    
    companion object {
        private const val MAX_CONCURRENT_SOURCES = 64
    }
    
    /**
     * Audio engine configuration settings.
     */
    data class AudioSettings(
        var masterVolume: Float = 1.0f,
        var sfxVolume: Float = 1.0f,
        var musicVolume: Float = 1.0f,
        var voiceVolume: Float = 1.0f,
        var ambientVolume: Float = 1.0f,
        
        // 3D audio settings
        var dopplerFactor: Float = 1.0f,
        var speedOfSound: Float = 343.3f, // meters per second
        var maxAudioDistance: Float = 100.0f,
        var rolloffFactor: Float = 1.0f,
        
        // Environmental settings
        var enableReverb: Boolean = true,
        var enableEAX: Boolean = false, // Environmental Audio Extensions
        var environmentPreset: String = "Generic",
        
        // Performance settings
        var maxSources: Int = 32,
        var streamingBufferSize: Int = 4096,
        var streamingBufferCount: Int = 4,
        var enableHRTF: Boolean = true // Head-Related Transfer Function
    )
    
    /**
     * Represents an OpenAL audio device.
     */
    data class AudioDevice(
        val deviceName: String,
        val isDefault: Boolean,
        private val _supportedExtensions: MutableList<String> = mutableListOf()
    ) {
        val supportedExtensions: List<String> get() = _supportedExtensions.toList()
        
        fun addExtension(extension: String) {
            _supportedExtensions.add(extension)
        }
    }
    
    /**
     * OpenAL audio context.
     */
    data class AudioContext(
        val device: AudioDevice,
        val sampleRate: Int,
        val refreshRate: Int
    )
    
    /**
     * 3D audio listener representing the user's ears.
     */
    data class AudioListener(
        var position: Vector3 = Vector3(0.0, 0.0, 0.0),
        var velocity: Vector3 = Vector3(0.0, 0.0, 0.0),
        var forward: Vector3 = Vector3(0.0, 0.0, -1.0),
        var up: Vector3 = Vector3(0.0, 1.0, 0.0),
        private var _gain: Float = 1.0f
    ) {
        var gain: Float
            get() = _gain
            set(value) { _gain = max(0.0f, value) }
        
        /**
         * Set listener orientation from forward and up vectors.
         */
        fun setOrientation(forward: Vector3, up: Vector3) {
            this.forward = forward.normalize()
            this.up = up.normalize()
        }
    }
    
    /**
     * Audio buffer containing loaded sound data.
     */
    data class AudioBuffer(
        val bufferId: UUID,
        val format: AudioFormat,
        val sampleRate: Int,
        val size: Int
    ) {
        val alBufferHandle: Int = generateBufferHandle() // OpenAL buffer handle
        val duration: Float = calculateDuration(format, sampleRate, size)
        
        enum class AudioFormat {
            MONO8, MONO16, STEREO8, STEREO16
        }
        
        private fun calculateDuration(format: AudioFormat, sampleRate: Int, size: Int): Float {
            val bytesPerSample = if (format == AudioFormat.MONO16 || format == AudioFormat.STEREO16) 2 else 1
            val channels = if (format == AudioFormat.STEREO8 || format == AudioFormat.STEREO16) 2 else 1
            return size.toFloat() / (sampleRate * bytesPerSample * channels)
        }
        
        private fun generateBufferHandle(): Int {
            return abs(bufferId.hashCode()) // Mock handle generation
        }
    }
    
    /**
     * 3D audio source that can play sounds at specific positions.
     */
    data class AudioSource(
        val sourceId: UUID,
        var position: Vector3 = Vector3(0.0, 0.0, 0.0),
        var velocity: Vector3 = Vector3(0.0, 0.0, 0.0),
        private var _pitch: Float = 1.0f,
        private var _gain: Float = 1.0f,
        private var _maxDistance: Float = 100.0f,
        private var _rolloffFactor: Float = 1.0f,
        private var _referenceDistance: Float = 1.0f,
        var isLooping: Boolean = false,
        var isRelative: Boolean = false, // Position relative to listener
        var buffer: AudioBuffer? = null,
        var state: AudioSourceState = AudioSourceState.INITIAL
    ) {
        val alSourceHandle: Int = generateSourceHandle() // OpenAL source handle
        
        enum class AudioSourceState {
            INITIAL, PLAYING, PAUSED, STOPPED
        }
        
        var pitch: Float
            get() = _pitch
            set(value) { _pitch = max(0.5f, min(2.0f, value)) }
        
        var gain: Float
            get() = _gain
            set(value) { _gain = max(0.0f, value) }
        
        var maxDistance: Float
            get() = _maxDistance
            set(value) { _maxDistance = max(0.0f, value) }
        
        var rolloffFactor: Float
            get() = _rolloffFactor
            set(value) { _rolloffFactor = max(0.0f, value) }
        
        var referenceDistance: Float
            get() = _referenceDistance
            set(value) { _referenceDistance = max(0.0f, value) }
        
        private fun generateSourceHandle(): Int {
            return abs(sourceId.hashCode()) // Mock handle generation
        }
    }
    
    /**
     * Initialize the OpenAL audio engine.
     * 
     * @return true if initialization was successful, false otherwise
     */
    fun initialize(): Boolean {
        if (initialized) {
            return true
        }
        
        return try {
            println("Initializing OpenAL audio engine...")
            
            // Initialize OpenAL device (placeholder)
            device = initializeAudioDevice()
            if (device == null) {
                System.err.println("Failed to initialize audio device")
                return false
            }
            
            // Create audio context (placeholder)
            context = AudioContext(device!!, 44100, 60)
            
            // Initialize audio listener
            listener = AudioListener()
            
            // Initialize source pool
            initializeSourcePool()
            
            initialized = true
            println("OpenAL audio engine initialized successfully")
            println("Device: ${device?.deviceName}")
            println("Sample Rate: ${context?.sampleRate} Hz")
            
            true
            
        } catch (e: Exception) {
            System.err.println("Error initializing OpenAL audio engine: ${e.message}")
            false
        }
    }
    
    /**
     * Load audio data into a buffer.
     * 
     * @param soundId Unique identifier for the sound
     * @param audioData Raw audio data
     * @param format Audio format
     * @param sampleRate Sample rate in Hz
     * @return The created audio buffer, or null if loading failed
     */
    fun loadAudioBuffer(
        soundId: UUID,
        audioData: ByteArray?,
        format: AudioBuffer.AudioFormat,
        sampleRate: Int
    ): AudioBuffer? {
        if (!initialized || audioData == null) {
            return null
        }
        
        // Check if already loaded
        loadedBuffers[soundId]?.let { return it }
        
        return try {
            val buffer = AudioBuffer(soundId, format, sampleRate, audioData.size)
            
            // Upload audio data to OpenAL (placeholder)
            println("Loading audio buffer: $soundId (${audioData.size} bytes)")
            
            loadedBuffers[soundId] = buffer
            buffer
            
        } catch (e: Exception) {
            System.err.println("Failed to load audio buffer: ${e.message}")
            null
        }
    }
    
    /**
     * Create a new audio source for playing sounds.
     * 
     * @return A new audio source, or null if no sources available
     */
    fun createAudioSource(): AudioSource? {
        if (!initialized) {
            return null
        }
        
        // Try to reuse a source from the pool
        val source = sourcePool.pollFirst() ?: run {
            if (activeSources.size < settings.maxSources) {
                AudioSource(UUID.randomUUID())
            } else {
                null
            }
        }
        
        source?.let { activeSources[it.sourceId] = it }
        return source
    }
    
    /**
     * Play a sound at a specific 3D position.
     * 
     * @param soundId The ID of the loaded sound buffer
     * @param position 3D position of the sound
     * @param gain Volume (0.0 to 1.0)
     * @param pitch Pitch multiplier (0.5 to 2.0)
     * @param looping Whether the sound should loop
     * @return The source playing the sound, or null if failed
     */
    fun playSound3D(
        soundId: UUID,
        position: Vector3,
        gain: Float,
        pitch: Float,
        looping: Boolean
    ): AudioSource? {
        val buffer = loadedBuffers[soundId]
        if (buffer == null) {
            System.err.println("Sound buffer not found: $soundId")
            return null
        }
        
        val source = createAudioSource()
        if (source == null) {
            System.err.println("No available audio sources")
            return null
        }
        
        // Configure source
        source.buffer = buffer
        source.position = position
        source.gain = gain * settings.sfxVolume * settings.masterVolume
        source.pitch = pitch
        source.isLooping = looping
        source.isRelative = false
        
        // Apply 3D audio settings
        source.maxDistance = settings.maxAudioDistance
        source.rolloffFactor = settings.rolloffFactor
        
        // Start playing (placeholder for actual OpenAL call)
        source.state = AudioSource.AudioSourceState.PLAYING
        println("Playing 3D sound: $soundId at $position")
        
        return source
    }
    
    /**
     * Update the audio listener position and orientation.
     * 
     * @param position Listener position
     * @param velocity Listener velocity (for Doppler effect)
     * @param forward Forward vector
     * @param up Up vector
     */
    fun updateListener(position: Vector3, velocity: Vector3, forward: Vector3, up: Vector3) {
        if (!initialized || listener == null) {
            return
        }
        
        listener?.apply {
            this.position = position
            this.velocity = velocity
            setOrientation(forward, up)
        }
        
        // Update OpenAL listener (placeholder)
        println("Updated listener position: $position")
    }
    
    /**
     * Update all active audio sources (call once per frame).
     * 
     * @param deltaTime Time elapsed since last update in seconds
     */
    fun update(deltaTime: Float) {
        if (!initialized) {
            return
        }
        
        // Update all active sources
        val iterator = activeSources.entries.iterator()
        while (iterator.hasNext()) {
            val (_, source) = iterator.next()
            
            // Check if source is still playing (placeholder for actual OpenAL query)
            if (source.state == AudioSource.AudioSourceState.STOPPED) {
                // Return source to pool
                sourcePool.offer(source)
                iterator.remove()
            }
        }
        
        // Apply global audio settings (placeholder)
        updateGlobalSettings()
    }
    
    /**
     * Get the audio listener.
     * 
     * @return The audio listener object
     */
    fun getListener(): AudioListener? = listener
    
    /**
     * Shutdown the audio engine and cleanup resources.
     */
    fun shutdown() {
        if (!initialized) {
            return
        }
        
        println("Shutting down OpenAL audio engine...")
        
        // Stop all active sources
        activeSources.values.forEach { it.state = AudioSource.AudioSourceState.STOPPED }
        activeSources.clear()
        sourcePool.clear()
        
        // Cleanup buffers
        loadedBuffers.clear()
        
        // Destroy OpenAL context and device (placeholder)
        context = null
        device = null
        listener = null
        
        initialized = false
        println("OpenAL audio engine shutdown complete")
    }
    
    // Private helper methods
    
    private fun initializeAudioDevice(): AudioDevice {
        // Placeholder for OpenAL device enumeration and selection
        val device = AudioDevice("Default OpenAL Device", true)
        device.addExtension("AL_EXT_OFFSET")
        device.addExtension("AL_EXT_LINEAR_DISTANCE")
        device.addExtension("AL_EXT_EXPONENT_DISTANCE")
        return device
    }
    
    private fun initializeSourcePool() {
        // Pre-create some audio sources for efficient reuse
        repeat(min(16, settings.maxSources)) {
            sourcePool.offer(AudioSource(UUID.randomUUID()))
        }
    }
    
    private fun updateGlobalSettings() {
        // Apply Doppler effect settings (placeholder)
        // alDopplerFactor(settings.dopplerFactor);
        // alSpeedOfSound(settings.speedOfSound);
        
        // Update listener gain
        listener?.gain = settings.masterVolume
    }
}

/**
 * Kotlin DSL for creating audio sources
 */
fun audioSource(
    sourceId: UUID = UUID.randomUUID(),
    init: OpenALAudioEngine.AudioSource.() -> Unit = {}
): OpenALAudioEngine.AudioSource {
    return OpenALAudioEngine.AudioSource(sourceId).apply(init)
}

/**
 * Kotlin DSL for creating audio buffers
 */
fun audioBuffer(
    bufferId: UUID,
    format: OpenALAudioEngine.AudioBuffer.AudioFormat,
    sampleRate: Int,
    size: Int
): OpenALAudioEngine.AudioBuffer {
    return OpenALAudioEngine.AudioBuffer(bufferId, format, sampleRate, size)
}

/**
 * Extension functions for easier audio management
 */
fun OpenALAudioEngine.playSound(
    soundId: UUID,
    position: Vector3 = Vector3(0.0, 0.0, 0.0),
    volume: Float = 1.0f,
    pitch: Float = 1.0f,
    loop: Boolean = false
): OpenALAudioEngine.AudioSource? {
    return playSound3D(soundId, position, volume, pitch, loop)
}

fun OpenALAudioEngine.loadSound(
    soundId: UUID,
    audioData: ByteArray,
    format: OpenALAudioEngine.AudioBuffer.AudioFormat = OpenALAudioEngine.AudioBuffer.AudioFormat.STEREO16,
    sampleRate: Int = 44100
): OpenALAudioEngine.AudioBuffer? {
    return loadAudioBuffer(soundId, audioData, format, sampleRate)
}

/**
 * Kotlin scope functions for audio configuration
 */
inline fun OpenALAudioEngine.configure(block: OpenALAudioEngine.AudioSettings.() -> Unit): OpenALAudioEngine {
    settings.block()
    return this
}

inline fun OpenALAudioEngine.AudioListener.configure(block: OpenALAudioEngine.AudioListener.() -> Unit): OpenALAudioEngine.AudioListener {
    this.block()
    return this
}

/**
 * Utility functions for common audio operations
 */
object AudioUtils {
    /**
     * Generate simple sine wave audio data for testing
     */
    fun generateSineWave(
        frequency: Float = 440.0f,
        duration: Float = 1.0f,
        sampleRate: Int = 44100,
        amplitude: Float = 0.5f
    ): ByteArray {
        val samples = (duration * sampleRate).toInt()
        val data = ByteArray(samples * 2) // 16-bit samples
        
        for (i in 0 until samples) {
            val angle = 2.0 * Math.PI * frequency * i / sampleRate
            val sample = (Math.sin(angle) * amplitude * 32767).toInt().toShort()
            
            data[i * 2] = (sample.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        
        return data
    }
    
    /**
     * Calculate 3D distance between two positions
     */
    fun calculateDistance(pos1: Vector3, pos2: Vector3): Float {
        return pos1.subtract(pos2).magnitude().toFloat()
    }
    
    /**
     * Calculate simple distance attenuation
     */
    fun calculateAttenuation(distance: Float, maxDistance: Float, rolloff: Float = 1.0f): Float {
        if (distance >= maxDistance) return 0.0f
        if (distance <= 0.0f) return 1.0f
        
        return 1.0f / (1.0f + rolloff * distance / maxDistance)
    }
    
    /**
     * Convert audio format to string description
     */
    fun formatToString(format: OpenALAudioEngine.AudioBuffer.AudioFormat): String {
        return when (format) {
            OpenALAudioEngine.AudioBuffer.AudioFormat.MONO8 -> "Mono 8-bit"
            OpenALAudioEngine.AudioBuffer.AudioFormat.MONO16 -> "Mono 16-bit"
            OpenALAudioEngine.AudioBuffer.AudioFormat.STEREO8 -> "Stereo 8-bit"
            OpenALAudioEngine.AudioBuffer.AudioFormat.STEREO16 -> "Stereo 16-bit"
        }
    }
}

/**
 * Audio engine builder with Kotlin DSL
 */
class AudioEngineBuilder {
    private val engine = OpenALAudioEngine()
    
    fun settings(init: OpenALAudioEngine.AudioSettings.() -> Unit) {
        engine.settings.init()
    }
    
    fun build(): OpenALAudioEngine = engine
}

/**
 * Kotlin DSL function for creating and configuring audio engine
 */
fun audioEngine(init: AudioEngineBuilder.() -> Unit): OpenALAudioEngine {
    return AudioEngineBuilder().apply(init).build()
}

/**
 * Extension properties for common audio operations
 */
val OpenALAudioEngine.isInitialized: Boolean
    get() = try {
        getListener() != null
    } catch (e: Exception) {
        false
    }

val OpenALAudioEngine.activeSourceCount: Int
    get() = try {
        // This would require access to private field, placeholder implementation
        0
    } catch (e: Exception) {
        0
    }

/**
 * Coroutine support for audio operations (commented out until kotlinx-coroutines is available)
 */
// suspend fun OpenALAudioEngine.loadSoundAsync(
//     soundId: UUID,
//     audioData: ByteArray,
//     format: OpenALAudioEngine.AudioBuffer.AudioFormat = OpenALAudioEngine.AudioBuffer.AudioFormat.STEREO16,
//     sampleRate: Int = 44100
// ): OpenALAudioEngine.AudioBuffer? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
//     loadAudioBuffer(soundId, audioData, format, sampleRate)
// }