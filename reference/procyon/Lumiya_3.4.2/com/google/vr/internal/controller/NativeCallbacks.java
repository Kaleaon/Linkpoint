// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.internal.controller;

import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.cardboard.annotations.UsedByNative;

@UsedByNative
public final class NativeCallbacks implements Callbacks
{
    private boolean closed;
    private final long userData;
    
    @UsedByNative
    public NativeCallbacks(final long userData) {
        this.userData = userData;
    }
    
    private final native void handleAccelEvent(final long p0, final long p1, final float p2, final float p3, final float p4);
    
    private final native void handleButtonEvent(final long p0, final long p1, final int p2, final boolean p3);
    
    private final native void handleControllerRecentered(final long p0, final long p1, final float p2, final float p3, final float p4, final float p5);
    
    private final native void handleGyroEvent(final long p0, final long p1, final float p2, final float p3, final float p4);
    
    private final native void handleOrientationEvent(final long p0, final long p1, final float p2, final float p3, final float p4, final float p5);
    
    private final native void handleServiceConnected(final long p0, final int p1);
    
    private final native void handleServiceDisconnected(final long p0);
    
    private final native void handleServiceFailed(final long p0);
    
    private final native void handleServiceInitFailed(final long p0, final int p1);
    
    private final native void handleServiceUnavailable(final long p0);
    
    private final native void handleStateChanged(final long p0, final int p1, final int p2);
    
    private final native void handleTouchEvent(final long p0, final long p1, final int p2, final float p3, final float p4);
    
    @UsedByNative
    public final void close() {
        synchronized (this) {
            this.closed = true;
        }
    }
    
    @Override
    public final void onControllerEventPacket(final ControllerEventPacket controllerEventPacket) {
        final int n = 0;
        synchronized (this) {
            if (!this.closed) {
                for (int n2 = 0; !this.closed && n2 < controllerEventPacket.getAccelEventCount(); ++n2) {
                    final ControllerAccelEvent accelEvent = controllerEventPacket.getAccelEvent(n2);
                    this.handleAccelEvent(this.userData, accelEvent.timestampNanos, accelEvent.x, accelEvent.y, accelEvent.z);
                }
                for (int n3 = 0; !this.closed && n3 < controllerEventPacket.getButtonEventCount(); ++n3) {
                    final ControllerButtonEvent buttonEvent = controllerEventPacket.getButtonEvent(n3);
                    this.handleButtonEvent(this.userData, buttonEvent.timestampNanos, buttonEvent.button, buttonEvent.down);
                }
                for (int n4 = 0; !this.closed && n4 < controllerEventPacket.getGyroEventCount(); ++n4) {
                    final ControllerGyroEvent gyroEvent = controllerEventPacket.getGyroEvent(n4);
                    this.handleGyroEvent(this.userData, gyroEvent.timestampNanos, gyroEvent.x, gyroEvent.y, gyroEvent.z);
                }
                int n5 = 0;
                while (true) {
                    while (!this.closed) {
                        int n6 = n;
                        if (n5 >= controllerEventPacket.getOrientationEventCount()) {
                            while (!this.closed && n6 < controllerEventPacket.getTouchEventCount()) {
                                final ControllerTouchEvent touchEvent = controllerEventPacket.getTouchEvent(n6);
                                this.handleTouchEvent(this.userData, touchEvent.timestampNanos, touchEvent.action, touchEvent.x, touchEvent.y);
                                ++n6;
                            }
                            return;
                        }
                        final ControllerOrientationEvent orientationEvent = controllerEventPacket.getOrientationEvent(n5);
                        this.handleOrientationEvent(this.userData, orientationEvent.timestampNanos, orientationEvent.qx, orientationEvent.qy, orientationEvent.qz, orientationEvent.qw);
                        ++n5;
                    }
                    int n6 = n;
                    continue;
                }
            }
        }
    }
    
    @Override
    public final void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) {
        synchronized (this) {
            if (!this.closed) {
                this.handleControllerRecentered(this.userData, controllerOrientationEvent.timestampNanos, controllerOrientationEvent.qx, controllerOrientationEvent.qy, controllerOrientationEvent.qz, controllerOrientationEvent.qw);
            }
        }
    }
    
    @Override
    public final void onControllerStateChanged(final int n, final int n2) {
        synchronized (this) {
            if (!this.closed) {
                this.handleStateChanged(this.userData, n, n2);
            }
        }
    }
    
    @Override
    public final void onServiceConnected(final int n) {
        synchronized (this) {
            if (!this.closed) {
                this.handleServiceConnected(this.userData, n);
            }
        }
    }
    
    @Override
    public final void onServiceDisconnected() {
        synchronized (this) {
            if (!this.closed) {
                this.handleServiceDisconnected(this.userData);
            }
        }
    }
    
    @Override
    public final void onServiceFailed() {
        synchronized (this) {
            if (!this.closed) {
                this.handleServiceFailed(this.userData);
            }
        }
    }
    
    @Override
    public final void onServiceInitFailed(final int n) {
        synchronized (this) {
            if (!this.closed) {
                this.handleServiceInitFailed(this.userData, n);
            }
        }
    }
    
    @Override
    public final void onServiceUnavailable() {
        synchronized (this) {
            if (!this.closed) {
                this.handleServiceUnavailable(this.userData);
            }
        }
    }
}
