// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.graphics.RectF;
import android.util.Log;

public class BufferViewport
{
    public static final int BUFFER_INDEX_EXTERNAL_SURFACE = -1;
    public static final int EXTERNAL_SURFACE_ID_NONE = -1;
    private static final String TAG;
    long nativeBufferViewport;
    
    static {
        TAG = BufferViewport.class.getSimpleName();
    }
    
    BufferViewport(final long nativeBufferViewport) {
        this.nativeBufferViewport = nativeBufferViewport;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof BufferViewport && GvrApi.nativeBufferViewportEqual(this.nativeBufferViewport, ((BufferViewport)o).nativeBufferViewport);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeBufferViewport != 0L) {
                Log.w(BufferViewport.TAG, "BufferViewport.shutdown() should be called to ensure resource cleanup");
                this.shutdown();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public int getExternalSurfaceId() {
        return GvrApi.nativeBufferViewportGetExternalSurfaceId(this.nativeBufferViewport);
    }
    
    public int getReprojection() {
        return GvrApi.nativeBufferViewportGetReprojection(this.nativeBufferViewport);
    }
    
    public int getSourceBufferIndex() {
        return GvrApi.nativeBufferViewportGetSourceBufferIndex(this.nativeBufferViewport);
    }
    
    public void getSourceFov(final RectF rectF) {
        GvrApi.nativeBufferViewportGetSourceFov(this.nativeBufferViewport, rectF);
    }
    
    public void getSourceUv(final RectF rectF) {
        GvrApi.nativeBufferViewportGetSourceUv(this.nativeBufferViewport, rectF);
    }
    
    public int getTargetEye() {
        return GvrApi.nativeBufferViewportGetTargetEye(this.nativeBufferViewport);
    }
    
    public void getTransform(final float[] array) {
        GvrApi.nativeBufferViewportGetTransform(this.nativeBufferViewport, array);
    }
    
    public void setExternalSurfaceId(final int n) {
        GvrApi.nativeBufferViewportSetExternalSurfaceId(this.nativeBufferViewport, n);
    }
    
    public void setReprojection(final int n) {
        GvrApi.nativeBufferViewportSetReprojection(this.nativeBufferViewport, n);
    }
    
    public void setSourceBufferIndex(final int n) {
        GvrApi.nativeBufferViewportSetSourceBufferIndex(this.nativeBufferViewport, n);
    }
    
    public void setSourceFov(final RectF rectF) {
        GvrApi.nativeBufferViewportSetSourceFov(this.nativeBufferViewport, rectF.left, rectF.top, rectF.right, rectF.bottom);
    }
    
    public void setSourceUv(final RectF rectF) {
        GvrApi.nativeBufferViewportSetSourceUv(this.nativeBufferViewport, rectF.left, rectF.top, rectF.right, rectF.bottom);
    }
    
    public void setTargetEye(final int n) {
        GvrApi.nativeBufferViewportSetTargetEye(this.nativeBufferViewport, n);
    }
    
    public void setTransform(final float[] array) {
        GvrApi.nativeBufferViewportSetTransform(this.nativeBufferViewport, array);
    }
    
    public void shutdown() {
        if (this.nativeBufferViewport != 0L) {
            GvrApi.nativeBufferViewportDestroy(this.nativeBufferViewport);
            this.nativeBufferViewport = 0L;
        }
    }
    
    public abstract static class EyeType
    {
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
    }
    
    public abstract static class Reprojection
    {
        public static final int FULL = 1;
        public static final int NONE = 0;
    }
}
