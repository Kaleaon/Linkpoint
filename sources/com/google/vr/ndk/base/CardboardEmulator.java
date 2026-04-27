package com.google.vr.ndk.base;

import android.content.Context;
import com.google.vr.cardboard.ThreadUtils;
import com.google.vr.internal.controller.ServiceBridge;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;

class CardboardEmulator {
    private static final boolean DEBUG = false;
    private static final String TAG = CardboardEmulator.class.getSimpleName();
    private final ServiceBridge controllerServiceBridge;
    private boolean resumed;

    private static class ControllerCallbacks implements ServiceBridge.Callbacks {
        private final Runnable cardboardTriggerCallback;

        ControllerCallbacks(Runnable runnable) {
            this.cardboardTriggerCallback = runnable;
        }

        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) {
            for (int i = 0; i < controllerEventPacket.getButtonEventCount(); i++) {
                ControllerButtonEvent buttonEvent = controllerEventPacket.getButtonEvent(i);
                if (buttonEvent.down) {
                    switch (buttonEvent.button) {
                        case 1:
                        case 3:
                            ThreadUtils.runOnUiThread(this.cardboardTriggerCallback);
                            break;
                    }
                }
            }
        }

        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
        }

        public void onControllerStateChanged(int i, int i2) {
        }

        public void onServiceConnected(int i) {
        }

        public void onServiceDisconnected() {
        }

        public void onServiceFailed() {
        }

        public void onServiceInitFailed(int i) {
        }

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

    /* access modifiers changed from: protected */
    public ServiceBridge createServiceBridge(Context context, ServiceBridge.Callbacks callbacks) {
        return new ServiceBridge(context, callbacks);
    }

    public void onPause() {
        if (this.resumed) {
            this.resumed = false;
            this.controllerServiceBridge.requestUnbind();
        }
    }

    public void onResume() {
        if (!this.resumed) {
            this.resumed = true;
            this.controllerServiceBridge.requestBind();
        }
    }
}
