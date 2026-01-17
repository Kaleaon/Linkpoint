package com.google.vr.ndk.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.vr.cardboard.VrParamsProvider;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vr.cardboard.VrSettingsProviderContract;
import com.google.vr.cardboard.annotations.UsedByReflection;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import com.google.vr.vrcore.common.api.ITransitionCallbacks;
import com.google.vr.vrcore.common.api.IVrCoreSdkService;
import com.google.vr.vrcore.common.api.SdkServiceConsts;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import java.util.ArrayList;
import java.util.List;
@UsedByReflection("IAP")
@TargetApi(24)
/* loaded from: classes.dex */
public class DaydreamApi implements AutoCloseable {
    private static final String DAYDREAM_CATEGORY = "com.google.intent.category.DAYDREAM";
    private static final int MIN_API_FOR_HEADSET_INSERTION = 11;
    private static final int MIN_VRCORE_API_VERSION = 8;
    private static final String TAG = DaydreamApi.class.getSimpleName();
    private boolean closed;
    private final Context context;
    private IDaydreamManager daydreamManager;
    private int vrCoreApiVersion;
    private IVrCoreSdkService vrCoreSdkService;
    private ArrayList<Runnable> queuedRunnables = new ArrayList<>();
    private final ServiceConnection connection = new ServiceConnection() { // from class: com.google.vr.ndk.base.DaydreamApi.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DaydreamApi.this.vrCoreSdkService = IVrCoreSdkService.Stub.asInterface(iBinder);
            try {
                DaydreamApi.this.daydreamManager = DaydreamApi.this.vrCoreSdkService.getDaydreamManager();
            } catch (RemoteException e) {
                Log.e(DaydreamApi.TAG, "RemoteException in onServiceConnected");
            }
            if (DaydreamApi.this.daydreamManager == null) {
                Log.w(DaydreamApi.TAG, "Daydream service component unavailable.");
            }
            ArrayList arrayList = DaydreamApi.this.queuedRunnables;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                ((Runnable) obj).run();
            }
            DaydreamApi.this.queuedRunnables.clear();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            DaydreamApi.this.vrCoreSdkService = null;
        }
    };

    private DaydreamApi(Context context) {
        this.context = context;
    }

    private void checkIntent(Intent intent) throws ActivityNotFoundException {
        List<ResolveInfo> queryIntentActivities = this.context.getPackageManager().queryIntentActivities(intent, 0);
        if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
            String valueOf = String.valueOf(intent);
            throw new ActivityNotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 43).append("No activity is available to handle intent: ").append(valueOf).toString());
        }
    }

    private void checkNotClosed() {
        if (this.closed) {
            throw new IllegalStateException("DaydreamApi object is closed and can no longer be used.");
        }
    }

    @UsedByReflection("IAP")
    public static DaydreamApi create(Context context) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            if (!DaydreamUtils.isDaydreamPhone(context)) {
                Log.i(TAG, "Phone is not Daydream-compatible");
                return null;
            }
            DaydreamApi daydreamApi = new DaydreamApi(context);
            if (daydreamApi.init()) {
                return daydreamApi;
            }
            Log.w(TAG, "Failed to initialize DaydreamApi object.");
            return null;
        }
        throw new IllegalStateException("DaydreamApi must only be used from the main thread.");
    }

    @UsedByReflection("IAP")
    public static Intent createVrIntent(ComponentName componentName) {
        Intent intent = new Intent();
        intent.setComponent(componentName);
        return setupVrIntent(intent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:37:0x006c  */
    /* JADX WARN: Type inference failed for: r0v9, types: [android.content.ContentProviderClient] */
    /* JADX WARN: Type inference failed for: r1v2, types: [android.net.Uri] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v6, types: [android.database.Cursor] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean getDaydreamSetupCompleted(android.content.Context r9) {
        /*
            r7 = 1
            r6 = 0
            r8 = 0
            com.google.vr.cardboard.VrParamsProviderFactory$ContentProviderClientHandle r0 = com.google.vr.cardboard.VrParamsProviderFactory.tryToGetContentProviderClientHandle(r9)
            if (r0 == 0) goto L21
            java.lang.String r1 = r0.authority
            java.lang.String r2 = "daydream_setup"
            android.net.Uri r1 = com.google.vr.cardboard.VrSettingsProviderContract.createUri(r1, r2)
            android.content.ContentProviderClient r0 = r0.client     // Catch: android.os.RemoteException -> L45 java.lang.SecurityException -> L56 java.lang.Throwable -> L67
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch: android.os.RemoteException -> L45 java.lang.SecurityException -> L56 java.lang.Throwable -> L67
            if (r1 != 0) goto L2a
        L1e:
            if (r1 != 0) goto L41
        L20:
            return r6
        L21:
            java.lang.String r0 = com.google.vr.ndk.base.DaydreamApi.TAG
            java.lang.String r1 = "No ContentProvider available for Daydream setup."
            android.util.Log.e(r0, r1)
            return r6
        L2a:
            boolean r0 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L70 java.lang.SecurityException -> L72 android.os.RemoteException -> L74
            if (r0 == 0) goto L1e
            r0 = 0
            int r0 = r1.getInt(r0)     // Catch: java.lang.Throwable -> L70 java.lang.SecurityException -> L72 android.os.RemoteException -> L74
            if (r0 == r7) goto L3b
            r0 = r6
        L38:
            if (r1 != 0) goto L3d
        L3a:
            return r0
        L3b:
            r0 = r7
            goto L38
        L3d:
            r1.close()
            goto L3a
        L41:
            r1.close()
            goto L20
        L45:
            r0 = move-exception
            r1 = r8
        L47:
            java.lang.String r2 = com.google.vr.ndk.base.DaydreamApi.TAG     // Catch: java.lang.Throwable -> L70
            java.lang.String r3 = "Failed to read Daydream setup completion from ContentProvider"
            android.util.Log.e(r2, r3, r0)     // Catch: java.lang.Throwable -> L70
            if (r1 != 0) goto L52
        L51:
            return r6
        L52:
            r1.close()
            goto L51
        L56:
            r0 = move-exception
            r1 = r8
        L58:
            java.lang.String r2 = com.google.vr.ndk.base.DaydreamApi.TAG     // Catch: java.lang.Throwable -> L70
            java.lang.String r3 = "Insufficient permissions to read Daydream setup completion from ContentProvider"
            android.util.Log.e(r2, r3, r0)     // Catch: java.lang.Throwable -> L70
            if (r1 != 0) goto L63
        L62:
            return r6
        L63:
            r1.close()
            goto L62
        L67:
            r0 = move-exception
            r1 = r8
        L69:
            if (r1 != 0) goto L6c
        L6b:
            throw r0
        L6c:
            r1.close()
            goto L6b
        L70:
            r0 = move-exception
            goto L69
        L72:
            r0 = move-exception
            goto L58
        L74:
            r0 = move-exception
            goto L47
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.DaydreamApi.getDaydreamSetupCompleted(android.content.Context):boolean");
    }

    private boolean init() {
        try {
            this.vrCoreApiVersion = VrCoreUtils.getVrCoreClientApiVersion(this.context);
        } catch (VrCoreNotAvailableException e) {
            String str = TAG;
            String valueOf = String.valueOf(e);
            Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 22).append("VrCore not available: ").append(valueOf).toString());
        }
        if (this.vrCoreApiVersion < 8) {
            Log.e(TAG, new StringBuilder(79).append("VrCore out of date, current version: ").append(this.vrCoreApiVersion).append(", required version: 8").toString());
            return false;
        }
        Intent intent = new Intent(SdkServiceConsts.BIND_INTENT_ACTION);
        intent.setPackage("com.google.vr.vrcore");
        if (this.context.bindService(intent, this.connection, 1)) {
            return true;
        }
        Log.e(TAG, "Unable to bind to VrCoreSdkService");
        return false;
    }

    public static boolean isDaydreamReadyPlatform(Context context) {
        return DaydreamUtils.isDaydreamPhone(context);
    }

    private void launchInVr(final PendingIntent pendingIntent, final ComponentName componentName) {
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.3
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        DaydreamApi.this.daydreamManager.launchInVr(pendingIntent, componentName);
                        return;
                    } catch (RemoteException e) {
                        Log.e(DaydreamApi.TAG, "RemoteException while launching PendingIntent in VR.", e);
                        return;
                    }
                }
                Log.w(DaydreamApi.TAG, "Can't launch PendingIntent via DaydreamManager: not available.");
                try {
                    pendingIntent.send();
                } catch (Exception e2) {
                    Log.e(DaydreamApi.TAG, "Couldn't launch PendingIntent: ", e2);
                }
            }
        });
    }

    private void launchTransitionCallbackInVr(final ITransitionCallbacks iTransitionCallbacks) {
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.4
            @Override // java.lang.Runnable
            public void run() {
                boolean z;
                if (DaydreamApi.this.daydreamManager == null) {
                    z = false;
                } else {
                    try {
                        z = DaydreamApi.this.daydreamManager.launchVrTransition(iTransitionCallbacks);
                    } catch (RemoteException e) {
                        Log.e(DaydreamApi.TAG, "RemoteException while launching VR transition: ", e);
                        z = false;
                    }
                }
                if (z) {
                    return;
                }
                Log.w(DaydreamApi.TAG, "Can't launch callbacks via DaydreamManager, sending manually");
                try {
                    iTransitionCallbacks.onTransitionComplete();
                } catch (RemoteException e2) {
                }
            }
        });
    }

    private void runWhenServiceConnected(Runnable runnable) {
        if (this.vrCoreSdkService == null) {
            this.queuedRunnables.add(runnable);
        } else {
            runnable.run();
        }
    }

    public static boolean setDaydreamSetupCompleted(Context context, boolean z) {
        VrParamsProviderFactory.ContentProviderClientHandle tryToGetContentProviderClientHandle = VrParamsProviderFactory.tryToGetContentProviderClientHandle(context);
        if (tryToGetContentProviderClientHandle == null) {
            Log.e(TAG, "No ContentProvider available for Daydream setup.");
            return false;
        }
        Uri createUri = VrSettingsProviderContract.createUri(tryToGetContentProviderClientHandle.authority, VrSettingsProviderContract.DAYDREAM_SETUP_COMPLETED);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(VrSettingsProviderContract.SETTING_VALUE_KEY, Boolean.valueOf(z));
            return tryToGetContentProviderClientHandle.client.update(createUri, contentValues, null, null) > 0;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to indicate Daydream setup completion to ContentProvider", e);
            return false;
        } catch (SecurityException e2) {
            Log.e(TAG, "Insufficient permissions to indicate Daydream setup completion to ContentProvider", e2);
            return false;
        }
    }

    @UsedByReflection("IAP")
    public static Intent setupVrIntent(Intent intent) {
        intent.addCategory("com.google.intent.category.DAYDREAM");
        intent.addFlags(335609856);
        return intent;
    }

    @Override // java.lang.AutoCloseable
    @UsedByReflection("IAP")
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.12
            @Override // java.lang.Runnable
            public void run() {
                DaydreamApi.this.context.unbindService(DaydreamApi.this.connection);
                DaydreamApi.this.vrCoreSdkService = null;
            }
        });
    }

    @UsedByReflection("IAP")
    public void exitFromVr(Activity activity, int i, Intent intent) {
        checkNotClosed();
        if (intent == null) {
            intent = new Intent();
        }
        final PendingIntent createPendingResult = activity.createPendingResult(i, intent, 1073741824);
        final Runnable runnable = new Runnable(this) { // from class: com.google.vr.ndk.base.DaydreamApi.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    createPendingResult.send(0);
                } catch (Exception e) {
                    String str = DaydreamApi.TAG;
                    String valueOf = String.valueOf(e);
                    Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 31).append("Couldn't launch PendingIntent: ").append(valueOf).toString());
                }
            }
        };
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.8
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.daydreamManager == null) {
                    Log.w(DaydreamApi.TAG, "Failed to exit VR: Daydream service unavailable.");
                    runnable.run();
                    return;
                }
                try {
                    if (DaydreamApi.this.daydreamManager.exitFromVr(createPendingResult)) {
                        return;
                    }
                    Log.w(DaydreamApi.TAG, "Failed to exit VR: Invalid request.");
                    runnable.run();
                } catch (RemoteException e) {
                    String str = DaydreamApi.TAG;
                    String valueOf = String.valueOf(e);
                    Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 49).append("Failed to exit VR: RemoteException while exiting:").append(valueOf).toString());
                    runnable.run();
                }
            }
        });
    }

    public int getCurrentViewerType() {
        checkNotClosed();
        if (isDaydreamReadyPlatform(this.context)) {
            VrParamsProvider create = VrParamsProviderFactory.create(this.context);
            try {
                CardboardDevice.DeviceParams readDeviceParams = create.readDeviceParams();
                if (readDeviceParams != null) {
                    if (DaydreamUtils.isDaydreamViewer(readDeviceParams)) {
                        create.close();
                        return 1;
                    }
                    return 0;
                }
                return 0;
            } finally {
                create.close();
            }
        }
        return 0;
    }

    public void handleInsertionIntoHeadset(final byte[] bArr) {
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.10
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.vrCoreApiVersion < 11) {
                    String str = DaydreamApi.TAG;
                    String valueOf = String.valueOf("Can't handle insertion of phone into headset: VrCore API too old. Need: 11, found: ");
                    Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 11).append(valueOf).append(DaydreamApi.this.vrCoreApiVersion).toString());
                } else if (DaydreamApi.this.daydreamManager == null) {
                    Log.e(DaydreamApi.TAG, "Can't handle insertion of phone into headset: no DaydreamManager.");
                } else {
                    try {
                        DaydreamApi.this.daydreamManager.handleInsertionIntoHeadset(bArr);
                    } catch (RemoteException e) {
                        Log.e(DaydreamApi.TAG, "RemoteException while notifying phone insertion.", e);
                    } catch (SecurityException e2) {
                        Log.e(DaydreamApi.TAG, "SecurityException when notifying phone insertion. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", e2);
                    }
                }
            }
        });
    }

    public void handleRemovalFromHeadset() {
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.11
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.vrCoreApiVersion < 11) {
                    String str = DaydreamApi.TAG;
                    String valueOf = String.valueOf("Can't handle removal of phone from headset: VrCore API too old. Need: 11, found: ");
                    Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 11).append(valueOf).append(DaydreamApi.this.vrCoreApiVersion).toString());
                } else if (DaydreamApi.this.daydreamManager == null) {
                    Log.e(DaydreamApi.TAG, "Can't handle removal of phone from headset: no DaydreamManager.");
                } else {
                    try {
                        DaydreamApi.this.daydreamManager.handleRemovalFromHeadset();
                    } catch (RemoteException e) {
                        Log.e(DaydreamApi.TAG, "RemoteException while notifying phone removal.", e);
                    } catch (SecurityException e2) {
                        Log.e(DaydreamApi.TAG, "SecurityException when notifying phone removal. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", e2);
                    }
                }
            }
        });
    }

    @UsedByReflection("IAP")
    public void launchInVr(PendingIntent pendingIntent) {
        checkNotClosed();
        launchInVr(pendingIntent, null);
    }

    @UsedByReflection("IAP")
    public void launchInVr(ComponentName componentName) throws ActivityNotFoundException {
        checkNotClosed();
        if (componentName == null) {
            throw new IllegalArgumentException("Null argument 'componentName' passed to launchInVr");
        }
        Intent createVrIntent = createVrIntent(componentName);
        checkIntent(createVrIntent);
        launchInVr(PendingIntent.getActivity(this.context, 0, createVrIntent, 1073741824), createVrIntent.getComponent());
    }

    @UsedByReflection("IAP")
    public void launchInVr(Intent intent) throws ActivityNotFoundException {
        checkNotClosed();
        if (intent == null) {
            throw new IllegalArgumentException("Null argument 'intent' passed to launchInVr");
        }
        checkIntent(intent);
        launchInVr(PendingIntent.getActivity(this.context, 0, intent, 1207959552), intent.getComponent());
    }

    @UsedByReflection("IAP")
    public void launchInVrForResult(final Activity activity, final PendingIntent pendingIntent, final int i) {
        checkNotClosed();
        launchTransitionCallbackInVr(new ITransitionCallbacks.Stub(this) { // from class: com.google.vr.ndk.base.DaydreamApi.6
            @Override // com.google.vr.vrcore.common.api.ITransitionCallbacks
            public void onTransitionComplete() {
                activity.runOnUiThread(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), i, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            String str = DaydreamApi.TAG;
                            String valueOf = String.valueOf(e);
                            Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 43).append("Exception while starting next VR activity: ").append(valueOf).toString());
                        }
                    }
                });
            }
        });
    }

    @UsedByReflection("IAP")
    public void launchVrHomescreen() {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.5
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.daydreamManager == null) {
                    Log.e(DaydreamApi.TAG, "Can't launch VR homescreen via DaydreamManager. Giving up trying to leave current VR activity...");
                    return;
                }
                try {
                    if (DaydreamApi.this.daydreamManager.launchVrHome()) {
                        return;
                    }
                    Log.e(DaydreamApi.TAG, "There is no VR homescreen installed.");
                } catch (RemoteException e) {
                    String str = DaydreamApi.TAG;
                    String valueOf = String.valueOf(e);
                    Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 47).append("RemoteException while launching VR homescreen: ").append(valueOf).toString());
                }
            }
        });
    }

    public void registerDaydreamIntent(final PendingIntent pendingIntent) {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.2
            @Override // java.lang.Runnable
            public void run() {
                if (DaydreamApi.this.daydreamManager == null) {
                    Log.w(DaydreamApi.TAG, "Can't register/unregister daydream intent: no DaydreamManager.");
                    return;
                }
                try {
                    if (pendingIntent == null) {
                        DaydreamApi.this.daydreamManager.unregisterDaydreamIntent();
                    } else {
                        DaydreamApi.this.daydreamManager.registerDaydreamIntent(pendingIntent);
                    }
                } catch (RemoteException e) {
                    Log.e(DaydreamApi.TAG, "Error when attempting to register/unregister daydream intent: ", e);
                }
            }
        });
    }

    public void setInhibitSystemButtons(final ComponentName componentName, final boolean z) {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() { // from class: com.google.vr.ndk.base.DaydreamApi.9
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SdkServiceConsts.OPTION_INHIBIT_SYSTEM_BUTTONS, z);
                    if (DaydreamApi.this.vrCoreSdkService.setClientOptions(componentName, bundle)) {
                        return;
                    }
                    Log.w(DaydreamApi.TAG, "Failed to set client options to inhibit system button.");
                } catch (RemoteException e) {
                    Log.e(DaydreamApi.TAG, "RemoteException while setting client options.", e);
                }
            }
        });
    }

    public void unregisterDaydreamIntent() {
        checkNotClosed();
        registerDaydreamIntent(null);
    }
}
