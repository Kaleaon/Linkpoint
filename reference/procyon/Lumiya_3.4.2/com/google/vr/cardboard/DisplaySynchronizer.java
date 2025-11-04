// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.os.Build$VERSION;
import android.util.Log;
import android.content.Context;
import java.util.concurrent.TimeUnit;
import android.view.Display;
import android.view.Choreographer$FrameCallback;

public class DisplaySynchronizer implements Choreographer$FrameCallback
{
    private static final boolean DEBUG = false;
    public static final long DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS;
    private static final int INVALID_DISPLAY_ROTATION = -1;
    private static final float MIN_VALID_DISPLAY_REFRESH_RATE = 30.0f;
    private static final String TAG = "DisplaySynchronizer";
    private volatile Display display;
    private int displayRotationDegrees;
    private final FrameMonitor frameMonitor;
    private long lastDisplayRotationUpdateTimeNanos;
    private long nativeDisplaySynchronizer;
    
    static {
        DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(1L);
    }
    
    public DisplaySynchronizer(final Context context, final Display display) {
        this.displayRotationDegrees = -1;
        this.lastDisplayRotationUpdateTimeNanos = 0L;
        this.nativeDisplaySynchronizer = this.nativeCreate(this.getClass().getClassLoader(), context.getApplicationContext());
        if (this.nativeDisplaySynchronizer == 0L) {
            throw new IllegalStateException("Native DisplaySynchronizer creation failed.");
        }
        this.setDisplay(display);
        this.frameMonitor = new FrameMonitor((Choreographer$FrameCallback)this);
    }
    
    private void checkNativeDisplaySynchronizer() {
        if (this.nativeDisplaySynchronizer == 0L) {
            throw new IllegalStateException("DisplaySynchronizer has already been shut down.");
        }
    }
    
    private void invalidateDisplayRotation() {
        this.displayRotationDegrees = -1;
    }
    
    public void doFrame(final long lastDisplayRotationUpdateTimeNanos) {
        this.checkNativeDisplaySynchronizer();
        Label_0066: {
            if (this.displayRotationDegrees != -1) {
                int n;
                if (lastDisplayRotationUpdateTimeNanos - this.lastDisplayRotationUpdateTimeNanos <= DisplaySynchronizer.DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    break Label_0066;
                }
            }
            switch (this.display.getRotation()) {
                default: {
                    Log.e("DisplaySynchronizer", "Unknown display rotation, defaulting to 0");
                    this.displayRotationDegrees = 0;
                    break;
                }
                case 0: {
                    this.displayRotationDegrees = 0;
                    break;
                }
                case 1: {
                    this.displayRotationDegrees = 90;
                    break;
                }
                case 2: {
                    this.displayRotationDegrees = 180;
                    break;
                }
                case 3: {
                    this.displayRotationDegrees = 270;
                    break;
                }
            }
            this.lastDisplayRotationUpdateTimeNanos = lastDisplayRotationUpdateTimeNanos;
        }
        this.nativeUpdate(this.nativeDisplaySynchronizer, lastDisplayRotationUpdateTimeNanos, this.displayRotationDegrees);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeDisplaySynchronizer != 0L) {
                Log.w("DisplaySynchronizer", "DisplaySynchronizer.shutdown() should be called to ensure resource cleanup");
                this.nativeDestroy(this.nativeDisplaySynchronizer);
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public Display getDisplay() {
        return this.display;
    }
    
    public long getNativeDisplaySynchronizer() {
        this.checkNativeDisplaySynchronizer();
        return this.nativeDisplaySynchronizer;
    }
    
    protected native long nativeCreate(final ClassLoader p0, final Context p1);
    
    protected native void nativeDestroy(final long p0);
    
    protected native void nativeReset(final long p0, final long p1, final long p2);
    
    protected native void nativeUpdate(final long p0, final long p1, final int p2);
    
    public void onConfigurationChanged() {
        this.invalidateDisplayRotation();
    }
    
    public void onPause() {
        this.frameMonitor.onPause();
    }
    
    public void onResume() {
        this.invalidateDisplayRotation();
        this.frameMonitor.onResume();
    }
    
    public void setDisplay(final Display display) {
        this.checkNativeDisplaySynchronizer();
        this.display = display;
        this.invalidateDisplayRotation();
        final float refreshRate = display.getRefreshRate();
        long n;
        if (refreshRate >= 30.0f) {
            n = (long)(TimeUnit.SECONDS.toNanos(1L) / refreshRate);
        }
        else {
            n = 0L;
        }
        long appVsyncOffsetNanos;
        if (Build$VERSION.SDK_INT < 21) {
            appVsyncOffsetNanos = 0L;
        }
        else {
            appVsyncOffsetNanos = display.getAppVsyncOffsetNanos();
        }
        this.nativeReset(this.nativeDisplaySynchronizer, n, appVsyncOffsetNanos);
    }
    
    public void shutdown() {
        if (this.nativeDisplaySynchronizer != 0L) {
            this.onPause();
            this.nativeDestroy(this.nativeDisplaySynchronizer);
            this.nativeDisplaySynchronizer = 0L;
        }
    }
}
