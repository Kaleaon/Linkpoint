package com.google.vr.cardboard;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import java.util.concurrent.TimeUnit;

public class DisplaySynchronizer implements Choreographer.FrameCallback {
    private static final boolean DEBUG = false;
    public static final long DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(1);
    private static final int INVALID_DISPLAY_ROTATION = -1;
    private static final float MIN_VALID_DISPLAY_REFRESH_RATE = 30.0f;
    private static final String TAG = "DisplaySynchronizer";
    private volatile Display display;
    private int displayRotationDegrees = -1;
    private final FrameMonitor frameMonitor;
    private long lastDisplayRotationUpdateTimeNanos = 0;
    private long nativeDisplaySynchronizer;

    public DisplaySynchronizer(Context context, Display display2) {
        this.nativeDisplaySynchronizer = nativeCreate(getClass().getClassLoader(), context.getApplicationContext());
        if (this.nativeDisplaySynchronizer == 0) {
            throw new IllegalStateException("Native DisplaySynchronizer creation failed.");
        }
        setDisplay(display2);
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

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0034, code lost:
        if ((r8 - r7.lastDisplayRotationUpdateTimeNanos <= DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS) == false) goto L_0x0009;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doFrame(long r8) {
        /*
            r7 = this;
            r1 = 0
            r7.checkNativeDisplaySynchronizer()
            int r0 = r7.displayRotationDegrees
            r2 = -1
            if (r0 != r2) goto L_0x0029
        L_0x0009:
            android.view.Display r0 = r7.display
            int r0 = r0.getRotation()
            switch(r0) {
                case 0: goto L_0x0039;
                case 1: goto L_0x003c;
                case 2: goto L_0x0041;
                case 3: goto L_0x0046;
                default: goto L_0x0012;
            }
        L_0x0012:
            java.lang.String r0 = "DisplaySynchronizer"
            java.lang.String r2 = "Unknown display rotation, defaulting to 0"
            android.util.Log.e(r0, r2)
            r7.displayRotationDegrees = r1
        L_0x001d:
            r7.lastDisplayRotationUpdateTimeNanos = r8
        L_0x001f:
            long r2 = r7.nativeDisplaySynchronizer
            int r6 = r7.displayRotationDegrees
            r1 = r7
            r4 = r8
            r1.nativeUpdate(r2, r4, r6)
            return
        L_0x0029:
            long r2 = r7.lastDisplayRotationUpdateTimeNanos
            long r2 = r8 - r2
            long r4 = DISPLAY_ROTATION_REFRESH_INTERVAL_NANOS
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 > 0) goto L_0x0037
            r0 = 1
        L_0x0034:
            if (r0 != 0) goto L_0x001f
            goto L_0x0009
        L_0x0037:
            r0 = r1
            goto L_0x0034
        L_0x0039:
            r7.displayRotationDegrees = r1
            goto L_0x001d
        L_0x003c:
            r0 = 90
            r7.displayRotationDegrees = r0
            goto L_0x001d
        L_0x0041:
            r0 = 180(0xb4, float:2.52E-43)
            r7.displayRotationDegrees = r0
            goto L_0x001d
        L_0x0046:
            r0 = 270(0x10e, float:3.78E-43)
            r7.displayRotationDegrees = r0
            goto L_0x001d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.cardboard.DisplaySynchronizer.doFrame(long):void");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
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

    /* access modifiers changed from: protected */
    public native long nativeCreate(ClassLoader classLoader, Context context);

    /* access modifiers changed from: protected */
    public native void nativeDestroy(long j);

    /* access modifiers changed from: protected */
    public native void nativeReset(long j, long j2, long j3);

    /* access modifiers changed from: protected */
    public native void nativeUpdate(long j, long j2, int i);

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

    public void setDisplay(Display display2) {
        checkNativeDisplaySynchronizer();
        this.display = display2;
        invalidateDisplayRotation();
        float refreshRate = display2.getRefreshRate();
        nativeReset(this.nativeDisplaySynchronizer, refreshRate >= MIN_VALID_DISPLAY_REFRESH_RATE ? (long) (((float) TimeUnit.SECONDS.toNanos(1)) / refreshRate) : 0, Build.VERSION.SDK_INT < 21 ? 0 : display2.getAppVsyncOffsetNanos());
    }

    public void shutdown() {
        if (this.nativeDisplaySynchronizer != 0) {
            onPause();
            nativeDestroy(this.nativeDisplaySynchronizer);
            this.nativeDisplaySynchronizer = 0;
        }
    }
}
