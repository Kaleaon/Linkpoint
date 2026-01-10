package com.linkpoint.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Service for handling streaming media (parcel audio, video)
 * Based on Lumiya's StreamingMediaService
 */
class StreamingMediaService : Service() {
    
    companion object {
        private const val TAG = "StreamingMediaService"
        
        const val ACTION_PLAY = "com.linkpoint.action.PLAY"
        const val ACTION_STOP = "com.linkpoint.action.STOP"
        const val EXTRA_URL = "url"
        const val EXTRA_TYPE = "type"
        
        const val TYPE_AUDIO = 0
        const val TYPE_VIDEO = 1
        
        fun playAudio(context: Context, url: String) {
            val intent = Intent(context, StreamingMediaService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TYPE, TYPE_AUDIO)
            }
            context.startService(intent)
        }
        
        fun stopAudio(context: Context) {
            val intent = Intent(context, StreamingMediaService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
    
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var currentUrl: String? = null
    private var isPlaying = false
    private var volume = 0.5f
    
    inner class LocalBinder : Binder() {
        fun getService(): StreamingMediaService = this@StreamingMediaService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_URL)
                val type = intent.getIntExtra(EXTRA_TYPE, TYPE_AUDIO)
                if (url != null) {
                    playStream(url, type)
                }
            }
            ACTION_STOP -> {
                stopStream()
            }
        }
        
        return START_NOT_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        releaseMediaPlayer()
    }
    
    fun playStream(url: String, type: Int = TYPE_AUDIO) {
        if (url == currentUrl && isPlaying) {
            Log.d(TAG, "Already playing: $url")
            return
        }
        
        Log.i(TAG, "Playing stream: $url")
        
        serviceScope.launch {
            try {
                releaseMediaPlayer()
                
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    
                    setDataSource(url)
                    
                    setOnPreparedListener {
                        it.setVolume(volume, volume)
                        it.start()
                        this@StreamingMediaService.isPlaying = true
                        this@StreamingMediaService.currentUrl = url
                        Log.i(TAG, "Stream started")
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: $what, $extra")
                        this@StreamingMediaService.isPlaying = false
                        this@StreamingMediaService.currentUrl = null
                        true
                    }
                    
                    setOnCompletionListener {
                        Log.d(TAG, "Stream completed")
                        this@StreamingMediaService.isPlaying = false
                    }
                    
                    prepareAsync()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error playing stream", e)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "MediaPlayer in invalid state", e)
            }
        }
    }
    
    fun stopStream() {
        Log.i(TAG, "Stopping stream")
        releaseMediaPlayer()
        currentUrl = null
        isPlaying = false
    }
    
    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(volume, volume)
    }
    
    fun isPlaying(): Boolean = isPlaying
    
    fun getCurrentUrl(): String? = currentUrl
    
    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}
