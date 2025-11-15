package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.provider.Settings

/**
 * Observes changes to audio stream volume levels.
 * Monitors system volume changes and notifies listeners when audio stream volumes change.
 */
class AudioStreamVolumeObserver {
    private var mAudioStreamVolumeContentObserver: AudioStreamVolumeContentObserver? = null
    private val mContext: Context

    constructor(context: Context) {
        this.mContext = context
    }

    fun start(streamTypes: IntArray, listener: OnAudioStreamVolumeChangedListener) {
        stop()
        mAudioStreamVolumeContentObserver = AudioStreamVolumeContentObserver(
            Handler(),
            mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
            streamTypes,
            listener
        )
        mContext.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            mAudioStreamVolumeContentObserver!!
        )
    }

    fun stop() {
        mAudioStreamVolumeContentObserver?.let {
            mContext.contentResolver.unregisterContentObserver(it)
            mAudioStreamVolumeContentObserver = null
        }
    }

    private class AudioStreamVolumeContentObserver(
        handler: Handler,
        private val mAudioManager: AudioManager,
        private val mAudioStreamTypes: IntArray,
        private val mListener: OnAudioStreamVolumeChangedListener
    ) : ContentObserver(handler) {
        
        private val mLastVolumes: IntArray = IntArray(mAudioStreamTypes.size) { i ->
            mAudioManager.getStreamVolume(mAudioStreamTypes[i])
        }

        override fun onChange(selfChange: Boolean) {
            for (i in mAudioStreamTypes.indices) {
                val currentVolume = mAudioManager.getStreamVolume(mAudioStreamTypes[i])
                if (currentVolume != mLastVolumes[i]) {
                    mLastVolumes[i] = currentVolume
                    mListener.onAudioStreamVolumeChanged(mAudioStreamTypes[i], currentVolume)
                }
            }
        }
    }

    interface OnAudioStreamVolumeChangedListener {
        fun onAudioStreamVolumeChanged(streamType: Int, volume: Int)
    }
}