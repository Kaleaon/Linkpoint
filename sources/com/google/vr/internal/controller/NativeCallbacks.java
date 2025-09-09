package com.google.vr.internal.controller;

import com.google.vr.cardboard.annotations.UsedByNative;
import com.google.vr.internal.controller.ServiceBridge;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;

@UsedByNative
public final class NativeCallbacks implements ServiceBridge.Callbacks {
    private boolean closed;
    private final long userData;

    @UsedByNative
    public NativeCallbacks(long j) {
        this.userData = j;
    }

    private final native void handleAccelEvent(long j, long j2, float f, float f2, float f3);

    private final native void handleButtonEvent(long j, long j2, int i, boolean z);

    private final native void handleControllerRecentered(long j, long j2, float f, float f2, float f3, float f4);

    private final native void handleGyroEvent(long j, long j2, float f, float f2, float f3);

    private final native void handleOrientationEvent(long j, long j2, float f, float f2, float f3, float f4);

    private final native void handleServiceConnected(long j, int i);

    private final native void handleServiceDisconnected(long j);

    private final native void handleServiceFailed(long j);

    private final native void handleServiceInitFailed(long j, int i);

    private final native void handleServiceUnavailable(long j);

    private final native void handleStateChanged(long j, int i, int i2);

    private final native void handleTouchEvent(long j, long j2, int i, float f, float f2);

    @UsedByNative
    public final synchronized void close() {
        this.closed = true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x001f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void onControllerEventPacket(com.google.vr.vrcore.controller.api.ControllerEventPacket r12) {
        /*
            r11 = this;
            r0 = 0
            monitor-enter(r11)
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 != 0) goto L_0x0020
            r9 = r0
        L_0x0007:
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x0022
        L_0x000b:
            r8 = r0
        L_0x000c:
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x003e
        L_0x0010:
            r9 = r0
        L_0x0011:
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x0058
        L_0x0015:
            r10 = r0
        L_0x0016:
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x0074
        L_0x001a:
            boolean r1 = r11.closed     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x0092
        L_0x001e:
            monitor-exit(r11)
            return
        L_0x0020:
            monitor-exit(r11)
            return
        L_0x0022:
            int r1 = r12.getAccelEventCount()     // Catch:{ all -> 0x00ae }
            if (r9 >= r1) goto L_0x000b
            com.google.vr.vrcore.controller.api.ControllerAccelEvent r1 = r12.getAccelEvent(r9)     // Catch:{ all -> 0x00ae }
            long r2 = r11.userData     // Catch:{ all -> 0x00ae }
            long r4 = r1.timestampNanos     // Catch:{ all -> 0x00ae }
            float r6 = r1.x     // Catch:{ all -> 0x00ae }
            float r7 = r1.y     // Catch:{ all -> 0x00ae }
            float r8 = r1.z     // Catch:{ all -> 0x00ae }
            r1 = r11
            r1.handleAccelEvent(r2, r4, r6, r7, r8)     // Catch:{ all -> 0x00ae }
            int r1 = r9 + 1
            r9 = r1
            goto L_0x0007
        L_0x003e:
            int r1 = r12.getButtonEventCount()     // Catch:{ all -> 0x00ae }
            if (r8 >= r1) goto L_0x0010
            com.google.vr.vrcore.controller.api.ControllerButtonEvent r1 = r12.getButtonEvent(r8)     // Catch:{ all -> 0x00ae }
            long r2 = r11.userData     // Catch:{ all -> 0x00ae }
            long r4 = r1.timestampNanos     // Catch:{ all -> 0x00ae }
            int r6 = r1.button     // Catch:{ all -> 0x00ae }
            boolean r7 = r1.down     // Catch:{ all -> 0x00ae }
            r1 = r11
            r1.handleButtonEvent(r2, r4, r6, r7)     // Catch:{ all -> 0x00ae }
            int r1 = r8 + 1
            r8 = r1
            goto L_0x000c
        L_0x0058:
            int r1 = r12.getGyroEventCount()     // Catch:{ all -> 0x00ae }
            if (r9 >= r1) goto L_0x0015
            com.google.vr.vrcore.controller.api.ControllerGyroEvent r1 = r12.getGyroEvent(r9)     // Catch:{ all -> 0x00ae }
            long r2 = r11.userData     // Catch:{ all -> 0x00ae }
            long r4 = r1.timestampNanos     // Catch:{ all -> 0x00ae }
            float r6 = r1.x     // Catch:{ all -> 0x00ae }
            float r7 = r1.y     // Catch:{ all -> 0x00ae }
            float r8 = r1.z     // Catch:{ all -> 0x00ae }
            r1 = r11
            r1.handleGyroEvent(r2, r4, r6, r7, r8)     // Catch:{ all -> 0x00ae }
            int r1 = r9 + 1
            r9 = r1
            goto L_0x0011
        L_0x0074:
            int r1 = r12.getOrientationEventCount()     // Catch:{ all -> 0x00ae }
            if (r10 >= r1) goto L_0x001a
            com.google.vr.vrcore.controller.api.ControllerOrientationEvent r1 = r12.getOrientationEvent(r10)     // Catch:{ all -> 0x00ae }
            long r2 = r11.userData     // Catch:{ all -> 0x00ae }
            long r4 = r1.timestampNanos     // Catch:{ all -> 0x00ae }
            float r6 = r1.qx     // Catch:{ all -> 0x00ae }
            float r7 = r1.qy     // Catch:{ all -> 0x00ae }
            float r8 = r1.qz     // Catch:{ all -> 0x00ae }
            float r9 = r1.qw     // Catch:{ all -> 0x00ae }
            r1 = r11
            r1.handleOrientationEvent(r2, r4, r6, r7, r8, r9)     // Catch:{ all -> 0x00ae }
            int r1 = r10 + 1
            r10 = r1
            goto L_0x0016
        L_0x0092:
            int r1 = r12.getTouchEventCount()     // Catch:{ all -> 0x00ae }
            if (r0 >= r1) goto L_0x001e
            com.google.vr.vrcore.controller.api.ControllerTouchEvent r1 = r12.getTouchEvent(r0)     // Catch:{ all -> 0x00ae }
            long r2 = r11.userData     // Catch:{ all -> 0x00ae }
            long r4 = r1.timestampNanos     // Catch:{ all -> 0x00ae }
            int r6 = r1.action     // Catch:{ all -> 0x00ae }
            float r7 = r1.x     // Catch:{ all -> 0x00ae }
            float r8 = r1.y     // Catch:{ all -> 0x00ae }
            r1 = r11
            r1.handleTouchEvent(r2, r4, r6, r7, r8)     // Catch:{ all -> 0x00ae }
            int r0 = r0 + 1
            goto L_0x001a
        L_0x00ae:
            r0 = move-exception
            monitor-exit(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.internal.controller.NativeCallbacks.onControllerEventPacket(com.google.vr.vrcore.controller.api.ControllerEventPacket):void");
    }

    public final synchronized void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
        if (!this.closed) {
            handleControllerRecentered(this.userData, controllerOrientationEvent.timestampNanos, controllerOrientationEvent.qx, controllerOrientationEvent.qy, controllerOrientationEvent.qz, controllerOrientationEvent.qw);
        }
    }

    public final synchronized void onControllerStateChanged(int i, int i2) {
        if (!this.closed) {
            handleStateChanged(this.userData, i, i2);
        }
    }

    public final synchronized void onServiceConnected(int i) {
        if (!this.closed) {
            handleServiceConnected(this.userData, i);
        }
    }

    public final synchronized void onServiceDisconnected() {
        if (!this.closed) {
            handleServiceDisconnected(this.userData);
        }
    }

    public final synchronized void onServiceFailed() {
        if (!this.closed) {
            handleServiceFailed(this.userData);
        }
    }

    public final synchronized void onServiceInitFailed(int i) {
        if (!this.closed) {
            handleServiceInitFailed(this.userData, i);
        }
    }

    public final synchronized void onServiceUnavailable() {
        if (!this.closed) {
            handleServiceUnavailable(this.userData);
        }
    }
}
