// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.graphics.Point;
import android.util.Log;

public class Frame
{
    private static final String TAG;
    private long nativeFrame;
    
    static {
        TAG = Frame.class.getSimpleName();
    }
    
    Frame() {
        this.nativeFrame = 0L;
    }
    
    private void checkAccess() {
        if (this.nativeFrame == 0L) {
            throw new RuntimeException("Frame was reused after submission");
        }
    }
    
    public void bindBuffer(final int n) {
        this.checkAccess();
        GvrApi.nativeFrameBindBuffer(this.nativeFrame, n);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeFrame != 0L) {
                Log.w(Frame.TAG, "Frame finalized before it was submitted");
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public void getBufferSize(final int n, final Point point) {
        this.checkAccess();
        GvrApi.nativeFrameGetBufferSize(this.nativeFrame, n, point);
    }
    
    public int getFramebufferObject(final int n) {
        this.checkAccess();
        return GvrApi.nativeFrameGetFramebufferObject(this.nativeFrame, n);
    }
    
    long getNativeFrame() {
        return this.nativeFrame;
    }
    
    void setNativeFrame(final long nativeFrame) {
        this.nativeFrame = nativeFrame;
    }
    
    public void submit(final BufferViewportList list, final float[] array) {
        this.checkAccess();
        GvrApi.nativeFrameSubmit(this.nativeFrame, list.nativeBufferViewportList, array);
        this.nativeFrame = 0L;
    }
    
    public void unbind() {
        this.checkAccess();
        GvrApi.nativeFrameUnbind(this.nativeFrame);
    }
}
