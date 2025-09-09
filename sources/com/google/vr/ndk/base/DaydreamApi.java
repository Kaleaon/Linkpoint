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

@TargetApi(24)
@UsedByReflection("IAP")
public class DaydreamApi implements AutoCloseable {
    private static final String DAYDREAM_CATEGORY = "com.google.intent.category.DAYDREAM";
    private static final int MIN_API_FOR_HEADSET_INSERTION = 11;
    private static final int MIN_VRCORE_API_VERSION = 8;
    /* access modifiers changed from: private */
    public static final String TAG = DaydreamApi.class.getSimpleName();
    private boolean closed;
    /* access modifiers changed from: private */
    public final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IVrCoreSdkService unused = DaydreamApi.this.vrCoreSdkService = IVrCoreSdkService.Stub.asInterface(iBinder);
            try {
                IDaydreamManager unused2 = DaydreamApi.this.daydreamManager = DaydreamApi.this.vrCoreSdkService.getDaydreamManager();
            } catch (RemoteException e) {
                Log.e(DaydreamApi.TAG, "RemoteException in onServiceConnected");
            }
            if (DaydreamApi.this.daydreamManager == null) {
                Log.w(DaydreamApi.TAG, "Daydream service component unavailable.");
            }
            ArrayList access$300 = DaydreamApi.this.queuedRunnables;
            int size = access$300.size();
            int i = 0;
            while (i < size) {
                Object obj = access$300.get(i);
                i++;
                ((Runnable) obj).run();
            }
            DaydreamApi.this.queuedRunnables.clear();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IVrCoreSdkService unused = DaydreamApi.this.vrCoreSdkService = null;
        }
    };
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public IDaydreamManager daydreamManager;
    /* access modifiers changed from: private */
    public ArrayList<Runnable> queuedRunnables = new ArrayList<>();
    /* access modifiers changed from: private */
    public int vrCoreApiVersion;
    /* access modifiers changed from: private */
    public IVrCoreSdkService vrCoreSdkService;

    private DaydreamApi(Context context2) {
        this.context = context2;
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
    public static DaydreamApi create(Context context2) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("DaydreamApi must only be used from the main thread.");
        } else if (DaydreamUtils.isDaydreamPhone(context2)) {
            DaydreamApi daydreamApi = new DaydreamApi(context2);
            if (daydreamApi.init()) {
                return daydreamApi;
            }
            Log.w(TAG, "Failed to initialize DaydreamApi object.");
            return null;
        } else {
            Log.i(TAG, "Phone is not Daydream-compatible");
            return null;
        }
    }

    @UsedByReflection("IAP")
    public static Intent createVrIntent(ComponentName componentName) {
        Intent intent = new Intent();
        intent.setComponent(componentName);
        return setupVrIntent(intent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x006c  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:23:0x0047=Splitter:B:23:0x0047, B:30:0x0058=Splitter:B:30:0x0058} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean getDaydreamSetupCompleted(android.content.Context r9) {
        /*
            r7 = 1
            r6 = 0
            r8 = 0
            com.google.vr.cardboard.VrParamsProviderFactory$ContentProviderClientHandle r0 = com.google.vr.cardboard.VrParamsProviderFactory.tryToGetContentProviderClientHandle(r9)
            if (r0 == 0) goto L_0x0021
            java.lang.String r1 = r0.authority
            java.lang.String r2 = "daydream_setup"
            android.net.Uri r1 = com.google.vr.cardboard.VrSettingsProviderContract.createUri(r1, r2)
            android.content.ContentProviderClient r0 = r0.client     // Catch:{ RemoteException -> 0x0045, SecurityException -> 0x0056, all -> 0x0067 }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ RemoteException -> 0x0045, SecurityException -> 0x0056, all -> 0x0067 }
            if (r1 != 0) goto L_0x002a
        L_0x001e:
            if (r1 != 0) goto L_0x0041
        L_0x0020:
            return r6
        L_0x0021:
            java.lang.String r0 = TAG
            java.lang.String r1 = "No ContentProvider available for Daydream setup."
            android.util.Log.e(r0, r1)
            return r6
        L_0x002a:
            boolean r0 = r1.moveToFirst()     // Catch:{ RemoteException -> 0x0074, SecurityException -> 0x0072 }
            if (r0 == 0) goto L_0x001e
            r0 = 0
            int r0 = r1.getInt(r0)     // Catch:{ RemoteException -> 0x0074, SecurityException -> 0x0072 }
            if (r0 == r7) goto L_0x003b
            r0 = r6
        L_0x0038:
            if (r1 != 0) goto L_0x003d
        L_0x003a:
            return r0
        L_0x003b:
            r0 = r7
            goto L_0x0038
        L_0x003d:
            r1.close()
            goto L_0x003a
        L_0x0041:
            r1.close()
            goto L_0x0020
        L_0x0045:
            r0 = move-exception
            r1 = r8
        L_0x0047:
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0070 }
            java.lang.String r3 = "Failed to read Daydream setup completion from ContentProvider"
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0070 }
            if (r1 != 0) goto L_0x0052
        L_0x0051:
            return r6
        L_0x0052:
            r1.close()
            goto L_0x0051
        L_0x0056:
            r0 = move-exception
            r1 = r8
        L_0x0058:
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0070 }
            java.lang.String r3 = "Insufficient permissions to read Daydream setup completion from ContentProvider"
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0070 }
            if (r1 != 0) goto L_0x0063
        L_0x0062:
            return r6
        L_0x0063:
            r1.close()
            goto L_0x0062
        L_0x0067:
            r0 = move-exception
            r1 = r8
        L_0x0069:
            if (r1 != 0) goto L_0x006c
        L_0x006b:
            throw r0
        L_0x006c:
            r1.close()
            goto L_0x006b
        L_0x0070:
            r0 = move-exception
            goto L_0x0069
        L_0x0072:
            r0 = move-exception
            goto L_0x0058
        L_0x0074:
            r0 = move-exception
            goto L_0x0047
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.DaydreamApi.getDaydreamSetupCompleted(android.content.Context):boolean");
    }

    private boolean init() {
        try {
            this.vrCoreApiVersion = VrCoreUtils.getVrCoreClientApiVersion(this.context);
            if (this.vrCoreApiVersion >= 8) {
                Intent intent = new Intent(SdkServiceConsts.BIND_INTENT_ACTION);
                intent.setPackage("com.google.vr.vrcore");
                if (this.context.bindService(intent, this.connection, 1)) {
                    return true;
                }
                Log.e(TAG, "Unable to bind to VrCoreSdkService");
                return false;
            }
            Log.e(TAG, new StringBuilder(79).append("VrCore out of date, current version: ").append(this.vrCoreApiVersion).append(", required version: 8").toString());
            return false;
        } catch (VrCoreNotAvailableException e) {
            String str = TAG;
            String valueOf = String.valueOf(e);
            Log.e(str, new StringBuilder(String.valueOf(valueOf).length() + 22).append("VrCore not available: ").append(valueOf).toString());
        }
    }

    public static boolean isDaydreamReadyPlatform(Context context2) {
        return DaydreamUtils.isDaydreamPhone(context2);
    }

    private void launchInVr(final PendingIntent pendingIntent, final ComponentName componentName) {
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.daydreamManager == null) {
                    Log.w(DaydreamApi.TAG, "Can't launch PendingIntent via DaydreamManager: not available.");
                    try {
                        pendingIntent.send();
                    } catch (Exception e) {
                        Log.e(DaydreamApi.TAG, "Couldn't launch PendingIntent: ", e);
                    }
                } else {
                    try {
                        DaydreamApi.this.daydreamManager.launchInVr(pendingIntent, componentName);
                    } catch (RemoteException e2) {
                        Log.e(DaydreamApi.TAG, "RemoteException while launching PendingIntent in VR.", e2);
                    }
                }
            }
        });
    }

    private void launchTransitionCallbackInVr(final ITransitionCallbacks iTransitionCallbacks) {
        runWhenServiceConnected(new Runnable() {
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
                if (!z) {
                    Log.w(DaydreamApi.TAG, "Can't launch callbacks via DaydreamManager, sending manually");
                    try {
                        iTransitionCallbacks.onTransitionComplete();
                    } catch (RemoteException e2) {
                    }
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

    public static boolean setDaydreamSetupCompleted(Context context2, boolean z) {
        VrParamsProviderFactory.ContentProviderClientHandle tryToGetContentProviderClientHandle = VrParamsProviderFactory.tryToGetContentProviderClientHandle(context2);
        if (tryToGetContentProviderClientHandle != null) {
            Uri createUri = VrSettingsProviderContract.createUri(tryToGetContentProviderClientHandle.authority, VrSettingsProviderContract.DAYDREAM_SETUP_COMPLETED);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(VrSettingsProviderContract.SETTING_VALUE_KEY, Boolean.valueOf(z));
                return tryToGetContentProviderClientHandle.client.update(createUri, contentValues, (String) null, (String[]) null) > 0;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to indicate Daydream setup completion to ContentProvider", e);
                return false;
            } catch (SecurityException e2) {
                Log.e(TAG, "Insufficient permissions to indicate Daydream setup completion to ContentProvider", e2);
                return false;
            }
        } else {
            Log.e(TAG, "No ContentProvider available for Daydream setup.");
            return false;
        }
    }

    @UsedByReflection("IAP")
    public static Intent setupVrIntent(Intent intent) {
        intent.addCategory("com.google.intent.category.DAYDREAM");
        intent.addFlags(335609856);
        return intent;
    }

    @UsedByReflection("IAP")
    public void close() {
        if (!this.closed) {
            this.closed = true;
            runWhenServiceConnected(new Runnable() {
                public void run() {
                    DaydreamApi.this.context.unbindService(DaydreamApi.this.connection);
                    IVrCoreSdkService unused = DaydreamApi.this.vrCoreSdkService = null;
                }
            });
        }
    }

    @UsedByReflection("IAP")
    public void exitFromVr(Activity activity, int i, Intent intent) {
        checkNotClosed();
        if (intent == null) {
            intent = new Intent();
        }
        final PendingIntent createPendingResult = activity.createPendingResult(i, intent, 1073741824);
        final AnonymousClass7 r1 = new Runnable(this) {
            public void run() {
                try {
                    createPendingResult.send(0);
                } catch (Exception e) {
                    String access$200 = DaydreamApi.TAG;
                    String valueOf = String.valueOf(e);
                    Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 31).append("Couldn't launch PendingIntent: ").append(valueOf).toString());
                }
            }
        };
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        if (!DaydreamApi.this.daydreamManager.exitFromVr(createPendingResult)) {
                            Log.w(DaydreamApi.TAG, "Failed to exit VR: Invalid request.");
                            r1.run();
                        }
                    } catch (RemoteException e) {
                        String access$200 = DaydreamApi.TAG;
                        String valueOf = String.valueOf(e);
                        Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 49).append("Failed to exit VR: RemoteException while exiting:").append(valueOf).toString());
                        r1.run();
                    }
                } else {
                    Log.w(DaydreamApi.TAG, "Failed to exit VR: Daydream service unavailable.");
                    r1.run();
                }
            }
        });
    }

    public int getCurrentViewerType() {
        checkNotClosed();
        if (!isDaydreamReadyPlatform(this.context)) {
            return 0;
        }
        VrParamsProvider create = VrParamsProviderFactory.create(this.context);
        try {
            CardboardDevice.DeviceParams readDeviceParams = create.readDeviceParams();
            if (readDeviceParams == null) {
                create.close();
                return 0;
            } else if (!DaydreamUtils.isDaydreamViewer(readDeviceParams)) {
                return 0;
            } else {
                create.close();
                return 1;
            }
        } finally {
            create.close();
        }
    }

    public void handleInsertionIntoHeadset(final byte[] bArr) {
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.vrCoreApiVersion < 11) {
                    String access$200 = DaydreamApi.TAG;
                    String valueOf = String.valueOf("Can't handle insertion of phone into headset: VrCore API too old. Need: 11, found: ");
                    Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 11).append(valueOf).append(DaydreamApi.this.vrCoreApiVersion).toString());
                } else if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        DaydreamApi.this.daydreamManager.handleInsertionIntoHeadset(bArr);
                    } catch (SecurityException e) {
                        Log.e(DaydreamApi.TAG, "SecurityException when notifying phone insertion. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", e);
                    } catch (RemoteException e2) {
                        Log.e(DaydreamApi.TAG, "RemoteException while notifying phone insertion.", e2);
                    }
                } else {
                    Log.e(DaydreamApi.TAG, "Can't handle insertion of phone into headset: no DaydreamManager.");
                }
            }
        });
    }

    public void handleRemovalFromHeadset() {
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.vrCoreApiVersion < 11) {
                    String access$200 = DaydreamApi.TAG;
                    String valueOf = String.valueOf("Can't handle removal of phone from headset: VrCore API too old. Need: 11, found: ");
                    Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 11).append(valueOf).append(DaydreamApi.this.vrCoreApiVersion).toString());
                } else if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        DaydreamApi.this.daydreamManager.handleRemovalFromHeadset();
                    } catch (SecurityException e) {
                        Log.e(DaydreamApi.TAG, "SecurityException when notifying phone removal. Check that the calling app is in the system image (must have the SYSTEM flag in package manager).", e);
                    } catch (RemoteException e2) {
                        Log.e(DaydreamApi.TAG, "RemoteException while notifying phone removal.", e2);
                    }
                } else {
                    Log.e(DaydreamApi.TAG, "Can't handle removal of phone from headset: no DaydreamManager.");
                }
            }
        });
    }

    @UsedByReflection("IAP")
    public void launchInVr(PendingIntent pendingIntent) {
        checkNotClosed();
        launchInVr(pendingIntent, (ComponentName) null);
    }

    @UsedByReflection("IAP")
    public void launchInVr(ComponentName componentName) throws ActivityNotFoundException {
        checkNotClosed();
        if (componentName != null) {
            Intent createVrIntent = createVrIntent(componentName);
            checkIntent(createVrIntent);
            launchInVr(PendingIntent.getActivity(this.context, 0, createVrIntent, 1073741824), createVrIntent.getComponent());
            return;
        }
        throw new IllegalArgumentException("Null argument 'componentName' passed to launchInVr");
    }

    @UsedByReflection("IAP")
    public void launchInVr(Intent intent) throws ActivityNotFoundException {
        checkNotClosed();
        if (intent != null) {
            checkIntent(intent);
            launchInVr(PendingIntent.getActivity(this.context, 0, intent, 1207959552), intent.getComponent());
            return;
        }
        throw new IllegalArgumentException("Null argument 'intent' passed to launchInVr");
    }

    @UsedByReflection("IAP")
    public void launchInVrForResult(final Activity activity, final PendingIntent pendingIntent, final int i) {
        checkNotClosed();
        launchTransitionCallbackInVr(new ITransitionCallbacks.Stub(this) {
            public void onTransitionComplete() {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), i, (Intent) null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            String access$200 = DaydreamApi.TAG;
                            String valueOf = String.valueOf(e);
                            Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 43).append("Exception while starting next VR activity: ").append(valueOf).toString());
                        }
                    }
                });
            }
        });
    }

    @UsedByReflection("IAP")
    public void launchVrHomescreen() {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        if (!DaydreamApi.this.daydreamManager.launchVrHome()) {
                            Log.e(DaydreamApi.TAG, "There is no VR homescreen installed.");
                        }
                    } catch (RemoteException e) {
                        String access$200 = DaydreamApi.TAG;
                        String valueOf = String.valueOf(e);
                        Log.e(access$200, new StringBuilder(String.valueOf(valueOf).length() + 47).append("RemoteException while launching VR homescreen: ").append(valueOf).toString());
                    }
                } else {
                    Log.e(DaydreamApi.TAG, "Can't launch VR homescreen via DaydreamManager. Giving up trying to leave current VR activity...");
                }
            }
        });
    }

    public void registerDaydreamIntent(final PendingIntent pendingIntent) {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() {
            public void run() {
                if (DaydreamApi.this.daydreamManager != null) {
                    try {
                        if (pendingIntent == null) {
                            DaydreamApi.this.daydreamManager.unregisterDaydreamIntent();
                        } else {
                            DaydreamApi.this.daydreamManager.registerDaydreamIntent(pendingIntent);
                        }
                    } catch (RemoteException e) {
                        Log.e(DaydreamApi.TAG, "Error when attempting to register/unregister daydream intent: ", e);
                    }
                } else {
                    Log.w(DaydreamApi.TAG, "Can't register/unregister daydream intent: no DaydreamManager.");
                }
            }
        });
    }

    public void setInhibitSystemButtons(final ComponentName componentName, final boolean z) {
        checkNotClosed();
        runWhenServiceConnected(new Runnable() {
            public void run() {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SdkServiceConsts.OPTION_INHIBIT_SYSTEM_BUTTONS, z);
                    if (!DaydreamApi.this.vrCoreSdkService.setClientOptions(componentName, bundle)) {
                        Log.w(DaydreamApi.TAG, "Failed to set client options to inhibit system button.");
                    }
                } catch (RemoteException e) {
                    Log.e(DaydreamApi.TAG, "RemoteException while setting client options.", e);
                }
            }
        });
    }

    public void unregisterDaydreamIntent() {
        checkNotClosed();
        registerDaydreamIntent((PendingIntent) null);
    }
}
