// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.os.Bundle;
import android.content.IntentSender$SendIntentException;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vr.cardboard.VrParamsProvider;
import android.app.Activity;
import android.net.Uri;
import android.content.ContentValues;
import com.google.vr.cardboard.VrSettingsProviderContract;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vr.vrcore.common.api.ITransitionCallbacks;
import android.app.PendingIntent;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import android.os.Looper;
import java.util.List;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.os.IBinder;
import android.content.ComponentName;
import com.google.vr.vrcore.common.api.IVrCoreSdkService;
import java.util.ArrayList;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import android.content.Context;
import android.content.ServiceConnection;
import com.google.vr.cardboard.annotations.UsedByReflection;
import android.annotation.TargetApi;

@TargetApi(24)
@UsedByReflection("IAP")
public class DaydreamApi implements AutoCloseable
{
    private static final String DAYDREAM_CATEGORY = "com.google.intent.category.DAYDREAM";
    private static final int MIN_API_FOR_HEADSET_INSERTION = 11;
    private static final int MIN_VRCORE_API_VERSION = 8;
    private static final String TAG;
    private boolean closed;
    private final ServiceConnection connection;
    private final Context context;
    private IDaydreamManager daydreamManager;
    private ArrayList<Runnable> queuedRunnables;
    private int vrCoreApiVersion;
    private IVrCoreSdkService vrCoreSdkService;
    
    static {
        TAG = DaydreamApi.class.getSimpleName();
    }
    
    private DaydreamApi(final Context context) {
        this.queuedRunnables = new ArrayList<Runnable>();
        this.connection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                ArrayList list = null;
                int size;
                int index = 0;
                Object value;
                Label_0042_Outer:Label_0061_Outer:
                while (true) {
                    DaydreamApi.this.vrCoreSdkService = IVrCoreSdkService.Stub.asInterface(binder);
                    while (true) {
                    Label_0103:
                        while (true) {
                            while (true) {
                                try {
                                    DaydreamApi.this.daydreamManager = DaydreamApi.this.vrCoreSdkService.getDaydreamManager();
                                    if (DaydreamApi.this.daydreamManager != null) {
                                        list = DaydreamApi.this.queuedRunnables;
                                        size = list.size();
                                        index = 0;
                                        if (index >= size) {
                                            DaydreamApi.this.queuedRunnables.clear();
                                            return;
                                        }
                                        break Label_0103;
                                    }
                                }
                                catch (final RemoteException ex) {
                                    Log.e(DaydreamApi.TAG, "RemoteException in onServiceConnected");
                                    continue Label_0042_Outer;
                                }
                                break;
                            }
                            Log.w(DaydreamApi.TAG, "Daydream service component unavailable.");
                            continue Label_0061_Outer;
                        }
                        value = list.get(index);
                        ++index;
                        ((Runnable)value).run();
                        continue;
                    }
                }
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                DaydreamApi.this.vrCoreSdkService = null;
            }
        };
        this.context = context;
    }
    
    private void checkIntent(final Intent obj) throws ActivityNotFoundException {
        final List queryIntentActivities = this.context.getPackageManager().queryIntentActivities(obj, 0);
        if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
            return;
        }
        final String value = String.valueOf(obj);
        throw new ActivityNotFoundException(new StringBuilder(String.valueOf(value).length() + 43).append("No activity is available to handle intent: ").append(value).toString());
    }
    
    private void checkNotClosed() {
        if (!this.closed) {
            return;
        }
        throw new IllegalStateException("DaydreamApi object is closed and can no longer be used.");
    }
    
    @UsedByReflection("IAP")
    public static DaydreamApi create(final Context context) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("DaydreamApi must only be used from the main thread.");
        }
        if (!DaydreamUtils.isDaydreamPhone(context)) {
            Log.i(DaydreamApi.TAG, "Phone is not Daydream-compatible");
            return null;
        }
        final DaydreamApi daydreamApi = new DaydreamApi(context);
        if (!daydreamApi.init()) {
            Log.w(DaydreamApi.TAG, "Failed to initialize DaydreamApi object.");
            return null;
        }
        return daydreamApi;
    }
    
    @UsedByReflection("IAP")
    public static Intent createVrIntent(final ComponentName component) {
        final Intent intent = new Intent();
        intent.setComponent(component);
        return setupVrIntent(intent);
    }
    
    public static boolean getDaydreamSetupCompleted(final Context p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokestatic    com/google/vr/cardboard/VrParamsProviderFactory.tryToGetContentProviderClientHandle:(Landroid/content/Context;)Lcom/google/vr/cardboard/VrParamsProviderFactory$ContentProviderClientHandle;
        //     4: astore_1       
        //     5: aload_1        
        //     6: ifnull          42
        //     9: aload_1        
        //    10: getfield        com/google/vr/cardboard/VrParamsProviderFactory$ContentProviderClientHandle.authority:Ljava/lang/String;
        //    13: ldc             "daydream_setup"
        //    15: invokestatic    com/google/vr/cardboard/VrSettingsProviderContract.createUri:(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
        //    18: astore_0       
        //    19: aload_1        
        //    20: getfield        com/google/vr/cardboard/VrParamsProviderFactory$ContentProviderClientHandle.client:Landroid/content/ContentProviderClient;
        //    23: aload_0        
        //    24: aconst_null    
        //    25: aconst_null    
        //    26: aconst_null    
        //    27: aconst_null    
        //    28: invokevirtual   android/content/ContentProviderClient.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    31: astore_1       
        //    32: aload_1        
        //    33: ifnonnull       53
        //    36: aload_1        
        //    37: ifnonnull       101
        //    40: iconst_0       
        //    41: ireturn        
        //    42: getstatic       com/google/vr/ndk/base/DaydreamApi.TAG:Ljava/lang/String;
        //    45: ldc             "No ContentProvider available for Daydream setup."
        //    47: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //    50: pop            
        //    51: iconst_0       
        //    52: ireturn        
        //    53: aload_1        
        //    54: astore_0       
        //    55: aload_1        
        //    56: invokeinterface android/database/Cursor.moveToFirst:()Z
        //    61: ifeq            36
        //    64: aload_1        
        //    65: astore_0       
        //    66: aload_1        
        //    67: iconst_0       
        //    68: invokeinterface android/database/Cursor.getInt:(I)I
        //    73: istore_2       
        //    74: iload_2        
        //    75: iconst_1       
        //    76: if_icmpeq       87
        //    79: iconst_0       
        //    80: istore_3       
        //    81: aload_1        
        //    82: ifnonnull       92
        //    85: iload_3        
        //    86: ireturn        
        //    87: iconst_1       
        //    88: istore_3       
        //    89: goto            81
        //    92: aload_1        
        //    93: invokeinterface android/database/Cursor.close:()V
        //    98: goto            85
        //   101: aload_1        
        //   102: invokeinterface android/database/Cursor.close:()V
        //   107: goto            40
        //   110: astore          4
        //   112: aconst_null    
        //   113: astore_1       
        //   114: aload_1        
        //   115: astore_0       
        //   116: getstatic       com/google/vr/ndk/base/DaydreamApi.TAG:Ljava/lang/String;
        //   119: ldc_w           "Failed to read Daydream setup completion from ContentProvider"
        //   122: aload           4
        //   124: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   127: pop            
        //   128: aload_1        
        //   129: ifnonnull       134
        //   132: iconst_0       
        //   133: ireturn        
        //   134: aload_1        
        //   135: invokeinterface android/database/Cursor.close:()V
        //   140: goto            132
        //   143: astore          4
        //   145: aconst_null    
        //   146: astore_1       
        //   147: aload_1        
        //   148: astore_0       
        //   149: getstatic       com/google/vr/ndk/base/DaydreamApi.TAG:Ljava/lang/String;
        //   152: ldc_w           "Insufficient permissions to read Daydream setup completion from ContentProvider"
        //   155: aload           4
        //   157: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   160: pop            
        //   161: aload_1        
        //   162: ifnonnull       167
        //   165: iconst_0       
        //   166: ireturn        
        //   167: aload_1        
        //   168: invokeinterface android/database/Cursor.close:()V
        //   173: goto            165
        //   176: astore_1       
        //   177: aconst_null    
        //   178: astore_0       
        //   179: aload_0        
        //   180: ifnonnull       185
        //   183: aload_1        
        //   184: athrow         
        //   185: aload_0        
        //   186: invokeinterface android/database/Cursor.close:()V
        //   191: goto            183
        //   194: astore_1       
        //   195: goto            179
        //   198: astore          4
        //   200: goto            147
        //   203: astore          4
        //   205: goto            114
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                         
        //  -----  -----  -----  -----  -----------------------------
        //  19     32     110    114    Landroid/os/RemoteException;
        //  19     32     143    147    Ljava/lang/SecurityException;
        //  19     32     176    179    Any
        //  55     64     203    208    Landroid/os/RemoteException;
        //  55     64     198    203    Ljava/lang/SecurityException;
        //  55     64     194    198    Any
        //  66     74     203    208    Landroid/os/RemoteException;
        //  66     74     198    203    Ljava/lang/SecurityException;
        //  66     74     194    198    Any
        //  116    128    194    198    Any
        //  149    161    194    198    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0081:
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
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean init() {
        try {
            this.vrCoreApiVersion = VrCoreUtils.getVrCoreClientApiVersion(this.context);
            if (this.vrCoreApiVersion < 8) {
                Log.e(DaydreamApi.TAG, new StringBuilder(79).append("VrCore out of date, current version: ").append(this.vrCoreApiVersion).append(", required version: 8").toString());
                return false;
            }
            final Intent intent = new Intent("com.google.vr.vrcore.BIND_SDK_SERVICE");
            intent.setPackage("com.google.vr.vrcore");
            if (!this.context.bindService(intent, this.connection, 1)) {
                Log.e(DaydreamApi.TAG, "Unable to bind to VrCoreSdkService");
                return false;
            }
            return true;
        }
        catch (final VrCoreNotAvailableException obj) {
            final String tag = DaydreamApi.TAG;
            final String value = String.valueOf(obj);
            Log.e(tag, new StringBuilder(String.valueOf(value).length() + 22).append("VrCore not available: ").append(value).toString());
            return false;
        }
    }
    
    public static boolean isDaydreamReadyPlatform(final Context context) {
        return DaydreamUtils.isDaydreamPhone(context);
    }
    
    private void launchInVr(final PendingIntent pendingIntent, final ComponentName componentName) {
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                Label_0027: {
                    if (DaydreamApi.this.daydreamManager != null) {
                        break Label_0027;
                    }
                    Log.w(DaydreamApi.TAG, "Can't launch PendingIntent via DaydreamManager: not available.");
                    try {
                        pendingIntent.send();
                        return;
                        try {
                            DaydreamApi.this.daydreamManager.launchInVr(pendingIntent, componentName);
                        }
                        catch (final RemoteException ex) {
                            Log.e(DaydreamApi.TAG, "RemoteException while launching PendingIntent in VR.", (Throwable)ex);
                        }
                    }
                    catch (final Exception ex2) {
                        Log.e(DaydreamApi.TAG, "Couldn't launch PendingIntent: ", (Throwable)ex2);
                    }
                }
            }
        });
    }
    
    private void launchTransitionCallbackInVr(final ITransitionCallbacks transitionCallbacks) {
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                int launchVrTransition;
                if (DaydreamApi.this.daydreamManager == null) {
                    launchVrTransition = 0;
                }
                else {
                    try {
                        launchVrTransition = (DaydreamApi.this.daydreamManager.launchVrTransition(transitionCallbacks) ? 1 : 0);
                    }
                    catch (final RemoteException ex) {
                        Log.e(DaydreamApi.TAG, "RemoteException while launching VR transition: ", (Throwable)ex);
                        launchVrTransition = 0;
                    }
                }
                if (launchVrTransition == 0) {
                    Log.w(DaydreamApi.TAG, "Can't launch callbacks via DaydreamManager, sending manually");
                    try {
                        transitionCallbacks.onTransitionComplete();
                    }
                    catch (final RemoteException ex2) {}
                }
            }
        });
    }
    
    private void runWhenServiceConnected(final Runnable e) {
        if (this.vrCoreSdkService == null) {
            this.queuedRunnables.add(e);
            return;
        }
        e.run();
    }
    
    public static boolean setDaydreamSetupCompleted(final Context context, final boolean b) {
        final VrParamsProviderFactory.ContentProviderClientHandle tryToGetContentProviderClientHandle = VrParamsProviderFactory.tryToGetContentProviderClientHandle(context);
        Label_0058: {
            if (tryToGetContentProviderClientHandle == null) {
                break Label_0058;
            }
            final Uri uri = VrSettingsProviderContract.createUri(tryToGetContentProviderClientHandle.authority, "daydream_setup");
            try {
                final ContentValues contentValues = new ContentValues();
                contentValues.put("value", Boolean.valueOf(b));
                return tryToGetContentProviderClientHandle.client.update(uri, contentValues, (String)null, (String[])null) > 0;
                Log.e(DaydreamApi.TAG, "No ContentProvider available for Daydream setup.");
                return false;
            }
            catch (final RemoteException ex) {
                Log.e(DaydreamApi.TAG, "Failed to indicate Daydream setup completion to ContentProvider", (Throwable)ex);
                return false;
            }
            catch (final SecurityException ex2) {
                Log.e(DaydreamApi.TAG, "Insufficient permissions to indicate Daydream setup completion to ContentProvider", (Throwable)ex2);
                return false;
            }
        }
    }
    
    @UsedByReflection("IAP")
    public static Intent setupVrIntent(final Intent intent) {
        intent.addCategory("com.google.intent.category.DAYDREAM");
        intent.addFlags(335609856);
        return intent;
    }
    
    @UsedByReflection("IAP")
    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.runWhenServiceConnected(new Runnable() {
                @Override
                public void run() {
                    DaydreamApi.this.context.unbindService(DaydreamApi.this.connection);
                    DaydreamApi.this.vrCoreSdkService = null;
                }
            });
        }
    }
    
    @UsedByReflection("IAP")
    public void exitFromVr(final Activity activity, final int n, Intent intent) {
        this.checkNotClosed();
        if (intent == null) {
            intent = new Intent();
        }
        final PendingIntent pendingResult = activity.createPendingResult(n, intent, 1073741824);
        this.runWhenServiceConnected(new Runnable() {
            final /* synthetic */ Runnable val$onFailureRunnable = new Runnable(this, pendingResult) {
                final /* synthetic */ PendingIntent val$pendingVrExitIntent;
                
                @Override
                public void run() {
                    try {
                        this.val$pendingVrExitIntent.send(0);
                    }
                    catch (final Exception obj) {
                        final String access$200 = DaydreamApi.TAG;
                        final String value = String.valueOf(obj);
                        Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 31).append("Couldn't launch PendingIntent: ").append(value).toString());
                    }
                }
            };
            
            @Override
            public void run() {
                Label_0032: {
                    if (DaydreamApi.this.daydreamManager == null) {
                        break Label_0032;
                    }
                    try {
                        if (!DaydreamApi.this.daydreamManager.exitFromVr(pendingResult)) {
                            Log.w(DaydreamApi.TAG, "Failed to exit VR: Invalid request.");
                            this.val$onFailureRunnable.run();
                        }
                        return;
                        Log.w(DaydreamApi.TAG, "Failed to exit VR: Daydream service unavailable.");
                        this.val$onFailureRunnable.run();
                    }
                    catch (final RemoteException obj) {
                        final String access$200 = DaydreamApi.TAG;
                        final String value = String.valueOf(obj);
                        Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 49).append("Failed to exit VR: RemoteException while exiting:").append(value).toString());
                        this.val$onFailureRunnable.run();
                    }
                }
            }
        });
    }
    
    public int getCurrentViewerType() {
        this.checkNotClosed();
        if (!isDaydreamReadyPlatform(this.context)) {
            return 0;
        }
        final VrParamsProvider create = VrParamsProviderFactory.create(this.context);
        try {
            final CardboardDevice.DeviceParams deviceParams = create.readDeviceParams();
            if (deviceParams == null) {
                return 0;
            }
            if (!DaydreamUtils.isDaydreamViewer(deviceParams)) {
                return 0;
            }
            return 1;
        }
        finally {
            create.close();
        }
    }
    
    public void handleInsertionIntoHeadset(final byte[] array) {
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                Label_0039: {
                    if (DaydreamApi.this.vrCoreApiVersion < 11) {
                        break Label_0039;
                    }
                    Label_0091: {
                        if (DaydreamApi.this.daydreamManager == null) {
                            break Label_0091;
                        }
                        try {
                            DaydreamApi.this.daydreamManager.handleInsertionIntoHeadset(array);
                            return;
                            Log.e(DaydreamApi.TAG, "Can't handle insertion of phone into headset: no DaydreamManager.");
                            return;
                            final String access$200 = DaydreamApi.TAG;
                            final String value = String.valueOf("Can't handle insertion of phone into headset: VrCore API too old. Need: 11, found: ");
                            Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 11).append(value).append(DaydreamApi.this.vrCoreApiVersion).toString());
                        }
                        catch (final SecurityException ex) {
                            Log.e(DaydreamApi.TAG, "SecurityException when notifying phone insertion. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", (Throwable)ex);
                        }
                        catch (final RemoteException ex2) {
                            Log.e(DaydreamApi.TAG, "RemoteException while notifying phone insertion.", (Throwable)ex2);
                        }
                    }
                }
            }
        });
    }
    
    public void handleRemovalFromHeadset() {
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                Label_0035: {
                    if (DaydreamApi.this.vrCoreApiVersion < 11) {
                        break Label_0035;
                    }
                    Label_0087: {
                        if (DaydreamApi.this.daydreamManager == null) {
                            break Label_0087;
                        }
                        try {
                            DaydreamApi.this.daydreamManager.handleRemovalFromHeadset();
                            return;
                            Log.e(DaydreamApi.TAG, "Can't handle removal of phone from headset: no DaydreamManager.");
                            return;
                            final String access$200 = DaydreamApi.TAG;
                            final String value = String.valueOf("Can't handle removal of phone from headset: VrCore API too old. Need: 11, found: ");
                            Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 11).append(value).append(DaydreamApi.this.vrCoreApiVersion).toString());
                        }
                        catch (final SecurityException ex) {
                            Log.e(DaydreamApi.TAG, "SecurityException when notifying phone removal. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", (Throwable)ex);
                        }
                        catch (final RemoteException ex2) {
                            Log.e(DaydreamApi.TAG, "RemoteException while notifying phone removal.", (Throwable)ex2);
                        }
                    }
                }
            }
        });
    }
    
    @UsedByReflection("IAP")
    public void launchInVr(final PendingIntent pendingIntent) {
        this.checkNotClosed();
        this.launchInVr(pendingIntent, null);
    }
    
    @UsedByReflection("IAP")
    public void launchInVr(final ComponentName componentName) throws ActivityNotFoundException {
        this.checkNotClosed();
        if (componentName != null) {
            final Intent vrIntent = createVrIntent(componentName);
            this.checkIntent(vrIntent);
            this.launchInVr(PendingIntent.getActivity(this.context, 0, vrIntent, 1073741824), vrIntent.getComponent());
            return;
        }
        throw new IllegalArgumentException("Null argument 'componentName' passed to launchInVr");
    }
    
    @UsedByReflection("IAP")
    public void launchInVr(final Intent intent) throws ActivityNotFoundException {
        this.checkNotClosed();
        if (intent != null) {
            this.checkIntent(intent);
            this.launchInVr(PendingIntent.getActivity(this.context, 0, intent, 1207959552), intent.getComponent());
            return;
        }
        throw new IllegalArgumentException("Null argument 'intent' passed to launchInVr");
    }
    
    @UsedByReflection("IAP")
    public void launchInVrForResult(final Activity activity, final PendingIntent pendingIntent, final int n) {
        this.checkNotClosed();
        this.launchTransitionCallbackInVr(new ITransitionCallbacks.Stub(this) {
            public void onTransitionComplete() {
                activity.runOnUiThread((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        try {
                            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), n, (Intent)null, 0, 0, 0);
                        }
                        catch (final IntentSender$SendIntentException obj) {
                            final String access$200 = DaydreamApi.TAG;
                            final String value = String.valueOf(obj);
                            Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 43).append("Exception while starting next VR activity: ").append(value).toString());
                        }
                    }
                });
            }
        });
    }
    
    @UsedByReflection("IAP")
    public void launchVrHomescreen() {
        this.checkNotClosed();
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                Label_0035: {
                    if (DaydreamApi.this.daydreamManager == null) {
                        break Label_0035;
                    }
                    try {
                        if (!DaydreamApi.this.daydreamManager.launchVrHome()) {
                            Log.e(DaydreamApi.TAG, "There is no VR homescreen installed.");
                            return;
                        }
                        return;
                        Log.e(DaydreamApi.TAG, "Can't launch VR homescreen via DaydreamManager. Giving up trying to leave current VR activity...");
                    }
                    catch (final RemoteException obj) {
                        final String access$200 = DaydreamApi.TAG;
                        final String value = String.valueOf(obj);
                        Log.e(access$200, new StringBuilder(String.valueOf(value).length() + 47).append("RemoteException while launching VR homescreen: ").append(value).toString());
                    }
                }
            }
        });
    }
    
    public void registerDaydreamIntent(final PendingIntent pendingIntent) {
        this.checkNotClosed();
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                Label_0030: {
                    if (DaydreamApi.this.daydreamManager == null) {
                        break Label_0030;
                    }
                    try {
                        if (pendingIntent == null) {
                            DaydreamApi.this.daydreamManager.unregisterDaydreamIntent();
                            return;
                        }
                        DaydreamApi.this.daydreamManager.registerDaydreamIntent(pendingIntent);
                        return;
                        Log.w(DaydreamApi.TAG, "Can't register/unregister daydream intent: no DaydreamManager.");
                    }
                    catch (final RemoteException ex) {
                        Log.e(DaydreamApi.TAG, "Error when attempting to register/unregister daydream intent: ", (Throwable)ex);
                    }
                }
            }
        });
    }
    
    public void setInhibitSystemButtons(final ComponentName componentName, final boolean b) {
        this.checkNotClosed();
        this.runWhenServiceConnected(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bundle bundle = new Bundle();
                    bundle.putBoolean("OPTION_INHIBIT_SYSTEM_BUTTONS", b);
                    if (!DaydreamApi.this.vrCoreSdkService.setClientOptions(componentName, bundle)) {
                        Log.w(DaydreamApi.TAG, "Failed to set client options to inhibit system button.");
                    }
                }
                catch (final RemoteException ex) {
                    Log.e(DaydreamApi.TAG, "RemoteException while setting client options.", (Throwable)ex);
                }
            }
        });
    }
    
    public void unregisterDaydreamIntent() {
        this.checkNotClosed();
        this.registerDaydreamIntent(null);
    }
}
