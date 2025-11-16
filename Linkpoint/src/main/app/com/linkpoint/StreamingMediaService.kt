package com.linkpoint

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * Handles parcelable audio stream playback for in-world media parcels or
 * voice streams. The implementation focuses on correctness and resource
 * hygiene so it can be used safely from the UI, tests, or background jobs.
 */
class StreamingMediaService : Service(), AudioManager.OnAudioFocusChangeListener {

    private val binder = MediaBinder()
    private lateinit var audioManager: AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: Uri? = null

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AudioManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> intent.uriExtra()?.let { playStream(it) }
            ACTION_STOP -> stopPlayback()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        stopPlayback()
        abandonFocus()
        super.onDestroy()
    }

    private fun playStream(uri: Uri) {
        if (uri == currentUri && mediaPlayer?.isPlaying == true) {
            return
        }
        currentUri = uri

        if (!requestFocus()) {
            Log.w(TAG, "Unable to obtain audio focus")
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(applicationContext, uri)
            setOnPreparedListener { start() }
            setOnCompletionListener { stopPlayback() }
            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error what=$what extra=$extra")
                stopPlayback()
                true
            }
            prepareAsync()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.run {
            setOnPreparedListener(null)
            setOnCompletionListener(null)
            stop()
            release()
        }
        mediaPlayer = null
        currentUri = null
        abandonFocus()
        stopSelf()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> stopPlayback()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                mediaPlayer?.setVolume(DUCK_VOLUME, DUCK_VOLUME)
            AudioManager.AUDIOFOCUS_GAIN ->
                mediaPlayer?.setVolume(NORMAL_VOLUME, NORMAL_VOLUME)
        }
    }

    private fun requestFocus(): Boolean {
        if (::audioManager.isInitialized.not()) {
            audioManager = getSystemService(AudioManager::class.java)
        }
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        val result = audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        if (result) {
            focusRequest = request
        }
        return result
    }

    private fun abandonFocus() {
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        focusRequest = null
    }

    inner class MediaBinder : Binder() {
        fun play(uri: Uri) = playStream(uri)
        fun stop() = stopPlayback()
        fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
        fun currentStream(): Uri? = currentUri
    }

    private fun Intent.uriExtra(): Uri? =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(EXTRA_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(EXTRA_URI)
        }

    companion object {
        private const val TAG = "StreamingMediaService"
        private const val ACTION_PLAY = "com.linkpoint.action.PLAY_STREAM"
        private const val ACTION_STOP = "com.linkpoint.action.STOP_STREAM"
        private const val EXTRA_URI = "extra_stream_uri"
        private const val DUCK_VOLUME = 0.2f
        private const val NORMAL_VOLUME = 1f

        fun play(context: Context, uri: Uri) {
            val intent = Intent(context, StreamingMediaService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_URI, uri)
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, StreamingMediaService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
