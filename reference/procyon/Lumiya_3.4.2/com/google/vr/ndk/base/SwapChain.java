// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.graphics.Point;
import android.util.Log;

public class SwapChain
{
    private static final String TAG;
    private int currentFrame;
    private final Frame[] frames;
    private long nativeSwapChain;
    
    static {
        TAG = SwapChain.class.getSimpleName();
    }
    
    SwapChain(final long nativeSwapChain) {
        this.nativeSwapChain = nativeSwapChain;
        (this.frames = new Frame[2])[0] = new Frame();
        this.frames[1] = new Frame();
        this.currentFrame = 0;
    }
    
    public Frame acquireFrame() {
        if (this.frames[0].getNativeFrame() != 0L || this.frames[1].getNativeFrame() != 0L) {
            throw new RuntimeException("Previous frame not submitted");
        }
        this.currentFrame = (this.currentFrame + 1) % 2;
        final long nativeSwapChainAcquireFrame = GvrApi.nativeSwapChainAcquireFrame(this.nativeSwapChain);
        if (nativeSwapChainAcquireFrame == 0L) {
            return null;
        }
        this.frames[this.currentFrame].setNativeFrame(nativeSwapChainAcquireFrame);
        return this.frames[this.currentFrame];
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeSwapChain != 0L) {
                Log.w(SwapChain.TAG, "SwapChain.shutdown() should be called to ensure resource cleanup");
                this.shutdown();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public int getBufferCount() {
        return GvrApi.nativeSwapChainGetBufferCount(this.nativeSwapChain);
    }
    
    public void getBufferSize(final int n, final Point point) {
        GvrApi.nativeSwapChainGetBufferSize(this.nativeSwapChain, n, point);
    }
    
    public void resizeBuffer(final int n, final Point point) {
        GvrApi.nativeSwapChainResizeBuffer(this.nativeSwapChain, n, point.x, point.y);
    }
    
    public void shutdown() {
        if (this.nativeSwapChain != 0L) {
            GvrApi.nativeSwapChainDestroy(this.nativeSwapChain);
            this.nativeSwapChain = 0L;
        }
    }
}
