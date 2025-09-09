package com.google.vr.ndk.base;

import android.graphics.Point;
import android.util.Log;

public class BufferSpec {
    private static final String TAG = BufferSpec.class.getSimpleName();
    long nativeBufferSpec;

    public static abstract class ColorFormat {
        public static final int NUM_FORMATS = 2;
        public static final int RGBA_8888 = 0;
        public static final int RGB_565 = 1;
    }

    public static abstract class DepthStencilFormat {
        public static final int DEPTH_16 = 0;
        public static final int DEPTH_24 = 1;
        public static final int DEPTH_24_STENCIL_8 = 2;
        public static final int DEPTH_32_F = 3;
        public static final int DEPTH_32_F_STENCIL_8 = 4;
        public static final int NONE = 255;
        public static final int NUM_FORMATS = 6;
        public static final int STENCIL_8 = 5;
    }

    BufferSpec(long j) {
        this.nativeBufferSpec = j;
    }

    public static boolean isValidColorFormat(int i) {
        return i >= 0 && i < 2;
    }

    public static boolean isValidDepthStencilFormat(int i) {
        if (i == 255) {
            return true;
        }
        return i >= 0 && i < 6;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.nativeBufferSpec != 0) {
                Log.w(TAG, "BufferSpec.shutdown() should be called to ensure resource cleanup");
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    public int getSamples() {
        return GvrApi.nativeBufferSpecGetSamples(this.nativeBufferSpec);
    }

    public void getSize(Point point) {
        GvrApi.nativeBufferSpecGetSize(this.nativeBufferSpec, point);
    }

    public void setColorFormat(int i) {
        if (isValidColorFormat(i)) {
            GvrApi.nativeBufferSpecSetColorFormat(this.nativeBufferSpec, i);
            return;
        }
        throw new IllegalArgumentException("Invalid color format.");
    }

    public void setDepthStencilFormat(int i) {
        if (isValidDepthStencilFormat(i)) {
            GvrApi.nativeBufferSpecSetDepthStencilFormat(this.nativeBufferSpec, i);
            return;
        }
        throw new IllegalArgumentException("Invalid depth-stencil format.");
    }

    public void setSamples(int i) {
        GvrApi.nativeBufferSpecSetSamples(this.nativeBufferSpec, i);
    }

    public void setSize(Point point) {
        GvrApi.nativeBufferSpecSetSize(this.nativeBufferSpec, point.x, point.y);
    }

    public void shutdown() {
        if (this.nativeBufferSpec != 0) {
            GvrApi.nativeBufferSpecDestroy(this.nativeBufferSpec);
            this.nativeBufferSpec = 0;
        }
    }
}
