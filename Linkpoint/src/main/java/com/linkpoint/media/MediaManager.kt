package com.linkpoint.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.world.ParcelInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * MediaManager - Handles parcel media and music streaming.
 * 
 * Features:
 * - Parcel music streaming (Shoutcast/Icecast)
 * - Parcel media (video/web)
 * - Media on a prim (MOAP)
 * - Volume control per media type
 * - Auto-play based on parcel settings
 * 
 * Based on Lumiya's media implementation.
 */
class MediaManager(
    private val context: Context,
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "MediaManager"
        
        // Media types
        const val MEDIA_TYPE_NONE = 0
        const val MEDIA_TYPE_MUSIC = 1
        const val MEDIA_TYPE_MEDIA = 2
        const val MEDIA_TYPE_MOAP = 3
        
        // Default volumes
        private const val DEFAULT_MUSIC_VOLUME = 0.5f
        private const val DEFAULT_MEDIA_VOLUME = 0.5f
    }
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Music player for parcel streams
    private var musicPlayer: MediaPlayer? = null
    private var currentMusicUrl: String? = null
    
    // Media state
    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying
    
    private val _musicUrl = MutableStateFlow<String?>(null)
    val musicUrl: StateFlow<String?> = _musicUrl
    
    private val _mediaUrl = MutableStateFlow<String?>(null)
    val mediaUrl: StateFlow<String?> = _mediaUrl
    
    private val _musicVolume = MutableStateFlow(DEFAULT_MUSIC_VOLUME)
    val musicVolume: StateFlow<Float> = _musicVolume
    
    private val _mediaVolume = MutableStateFlow(DEFAULT_MEDIA_VOLUME)
    val mediaVolume: StateFlow<Float> = _mediaVolume
    
    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled
    
    private val _isMediaEnabled = MutableStateFlow(true)
    val isMediaEnabled: StateFlow<Boolean> = _isMediaEnabled
    
    // MOAP (Media on a Prim) state
    private val moapSurfaces = ConcurrentHashMap<UUID, MoapSurface>()
    
    // Stream metadata
    private val _streamTitle = MutableStateFlow<String?>(null)
    val streamTitle: StateFlow<String?> = _streamTitle
    
    private val _streamArtist = MutableStateFlow<String?>(null)
    val streamArtist: StateFlow<String?> = _streamArtist
    
    /**
     * Update parcel media/music from parcel info.
     */
    fun updateFromParcel(parcel: ParcelInfo) {
        scope.launch {
            // Handle music URL change
            val newMusicUrl = parcel.musicUrl
            if (newMusicUrl != currentMusicUrl) {
                if (_isMusicEnabled.value && !newMusicUrl.isNullOrBlank()) {
                    playMusic(newMusicUrl)
                } else {
                    stopMusic()
                }
            }
            
            // Handle media URL
            _mediaUrl.value = parcel.mediaUrl
        }
    }
    
    /**
     * Play music stream.
     */
    fun playMusic(url: String) {
        if (!_isMusicEnabled.value) return
        
        scope.launch {
            try {
                // Stop existing music
                stopMusicInternal()
                
                currentMusicUrl = url
                _musicUrl.value = url
                
                musicPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    
                    setOnPreparedListener { player ->
                        player.setVolume(_musicVolume.value, _musicVolume.value)
                        player.start()
                        _isMusicPlaying.value = true
                        Log.i(TAG, "Music streaming started: $url")
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "Music player error: what=$what, extra=$extra")
                        _isMusicPlaying.value = false
                        true
                    }
                    
                    setOnInfoListener { _, what, _ ->
                        // Could extract stream metadata here
                        when (what) {
                            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                                Log.d(TAG, "Music buffering started")
                            }
                            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                                Log.d(TAG, "Music buffering ended")
                            }
                        }
                        false
                    }
                    
                    setOnCompletionListener {
                        // Stream ended or disconnected
                        _isMusicPlaying.value = false
                    }
                    
                    setDataSource(context, Uri.parse(url))
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play music: $url", e)
                _isMusicPlaying.value = false
            }
        }
    }
    
    /**
     * Stop music playback.
     */
    fun stopMusic() {
        scope.launch {
            stopMusicInternal()
        }
    }
    
    private fun stopMusicInternal() {
        try {
            musicPlayer?.apply {
                try {
                    if (isPlaying) {
                        stop()
                    }
                } catch (e: IllegalStateException) {
                    // Player already stopped, ignore
                }
                try {
                    reset()
                } catch (e: IllegalStateException) {
                    // Player in error state, just release
                }
                release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping music player", e)
        }
        musicPlayer = null
        currentMusicUrl = null
        _isMusicPlaying.value = false
        _musicUrl.value = null
        _streamTitle.value = null
        _streamArtist.value = null
    }
    
    /**
     * Set music volume (0.0 - 1.0).
     */
    fun setMusicVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        _musicVolume.value = clampedVolume
        musicPlayer?.setVolume(clampedVolume, clampedVolume)
    }
    
    /**
     * Set media volume (0.0 - 1.0).
     */
    fun setMediaVolume(volume: Float) {
        _mediaVolume.value = volume.coerceIn(0f, 1f)
    }
    
    /**
     * Enable/disable music streaming.
     */
    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        if (!enabled) {
            stopMusic()
        }
    }
    
    /**
     * Enable/disable media playback.
     */
    fun setMediaEnabled(enabled: Boolean) {
        _isMediaEnabled.value = enabled
    }
    
    /**
     * Toggle music playback.
     */
    fun toggleMusic() {
        if (_isMusicPlaying.value) {
            stopMusic()
        } else {
            currentMusicUrl?.let { playMusic(it) }
        }
    }
    
    // ==================== MOAP (Media on a Prim) ====================
    
    /**
     * Set media on a prim face.
     */
    fun setMoapMedia(objectId: UUID, face: Int, url: String, autoPlay: Boolean = false) {
        val surface = MoapSurface(
            objectId = objectId,
            face = face,
            url = url,
            autoPlay = autoPlay,
            isPlaying = autoPlay
        )
        moapSurfaces[objectId] = surface
        
        // Would send ObjectMedia message to sim
        Log.d(TAG, "Set MOAP on object $objectId face $face: $url")
    }
    
    /**
     * Get MOAP surface for object.
     */
    fun getMoapSurface(objectId: UUID): MoapSurface? = moapSurfaces[objectId]
    
    /**
     * Remove MOAP from object.
     */
    fun removeMoapMedia(objectId: UUID) {
        moapSurfaces.remove(objectId)
    }
    
    /**
     * Handle ObjectMedia message from sim.
     */
    fun handleObjectMedia(objectId: UUID, mediaList: List<ObjectMediaEntry>) {
        for (entry in mediaList) {
            if (!entry.url.isNullOrBlank()) {
                moapSurfaces[objectId] = MoapSurface(
                    objectId = objectId,
                    face = entry.face,
                    url = entry.url,
                    autoPlay = entry.autoPlay,
                    autoLoop = entry.autoLoop,
                    autoScale = entry.autoScale,
                    autoZoom = entry.autoZoom,
                    width = entry.width,
                    height = entry.height
                )
            }
        }
    }
    
    fun shutdown() {
        stopMusicInternal()
        scope.cancel()
    }
}

/**
 * Represents media on a prim surface.
 */
data class MoapSurface(
    val objectId: UUID,
    val face: Int,
    val url: String,
    val autoPlay: Boolean = false,
    val autoLoop: Boolean = false,
    val autoScale: Boolean = true,
    val autoZoom: Boolean = false,
    val width: Int = 0,
    val height: Int = 0,
    val isPlaying: Boolean = false
)

/**
 * Media entry from ObjectMedia message.
 */
data class ObjectMediaEntry(
    val face: Int,
    val url: String?,
    val autoPlay: Boolean = false,
    val autoLoop: Boolean = false,
    val autoScale: Boolean = true,
    val autoZoom: Boolean = false,
    val width: Int = 0,
    val height: Int = 0
)
