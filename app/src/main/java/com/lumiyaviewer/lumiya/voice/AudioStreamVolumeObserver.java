/*
import java.util.*;
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.database.ContentObserver
 *  android.media.AudioManager
 *  android.os.Handler
 *  android.provider.Settings$System
 */
package com.lumiyaviewer.lumiya.voice;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import javax.annotation.Nonnull;

public class AudioStreamVolumeObserver {
    private AudioStreamVolumeContentObserver mAudioStreamVolumeContentObserver;
    private final Context mContext;

    public AudioStreamVolumeObserver(@Nonnull Context context) {
        this.mContext = context;
    }

    public void start(int[] nArray, @Nonnull OnAudioStreamVolumeChangedListener onAudioStreamVolumeChangedListener) {
        this.stop();
        this.mAudioStreamVolumeContentObserver = new AudioStreamVolumeContentObserver(new Handler(), (AudioManager)this.mContext.getSystemService("audio"), nArray, onAudioStreamVolumeChangedListener);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, (ContentObserver)this.mAudioStreamVolumeContentObserver);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void stop() {
        if (this.mAudioStreamVolumeContentObserver == null) {
            return;
        }
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this.mAudioStreamVolumeContentObserver);
        this.mAudioStreamVolumeContentObserver = null;
    }

    private static class AudioStreamVolumeContentObserver
    extends ContentObserver {
        private final AudioManager mAudioManager;
        private final int[] mAudioStreamTypes;
        private final int[] mLastVolumes;
        private final OnAudioStreamVolumeChangedListener mListener;

        AudioStreamVolumeContentObserver(@Nonnull Handler handler, @Nonnull AudioManager audioManager, int[] nArray, @Nonnull OnAudioStreamVolumeChangedListener onAudioStreamVolumeChangedListener) {
            super(handler);
            this.mAudioManager = audioManager;
            this.mAudioStreamTypes = nArray;
            this.mListener = onAudioStreamVolumeChangedListener;
            this.mLastVolumes = new int[this.mAudioStreamTypes.length];
            for (int i = 0; i < this.mAudioStreamTypes.length; ++i) {
                this.mLastVolumes[i] = this.mAudioManager.getStreamVolume(this.mAudioStreamTypes[i]);
            }
        }

        public void onChange(boolean bl) {
            for (int i = 0; i < this.mAudioStreamTypes.length; ++i) {
                int n = this.mAudioManager.getStreamVolume(this.mAudioStreamTypes[i]);
                if (n == this.mLastVolumes[i]) continue;
                this.mLastVolumes[i] = n;
                this.mListener.onAudioStreamVolumeChanged(this.mAudioStreamTypes[i], n);
            }
        }
    }

    public static interface OnAudioStreamVolumeChangedListener {
        public void onAudioStreamVolumeChanged(int var1, int var2);
    }
}

