package com.google.vr.internal.controller;

import com.google.vr.cardboard.annotations.UsedByNative;
import com.google.vr.internal.controller.ServiceBridge;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
@UsedByNative
/* loaded from: classes.dex */
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

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onControllerEventPacket(ControllerEventPacket controllerEventPacket) {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            for (int i = 0; !this.closed && i < controllerEventPacket.getAccelEventCount(); i++) {
                ControllerAccelEvent accelEvent = controllerEventPacket.getAccelEvent(i);
                handleAccelEvent(this.userData, accelEvent.timestampNanos, accelEvent.x, accelEvent.y, accelEvent.z);
            }
            for (int i2 = 0; !this.closed && i2 < controllerEventPacket.getButtonEventCount(); i2++) {
                ControllerButtonEvent buttonEvent = controllerEventPacket.getButtonEvent(i2);
                handleButtonEvent(this.userData, buttonEvent.timestampNanos, buttonEvent.button, buttonEvent.down);
            }
            for (int i3 = 0; !this.closed && i3 < controllerEventPacket.getGyroEventCount(); i3++) {
                ControllerGyroEvent gyroEvent = controllerEventPacket.getGyroEvent(i3);
                handleGyroEvent(this.userData, gyroEvent.timestampNanos, gyroEvent.x, gyroEvent.y, gyroEvent.z);
            }
            for (int i4 = 0; !this.closed && i4 < controllerEventPacket.getOrientationEventCount(); i4++) {
                ControllerOrientationEvent orientationEvent = controllerEventPacket.getOrientationEvent(i4);
                handleOrientationEvent(this.userData, orientationEvent.timestampNanos, orientationEvent.qx, orientationEvent.qy, orientationEvent.qz, orientationEvent.qw);
            }
            for (int i5 = 0; !this.closed && i5 < controllerEventPacket.getTouchEventCount(); i5++) {
                ControllerTouchEvent touchEvent = controllerEventPacket.getTouchEvent(i5);
                handleTouchEvent(this.userData, touchEvent.timestampNanos, touchEvent.action, touchEvent.x, touchEvent.y);
            }
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
        if (!this.closed) {
            handleControllerRecentered(this.userData, controllerOrientationEvent.timestampNanos, controllerOrientationEvent.qx, controllerOrientationEvent.qy, controllerOrientationEvent.qz, controllerOrientationEvent.qw);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onControllerStateChanged(int i, int i2) {
        if (!this.closed) {
            handleStateChanged(this.userData, i, i2);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onServiceConnected(int i) {
        if (!this.closed) {
            handleServiceConnected(this.userData, i);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onServiceDisconnected() {
        if (!this.closed) {
            handleServiceDisconnected(this.userData);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onServiceFailed() {
        if (!this.closed) {
            handleServiceFailed(this.userData);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onServiceInitFailed(int i) {
        if (!this.closed) {
            handleServiceInitFailed(this.userData, i);
        }
    }

    @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
    public final synchronized void onServiceUnavailable() {
        if (!this.closed) {
            handleServiceUnavailable(this.userData);
        }
    }
}
