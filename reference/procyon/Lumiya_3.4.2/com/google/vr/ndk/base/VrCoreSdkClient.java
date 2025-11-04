// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.os.Message;
import android.os.Handler;
import java.lang.ref.WeakReference;
import com.google.vr.vrcore.common.api.IDaydreamListener;
import android.app.PendingIntent;
import com.google.vr.cardboard.UiUtils;
import com.google.vr.cardboard.R;
import android.app.ActivityManager;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import android.os.RemoteException;
import android.util.Log;
import android.content.Intent;
import com.google.vr.vrcore.common.api.HeadTrackingState;
import android.os.IBinder;
import com.google.vr.vrcore.common.api.IVrCoreSdkService;
import android.content.ServiceConnection;
import android.app.AlertDialog;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import android.content.Context;
import android.content.ComponentName;

class VrCoreSdkClient
{
    private static final boolean DEBUG = false;
    private static final int FADE_FLUSH_DELAY_FOR_TRACKING_STABILIZATION_MILLIS = 200;
    static final int MIN_VRCORE_API_VERSION = 5;
    private static final String TAG = "VrCoreSdkClient";
    static final int TARGET_VRCORE_API_VERSION = 10;
    private final Runnable closeVrRunnable;
    private final ComponentName componentName;
    private final Context context;
    private final DaydreamListenerImpl daydreamListener;
    private IDaydreamManager daydreamManager;
    private final DaydreamUtilsWrapper daydreamUtils;
    private final FadeOverlayView fadeOverlayView;
    private final GvrApi gvrApi;
    private AlertDialog helpCenterDialog;
    private boolean isBound;
    private boolean isEnabled;
    private boolean isResumed;
    private final ServiceConnection serviceConnection;
    private final boolean shouldBind;
    private IVrCoreSdkService vrCoreSdkService;
    
    public VrCoreSdkClient(final Context context, final GvrApi gvrApi, final ComponentName componentName, final DaydreamUtilsWrapper daydreamUtils, final Runnable closeVrRunnable, final FadeOverlayView fadeOverlayView) {
        this.isEnabled = true;
        this.serviceConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName p0, final IBinder p1) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: astore_1       
                //     2: aload_2        
                //     3: invokestatic    com/google/vr/vrcore/common/api/IVrCoreSdkService$Stub.asInterface:(Landroid/os/IBinder;)Lcom/google/vr/vrcore/common/api/IVrCoreSdkService;
                //     6: astore_2       
                //     7: aload_2        
                //     8: bipush          10
                //    10: invokeinterface com/google/vr/vrcore/common/api/IVrCoreSdkService.initialize:(I)Z
                //    15: istore_3       
                //    16: iload_3        
                //    17: ifeq            136
                //    20: aload_0        
                //    21: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    24: aload_2        
                //    25: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$102:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/IVrCoreSdkService;)Lcom/google/vr/vrcore/common/api/IVrCoreSdkService;
                //    28: pop            
                //    29: aload_0        
                //    30: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    33: aload_0        
                //    34: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    37: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$100:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Lcom/google/vr/vrcore/common/api/IVrCoreSdkService;
                //    40: invokeinterface com/google/vr/vrcore/common/api/IVrCoreSdkService.getDaydreamManager:()Lcom/google/vr/vrcore/common/api/IDaydreamManager;
                //    45: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$202:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/IDaydreamManager;)Lcom/google/vr/vrcore/common/api/IDaydreamManager;
                //    48: pop            
                //    49: aload_0        
                //    50: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    53: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$200:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Lcom/google/vr/vrcore/common/api/IDaydreamManager;
                //    56: ifnull          201
                //    59: aload_0        
                //    60: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    63: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$200:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Lcom/google/vr/vrcore/common/api/IDaydreamManager;
                //    66: aload_0        
                //    67: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    70: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$400:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Landroid/content/ComponentName;
                //    73: aload_0        
                //    74: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    77: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$500:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Lcom/google/vr/ndk/base/VrCoreSdkClient$DaydreamListenerImpl;
                //    80: invokeinterface com/google/vr/vrcore/common/api/IDaydreamManager.registerListener:(Landroid/content/ComponentName;Lcom/google/vr/vrcore/common/api/IDaydreamListener;)Z
                //    85: pop            
                //    86: aload_0        
                //    87: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    90: invokevirtual   com/google/vr/ndk/base/VrCoreSdkClient.getHeadTrackingState:()Lcom/google/vr/vrcore/common/api/HeadTrackingState;
                //    93: astore_2       
                //    94: aload_0        
                //    95: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //    98: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$200:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Lcom/google/vr/vrcore/common/api/IDaydreamManager;
                //   101: aload_0        
                //   102: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   105: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$400:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)Landroid/content/ComponentName;
                //   108: aload_2        
                //   109: invokeinterface com/google/vr/vrcore/common/api/IDaydreamManager.prepareVr:(Landroid/content/ComponentName;Lcom/google/vr/vrcore/common/api/HeadTrackingState;)I
                //   114: istore          4
                //   116: iload           4
                //   118: iconst_2       
                //   119: if_icmpeq       266
                //   122: iload           4
                //   124: ifeq            290
                //   127: aload_0        
                //   128: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   131: aload_1        
                //   132: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$700:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/HeadTrackingState;)V
                //   135: return         
                //   136: ldc             "VrCoreSdkClient"
                //   138: ldc             "Failed to initialize VrCore SDK Service."
                //   140: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
                //   143: pop            
                //   144: aload_0        
                //   145: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   148: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$000:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)V
                //   151: return         
                //   152: astore_1       
                //   153: aload_1        
                //   154: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   157: astore_1       
                //   158: ldc             "VrCoreSdkClient"
                //   160: new             Ljava/lang/StringBuilder;
                //   163: dup            
                //   164: aload_1        
                //   165: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   168: invokevirtual   java/lang/String.length:()I
                //   171: bipush          41
                //   173: iadd           
                //   174: invokespecial   java/lang/StringBuilder.<init>:(I)V
                //   177: ldc             "Failed to initialize VrCore SDK Service: "
                //   179: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   182: aload_1        
                //   183: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   186: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                //   189: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
                //   192: pop            
                //   193: aload_0        
                //   194: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   197: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$000:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)V
                //   200: return         
                //   201: ldc             "VrCoreSdkClient"
                //   203: ldc             "Failed to obtain DaydreamManager from VrCore SDK Service."
                //   205: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
                //   208: pop            
                //   209: aload_0        
                //   210: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   213: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$300:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)V
                //   216: return         
                //   217: astore_1       
                //   218: aload_1        
                //   219: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   222: astore_1       
                //   223: ldc             "VrCoreSdkClient"
                //   225: new             Ljava/lang/StringBuilder;
                //   228: dup            
                //   229: aload_1        
                //   230: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   233: invokevirtual   java/lang/String.length:()I
                //   236: bipush          57
                //   238: iadd           
                //   239: invokespecial   java/lang/StringBuilder.<init>:(I)V
                //   242: ldc             "Failed to obtain DaydreamManager from VrCore SDK Service:"
                //   244: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   247: aload_1        
                //   248: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   251: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                //   254: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
                //   257: pop            
                //   258: aload_0        
                //   259: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   262: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$300:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)V
                //   265: return         
                //   266: ldc             "VrCoreSdkClient"
                //   268: ldc             "Daydream VR preparation failed, closing VR session."
                //   270: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
                //   273: pop            
                //   274: aload_0        
                //   275: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   278: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$600:(Lcom/google/vr/ndk/base/VrCoreSdkClient;)V
                //   281: aload_0        
                //   282: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   285: aconst_null    
                //   286: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$700:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/HeadTrackingState;)V
                //   289: return         
                //   290: aload_2        
                //   291: astore_1       
                //   292: goto            127
                //   295: astore_1       
                //   296: aload_1        
                //   297: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   300: astore_1       
                //   301: aload_1        
                //   302: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
                //   305: invokevirtual   java/lang/String.length:()I
                //   308: istore          4
                //   310: new             Ljava/lang/StringBuilder;
                //   313: astore_2       
                //   314: aload_2        
                //   315: iload           4
                //   317: bipush          61
                //   319: iadd           
                //   320: invokespecial   java/lang/StringBuilder.<init>:(I)V
                //   323: ldc             "VrCoreSdkClient"
                //   325: aload_2        
                //   326: ldc             "Error while registering listener with the VrCore SDK Service:"
                //   328: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   331: aload_1        
                //   332: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   335: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                //   338: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
                //   341: pop            
                //   342: aload_0        
                //   343: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   346: aconst_null    
                //   347: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$700:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/HeadTrackingState;)V
                //   350: return         
                //   351: astore_1       
                //   352: aload_0        
                //   353: getfield        com/google/vr/ndk/base/VrCoreSdkClient$1.this$0:Lcom/google/vr/ndk/base/VrCoreSdkClient;
                //   356: aconst_null    
                //   357: invokestatic    com/google/vr/ndk/base/VrCoreSdkClient.access$700:(Lcom/google/vr/ndk/base/VrCoreSdkClient;Lcom/google/vr/vrcore/common/api/HeadTrackingState;)V
                //   360: aload_1        
                //   361: athrow         
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                        
                //  -----  -----  -----  -----  ----------------------------
                //  7      16     152    201    Landroid/os/RemoteException;
                //  29     86     217    266    Landroid/os/RemoteException;
                //  86     116    295    351    Landroid/os/RemoteException;
                //  86     116    351    362    Any
                //  136    151    152    201    Landroid/os/RemoteException;
                //  201    216    217    266    Landroid/os/RemoteException;
                //  266    281    295    351    Landroid/os/RemoteException;
                //  266    281    351    362    Any
                //  296    342    351    362    Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IndexOutOfBoundsException: Index 177 out of bounds for length 177
                //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
                //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
                //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
                //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
                //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
                //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
                VrCoreSdkClient.this.vrCoreSdkService = null;
                VrCoreSdkClient.this.daydreamManager = null;
            }
        };
        this.context = context;
        this.gvrApi = gvrApi;
        this.componentName = componentName;
        this.daydreamUtils = daydreamUtils;
        this.closeVrRunnable = closeVrRunnable;
        this.fadeOverlayView = fadeOverlayView;
        this.daydreamListener = new DaydreamListenerImpl(gvrApi, fadeOverlayView);
        this.shouldBind = hasCompatibleSdkService(context);
        gvrApi.setIgnoreManualTrackerPauseResume(true);
    }
    
    private boolean doBind() {
        if (!this.isBound) {
            if (this.shouldBind) {
                final Intent intent = new Intent("com.google.vr.vrcore.BIND_SDK_SERVICE");
                intent.setPackage("com.google.vr.vrcore");
                this.isBound = this.context.bindService(intent, this.serviceConnection, 1);
            }
            if (!this.isBound) {
                this.handleBindFailed();
            }
            return this.isBound;
        }
        return true;
    }
    
    private void doUnbind() {
        if (!this.isResumed) {
            this.gvrApi.pauseTrackingGetState();
        }
        else {
            this.resumeTracking(null);
        }
        if (this.isBound) {
            if (this.daydreamManager != null) {
                while (true) {
                    try {
                        this.daydreamManager.unregisterListener(this.componentName);
                        this.daydreamManager = null;
                    }
                    catch (final RemoteException obj) {
                        final String value = String.valueOf(obj);
                        Log.w("VrCoreSdkClient", new StringBuilder(String.valueOf(value).length() + 40).append("Failed to unregister Daydream listener: ").append(value).toString());
                        continue;
                    }
                    break;
                }
            }
            this.vrCoreSdkService = null;
            this.context.unbindService(this.serviceConnection);
            this.isBound = false;
        }
    }
    
    private void handleBindFailed() {
        this.doUnbind();
        this.warnIfIncompatibleClient();
    }
    
    private void handleNoDaydreamManager() {
        this.doUnbind();
        this.warnIfIncompatibleClient();
    }
    
    private void handlePrepareVrFailed() {
        this.doUnbind();
        this.closeVrRunnable.run();
    }
    
    private static boolean hasCompatibleSdkService(final Context context) {
        try {
            final int vrCoreClientApiVersion = VrCoreUtils.getVrCoreClientApiVersion(context);
            if (vrCoreClientApiVersion < 5) {
                Log.w("VrCoreSdkClient", String.format("VrCore service obsolete, GVR SDK requires API %d but found API %d.", 5, vrCoreClientApiVersion));
                return false;
            }
            return true;
        }
        catch (final VrCoreNotAvailableException ex) {
            return false;
        }
    }
    
    private static void resumeTracking(final GvrApi gvrApi, final HeadTrackingState headTrackingState) {
        byte[] data = null;
        if (headTrackingState != null && !headTrackingState.isEmpty()) {
            data = headTrackingState.getData();
        }
        gvrApi.resumeTrackingSetState(data);
    }
    
    private void resumeTracking(final HeadTrackingState headTrackingState) {
        resumeTracking(this.gvrApi, headTrackingState);
        if (this.fadeOverlayView != null) {
            this.fadeOverlayView.flushAutoFade(200L);
        }
    }
    
    private void warnIfIncompatibleClient() {
        if (!this.daydreamUtils.isDaydreamPhone(this.context) && this.daydreamUtils.isDaydreamRequiredActivity(ContextUtils.getActivity(this.context)) && !ActivityManager.isRunningInTestHarness()) {
            if (this.helpCenterDialog != null) {
                this.helpCenterDialog.show();
                return;
            }
            this.helpCenterDialog = UiUtils.showDaydreamHelpCenterDialog(this.context, R.string.dialog_title_incompatible_phone, R.string.dialog_message_incompatible_phone, this.closeVrRunnable);
        }
    }
    
    IDaydreamManager getDaydreamManager() {
        return this.daydreamManager;
    }
    
    HeadTrackingState getHeadTrackingState() {
        return new HeadTrackingState();
    }
    
    public boolean launchInVr(final PendingIntent pendingIntent) {
        if (this.daydreamManager == null) {
            return false;
        }
        try {
            this.daydreamManager.deprecatedLaunchInVr(pendingIntent);
            return true;
        }
        catch (final RemoteException obj) {
            final String value = String.valueOf(obj);
            Log.w("VrCoreSdkClient", new StringBuilder(String.valueOf(value).length() + 28).append("Failed to launch app in VR: ").append(value).toString());
            return true;
        }
    }
    
    public void onPause() {
        this.isResumed = false;
        this.daydreamListener.resetSafeguards();
        if (this.isEnabled) {
            this.doUnbind();
        }
    }
    
    public boolean onResume() {
        this.isResumed = true;
        return this.isEnabled && this.doBind();
    }
    
    public void setEnabled(final boolean b) {
        if (this.isEnabled != b) {
            this.isEnabled = b;
            this.gvrApi.setIgnoreManualTrackerPauseResume(b);
            if (this.isResumed) {
                if (this.isEnabled) {
                    this.doBind();
                    return;
                }
                this.doUnbind();
            }
        }
    }
    
    private static final class DaydreamListenerImpl extends Stub
    {
        private static final long FADE_SAFEGUARD_DELAY_MILLIS = 3500L;
        private static final int MSG_FADE_IN_SAFEGUARD = 2;
        private static final int MSG_TRACKING_RESUME_SAFEGUARD = 1;
        private static final long TRACKING_SAFEGUARD_DELAY_MILLIS = 3000L;
        private final WeakReference<FadeOverlayView> fadeOverlayViewWeak;
        private final WeakReference<GvrApi> gvrApiWeak;
        private final Handler safeguardHandler;
        
        DaydreamListenerImpl(final GvrApi referent, final FadeOverlayView referent2) {
            this.safeguardHandler = new Handler() {
                public void handleMessage(final Message message) {
                    switch (message.what) {
                        default: {
                            super.handleMessage(message);
                            return;
                        }
                        case 2: {
                            Log.w("VrCoreSdkClient", "Forcing fade in: VrCore unresponsive");
                            DaydreamListenerImpl.this.applyFadeImpl(1, 350L);
                            return;
                        }
                        case 1: {
                            Log.w("VrCoreSdkClient", "Forcing tracking resume: VrCore unresponsive");
                            DaydreamListenerImpl.this.resumeHeadTrackingImpl(null);
                        }
                    }
                }
            };
            this.gvrApiWeak = new WeakReference<GvrApi>(referent);
            this.fadeOverlayViewWeak = new WeakReference<FadeOverlayView>(referent2);
        }
        
        private final void applyFadeImpl(final int n, final long n2) {
            final FadeOverlayView fadeOverlayView = this.fadeOverlayViewWeak.get();
            if (fadeOverlayView != null) {
                this.cancelSafeguard(2);
                fadeOverlayView.post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        fadeOverlayView.startFade(n, n2);
                    }
                });
                if (n == 2) {
                    this.rescheduleSafeguard(2, 3500L + n2);
                }
            }
        }
        
        private final void cancelSafeguard(final int n) {
            this.safeguardHandler.removeMessages(n);
        }
        
        private final void rescheduleSafeguard(final int n, final long n2) {
            this.cancelSafeguard(n);
            this.safeguardHandler.sendEmptyMessageDelayed(n, n2);
        }
        
        private final void resumeHeadTrackingImpl(final HeadTrackingState headTrackingState) {
            final GvrApi gvrApi = this.gvrApiWeak.get();
            if (gvrApi != null) {
                this.cancelSafeguard(1);
                resumeTracking(gvrApi, headTrackingState);
                return;
            }
            Log.w("VrCoreSdkClient", "Invalid resumeHeadTracking() call: GvrApi no longer valid");
        }
        
        public final void applyFade(final int n, final long n2) {
            this.applyFadeImpl(n, n2);
        }
        
        public final void dumpDebugData() throws RemoteException {
            final GvrApi gvrApi = this.gvrApiWeak.get();
            if (gvrApi != null) {
                gvrApi.dumpDebugData();
                return;
            }
            Log.w("VrCoreSdkClient", "Invalid dumpDebugData() call: GvrApi no longer valid");
        }
        
        public final int getTargetApiVersion() throws RemoteException {
            return 10;
        }
        
        public final void recenterHeadTracking() throws RemoteException {
            final GvrApi gvrApi = this.gvrApiWeak.get();
            if (gvrApi != null) {
                gvrApi.recenterTracking();
                return;
            }
            Log.w("VrCoreSdkClient", "Invalid recenterHeadTracking() call: GvrApi no longer valid");
        }
        
        public final HeadTrackingState requestStopTracking() throws RemoteException {
            final GvrApi gvrApi = this.gvrApiWeak.get();
            if (gvrApi == null) {
                Log.w("VrCoreSdkClient", "Invalid requestStopTracking() call: GvrApi no longer valid");
                return null;
            }
            final byte[] pauseTrackingGetState = gvrApi.pauseTrackingGetState();
            this.rescheduleSafeguard(1, 3000L);
            if (pauseTrackingGetState == null) {
                return null;
            }
            return new HeadTrackingState(pauseTrackingGetState);
        }
        
        final void resetSafeguards() {
            this.safeguardHandler.removeCallbacksAndMessages((Object)null);
        }
        
        public final void resumeHeadTracking(final HeadTrackingState headTrackingState) {
            this.resumeHeadTrackingImpl(headTrackingState);
        }
    }
}
