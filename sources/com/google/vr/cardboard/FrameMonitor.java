package com.google.vr.cardboard;

import android.view.Choreographer;

public class FrameMonitor implements Choreographer.FrameCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Choreographer.FrameCallback callback;
    private final Choreographer choreographer;
    private boolean isPaused;

    public FrameMonitor(Choreographer.FrameCallback frameCallback) {
        this(Choreographer.getInstance(), frameCallback);
    }

    public FrameMonitor(Choreographer choreographer2, Choreographer.FrameCallback frameCallback) {
        ThreadUtils.assertOnUiThread();
        this.callback = frameCallback;
        this.choreographer = choreographer2;
        choreographer2.postFrameCallback(this);
    }

    public void doFrame(long j) {
        this.choreographer.postFrameCallback(this);
        this.callback.doFrame(j);
    }

    public void onPause() {
        if (!this.isPaused) {
            this.choreographer.removeFrameCallback(this);
            this.isPaused = true;
        }
    }

    public void onResume() {
        if (this.isPaused) {
            this.isPaused = false;
            this.choreographer.postFrameCallback(this);
        }
    }
}
