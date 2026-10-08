package com.google.vr.cardboard;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DisplaySynchronizer implements Choreographer.FrameCallback {
    private static final boolean DEBUG = false;
    public static final long DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(1);
    private static final int INVALID_DISPLAY_ROTATION = -1;
    private static final float MIN_VALID_DISPLAY_REFRESH_RATE = 30.0f;
    private static final String TAG = "DisplaySynchronizer";
    private volatile Display display;
    private final FrameMonitor frameMonitor;
    private long nativeDisplaySynchronizer;
    private int displayRotationDegrees = -1;
    private long lastDisplayRotationUpdateTimeNanos = 0;

    public DisplaySynchronizer(Context context, Display display) {
        this.nativeDisplaySynchronizer = nativeCreate(getClass().getClassLoader(), context.getApplicationContext());
        if (this.nativeDisplaySynchronizer == 0) {
            throw new IllegalStateException("Native DisplaySynchronizer creation failed.");
        }
        setDisplay(display);
        this.frameMonitor = new FrameMonitor(this);
    }

    private void checkNativeDisplaySynchronizer() {
        if (this.nativeDisplaySynchronizer == 0) {
            throw new IllegalStateException("DisplaySynchronizer has already been shut down.");
        }
    }

    private void invalidateDisplayRotation() {
        this.displayRotationDegrees = -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0034, code lost:
        if ((r8 - r7.lastDisplayRotationUpdateTimeNanos <= com.google.vr.cardboard.DisplaySynchronizer.DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS) == false) goto L3;
     */
    @Override // android.view.Choreographer.FrameCallback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void doFrame(long r8) {
        /*
            r7 = this;
            r1 = 0
            r7.checkNativeDisplaySynchronizer()
            int r0 = r7.displayRotationDegrees
            r2 = -1
            if (r0 != r2) goto L29
        L9:
            android.view.Display r0 = r7.display
            int r0 = r0.getRotation()
            switch(r0) {
                case 0: goto L39;
                case 1: goto L3c;
                case 2: goto L41;
                case 3: goto L46;
                default: goto L12;
            }
        L12:
            java.lang.String r0 = "DisplaySynchronizer"
            java.lang.String r2 = "Unknown display rotation, defaulting to 0"
            android.util.Log.e(r0, r2)
            r7.displayRotationDegrees = r1
        L1d:
            r7.lastDisplayRotationUpdateTimeNanos = r8
        L1f:
            long r2 = r7.nativeDisplaySynchronizer
            int r6 = r7.displayRotationDegrees
            r1 = r7
            r4 = r8
            r1.nativeUpdate(r2, r4, r6)
            return
        L29:
            long r2 = r7.lastDisplayRotationUpdateTimeNanos
            long r2 = r8 - r2
            long r4 = com.google.vr.cardboard.DisplaySynchronizer.DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 > 0) goto L37
            r0 = 1
        L34:
            if (r0 != 0) goto L1f
            goto L9
        L37:
            r0 = r1
            goto L34
        L39:
            r7.displayRotationDegrees = r1
            goto L1d
        L3c:
            r0 = 90
            r7.displayRotationDegrees = r0
            goto L1d
        L41:
            r0 = 180(0xb4, float:2.52E-43)
            r7.displayRotationDegrees = r0
            goto L1d
        L46:
            r0 = 270(0x10e, float:3.78E-43)
            r7.displayRotationDegrees = r0
            goto L1d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.cardboard.DisplaySynchronizer.doFrame(long):void");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.nativeDisplaySynchronizer != 0) {
                Log.w(TAG, "DisplaySynchronizer.shutdown() should be called to ensure resource cleanup");
                nativeDestroy(this.nativeDisplaySynchronizer);
            }
        } finally {
            super.finalize();
        }
    }

    public Display getDisplay() {
        return this.display;
    }

    public long getNativeDisplaySynchronizer() {
        checkNativeDisplaySynchronizer();
        return this.nativeDisplaySynchronizer;
    }

    protected native long nativeCreate(ClassLoader classLoader, Context context);

    protected native void nativeDestroy(long j);

    protected native void nativeReset(long j, long j2, long j3);

    protected native void nativeUpdate(long j, long j2, int i);

    public void onConfigurationChanged() {
        invalidateDisplayRotation();
    }

    public void onPause() {
        this.frameMonitor.onPause();
    }

    public void onResume() {
        invalidateDisplayRotation();
        this.frameMonitor.onResume();
    }

    public void setDisplay(Display display) {
        checkNativeDisplaySynchronizer();
        this.display = display;
        invalidateDisplayRotation();
        float refreshRate = display.getRefreshRate();
        nativeReset(this.nativeDisplaySynchronizer, refreshRate >= MIN_VALID_DISPLAY_REFRESH_RATE ? ((float) TimeUnit.SECONDS.toNanos(1L)) / refreshRate : 0L, Build.VERSION.SDK_INT < 21 ? 0L : display.getAppVsyncOffsetNanos());
    }

    public void shutdown() {
        if (this.nativeDisplaySynchronizer != 0) {
            onPause();
            nativeDestroy(this.nativeDisplaySynchronizer);
            this.nativeDisplaySynchronizer = 0L;
        }
    }
}
