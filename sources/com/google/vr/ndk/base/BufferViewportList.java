package com.google.vr.ndk.base;

import android.util.Log;

public class BufferViewportList {
    private static final String TAG = BufferViewportList.class.getSimpleName();
    long nativeBufferViewportList;

    BufferViewportList(long j) {
        this.nativeBufferViewportList = j;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.nativeBufferViewportList != 0) {
                Log.w(TAG, "BufferViewportList.shutdown() should be called to ensure resource cleanup");
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    public void get(int i, BufferViewport bufferViewport) {
        GvrApi.nativeBufferViewportListGetItem(this.nativeBufferViewportList, i, bufferViewport.nativeBufferViewport);
    }

    public void set(int i, BufferViewport bufferViewport) {
        GvrApi.nativeBufferViewportListSetItem(this.nativeBufferViewportList, i, bufferViewport.nativeBufferViewport);
    }

    public void shutdown() {
        if (this.nativeBufferViewportList != 0) {
            GvrApi.nativeBufferViewportListDestroy(this.nativeBufferViewportList);
            this.nativeBufferViewportList = 0;
        }
    }

    public int size() {
        return GvrApi.nativeBufferViewportListGetSize(this.nativeBufferViewportList);
    }
}
