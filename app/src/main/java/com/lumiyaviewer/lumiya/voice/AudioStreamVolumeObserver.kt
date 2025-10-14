/*
import java.util.*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.database.ContentObserver
 *  android.media.AudioManager
 *  android.os.Handler
 *  android.provider.Settings$System
 */
package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.provider.Settings
import javax.annotation.Nonnull

class AudioStreamVolumeObserver {
    private AudioStreamVolumeContentObserver mAudioStreamVolumeContentObserver
    private Context mContext

    AudioStreamVolumeObserver(@Nonnull Context context) {
        this.mContext = context
    }

    Unit start(Int[] nArray, @Nonnull OnAudioStreamVolumeChangedListener onAudioStreamVolumeChangedListener) {
        this.stop()
        this.mAudioStreamVolumeContentObserver = AudioStreamVolumeContentObserver(Handler(), (AudioManager)this.mContext.getSystemService("audio"), nArray, onAudioStreamVolumeChangedListener)
        this.mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, (ContentObserver)this.mAudioStreamVolumeContentObserver)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Unit stop() {
        if (this.mAudioStreamVolumeContentObserver == null) {
            return
        }
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this.mAudioStreamVolumeContentObserver)
        this.mAudioStreamVolumeContentObserver = null
    }

    private class AudioStreamVolumeContentObserver
    : ContentObserver {
        private AudioManager mAudioManager
        private Int[] mAudioStreamTypes
        private Int[] mLastVolumes
        private OnAudioStreamVolumeChangedListener mListener

        AudioStreamVolumeContentObserver(@Nonnull Handler handler, @Nonnull AudioManager audioManager, Int[] nArray, @Nonnull OnAudioStreamVolumeChangedListener onAudioStreamVolumeChangedListener) {
            super(handler)
            this.mAudioManager = audioManager
            this.mAudioStreamTypes = nArray
            this.mListener = onAudioStreamVolumeChangedListener
            this.mLastVolumes = Int[this.mAudioStreamTypes.length]
            for (Int i = 0; i < this.mAudioStreamTypes.length; ++i) {
                this.mLastVolumes[i] = this.mAudioManager.getStreamVolume(this.mAudioStreamTypes[i])
            }
        }

        Unit onChange(Boolean bl) {
            for (Int i = 0; i < this.mAudioStreamTypes.length; ++i) {
                Int n = this.mAudioManager.getStreamVolume(this.mAudioStreamTypes[i])
                if (n == this.mLastVolumes[i]) continue
                this.mLastVolumes[i] = n
                this.mListener.onAudioStreamVolumeChanged(this.mAudioStreamTypes[i], n)
            }
        }
    }

    interface OnAudioStreamVolumeChangedListener {
        Unit onAudioStreamVolumeChanged(Int var1, Int var2)
    }
}

