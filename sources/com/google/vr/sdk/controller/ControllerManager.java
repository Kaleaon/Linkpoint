package com.google.vr.sdk.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerEventPacket2;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerListenerOptions;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerPositionEvent;
import com.google.vr.vrcore.controller.api.ControllerServiceConsts;
import com.google.vr.vrcore.controller.api.ControllerStates;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
import com.google.vr.vrcore.controller.api.IControllerListener;
import com.google.vr.vrcore.controller.api.IControllerService;
import java.lang.ref.WeakReference;

public class ControllerManager {
    private static final boolean DEBUG = false;
    private static final String LISTENER_KEY = "com.google.vr.cardboard.controller.ControllerManager";
    public static final int SERVICE_TARGET_API_VERSION = 11;
    private static final String TAG = "ControllerManager";
    private final Intent bindIntent;
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public final Controller controller = new Controller(this);
    /* access modifiers changed from: private */
    public IControllerService controllerService;
    /* access modifiers changed from: private */
    public final Controller currentControllerState = new Controller((ControllerManager) null);
    private final InnerControllerListener innerControllerListener = new InnerControllerListener();
    /* access modifiers changed from: private */
    public final OuterControllerListener outerControllerListener = new OuterControllerListener(this.innerControllerListener);
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            int i = 5;
            boolean z = false;
            IControllerService unused = ControllerManager.this.controllerService = IControllerService.Stub.asInterface(iBinder);
            try {
                int initialize = ControllerManager.this.controllerService.initialize(11);
                switch (initialize) {
                    case 0:
                        i = 0;
                        break;
                    case 1:
                        i = 1;
                        break;
                    case 2:
                        i = 2;
                        break;
                }
                if (i != 0) {
                    Log.e(ControllerManager.TAG, String.format(".onServiceConnected %d, %d", new Object[]{Integer.valueOf(initialize), Integer.valueOf(i)}));
                } else {
                    ControllerManager.this.controllerService.registerListener(0, ControllerManager.LISTENER_KEY, ControllerManager.this.outerControllerListener);
                }
                try {
                    Controller access$300 = ControllerManager.this.currentControllerState;
                    if (VrCoreUtils.getVrCoreClientApiVersion(ControllerManager.this.context) < 8) {
                        z = true;
                    }
                    access$300.enableRecenterShim = z;
                } catch (VrCoreNotAvailableException e) {
                    Log.e(ControllerManager.TAG, "Unable to set Controller.enableRecenterShim: ", e);
                }
                ControllerManager.this.serviceEventListener.onApiStatusChanged(i);
            } catch (RemoteException e2) {
                Log.e(ControllerManager.TAG, "Initialization failed: ", e2);
                ControllerManager.this.serviceEventListener.onApiStatusChanged(3);
                ControllerManager.this.stop();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(ControllerManager.TAG, ".onServiceDisconnected");
            IControllerService unused = ControllerManager.this.controllerService = null;
            ControllerManager.this.serviceEventListener.onApiStatusChanged(3);
        }
    };
    /* access modifiers changed from: private */
    public final EventListener serviceEventListener;

    public static class ApiStatus {
        public static final int ERROR_CLIENT_OBSOLETE = 5;
        public static final int ERROR_MALFUNCTION = 6;
        public static final int ERROR_NOT_AUTHORIZED = 2;
        public static final int ERROR_SERVICE_OBSOLETE = 4;
        public static final int ERROR_UNAVAILABLE = 3;
        public static final int ERROR_UNSUPPORTED = 1;
        public static final int OK = 0;

        private ApiStatus() {
        }

        public static final String toString(int i) {
            switch (i) {
                case 0:
                    return "OK";
                case 1:
                    return "ERROR_UNSUPPORTED";
                case 2:
                    return "ERROR_NOT_AUTHORIZED";
                case 3:
                    return "ERROR_UNAVAILABLE";
                case 4:
                    return "ERROR_SERVICE_OBSOLETE";
                case 5:
                    return "ERROR_CLIENT_OBSOLETE";
                case 6:
                    return "ERROR_MALFUNCTION";
                default:
                    return new StringBuilder(58).append("[UNKNOWN CONTROLLER MANAGER CONNECTION STATE: ").append(i).append("]").toString();
            }
        }
    }

    public interface EventListener {
        void onApiStatusChanged(int i);

        void onRecentered();
    }

    private class InnerControllerListener {
        private InnerControllerListener() {
        }

        public void handleButtonEvent(ControllerButtonEvent controllerButtonEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerButtonEvent.timestampNanos;
                switch (controllerButtonEvent.button) {
                    case 1:
                        ControllerManager.this.currentControllerState.clickButtonState = controllerButtonEvent.down;
                        break;
                    case 2:
                        if (ControllerManager.this.currentControllerState.setHomeButtonState(controllerButtonEvent.down)) {
                            ControllerManager.this.serviceEventListener.onRecentered();
                            break;
                        }
                        break;
                    case 3:
                        ControllerManager.this.currentControllerState.appButtonState = controllerButtonEvent.down;
                        break;
                    case 5:
                        ControllerManager.this.currentControllerState.volumeUpButtonState = controllerButtonEvent.down;
                        break;
                    case 6:
                        ControllerManager.this.currentControllerState.volumeDownButtonState = controllerButtonEvent.down;
                        break;
                    default:
                        Log.w(ControllerManager.TAG, String.format("onControllerButtonEvent didn't handle %d", new Object[]{Integer.valueOf(controllerButtonEvent.button)}));
                        break;
                }
            }
        }

        public void handleOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerOrientationEvent.timestampNanos;
                ControllerManager.this.currentControllerState.setOrientationInSensorSpace(controllerOrientationEvent.qx, controllerOrientationEvent.qy, controllerOrientationEvent.qz, controllerOrientationEvent.qw);
                ControllerManager.this.controller.notifyUpdate();
            }
        }

        public void handlePositionEvent(ControllerPositionEvent controllerPositionEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerPositionEvent.timestampNanos;
                ControllerManager.this.currentControllerState.position[0] = controllerPositionEvent.x;
                ControllerManager.this.currentControllerState.position[1] = controllerPositionEvent.y;
                ControllerManager.this.currentControllerState.position[2] = controllerPositionEvent.z;
                ControllerManager.this.controller.notifyUpdate();
            }
        }

        public void handleTouchEvent(ControllerTouchEvent controllerTouchEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerTouchEvent.timestampNanos;
                ControllerManager.this.currentControllerState.touch.x = controllerTouchEvent.x;
                ControllerManager.this.currentControllerState.touch.y = controllerTouchEvent.y;
                switch (controllerTouchEvent.action) {
                    case 0:
                    case 3:
                    case 4:
                        ControllerManager.this.currentControllerState.isTouching = false;
                        break;
                    case 1:
                    case 2:
                        ControllerManager.this.currentControllerState.isTouching = true;
                        break;
                    default:
                        Log.w(ControllerManager.TAG, String.format(".handleTouchEvent didn't handle %d", new Object[]{Integer.valueOf(controllerTouchEvent.action)}));
                        break;
                }
            }
        }

        public void notifyConnectionStateChange(int i) {
            ControllerManager.this.controller.notifyConnectionStateChange(i);
        }

        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
            handleOrientationEvent(controllerOrientationEvent);
            ControllerManager.this.serviceEventListener.onRecentered();
        }
    }

    private static class OuterControllerListener extends IControllerListener.Stub {
        private final WeakReference<InnerControllerListener> inner;

        public OuterControllerListener(InnerControllerListener innerControllerListener) {
            this.inner = new WeakReference<>(innerControllerListener);
        }

        private void handleEventsBackwardCompatible(ControllerEventPacket controllerEventPacket, InnerControllerListener innerControllerListener) {
            for (int i = 0; i < controllerEventPacket.getButtonEventCount(); i++) {
                innerControllerListener.handleButtonEvent(controllerEventPacket.getButtonEvent(i));
            }
            for (int i2 = 0; i2 < controllerEventPacket.getOrientationEventCount(); i2++) {
                innerControllerListener.handleOrientationEvent(controllerEventPacket.getOrientationEvent(i2));
            }
            for (int i3 = 0; i3 < controllerEventPacket.getTouchEventCount(); i3++) {
                innerControllerListener.handleTouchEvent(controllerEventPacket.getTouchEvent(i3));
            }
        }

        public void deprecatedOnControllerAccelEvent(ControllerAccelEvent controllerAccelEvent) {
        }

        public void deprecatedOnControllerButtonEvent(ControllerButtonEvent controllerButtonEvent) {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleButtonEvent(controllerButtonEvent);
            }
        }

        public boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) {
            return true;
        }

        public void deprecatedOnControllerGyroEvent(ControllerGyroEvent controllerGyroEvent) {
        }

        public void deprecatedOnControllerOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleOrientationEvent(controllerOrientationEvent);
            }
        }

        public void deprecatedOnControllerTouchEvent(ControllerTouchEvent controllerTouchEvent) {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleTouchEvent(controllerTouchEvent);
            }
        }

        public int getApiVersion() throws RemoteException {
            return 11;
        }

        public ControllerListenerOptions getOptions() throws RemoteException {
            return null;
        }

        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                handleEventsBackwardCompatible(controllerEventPacket, innerControllerListener);
                controllerEventPacket.recycle();
            }
        }

        public void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                handleEventsBackwardCompatible(controllerEventPacket2, innerControllerListener);
                for (int i = 0; i < controllerEventPacket2.getPositionEventCount(); i++) {
                    innerControllerListener.handlePositionEvent(controllerEventPacket2.getPositionEvent(i));
                }
                controllerEventPacket2.recycle();
            }
        }

        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.onControllerRecentered(controllerOrientationEvent);
            }
        }

        public void onControllerStateChanged(int i, int i2) throws RemoteException {
            ControllerStates.toString(i2);
            InnerControllerListener innerControllerListener = (InnerControllerListener) this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.notifyConnectionStateChange(i2);
            }
        }
    }

    public ControllerManager(Context context2, EventListener eventListener) {
        this.context = context2;
        this.serviceEventListener = eventListener;
        this.bindIntent = new Intent(ControllerServiceConsts.BIND_INTENT_ACTION);
        this.bindIntent.setPackage("com.google.vr.vrcore");
    }

    public Controller getController() {
        return this.controller;
    }

    public void start() {
        if (this.controllerService == null && !this.context.bindService(this.bindIntent, this.serviceConnection, 1)) {
            this.serviceEventListener.onApiStatusChanged(3);
        }
    }

    public void stop() {
        if (this.controllerService != null) {
            try {
                this.controllerService.unregisterListener(LISTENER_KEY);
            } catch (RemoteException e) {
                String valueOf = String.valueOf(e);
                Log.w(TAG, new StringBuilder(String.valueOf(valueOf).length() + 27).append("unregisterListener failed: ").append(valueOf).toString());
            }
            this.context.unbindService(this.serviceConnection);
            this.controllerService = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateController(Controller controller2) {
        synchronized (this.currentControllerState) {
            if (controller2.timestamp != this.currentControllerState.timestamp) {
                controller2.setPublicState(this.currentControllerState);
            }
        }
    }
}
