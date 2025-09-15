package com.lumiyaviewer.lumiya.media;
import java.util.*;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import com.lumiyaviewer.lumiya.Debug;

public class MediaPlayerWrapper implements Runnable, OnErrorListener, OnInfoListener, OnPreparedListener {
    private MediaPlayer mediaPlayer = null;
    private volatile String mediaURL = "";
    private volatile boolean mustExit = false;
    private volatile boolean mustPlay = false;
    private Thread workingThread = null;

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Debug.Log("MediaPlayerWrapper: onError: what = " + i + ", extra = " + i2);
        return false;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        Debug.Log("MediaPlayerWrapper: onInfo: what = " + i + ", extra = " + i2);
        return false;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        Debug.Log("MediaPlayerWrapper: prepared, starting playback");
        mediaPlayer.start();
    }

    public void play(String str) {
        synchronized (this) {
            if (this.mustExit) {
                return;
            }
            this.mustPlay = true;
            this.mustExit = false;
            String trim = str.trim();
            if (trim.toLowerCase().startsWith("http://")) {
                trim = "http://" + trim.substring(7);
            } else if (trim.toLowerCase().startsWith("https://")) {
                trim = "https://" + trim.substring(8);
            }
            this.mediaURL = trim;
            if (this.workingThread == null) {
                this.workingThread = new Thread(this);
                this.workingThread.start();
            }
            notify();
        }
    }

    public void release() {
        synchronized (this) {
            this.mustPlay = false;
            this.mustExit = true;
            this.mediaURL = "";
            this.workingThread = null;
            notify();
        }
    }

    public void run() {
        Debug.Log("MediaPlayerWrapper: working thread started");
        while (!this.mustExit) {
            if (this.mustPlay) {
                Debug.Log("MediaPlayerWrapper: working thread must play, URL = " + this.mediaURL);
                if (this.mediaPlayer != null) {
                    this.mediaPlayer.release();
                    this.mediaPlayer = null;
                }
                this.mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setAudioStreamType(3);
                this.mediaPlayer.setOnErrorListener(this);
                this.mediaPlayer.setOnInfoListener(this);
                this.mediaPlayer.setOnPreparedListener(this);
                try {
                    this.mediaPlayer.setDataSource(this.mediaURL);
                } catch (Exception e) {
                    Debug.Log("MediaPlayerWrapper: Failed to set data source to " + this.mediaURL);
                    e.printStackTrace();
                    this.mustPlay = false;
                }
                try {
                    this.mediaPlayer.prepareAsync();
                } catch (Exception e2) {
                    Debug.Log("MediaPlayerWrapper: PrepareAsync exception while playing " + this.mediaURL);
                    e2.printStackTrace();
                    this.mustPlay = false;
                }
            } else {
                Debug.Log("MediaPlayerWrapper: working thread must stop playing");
                if (this.mediaPlayer != null) {
                    this.mediaPlayer.release();
                    this.mediaPlayer = null;
                }
            }
            Debug.Log("MediaPlayerWrapper: working thread waiting");
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
            Debug.Log("MediaPlayerWrapper: working thread wake up");
        }
        Debug.Log("MediaPlayerWrapper: working thread exiting");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public void stop() {
        synchronized (this) {
            if (this.mustExit) {
                return;
            }
            this.mustPlay = false;
            this.mediaURL = "";
            notify();
        }
    }
}
