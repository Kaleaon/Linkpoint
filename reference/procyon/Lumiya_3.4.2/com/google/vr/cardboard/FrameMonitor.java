// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.view.Choreographer;
import android.view.Choreographer$FrameCallback;

public class FrameMonitor implements Choreographer$FrameCallback
{
    private final Choreographer$FrameCallback callback;
    private final Choreographer choreographer;
    private boolean isPaused;
    
    public FrameMonitor(final Choreographer$FrameCallback choreographer$FrameCallback) {
        this(Choreographer.getInstance(), choreographer$FrameCallback);
    }
    
    public FrameMonitor(final Choreographer choreographer, final Choreographer$FrameCallback callback) {
        ThreadUtils.assertOnUiThread();
        this.callback = callback;
        (this.choreographer = choreographer).postFrameCallback((Choreographer$FrameCallback)this);
    }
    
    public void doFrame(final long n) {
        this.choreographer.postFrameCallback((Choreographer$FrameCallback)this);
        this.callback.doFrame(n);
    }
    
    public void onPause() {
        if (!this.isPaused) {
            this.choreographer.removeFrameCallback((Choreographer$FrameCallback)this);
            this.isPaused = true;
        }
    }
    
    public void onResume() {
        if (this.isPaused) {
            this.isPaused = false;
            this.choreographer.postFrameCallback((Choreographer$FrameCallback)this);
        }
    }
}
