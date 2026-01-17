package com.google.vr.ndk.base;

import android.content.Context;
import com.google.vr.cardboard.ThreadUtils;
import com.google.vr.internal.controller.ServiceBridge;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
/* loaded from: classes.dex */
class CardboardEmulator {
    private static final boolean DEBUG = false;
    private static final String TAG = CardboardEmulator.class.getSimpleName();
    private final ServiceBridge controllerServiceBridge;
    private boolean resumed;

    /* loaded from: classes.dex */
    private static class ControllerCallbacks implements ServiceBridge.Callbacks {
        private final Runnable cardboardTriggerCallback;

        ControllerCallbacks(Runnable runnable) {
            this.cardboardTriggerCallback = runnable;
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) {
            for (int i = 0; i < controllerEventPacket.getButtonEventCount(); i++) {
                ControllerButtonEvent buttonEvent = controllerEventPacket.getButtonEvent(i);
                if (buttonEvent.down) {
                    switch (buttonEvent.button) {
                        case 1:
                        case 3:
                            ThreadUtils.runOnUiThread(this.cardboardTriggerCallback);
                            continue;
                    }
                }
            }
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onControllerStateChanged(int i, int i2) {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onServiceConnected(int i) {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onServiceDisconnected() {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onServiceFailed() {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onServiceInitFailed(int i) {
        }

        @Override // com.google.vr.internal.controller.ServiceBridge.Callbacks
        public void onServiceUnavailable() {
        }
    }

    public CardboardEmulator(Context context, Runnable runnable) {
        this.controllerServiceBridge = createServiceBridge(context, new ControllerCallbacks(runnable));
        this.controllerServiceBridge.setOrientationEnabled(false);
        this.controllerServiceBridge.setGyroEnabled(false);
        this.controllerServiceBridge.setAccelEnabled(false);
        this.controllerServiceBridge.setTouchEnabled(false);
        this.controllerServiceBridge.setGesturesEnabled(false);
    }

    protected ServiceBridge createServiceBridge(Context context, ServiceBridge.Callbacks callbacks) {
        return new ServiceBridge(context, callbacks);
    }

    public void onPause() {
        if (this.resumed) {
            this.resumed = false;
            this.controllerServiceBridge.requestUnbind();
        }
    }

    public void onResume() {
        if (this.resumed) {
            return;
        }
        this.resumed = true;
        this.controllerServiceBridge.requestBind();
    }
}
