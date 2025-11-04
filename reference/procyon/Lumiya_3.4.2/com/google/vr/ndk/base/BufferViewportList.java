// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.util.Log;

public class BufferViewportList
{
    private static final String TAG;
    long nativeBufferViewportList;
    
    static {
        TAG = BufferViewportList.class.getSimpleName();
    }
    
    BufferViewportList(final long nativeBufferViewportList) {
        this.nativeBufferViewportList = nativeBufferViewportList;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeBufferViewportList != 0L) {
                Log.w(BufferViewportList.TAG, "BufferViewportList.shutdown() should be called to ensure resource cleanup");
                this.shutdown();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public void get(final int n, final BufferViewport bufferViewport) {
        GvrApi.nativeBufferViewportListGetItem(this.nativeBufferViewportList, n, bufferViewport.nativeBufferViewport);
    }
    
    public void set(final int n, final BufferViewport bufferViewport) {
        GvrApi.nativeBufferViewportListSetItem(this.nativeBufferViewportList, n, bufferViewport.nativeBufferViewport);
    }
    
    public void shutdown() {
        if (this.nativeBufferViewportList != 0L) {
            GvrApi.nativeBufferViewportListDestroy(this.nativeBufferViewportList);
            this.nativeBufferViewportList = 0L;
        }
    }
    
    public int size() {
        return GvrApi.nativeBufferViewportListGetSize(this.nativeBufferViewportList);
    }
}
