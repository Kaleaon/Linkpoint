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
/* loaded from: classes.dex */
public class ControllerManager {
    private static final boolean DEBUG = false;
    private static final String LISTENER_KEY = "com.google.vr.cardboard.controller.ControllerManager";
    public static final int SERVICE_TARGET_API_VERSION = 11;
    private static final String TAG = "ControllerManager";
    private final Context context;
    private IControllerService controllerService;
    private final EventListener serviceEventListener;
    private final InnerControllerListener innerControllerListener = new InnerControllerListener();
    private final OuterControllerListener outerControllerListener = new OuterControllerListener(this.innerControllerListener);
    private final Controller currentControllerState = new Controller(null);
    private final Controller controller = new Controller(this);
    private final ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.google.vr.sdk.controller.ControllerManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            int i = 5;
            ControllerManager.this.controllerService = IControllerService.Stub.asInterface(iBinder);
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
                    Log.e(ControllerManager.TAG, String.format(".onServiceConnected %d, %d", Integer.valueOf(initialize), Integer.valueOf(i)));
                } else {
                    ControllerManager.this.controllerService.registerListener(0, ControllerManager.LISTENER_KEY, ControllerManager.this.outerControllerListener);
                }
                try {
                    ControllerManager.this.currentControllerState.enableRecenterShim = VrCoreUtils.getVrCoreClientApiVersion(ControllerManager.this.context) < 8;
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

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(ControllerManager.TAG, ".onServiceDisconnected");
            ControllerManager.this.controllerService = null;
            ControllerManager.this.serviceEventListener.onApiStatusChanged(3);
        }
    };
    private final Intent bindIntent = new Intent(ControllerServiceConsts.BIND_INTENT_ACTION);

    /* loaded from: classes.dex */
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

    /* loaded from: classes.dex */
    public interface EventListener {
        void onApiStatusChanged(int i);

        void onRecentered();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InnerControllerListener {
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
                    case 4:
                    default:
                        Log.w(ControllerManager.TAG, String.format("onControllerButtonEvent didn't handle %d", Integer.valueOf(controllerButtonEvent.button)));
                        break;
                    case 5:
                        ControllerManager.this.currentControllerState.volumeUpButtonState = controllerButtonEvent.down;
                        break;
                    case 6:
                        ControllerManager.this.currentControllerState.volumeDownButtonState = controllerButtonEvent.down;
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
                        Log.w(ControllerManager.TAG, String.format(".handleTouchEvent didn't handle %d", Integer.valueOf(controllerTouchEvent.action)));
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

    /* loaded from: classes.dex */
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void deprecatedOnControllerAccelEvent(ControllerAccelEvent controllerAccelEvent) {
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void deprecatedOnControllerButtonEvent(ControllerButtonEvent controllerButtonEvent) {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleButtonEvent(controllerButtonEvent);
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) {
            return true;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void deprecatedOnControllerGyroEvent(ControllerGyroEvent controllerGyroEvent) {
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void deprecatedOnControllerOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleOrientationEvent(controllerOrientationEvent);
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void deprecatedOnControllerTouchEvent(ControllerTouchEvent controllerTouchEvent) {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleTouchEvent(controllerTouchEvent);
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public int getApiVersion() throws RemoteException {
            return 11;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public ControllerListenerOptions getOptions() throws RemoteException {
            return null;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                handleEventsBackwardCompatible(controllerEventPacket, innerControllerListener);
                controllerEventPacket.recycle();
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                handleEventsBackwardCompatible(controllerEventPacket2, innerControllerListener);
                for (int i = 0; i < controllerEventPacket2.getPositionEventCount(); i++) {
                    innerControllerListener.handlePositionEvent(controllerEventPacket2.getPositionEvent(i));
                }
                controllerEventPacket2.recycle();
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.onControllerRecentered(controllerOrientationEvent);
            }
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerStateChanged(int i, int i2) throws RemoteException {
            ControllerStates.toString(i2);
            InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.notifyConnectionStateChange(i2);
            }
        }
    }

    public ControllerManager(Context context, EventListener eventListener) {
        this.context = context;
        this.serviceEventListener = eventListener;
        this.bindIntent.setPackage("com.google.vr.vrcore");
    }

    public Controller getController() {
        return this.controller;
    }

    public void start() {
        if (this.controllerService != null || this.context.bindService(this.bindIntent, this.serviceConnection, 1)) {
            return;
        }
        this.serviceEventListener.onApiStatusChanged(3);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateController(Controller controller) {
        synchronized (this.currentControllerState) {
            if (controller.timestamp != this.currentControllerState.timestamp) {
                controller.setPublicState(this.currentControllerState);
            }
        }
    }
}
