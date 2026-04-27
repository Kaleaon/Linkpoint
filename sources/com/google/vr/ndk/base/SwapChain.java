package com.google.vr.ndk.base;

import android.graphics.Point;
import android.util.Log;

public class SwapChain {
    private static final String TAG = SwapChain.class.getSimpleName();
    private int currentFrame;
    private final Frame[] frames = new Frame[2];
    private long nativeSwapChain;

    SwapChain(long j) {
        this.nativeSwapChain = j;
        this.frames[0] = new Frame();
        this.frames[1] = new Frame();
        this.currentFrame = 0;
    }

    public Frame acquireFrame() {
        if (this.frames[0].getNativeFrame() == 0 && this.frames[1].getNativeFrame() == 0) {
            this.currentFrame = (this.currentFrame + 1) % 2;
            long nativeSwapChainAcquireFrame = GvrApi.nativeSwapChainAcquireFrame(this.nativeSwapChain);
            if (nativeSwapChainAcquireFrame == 0) {
                return null;
            }
            this.frames[this.currentFrame].setNativeFrame(nativeSwapChainAcquireFrame);
            return this.frames[this.currentFrame];
        }
        throw new RuntimeException("Previous frame not submitted");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.nativeSwapChain != 0) {
                Log.w(TAG, "SwapChain.shutdown() should be called to ensure resource cleanup");
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    public int getBufferCount() {
        return GvrApi.nativeSwapChainGetBufferCount(this.nativeSwapChain);
    }

    public void getBufferSize(int i, Point point) {
        GvrApi.nativeSwapChainGetBufferSize(this.nativeSwapChain, i, point);
    }

    public void resizeBuffer(int i, Point point) {
        GvrApi.nativeSwapChainResizeBuffer(this.nativeSwapChain, i, point.x, point.y);
    }

    public void shutdown() {
        if (this.nativeSwapChain != 0) {
            GvrApi.nativeSwapChainDestroy(this.nativeSwapChain);
            this.nativeSwapChain = 0;
        }
    }
}
