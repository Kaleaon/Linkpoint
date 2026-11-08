package com.linkpoint.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Service for handling streaming media (parcel audio, video)
 * Based on the reference viewer's StreamingMediaService
 */
class StreamingMediaService : Service(), AudioManager.OnAudioFocusChangeListener {

    companion object {
        private const val TAG = "StreamingMediaService"
        private const val CHANNEL_ID = "media_playback_channel"
        private const val NOTIFICATION_ID = 1001

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
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
    private var isPausedForTransientLoss = false
    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

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
        stopStream()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        Log.d(TAG, "onAudioFocusChange: focusChange = $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                setVolumeInternal(volume)
                if (isPausedForTransientLoss) {
                    try {
                        mediaPlayer?.start()
                        isPlaying = true
                        Log.i(TAG, "Playback resumed on audio focus gain")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error resuming playback on focus gain", e)
                    }
                    isPausedForTransientLoss = false
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                isPausedForTransientLoss = false
                stopStream()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                isPausedForTransientLoss = true
                try {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        isPlaying = false
                        Log.i(TAG, "Playback paused on transient audio focus loss")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error pausing playback on transient loss", e)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                val duckedVolume = (volume * 0.2f).coerceIn(0f, 1f)
                setVolumeInternal(duckedVolume)
                Log.i(TAG, "Ducked volume to $duckedVolume on transient focus loss can duck")
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        Log.d(TAG, "requesting audio focus")
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()

                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener(this)
                    .build()

                this.audioFocusRequest = focusRequest
                val res = audioManager.requestAudioFocus(focusRequest)
                res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val res = audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
                res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting audio focus", e)
            true
        }
    }

    private fun abandonAudioFocus() {
        Log.d(TAG, "abandoning audio focus")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { request ->
                    audioManager.abandonAudioFocusRequest(request)
                    audioFocusRequest = null
                }
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error abandoning audio focus", e)
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        val stopIntent = Intent(this, StreamingMediaService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Playing parcel audio")
            .setContentText(currentUrl ?: "Streaming media")
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "Stop", pendingStopIntent)
            .setOnlyAlertOnce(true)

        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Media Playback"
            val descriptionText = "Parcel audio stream playback controls"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun playStream(url: String, type: Int = TYPE_AUDIO) {
        if (url == currentUrl && isPlaying) {
            Log.d(TAG, "Already playing: $url")
            return
        }

        Log.i(TAG, "Playing stream: $url")

        if (!requestAudioFocus()) {
            Log.w(TAG, "Audio focus request denied; skipping playback")
            return
        }

        currentUrl = url

        if (type == TYPE_AUDIO) {
            showNotification()
        }

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
                        Log.i(TAG, "Stream started")
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: $what, $extra")
                        this@StreamingMediaService.isPlaying = false
                        this@StreamingMediaService.currentUrl = null
                        stopStream()
                        true
                    }

                    setOnCompletionListener {
                        Log.d(TAG, "Stream completed")
                        this@StreamingMediaService.isPlaying = false
                        stopStream()
                    }

                    prepareAsync()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error playing stream", e)
                stopStream()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "MediaPlayer in invalid state", e)
                stopStream()
            }
        }
    }

    fun stopStream() {
        Log.i(TAG, "Stopping stream")
        releaseMediaPlayer()
        abandonAudioFocus()
        currentUrl = null
        isPlaying = false
        isPausedForTransientLoss = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setVolumeInternal(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting volume on MediaPlayer", e)
        }
    }

    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
        if (!isPausedForTransientLoss) {
            setVolumeInternal(volume)
        }
    }

    fun isPlaying(): Boolean = isPlaying

    fun getCurrentUrl(): String? = currentUrl

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()
                }
            } catch (_: Exception) {
            }
            release()
        }
        mediaPlayer = null
    }
}
