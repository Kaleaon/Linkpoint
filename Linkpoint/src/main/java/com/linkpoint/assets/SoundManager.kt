package com.linkpoint.assets

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Manages sound assets and playback
 * Handles SL sound effects with spatial audio
 */
class SoundManager(
    private val context: Context,
    private val cache: AssetCache
) {
    companion object {
        private const val TAG = "SoundManager"
        private const val MAX_STREAMS = 20
    }
    
    private val soundPool: SoundPool
    private val loadedSounds = ConcurrentHashMap<UUID, Int>() // UUID -> SoundPool ID
    private val playingSounds = ConcurrentHashMap<Int, SoundPlayback>() // Stream ID -> Playback info
    
    private val soundDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "SoundThread").apply { isDaemon = true }
    }.asCoroutineDispatcher()
    private val scope = CoroutineScope(soundDispatcher + SupervisorJob())
    
    // Listener position for spatial audio
    private var listenerPosition = LLVector3(128f, 128f, 30f)
    private var listenerForward = LLVector3(1f, 0f, 0f)
    
    // Volume settings
    private var masterVolume = 1.0f
    private var soundEffectsVolume = 0.5f
    private var ambientVolume = 0.5f
    
    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(attributes)
            .build()
        
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status != 0) {
                Log.w(TAG, "[${Thread.currentThread().name}] Sound load failed: $sampleId")
            }
        }
    }
    
    /**
     * Update listener position for spatial audio
     */
    fun updateListener(position: LLVector3, forward: LLVector3) {
        listenerPosition = position
        listenerForward = forward.normalize()
        
        // Update playing sounds
        playingSounds.values.forEach { playback ->
            updateSpatialSound(playback)
        }
    }
    
    /**
     * Play a sound at a position
     */
    suspend fun playSound(
        soundId: UUID,
        position: LLVector3,
        gain: Float = 1.0f,
        loop: Boolean = false
    ): Int? {
        return withContext(soundDispatcher) {
            val soundPoolId = loadSound(soundId) ?: return@withContext null

            // Calculate spatial audio
            val distance = position.distance(listenerPosition)
            val maxDistance = 30f // SL uses ~30m for sound falloff
            val volume = calculateVolume(distance, maxDistance, gain)
            val pan = calculatePan(position)

            val streamId = soundPool.play(
                soundPoolId,
                volume * soundEffectsVolume * masterVolume,
                volume * soundEffectsVolume * masterVolume,
                1,
                if (loop) -1 else 0,
                1.0f
            )

            if (streamId != 0) {
                Log.d(TAG, "[${Thread.currentThread().name}] Started sound $soundId")
                soundPlayCount.incrementAndGet()
                playingSounds[streamId] = SoundPlayback(
                    streamId = streamId,
                    soundId = soundId,
                    position = position,
                    gain = gain,
                    loop = loop
                )
            }

            streamId
        }
    }
    
    /**
     * Play a UI sound (no spatial)
     */
    suspend fun playUISound(soundId: UUID, volume: Float = 1.0f): Int? {
        return withContext(soundDispatcher) {
            val soundPoolId = loadSound(soundId) ?: return@withContext null

            val streamId = soundPool.play(
                soundPoolId,
                volume * masterVolume,
                volume * masterVolume,
                1,
                0,
                1.0f
            )
            if (streamId != 0) {
                Log.d(TAG, "[${Thread.currentThread().name}] Started UI sound $soundId")
            }
            streamId
        }
    }
    
    /**
     * Stop a playing sound
     */
    fun stopSound(streamId: Int) {
        soundPool.stop(streamId)
        playingSounds.remove(streamId)
    }
    
    /**
     * Stop all sounds
     */
    fun stopAllSounds() {
        playingSounds.keys.forEach { soundPool.stop(it) }
        playingSounds.clear()
    }
    
    /**
     * Load a sound into the pool
     */
    private suspend fun loadSound(soundId: UUID): Int? {
        loadedSounds[soundId]?.let { return it }
        
        soundLoadAttempts.incrementAndGet()

        return withContext(soundDispatcher) {
            val data = cache.get(soundId, AssetType.SOUND)
            if (data == null) {
                lastError = "Sound not in cache: $soundId"
                lastErrorTime = System.currentTimeMillis()
                soundLoadFailures.incrementAndGet()
                return@withContext null
            }

            // Write to temp file (SoundPool requires file)
            val tempFile = File(context.cacheDir, "sound_$soundId.ogg")
            try {
                FileOutputStream(tempFile).use { it.write(data) }

                val poolId = soundPool.load(tempFile.absolutePath, 1)
                loadedSounds[soundId] = poolId

                // Wait for load
                delay(100)

                Log.d(TAG, "[${Thread.currentThread().name}] Loaded sound $soundId")
                poolId
            } catch (e: Exception) {
                Log.e(TAG, "[${Thread.currentThread().name}] Failed to load sound: $soundId", e)
                lastError = "${e.javaClass.simpleName}: ${e.message}"
                lastErrorTime = System.currentTimeMillis()
                soundLoadFailures.incrementAndGet()
                null
            } finally {
                tempFile.delete()
            }
        }
    }
    
    /**
     * Play an attached sound from UDP message.
     * Attached sounds are played relative to an object in the world.
     */
    fun playAttachedSound(soundId: UUID, objectId: UUID, ownerId: UUID, gain: Float) {
        scope.launch {
            // For now, play at listener position - in full implementation,
            // would look up object position from ObjectManager
            playSound(soundId, listenerPosition, gain, loop = false)
            Log.d(TAG, "[${Thread.currentThread().name}] Playing attached sound $soundId on object $objectId")
        }
    }
    
    /**
     * Preload a sound for later playback.
     */
    fun preloadSound(soundId: UUID) {
        scope.launch {
            loadSound(soundId)
            Log.d(TAG, "[${Thread.currentThread().name}] Preloaded sound $soundId")
        }
    }
    
    private fun calculateVolume(distance: Float, maxDistance: Float, gain: Float): Float {
        if (distance >= maxDistance) return 0f
        val attenuation = 1f - (distance / maxDistance)
        return (attenuation * attenuation * gain).coerceIn(0f, 1f)
    }
    
    private fun calculatePan(position: LLVector3): Float {
        val toSound = (position - listenerPosition).normalize()
        val right = LLVector3(-listenerForward.y, listenerForward.x, 0f).normalize()
        return toSound.dot(right).coerceIn(-1f, 1f)
    }
    
    private fun updateSpatialSound(playback: SoundPlayback) {
        val distance = playback.position.distance(listenerPosition)
        val volume = calculateVolume(distance, 30f, playback.gain)
        val pan = calculatePan(playback.position)
        
        val leftVol = volume * (1f - pan.coerceIn(0f, 1f))
        val rightVol = volume * (1f + pan.coerceIn(-1f, 0f))
        
        soundPool.setVolume(playback.streamId, 
            leftVol * soundEffectsVolume * masterVolume,
            rightVol * soundEffectsVolume * masterVolume
        )
    }
    
    /**
     * Set volume levels
     */
    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }
    
    fun setSoundEffectsVolume(volume: Float) {
        soundEffectsVolume = volume.coerceIn(0f, 1f)
    }
    
    fun setAmbientVolume(volume: Float) {
        ambientVolume = volume.coerceIn(0f, 1f)
    }
    
    fun shutdown() {
        scope.cancel()
        soundDispatcher.close()
        soundPool.release()
        loadedSounds.clear()
        playingSounds.clear()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking for diagnostics (volatile for thread safety)
    private var soundLoadAttempts = java.util.concurrent.atomic.AtomicInteger(0)
    private var soundLoadFailures = java.util.concurrent.atomic.AtomicInteger(0)
    private var soundPlayCount = java.util.concurrent.atomic.AtomicInteger(0)
    @Volatile private var lastError: String? = null
    @Volatile private var lastErrorTime: Long = 0
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): SoundManagerDiagnostics {
        return SoundManagerDiagnostics(
            loadedSoundCount = loadedSounds.size,
            playingSoundCount = playingSounds.size,
            maxStreams = MAX_STREAMS,
            masterVolume = masterVolume,
            effectsVolume = soundEffectsVolume,
            ambientVolume = ambientVolume,
            loadAttempts = soundLoadAttempts.get(),
            loadFailures = soundLoadFailures.get(),
            totalPlays = soundPlayCount.get(),
            listenerPosition = "${listenerPosition.x}, ${listenerPosition.y}, ${listenerPosition.z}",
            lastError = lastError,
            lastErrorTimeAgo = if (lastErrorTime > 0) System.currentTimeMillis() - lastErrorTime else null
        )
    }
    
    /**
     * Diagnostic data class for sound manager state
     */
    data class SoundManagerDiagnostics(
        val loadedSoundCount: Int,
        val playingSoundCount: Int,
        val maxStreams: Int,
        val masterVolume: Float,
        val effectsVolume: Float,
        val ambientVolume: Float,
        val loadAttempts: Int,
        val loadFailures: Int,
        val totalPlays: Int,
        val listenerPosition: String,
        val lastError: String?,
        val lastErrorTimeAgo: Long?
    )
}

data class SoundPlayback(
    val streamId: Int,
    val soundId: UUID,
    val position: LLVector3,
    val gain: Float,
    val loop: Boolean
)
