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
/* loaded from: classes.dex */
public class ServiceBridge implements ServiceConnection {
    private static final boolean DEBUG = false;
    public static final int FLAG_SUPPORTS_RECENTER = 1;
    static final String LISTENER_KEY = "com.google.vr.internal.controller.LISTENER_KEY";
    private static final int MIN_SERVICE_API_FOR_RECENTERING = 8;
    private static final String TAG = ServiceBridge.class.getSimpleName();
    static final int TARGET_SERVICE_API_VERSION = 10;
    private final Callbacks callbacks;
    private final Context context;
    private boolean isBound;
    private IControllerService service;
    private final ControllerListenerOptions options = new ControllerListenerOptions();
    private final Runnable bindRunnable = new Runnable() { // from class: com.google.vr.internal.controller.ServiceBridge.1
        @Override // java.lang.Runnable
        public void run() {
            ServiceBridge.this.doBind();
        }
    };
    private final Runnable unbindRunnable = new Runnable() { // from class: com.google.vr.internal.controller.ServiceBridge.2
        @Override // java.lang.Runnable
        public void run() {
            ServiceBridge.this.doUnbind();
        }
    };
    private final IControllerListener controllerListener = new IControllerListener.Stub() { // from class: com.google.vr.internal.controller.ServiceBridge.3
        @Override // com.google.vr.vrcore.controller.api.IControllerListener
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) {
            return true;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
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

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public int getApiVersion() throws RemoteException {
            return 10;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public ControllerListenerOptions getOptions() throws RemoteException {
            return ServiceBridge.this.options;
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket);
            controllerEventPacket.recycle();
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket2);
            controllerEventPacket2.recycle();
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) {
            ServiceBridge.this.callbacks.onControllerRecentered(controllerOrientationEvent);
        }

        @Override // com.google.vr.vrcore.controller.api.IControllerListener
        public void onControllerStateChanged(int i, int i2) throws RemoteException {
            ServiceBridge.this.callbacks.onControllerStateChanged(i, i2);
        }
    };
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @UsedByNative
    /* loaded from: classes.dex */
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
    public ServiceBridge(Context context, Callbacks callbacks) {
        this.context = context.getApplicationContext();
        this.callbacks = callbacks;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doBind() {
        ensureOnMainThread();
        if (this.isBound) {
            Log.w(TAG, "Service is already bound.");
            return;
        }
        Intent intent = new Intent(ControllerServiceConsts.BIND_INTENT_ACTION);
        intent.setPackage("com.google.vr.vrcore");
        if (this.context.bindService(intent, this, 1)) {
            this.isBound = true;
            return;
        }
        Log.w(TAG, "Bind failed. Service is not available.");
        this.callbacks.onServiceUnavailable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doUnbind() {
        ensureOnMainThread();
        if (!this.isBound) {
            Log.w(TAG, "Service is already unbound.");
            return;
        }
        unregisterListener();
        this.context.unbindService(this);
        this.isBound = false;
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

    protected int getVrCoreClientApiVersion() {
        try {
            return VrCoreUtils.getVrCoreClientApiVersion(this.context);
        } catch (VrCoreNotAvailableException e) {
            Log.w(TAG, "VrCore not available.", e);
            return -1;
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        int i = 0;
        ensureOnMainThread();
        this.service = IControllerService.Stub.asInterface(iBinder);
        try {
            int initialize = this.service.initialize(10);
            if (initialize != 0) {
                String str = TAG;
                String valueOf = String.valueOf(ControllerInitResults.toString(initialize));
                Log.e(str, valueOf.length() == 0 ? new String("initialize() returned error: ") : "initialize() returned error: ".concat(valueOf));
                this.callbacks.onServiceInitFailed(initialize);
                doUnbind();
                return;
            }
            int vrCoreClientApiVersion = getVrCoreClientApiVersion();
            if (vrCoreClientApiVersion < 0) {
                Log.w(TAG, "Failed to get VrCore client API version.");
                this.callbacks.onServiceFailed();
                doUnbind();
                return;
            }
            if (vrCoreClientApiVersion < 8) {
                Log.w(TAG, new StringBuilder(62).append("Recentering is not supported by VrCore API version ").append(vrCoreClientApiVersion).toString());
            } else {
                i = 1;
            }
            this.callbacks.onServiceConnected(i);
            try {
                if (this.service.registerListener(0, LISTENER_KEY, this.controllerListener)) {
                    return;
                }
                Log.w(TAG, "Failed to register listener.");
                this.callbacks.onServiceFailed();
                doUnbind();
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.w(TAG, "RemoteException while registering listener.");
                this.callbacks.onServiceFailed();
                doUnbind();
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
            Log.e(TAG, "Failed to call initialize() on controller service (RemoteException).");
            this.callbacks.onServiceFailed();
            doUnbind();
        }
    }

    @Override // android.content.ServiceConnection
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
