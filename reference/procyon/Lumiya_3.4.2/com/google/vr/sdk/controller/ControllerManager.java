// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.controller;

import com.google.vr.vrcore.controller.api.ControllerStates;
import com.google.vr.vrcore.controller.api.ControllerEventPacket2;
import com.google.vr.vrcore.controller.api.ControllerListenerOptions;
import com.google.vr.vrcore.controller.api.ControllerGyroEvent;
import com.google.vr.vrcore.controller.api.ControllerAccelEvent;
import com.google.vr.vrcore.controller.api.ControllerEventPacket;
import java.lang.ref.WeakReference;
import com.google.vr.vrcore.controller.api.IControllerListener;
import com.google.vr.vrcore.controller.api.ControllerTouchEvent;
import com.google.vr.vrcore.controller.api.ControllerPositionEvent;
import com.google.vr.vrcore.controller.api.ControllerOrientationEvent;
import com.google.vr.vrcore.controller.api.ControllerButtonEvent;
import android.os.RemoteException;
import android.util.Log;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import com.google.vr.vrcore.controller.api.IControllerService;
import android.content.Context;
import android.content.Intent;

public class ControllerManager
{
    private static final boolean DEBUG = false;
    private static final String LISTENER_KEY = "com.google.vr.cardboard.controller.ControllerManager";
    public static final int SERVICE_TARGET_API_VERSION = 11;
    private static final String TAG = "ControllerManager";
    private final Intent bindIntent;
    private final Context context;
    private final Controller controller;
    private IControllerService controllerService;
    private final Controller currentControllerState;
    private final InnerControllerListener innerControllerListener;
    private final OuterControllerListener outerControllerListener;
    private final ServiceConnection serviceConnection;
    private final EventListener serviceEventListener;
    
    public ControllerManager(final Context context, final EventListener serviceEventListener) {
        this.innerControllerListener = new InnerControllerListener();
        this.outerControllerListener = new OuterControllerListener(this.innerControllerListener);
        this.currentControllerState = new Controller(null);
        this.controller = new Controller(this);
        this.serviceConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName p0, final IBinder p1) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_3       
                //     2: iconst_0       
                //     3: istore          4
                //     5: aload_0        
                //     6: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //     9: aload_2        
                //    10: invokestatic    com/google/vr/vrcore/controller/api/IControllerService$Stub.asInterface:(Landroid/os/IBinder;)Lcom/google/vr/vrcore/controller/api/IControllerService;
                //    13: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$102:(Lcom/google/vr/sdk/controller/ControllerManager;Lcom/google/vr/vrcore/controller/api/IControllerService;)Lcom/google/vr/vrcore/controller/api/IControllerService;
                //    16: pop            
                //    17: aload_0        
                //    18: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //    21: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$100:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/vrcore/controller/api/IControllerService;
                //    24: bipush          11
                //    26: invokeinterface com/google/vr/vrcore/controller/api/IControllerService.initialize:(I)I
                //    31: istore          5
                //    33: iload_3        
                //    34: istore          6
                //    36: iload           5
                //    38: tableswitch {
                //                0: 151
                //                1: 157
                //                2: 163
                //                3: 71
                //          default: 68
                //        }
                //    68: iload_3        
                //    69: istore          6
                //    71: iload           6
                //    73: ifeq            169
                //    76: ldc             "ControllerManager"
                //    78: ldc             ".onServiceConnected %d, %d"
                //    80: iconst_2       
                //    81: anewarray       Ljava/lang/Object;
                //    84: dup            
                //    85: iconst_0       
                //    86: iload           5
                //    88: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //    91: aastore        
                //    92: dup            
                //    93: iconst_1       
                //    94: iload           6
                //    96: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //    99: aastore        
                //   100: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
                //   103: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
                //   106: pop            
                //   107: aload_0        
                //   108: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   111: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$300:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/sdk/controller/Controller;
                //   114: astore_1       
                //   115: aload_0        
                //   116: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   119: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$400:(Lcom/google/vr/sdk/controller/ControllerManager;)Landroid/content/Context;
                //   122: invokestatic    com/google/vr/vrcore/base/api/VrCoreUtils.getVrCoreClientApiVersion:(Landroid/content/Context;)I
                //   125: bipush          8
                //   127: if_icmplt       228
                //   130: aload_1        
                //   131: iload           4
                //   133: putfield        com/google/vr/sdk/controller/Controller.enableRecenterShim:Z
                //   136: aload_0        
                //   137: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   140: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$500:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/sdk/controller/ControllerManager$EventListener;
                //   143: iload           6
                //   145: invokeinterface com/google/vr/sdk/controller/ControllerManager$EventListener.onApiStatusChanged:(I)V
                //   150: return         
                //   151: iconst_0       
                //   152: istore          6
                //   154: goto            71
                //   157: iconst_1       
                //   158: istore          6
                //   160: goto            71
                //   163: iconst_2       
                //   164: istore          6
                //   166: goto            71
                //   169: aload_0        
                //   170: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   173: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$100:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/vrcore/controller/api/IControllerService;
                //   176: iconst_0       
                //   177: ldc             "com.google.vr.cardboard.controller.ControllerManager"
                //   179: aload_0        
                //   180: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   183: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$200:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/sdk/controller/ControllerManager$OuterControllerListener;
                //   186: invokeinterface com/google/vr/vrcore/controller/api/IControllerService.registerListener:(ILjava/lang/String;Lcom/google/vr/vrcore/controller/api/IControllerListener;)Z
                //   191: pop            
                //   192: goto            107
                //   195: astore_1       
                //   196: ldc             "ControllerManager"
                //   198: ldc             "Initialization failed: "
                //   200: aload_1        
                //   201: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
                //   204: pop            
                //   205: aload_0        
                //   206: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   209: invokestatic    com/google/vr/sdk/controller/ControllerManager.access$500:(Lcom/google/vr/sdk/controller/ControllerManager;)Lcom/google/vr/sdk/controller/ControllerManager$EventListener;
                //   212: iconst_3       
                //   213: invokeinterface com/google/vr/sdk/controller/ControllerManager$EventListener.onApiStatusChanged:(I)V
                //   218: aload_0        
                //   219: getfield        com/google/vr/sdk/controller/ControllerManager$1.this$0:Lcom/google/vr/sdk/controller/ControllerManager;
                //   222: invokevirtual   com/google/vr/sdk/controller/ControllerManager.stop:()V
                //   225: goto            150
                //   228: iconst_1       
                //   229: istore          4
                //   231: goto            130
                //   234: astore_1       
                //   235: ldc             "ControllerManager"
                //   237: ldc             "Unable to set Controller.enableRecenterShim: "
                //   239: aload_1        
                //   240: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
                //   243: pop            
                //   244: goto            136
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                                       
                //  -----  -----  -----  -----  -----------------------------------------------------------
                //  17     33     195    228    Landroid/os/RemoteException;
                //  76     107    195    228    Landroid/os/RemoteException;
                //  107    130    234    247    Lcom/google/vr/vrcore/base/api/VrCoreNotAvailableException;
                //  107    130    195    228    Landroid/os/RemoteException;
                //  130    136    234    247    Lcom/google/vr/vrcore/base/api/VrCoreNotAvailableException;
                //  130    136    195    228    Landroid/os/RemoteException;
                //  136    150    195    228    Landroid/os/RemoteException;
                //  169    192    195    228    Landroid/os/RemoteException;
                //  235    244    195    228    Landroid/os/RemoteException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0107:
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                Log.e("ControllerManager", ".onServiceDisconnected");
                ControllerManager.this.controllerService = null;
                ControllerManager.this.serviceEventListener.onApiStatusChanged(3);
            }
        };
        this.context = context;
        this.serviceEventListener = serviceEventListener;
        (this.bindIntent = new Intent("com.google.vr.vrcore.controller.BIND")).setPackage("com.google.vr.vrcore");
    }
    
    public Controller getController() {
        return this.controller;
    }
    
    public void start() {
        if (this.controllerService == null) {
            if (!this.context.bindService(this.bindIntent, this.serviceConnection, 1)) {
                this.serviceEventListener.onApiStatusChanged(3);
            }
        }
    }
    
    public void stop() {
        if (this.controllerService == null) {
            return;
        }
        while (true) {
            try {
                this.controllerService.unregisterListener("com.google.vr.cardboard.controller.ControllerManager");
                this.context.unbindService(this.serviceConnection);
                this.controllerService = null;
            }
            catch (final RemoteException obj) {
                final String value = String.valueOf(obj);
                Log.w("ControllerManager", new StringBuilder(String.valueOf(value).length() + 27).append("unregisterListener failed: ").append(value).toString());
                continue;
            }
            break;
        }
    }
    
    void updateController(final Controller controller) {
        synchronized (this.currentControllerState) {
            if (controller.timestamp != this.currentControllerState.timestamp) {
                controller.setPublicState(this.currentControllerState);
            }
        }
    }
    
    public static class ApiStatus
    {
        public static final int ERROR_CLIENT_OBSOLETE = 5;
        public static final int ERROR_MALFUNCTION = 6;
        public static final int ERROR_NOT_AUTHORIZED = 2;
        public static final int ERROR_SERVICE_OBSOLETE = 4;
        public static final int ERROR_UNAVAILABLE = 3;
        public static final int ERROR_UNSUPPORTED = 1;
        public static final int OK = 0;
        
        private ApiStatus() {
        }
        
        public static final String toString(final int i) {
            switch (i) {
                default: {
                    return new StringBuilder(58).append("[UNKNOWN CONTROLLER MANAGER CONNECTION STATE: ").append(i).append("]").toString();
                }
                case 0: {
                    return "OK";
                }
                case 1: {
                    return "ERROR_UNSUPPORTED";
                }
                case 2: {
                    return "ERROR_NOT_AUTHORIZED";
                }
                case 3: {
                    return "ERROR_UNAVAILABLE";
                }
                case 4: {
                    return "ERROR_SERVICE_OBSOLETE";
                }
                case 5: {
                    return "ERROR_CLIENT_OBSOLETE";
                }
                case 6: {
                    return "ERROR_MALFUNCTION";
                }
            }
        }
    }
    
    public interface EventListener
    {
        void onApiStatusChanged(final int p0);
        
        void onRecentered();
    }
    
    private class InnerControllerListener
    {
        public void handleButtonEvent(final ControllerButtonEvent controllerButtonEvent) {
            while (true) {
                Label_0184: {
                    Label_0167: {
                        Label_0135: {
                            Label_0118: {
                                synchronized (ControllerManager.this.currentControllerState) {
                                    ControllerManager.this.currentControllerState.timestamp = controllerButtonEvent.timestampNanos;
                                    switch (controllerButtonEvent.button) {
                                        default: {
                                            Log.w("ControllerManager", String.format("onControllerButtonEvent didn't handle %d", controllerButtonEvent.button));
                                            break;
                                        }
                                        case 3: {
                                            ControllerManager.this.currentControllerState.appButtonState = controllerButtonEvent.down;
                                            break;
                                        }
                                        case 1: {
                                            break Label_0118;
                                        }
                                        case 2: {
                                            break Label_0135;
                                        }
                                        case 5: {
                                            break Label_0167;
                                        }
                                        case 6: {
                                            break Label_0184;
                                        }
                                    }
                                    return;
                                }
                            }
                            ControllerManager.this.currentControllerState.clickButtonState = controllerButtonEvent.down;
                            return;
                        }
                        if (ControllerManager.this.currentControllerState.setHomeButtonState(controllerButtonEvent.down)) {
                            ControllerManager.this.serviceEventListener.onRecentered();
                            return;
                        }
                        return;
                    }
                    ControllerManager.this.currentControllerState.volumeUpButtonState = controllerButtonEvent.down;
                    return;
                }
                ControllerManager.this.currentControllerState.volumeDownButtonState = controllerButtonEvent.down;
            }
        }
        
        public void handleOrientationEvent(final ControllerOrientationEvent controllerOrientationEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerOrientationEvent.timestampNanos;
                ControllerManager.this.currentControllerState.setOrientationInSensorSpace(controllerOrientationEvent.qx, controllerOrientationEvent.qy, controllerOrientationEvent.qz, controllerOrientationEvent.qw);
                ControllerManager.this.controller.notifyUpdate();
            }
        }
        
        public void handlePositionEvent(final ControllerPositionEvent controllerPositionEvent) {
            synchronized (ControllerManager.this.currentControllerState) {
                ControllerManager.this.currentControllerState.timestamp = controllerPositionEvent.timestampNanos;
                ControllerManager.this.currentControllerState.position[0] = controllerPositionEvent.x;
                ControllerManager.this.currentControllerState.position[1] = controllerPositionEvent.y;
                ControllerManager.this.currentControllerState.position[2] = controllerPositionEvent.z;
                ControllerManager.this.controller.notifyUpdate();
            }
        }
        
        public void handleTouchEvent(final ControllerTouchEvent controllerTouchEvent) {
            while (true) {
                Label_0143: {
                    synchronized (ControllerManager.this.currentControllerState) {
                        ControllerManager.this.currentControllerState.timestamp = controllerTouchEvent.timestampNanos;
                        ControllerManager.this.currentControllerState.touch.x = controllerTouchEvent.x;
                        ControllerManager.this.currentControllerState.touch.y = controllerTouchEvent.y;
                        switch (controllerTouchEvent.action) {
                            default: {
                                Log.w("ControllerManager", String.format(".handleTouchEvent didn't handle %d", controllerTouchEvent.action));
                                break;
                            }
                            case 1:
                            case 2: {
                                ControllerManager.this.currentControllerState.isTouching = true;
                                break;
                            }
                            case 0:
                            case 3:
                            case 4: {
                                break Label_0143;
                            }
                        }
                        return;
                    }
                }
                ControllerManager.this.currentControllerState.isTouching = false;
            }
        }
        
        public void notifyConnectionStateChange(final int n) {
            ControllerManager.this.controller.notifyConnectionStateChange(n);
        }
        
        public void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) {
            this.handleOrientationEvent(controllerOrientationEvent);
            ControllerManager.this.serviceEventListener.onRecentered();
        }
    }
    
    private static class OuterControllerListener extends Stub
    {
        private final WeakReference<InnerControllerListener> inner;
        
        public OuterControllerListener(final InnerControllerListener referent) {
            this.inner = new WeakReference<InnerControllerListener>(referent);
        }
        
        private void handleEventsBackwardCompatible(final ControllerEventPacket controllerEventPacket, final InnerControllerListener innerControllerListener) {
            final int n = 0;
            for (int i = 0; i < controllerEventPacket.getButtonEventCount(); ++i) {
                innerControllerListener.handleButtonEvent(controllerEventPacket.getButtonEvent(i));
            }
            for (int j = 0; j < controllerEventPacket.getOrientationEventCount(); ++j) {
                innerControllerListener.handleOrientationEvent(controllerEventPacket.getOrientationEvent(j));
            }
            for (int k = n; k < controllerEventPacket.getTouchEventCount(); ++k) {
                innerControllerListener.handleTouchEvent(controllerEventPacket.getTouchEvent(k));
            }
        }
        
        public void deprecatedOnControllerAccelEvent(final ControllerAccelEvent controllerAccelEvent) {
        }
        
        public void deprecatedOnControllerButtonEvent(final ControllerButtonEvent controllerButtonEvent) {
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleButtonEvent(controllerButtonEvent);
            }
        }
        
        public boolean deprecatedOnControllerButtonEventV1(final ControllerButtonEvent controllerButtonEvent) {
            return true;
        }
        
        public void deprecatedOnControllerGyroEvent(final ControllerGyroEvent controllerGyroEvent) {
        }
        
        public void deprecatedOnControllerOrientationEvent(final ControllerOrientationEvent controllerOrientationEvent) {
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.handleOrientationEvent(controllerOrientationEvent);
            }
        }
        
        public void deprecatedOnControllerTouchEvent(final ControllerTouchEvent controllerTouchEvent) {
            final InnerControllerListener innerControllerListener = this.inner.get();
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
        
        public void onControllerEventPacket(final ControllerEventPacket controllerEventPacket) throws RemoteException {
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                this.handleEventsBackwardCompatible(controllerEventPacket, innerControllerListener);
                controllerEventPacket.recycle();
            }
        }
        
        public void onControllerEventPacket2(final ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                this.handleEventsBackwardCompatible(controllerEventPacket2, innerControllerListener);
                for (int i = 0; i < controllerEventPacket2.getPositionEventCount(); ++i) {
                    innerControllerListener.handlePositionEvent(controllerEventPacket2.getPositionEvent(i));
                }
                controllerEventPacket2.recycle();
            }
        }
        
        public void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) {
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.onControllerRecentered(controllerOrientationEvent);
            }
        }
        
        public void onControllerStateChanged(final int n, final int n2) throws RemoteException {
            ControllerStates.toString(n2);
            final InnerControllerListener innerControllerListener = this.inner.get();
            if (innerControllerListener != null) {
                innerControllerListener.notifyConnectionStateChange(n2);
            }
        }
    }
}
