// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.graphics.Point;
import android.util.Log;

public class BufferSpec
{
    private static final String TAG;
    long nativeBufferSpec;
    
    static {
        TAG = BufferSpec.class.getSimpleName();
    }
    
    BufferSpec(final long nativeBufferSpec) {
        this.nativeBufferSpec = nativeBufferSpec;
    }
    
    public static boolean isValidColorFormat(final int n) {
        return n >= 0 && n < 2;
    }
    
    public static boolean isValidDepthStencilFormat(final int n) {
        return n == 255 || (n >= 0 && n < 6);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeBufferSpec != 0L) {
                Log.w(BufferSpec.TAG, "BufferSpec.shutdown() should be called to ensure resource cleanup");
                this.shutdown();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public int getSamples() {
        return GvrApi.nativeBufferSpecGetSamples(this.nativeBufferSpec);
    }
    
    public void getSize(final Point point) {
        GvrApi.nativeBufferSpecGetSize(this.nativeBufferSpec, point);
    }
    
    public void setColorFormat(final int n) {
        if (isValidColorFormat(n)) {
            GvrApi.nativeBufferSpecSetColorFormat(this.nativeBufferSpec, n);
            return;
        }
        throw new IllegalArgumentException("Invalid color format.");
    }
    
    public void setDepthStencilFormat(final int n) {
        if (isValidDepthStencilFormat(n)) {
            GvrApi.nativeBufferSpecSetDepthStencilFormat(this.nativeBufferSpec, n);
            return;
        }
        throw new IllegalArgumentException("Invalid depth-stencil format.");
    }
    
    public void setSamples(final int n) {
        GvrApi.nativeBufferSpecSetSamples(this.nativeBufferSpec, n);
    }
    
    public void setSize(final Point point) {
        GvrApi.nativeBufferSpecSetSize(this.nativeBufferSpec, point.x, point.y);
    }
    
    public void shutdown() {
        if (this.nativeBufferSpec != 0L) {
            GvrApi.nativeBufferSpecDestroy(this.nativeBufferSpec);
            this.nativeBufferSpec = 0L;
        }
    }
    
    public abstract static class ColorFormat
    {
        public static final int NUM_FORMATS = 2;
        public static final int RGBA_8888 = 0;
        public static final int RGB_565 = 1;
    }
    
    public abstract static class DepthStencilFormat
    {
        public static final int DEPTH_16 = 0;
        public static final int DEPTH_24 = 1;
        public static final int DEPTH_24_STENCIL_8 = 2;
        public static final int DEPTH_32_F = 3;
        public static final int DEPTH_32_F_STENCIL_8 = 4;
        public static final int NONE = 255;
        public static final int NUM_FORMATS = 6;
        public static final int STENCIL_8 = 5;
    }
}
