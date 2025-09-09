package com.google.vr.internal.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import com.google.vr.cardboard.annotations.UsedByNative;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerEventPacket2;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerInitResults;
import com.google.vr.vrcore.controller.api.ControllerListenerOptions;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerServiceConsts;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
import com.google.vr.vrcore.controller.api.IControllerListener;
import com.google.vr.vrcore.controller.api.IControllerService;

@UsedByNative
public class ServiceBridge implements ServiceConnection {
    private static final boolean DEBUG = false;
    public static final int FLAG_SUPPORTS_RECENTER = 1;
    static final String LISTENER_KEY = "com.google.vr.internal.controller.LISTENER_KEY";
    private static final int MIN_SERVICE_API_FOR_RECENTERING = 8;
    private static final String TAG = ServiceBridge.class.getSimpleName();
    static final int TARGET_SERVICE_API_VERSION = 10;
    private final Runnable bindRunnable = new Runnable() {
        public void run() {
            ServiceBridge.this.doBind();
        }
    };
    /* access modifiers changed from: private */
    public final Callbacks callbacks;
    private final Context context;
    private final IControllerListener controllerListener = new IControllerListener.Stub() {
        public void deprecatedOnControllerAccelEvent(ControllerAccelEvent controllerAccelEvent) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            Parcel obtain2 = Parcel.obtain();
            controllerAccelEvent.writeToParcel(obtain2, 0);
            obtain2.setDataPosition(0);
            obtain.addAccelEvent().readFromParcel(obtain2);
            ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
            obtain.recycle();
            obtain2.recycle();
        }

        public void deprecatedOnControllerButtonEvent(ControllerButtonEvent controllerButtonEvent) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            Parcel obtain2 = Parcel.obtain();
            controllerButtonEvent.writeToParcel(obtain2, 0);
            obtain2.setDataPosition(0);
            obtain.addButtonEvent().readFromParcel(obtain2);
            ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
            obtain.recycle();
            obtain2.recycle();
        }

        public boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) {
            return true;
        }

        public void deprecatedOnControllerGyroEvent(ControllerGyroEvent controllerGyroEvent) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            Parcel obtain2 = Parcel.obtain();
            controllerGyroEvent.writeToParcel(obtain2, 0);
            obtain2.setDataPosition(0);
            obtain.addGyroEvent().readFromParcel(obtain2);
            ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
            obtain.recycle();
            obtain2.recycle();
        }

        public void deprecatedOnControllerOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            Parcel obtain2 = Parcel.obtain();
            controllerOrientationEvent.writeToParcel(obtain2, 0);
            obtain2.setDataPosition(0);
            obtain.addOrientationEvent().readFromParcel(obtain2);
            ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
            obtain.recycle();
            obtain2.recycle();
        }

        public void deprecatedOnControllerTouchEvent(ControllerTouchEvent controllerTouchEvent) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            Parcel obtain2 = Parcel.obtain();
            controllerTouchEvent.writeToParcel(obtain2, 0);
            obtain2.setDataPosition(0);
            obtain.addTouchEvent().readFromParcel(obtain2);
            ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
            obtain.recycle();
            obtain2.recycle();
        }

        public int getApiVersion() throws RemoteException {
            return 10;
        }

        public ControllerListenerOptions getOptions() throws RemoteException {
            return ServiceBridge.this.options;
        }

        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket);
            controllerEventPacket.recycle();
        }

        public void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket2);
            controllerEventPacket2.recycle();
        }

        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
            ServiceBridge.this.callbacks.onControllerRecentered(controllerOrientationEvent);
        }

        public void onControllerStateChanged(int i, int i2) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerStateChanged(i, i2);
        }
    };
    private boolean isBound;
    private final Handler mainThreadHandler;
    /* access modifiers changed from: private */
    public final ControllerListenerOptions options = new ControllerListenerOptions();
    private IControllerService service;
    private final Runnable unbindRunnable = new Runnable() {
        public void run() {
            ServiceBridge.this.doUnbind();
        }
    };

    @UsedByNative
    public interface Callbacks {
        void onControllerEventPacket(ControllerEventPacket controllerEventPacket);

        void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent);

        void onControllerStateChanged(int i, int i2);

        void onServiceConnected(int i);

        void onServiceDisconnected();

        void onServiceFailed();

        void onServiceInitFailed(int i);

        void onServiceUnavailable();
    }

    @UsedByNative
    public ServiceBridge(Context context2, Callbacks callbacks2) {
        this.context = context2.getApplicationContext();
        this.callbacks = callbacks2;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    /* access modifiers changed from: private */
    public void doBind() {
        ensureOnMainThread();
        if (!this.isBound) {
            Intent intent = new Intent(ControllerServiceConsts.BIND_INTENT_ACTION);
            intent.setPackage("com.google.vr.vrcore");
            if (this.context.bindService(intent, this, 1)) {
                this.isBound = true;
                return;
            }
            Log.w(TAG, "Bind failed. Service is not available.");
            this.callbacks.onServiceUnavailable();
            return;
        }
        Log.w(TAG, "Service is already bound.");
    }

    /* access modifiers changed from: private */
    public void doUnbind() {
        ensureOnMainThread();
        if (this.isBound) {
            unregisterListener();
            this.context.unbindService(this);
            this.isBound = false;
            return;
        }
        Log.w(TAG, "Service is already unbound.");
    }

    private void ensureOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("This should be running on the main thread.");
        }
    }

    private void unregisterListener() {
        if (this.service != null) {
            try {
                this.service.unregisterListener(LISTENER_KEY);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.w(TAG, "RemoteException while unregistering listener.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getVrCoreClientApiVersion() {
        try {
            return VrCoreUtils.getVrCoreClientApiVersion(this.context);
        } catch (VrCoreNotAvailableException e) {
            Log.w(TAG, "VrCore not available.", e);
            return -1;
        }
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        int i = 0;
        ensureOnMainThread();
        this.service = IControllerService.Stub.asInterface(iBinder);
        try {
            int initialize = this.service.initialize(10);
            if (initialize == 0) {
                int vrCoreClientApiVersion = getVrCoreClientApiVersion();
                if (vrCoreClientApiVersion >= 0) {
                    if (vrCoreClientApiVersion < 8) {
                        Log.w(TAG, new StringBuilder(62).append("Recentering is not supported by VrCore API version ").append(vrCoreClientApiVersion).toString());
                    } else {
                        i = 1;
                    }
                    this.callbacks.onServiceConnected(i);
                    try {
                        if (!this.service.registerListener(0, LISTENER_KEY, this.controllerListener)) {
                            Log.w(TAG, "Failed to register listener.");
                            this.callbacks.onServiceFailed();
                            doUnbind();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.w(TAG, "RemoteException while registering listener.");
                        this.callbacks.onServiceFailed();
                        doUnbind();
                    }
                } else {
                    Log.w(TAG, "Failed to get VrCore client API version.");
                    this.callbacks.onServiceFailed();
                    doUnbind();
                }
            } else {
                String str = TAG;
                String valueOf = String.valueOf(ControllerInitResults.toString(initialize));
                Log.e(str, valueOf.length() == 0 ? new String("initialize() returned error: ") : "initialize() returned error: ".concat(valueOf));
                this.callbacks.onServiceInitFailed(initialize);
                doUnbind();
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
            Log.e(TAG, "Failed to call initialize() on controller service (RemoteException).");
            this.callbacks.onServiceFailed();
            doUnbind();
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        ensureOnMainThread();
        this.service = null;
        this.callbacks.onServiceDisconnected();
    }

    @UsedByNative
    public void requestBind() {
        this.mainThreadHandler.post(this.bindRunnable);
    }

    @UsedByNative
    public void requestUnbind() {
        this.mainThreadHandler.post(this.unbindRunnable);
    }

    @UsedByNative
    public void setAccelEnabled(boolean z) {
        this.options.enableAccel = z;
    }

    @UsedByNative
    public void setGesturesEnabled(boolean z) {
        this.options.enableGestures = z;
    }

    @UsedByNative
    public void setGyroEnabled(boolean z) {
        this.options.enableGyro = z;
    }

    @UsedByNative
    public void setOrientationEnabled(boolean z) {
        this.options.enableOrientation = z;
    }

    @UsedByNative
    public void setTouchEnabled(boolean z) {
        this.options.enableTouch = z;
    }
}
