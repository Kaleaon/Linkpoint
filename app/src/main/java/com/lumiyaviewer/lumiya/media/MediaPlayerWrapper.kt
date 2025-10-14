package com.lumiyaviewer.lumiya.media

import android.media.AudioManager
import android.media.MediaPlayer
import com.lumiyaviewer.lumiya.Debug

/**
 * Modern Kotlin MediaPlayerWrapper
 * Manages media playback in a background thread with proper lifecycle management
 */
class MediaPlayerWrapper :
    Runnable,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnInfoListener,
    MediaPlayer.OnPreparedListener {
    private var mediaPlayer: MediaPlayer? = null

    @Volatile private var mediaURL: String = ""

    @Volatile private var mustExit: Boolean = false

    @Volatile private var mustPlay: Boolean = false
    private var workingThread: Thread? = null

    /**
     * Called when media player encounters an error
     */
    override fun onError(
        mp: MediaPlayer?,
        what: Int,
        extra: Int,
    ): Boolean {
        Debug.Log("MediaPlayerWrapper: onError: what = $what, extra = $extra")
        return false
    }

    /**
     * Called when media player has info to report
     */
    override fun onInfo(
        mp: MediaPlayer?,
        what: Int,
        extra: Int,
    ): Boolean {
        Debug.Log("MediaPlayerWrapper: onInfo: what = $what, extra = $extra")
        return false
    }

    /**
     * Called when media player is prepared and ready to play
     */
    override fun onPrepared(mp: MediaPlayer?) {
        Debug.Log("MediaPlayerWrapper: prepared, starting playback")
        mp?.start()
    }

    /**
     * Starts playing media from the given URL
     */
    @Synchronized
    fun play(url: String) {
        if (mustExit) return

        mustPlay = true
        mustExit = false

        // Normalize URL
        mediaURL =
            url.trim().let { trimmed ->
                when {
                    trimmed.lowercase().startsWith("http://") ->
                        "http://" + trimmed.substring(7)
                    trimmed.lowercase().startsWith("https://") ->
                        "https://" + trimmed.substring(8)
                    else -> trimmed
                }
            }

        // Start working thread if not already running
        if (workingThread == null) {
            workingThread = Thread(this).apply { start() }
        }

        (this as Object).notify()
    }

    /**
     * Stops playback
     */
    @Synchronized
    fun stop() {
        if (mustExit) return

        mustPlay = false
        mediaURL = ""

        (this as Object).notify()
    }

    /**
     * Releases all resources
     */
    @Synchronized
    fun release() {
        mustPlay = false
        mustExit = true
        mediaURL = ""
        workingThread = null

        (this as Object).notify()
    }

    /**
     * Working thread that manages media playback
     */
    override fun run() {
        Debug.Log("MediaPlayerWrapper: working thread started")

        while (!mustExit) {
            if (mustPlay) {
                Debug.Log("MediaPlayerWrapper: working thread must play, URL = $mediaURL")

                // Release existing player
                mediaPlayer?.release()
                mediaPlayer = null

                // Create new player
                mediaPlayer =
                    MediaPlayer().apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setOnErrorListener(this@MediaPlayerWrapper)
                        setOnInfoListener(this@MediaPlayerWrapper)
                        setOnPreparedListener(this@MediaPlayerWrapper)

                        try {
                            setDataSource(mediaURL)
                        } catch (e: Exception) {
                            Debug.Log("MediaPlayerWrapper: Failed to set data source to $mediaURL")
                            e.printStackTrace()
                            mustPlay = false
                            return@apply
                        }

                        try {
                            prepareAsync()
                        } catch (e: Exception) {
                            Debug.Log("MediaPlayerWrapper: PrepareAsync exception while playing $mediaURL")
                            e.printStackTrace()
                            mustPlay = false
                        }
                    }
            } else {
                Debug.Log("MediaPlayerWrapper: working thread must stop playing")
                mediaPlayer?.release()
                mediaPlayer = null
            }

            Debug.Log("MediaPlayerWrapper: working thread waiting")
            synchronized(this) {
                try {
                    (this as Object).wait()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Debug.Log("MediaPlayerWrapper: working thread wake up")
        }

        Debug.Log("MediaPlayerWrapper: working thread exiting")
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
