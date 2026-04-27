// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.internal.controller;

import com.google.vr.vrcore.controller.api.ControllerInitResults;
import android.os.IBinder;
import android.content.ComponentName;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import android.content.Intent;
import android.util.Log;
import android.os.Looper;
import com.google.vr.vrcore.controller.api.ControllerEventPacket2;
import android.os.RemoteException;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import android.os.Parcel;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.IControllerService;
import com.google.vr.vrcore.controller.api.ControllerListenerOptions;
import android.os.Handler;
import com.google.vr.vrcore.controller.api.IControllerListener;
import android.content.Context;
import com.google.vr.cardboard.annotations.UsedByNative;
import android.content.ServiceConnection;

@UsedByNative
public class ServiceBridge implements ServiceConnection
{
    private static final boolean DEBUG = false;
    public static final int FLAG_SUPPORTS_RECENTER = 1;
    static final String LISTENER_KEY = "com.google.vr.internal.controller.LISTENER_KEY";
    private static final int MIN_SERVICE_API_FOR_RECENTERING = 8;
    private static final String TAG;
    static final int TARGET_SERVICE_API_VERSION = 10;
    private final Runnable bindRunnable;
    private final Callbacks callbacks;
    private final Context context;
    private final IControllerListener controllerListener;
    private boolean isBound;
    private final Handler mainThreadHandler;
    private final ControllerListenerOptions options;
    private IControllerService service;
    private final Runnable unbindRunnable;
    
    static {
        TAG = ServiceBridge.class.getSimpleName();
    }
    
    @UsedByNative
    public ServiceBridge(final Context context, final Callbacks callbacks) {
        this.options = new ControllerListenerOptions();
        this.bindRunnable = new Runnable() {
            @Override
            public void run() {
                ServiceBridge.this.doBind();
            }
        };
        this.unbindRunnable = new Runnable() {
            @Override
            public void run() {
                ServiceBridge.this.doUnbind();
            }
        };
        this.controllerListener = new IControllerListener.Stub() {
            public void deprecatedOnControllerAccelEvent(final ControllerAccelEvent controllerAccelEvent) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                final Parcel obtain2 = Parcel.obtain();
                controllerAccelEvent.writeToParcel(obtain2, 0);
                obtain2.setDataPosition(0);
                obtain.addAccelEvent().readFromParcel(obtain2);
                ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
                obtain.recycle();
                obtain2.recycle();
            }
            
            public void deprecatedOnControllerButtonEvent(final ControllerButtonEvent controllerButtonEvent) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                final Parcel obtain2 = Parcel.obtain();
                controllerButtonEvent.writeToParcel(obtain2, 0);
                obtain2.setDataPosition(0);
                obtain.addButtonEvent().readFromParcel(obtain2);
                ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
                obtain.recycle();
                obtain2.recycle();
            }
            
            public boolean deprecatedOnControllerButtonEventV1(final ControllerButtonEvent controllerButtonEvent) {
                return true;
            }
            
            public void deprecatedOnControllerGyroEvent(final ControllerGyroEvent controllerGyroEvent) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                final Parcel obtain2 = Parcel.obtain();
                controllerGyroEvent.writeToParcel(obtain2, 0);
                obtain2.setDataPosition(0);
                obtain.addGyroEvent().readFromParcel(obtain2);
                ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
                obtain.recycle();
                obtain2.recycle();
            }
            
            public void deprecatedOnControllerOrientationEvent(final ControllerOrientationEvent controllerOrientationEvent) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                final Parcel obtain2 = Parcel.obtain();
                controllerOrientationEvent.writeToParcel(obtain2, 0);
                obtain2.setDataPosition(0);
                obtain.addOrientationEvent().readFromParcel(obtain2);
                ServiceBridge.this.callbacks.onControllerEventPacket(obtain);
                obtain.recycle();
                obtain2.recycle();
            }
            
            public void deprecatedOnControllerTouchEvent(final ControllerTouchEvent controllerTouchEvent) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                final Parcel obtain2 = Parcel.obtain();
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
            
            public void onControllerEventPacket(final ControllerEventPacket controllerEventPacket) throws RemoteException {
                ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket);
                controllerEventPacket.recycle();
            }
            
            public void onControllerEventPacket2(final ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
                ServiceBridge.this.callbacks.onControllerEventPacket(controllerEventPacket2);
                controllerEventPacket2.recycle();
            }
            
            public void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) {
                ServiceBridge.this.callbacks.onControllerRecentered(controllerOrientationEvent);
            }
            
            public void onControllerStateChanged(final int n, final int n2) throws RemoteException {
                ServiceBridge.this.callbacks.onControllerStateChanged(n, n2);
            }
        };
        this.context = context.getApplicationContext();
        this.callbacks = callbacks;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }
    
    private void doBind() {
        this.ensureOnMainThread();
        if (this.isBound) {
            Log.w(ServiceBridge.TAG, "Service is already bound.");
            return;
        }
        final Intent intent = new Intent("com.google.vr.vrcore.controller.BIND");
        intent.setPackage("com.google.vr.vrcore");
        if (this.context.bindService(intent, (ServiceConnection)this, 1)) {
            this.isBound = true;
            return;
        }
        Log.w(ServiceBridge.TAG, "Bind failed. Service is not available.");
        this.callbacks.onServiceUnavailable();
    }
    
    private void doUnbind() {
        this.ensureOnMainThread();
        if (this.isBound) {
            this.unregisterListener();
            this.context.unbindService((ServiceConnection)this);
            this.isBound = false;
            return;
        }
        Log.w(ServiceBridge.TAG, "Service is already unbound.");
    }
    
    private void ensureOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return;
        }
        throw new IllegalStateException("This should be running on the main thread.");
    }
    
    private void unregisterListener() {
        if (this.service == null) {
            return;
        }
        try {
            this.service.unregisterListener("com.google.vr.internal.controller.LISTENER_KEY");
        }
        catch (final RemoteException ex) {
            ex.printStackTrace();
            Log.w(ServiceBridge.TAG, "RemoteException while unregistering listener.");
        }
    }
    
    protected int getVrCoreClientApiVersion() {
        try {
            return VrCoreUtils.getVrCoreClientApiVersion(this.context);
        }
        catch (final VrCoreNotAvailableException ex) {
            Log.w(ServiceBridge.TAG, "VrCore not available.", (Throwable)ex);
            return -1;
        }
    }
    
    public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
        int n = 0;
        this.ensureOnMainThread();
        this.service = IControllerService.Stub.asInterface(binder);
    Label_0171_Outer:
        while (true) {
            Label_0232: {
                try {
                    final int initialize = this.service.initialize(10);
                    if (initialize != 0) {
                        break Label_0171_Outer;
                    }
                    final int vrCoreClientApiVersion = this.getVrCoreClientApiVersion();
                    if (vrCoreClientApiVersion < 0) {
                        break Label_0171_Outer;
                    }
                    if (vrCoreClientApiVersion >= 8) {
                        break Label_0171_Outer;
                    }
                    Log.w(ServiceBridge.TAG, new StringBuilder(62).append("Recentering is not supported by VrCore API version ").append(vrCoreClientApiVersion).toString());
                    this.callbacks.onServiceConnected(n);
                    final ServiceBridge serviceBridge = this;
                    final IControllerService controllerService = serviceBridge.service;
                    final int n2 = 0;
                    final String s = "com.google.vr.internal.controller.LISTENER_KEY";
                    final ServiceBridge serviceBridge2 = this;
                    final IControllerListener controllerListener = serviceBridge2.controllerListener;
                    final boolean registerListener = controllerService.registerListener(n2, s, controllerListener);
                    final boolean registerListener2 = registerListener;
                    if (registerListener2) {
                        return;
                    }
                    break Label_0232;
                }
                catch (final RemoteException ex) {
                    ex.printStackTrace();
                    Log.e(ServiceBridge.TAG, "Failed to call initialize() on controller service (RemoteException).");
                    this.callbacks.onServiceFailed();
                    this.doUnbind();
                    return;
                }
                try {
                    final ServiceBridge serviceBridge = this;
                    final IControllerService controllerService = serviceBridge.service;
                    final int n2 = 0;
                    final String s = "com.google.vr.internal.controller.LISTENER_KEY";
                    final ServiceBridge serviceBridge2 = this;
                    final IControllerListener controllerListener = serviceBridge2.controllerListener;
                    final boolean registerListener2;
                    final boolean registerListener = registerListener2 = controllerService.registerListener(n2, s, controllerListener);
                    if (registerListener2) {
                        return;
                    }
                    Log.w(ServiceBridge.TAG, "Failed to register listener.");
                    this.callbacks.onServiceFailed();
                    this.doUnbind();
                    return;
                    final String tag = ServiceBridge.TAG;
                    final int initialize;
                    final String value = String.valueOf(ControllerInitResults.toString(initialize));
                    iftrue(Label_0193:)(value.length() != 0);
                    while (true) {
                        Block_12: {
                            break Block_12;
                            n = 1;
                            continue Label_0171_Outer;
                            final String concat;
                            Log.e(tag, concat);
                            this.callbacks.onServiceInitFailed(initialize);
                            this.doUnbind();
                            return;
                            Log.w(ServiceBridge.TAG, "Failed to get VrCore client API version.");
                            this.callbacks.onServiceFailed();
                            this.doUnbind();
                            return;
                        }
                        String concat = new String("initialize() returned error: ");
                        continue;
                        Label_0193: {
                            concat = "initialize() returned error: ".concat(value);
                        }
                        continue;
                    }
                }
                catch (final RemoteException ex2) {
                    ex2.printStackTrace();
                    Log.w(ServiceBridge.TAG, "RemoteException while registering listener.");
                    this.callbacks.onServiceFailed();
                    this.doUnbind();
                }
            }
            break;
        }
    }
    
    public void onServiceDisconnected(final ComponentName componentName) {
        this.ensureOnMainThread();
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
    public void setAccelEnabled(final boolean enableAccel) {
        this.options.enableAccel = enableAccel;
    }
    
    @UsedByNative
    public void setGesturesEnabled(final boolean enableGestures) {
        this.options.enableGestures = enableGestures;
    }
    
    @UsedByNative
    public void setGyroEnabled(final boolean enableGyro) {
        this.options.enableGyro = enableGyro;
    }
    
    @UsedByNative
    public void setOrientationEnabled(final boolean enableOrientation) {
        this.options.enableOrientation = enableOrientation;
    }
    
    @UsedByNative
    public void setTouchEnabled(final boolean enableTouch) {
        this.options.enableTouch = enableTouch;
    }
    
    @UsedByNative
    public interface Callbacks
    {
        void onControllerEventPacket(final ControllerEventPacket p0);
        
        void onControllerRecentered(final ControllerOrientationEvent p0);
        
        void onControllerStateChanged(final int p0, final int p1);
        
        void onServiceConnected(final int p0);
        
        void onServiceDisconnected();
        
        void onServiceFailed();
        
        void onServiceInitFailed(final int p0);
        
        void onServiceUnavailable();
    }
}
