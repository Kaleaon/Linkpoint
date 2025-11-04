// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.cardboard.ThreadUtils;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import android.content.Context;
import com.google.vr.internal.controller.ServiceBridge;

class CardboardEmulator
{
    private static final boolean DEBUG = false;
    private static final String TAG;
    private final ServiceBridge controllerServiceBridge;
    private boolean resumed;
    
    static {
        TAG = CardboardEmulator.class.getSimpleName();
    }
    
    public CardboardEmulator(final Context context, final Runnable runnable) {
        (this.controllerServiceBridge = this.createServiceBridge(context, new ControllerCallbacks(runnable))).setOrientationEnabled(false);
        this.controllerServiceBridge.setGyroEnabled(false);
        this.controllerServiceBridge.setAccelEnabled(false);
        this.controllerServiceBridge.setTouchEnabled(false);
        this.controllerServiceBridge.setGesturesEnabled(false);
    }
    
    protected ServiceBridge createServiceBridge(final Context context, final ServiceBridge.Callbacks callbacks) {
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
    
    private static class ControllerCallbacks implements Callbacks
    {
        private final Runnable cardboardTriggerCallback;
        
        ControllerCallbacks(final Runnable cardboardTriggerCallback) {
            this.cardboardTriggerCallback = cardboardTriggerCallback;
        }
        
        @Override
        public void onControllerEventPacket(final ControllerEventPacket controllerEventPacket) {
            for (int i = 0; i < controllerEventPacket.getButtonEventCount(); ++i) {
                final ControllerButtonEvent buttonEvent = controllerEventPacket.getButtonEvent(i);
                if (buttonEvent.down) {
                    switch (buttonEvent.button) {
                        case 1:
                        case 3: {
                            ThreadUtils.runOnUiThread(this.cardboardTriggerCallback);
                            break;
                        }
                    }
                }
            }
        }
        
        @Override
        public void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) {
        }
        
        @Override
        public void onControllerStateChanged(final int n, final int n2) {
        }
        
        @Override
        public void onServiceConnected(final int n) {
        }
        
        @Override
        public void onServiceDisconnected() {
        }
        
        @Override
        public void onServiceFailed() {
        }
        
        @Override
        public void onServiceInitFailed(final int n) {
        }
        
        @Override
        public void onServiceUnavailable() {
        }
    }
}
