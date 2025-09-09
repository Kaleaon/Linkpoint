package com.google.vr.ndk.base;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.cardboard.R;
import com.google.vr.cardboard.UiUtils;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import com.google.vr.vrcore.common.api.HeadTrackingState;
import com.google.vr.vrcore.common.api.IDaydreamListener;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import com.google.vr.vrcore.common.api.IVrCoreSdkService;
import com.google.vr.vrcore.common.api.SdkServiceConsts;
import java.lang.ref.WeakReference;

class VrCoreSdkClient {
    private static final boolean DEBUG = false;
    private static final int FADE_FLUSH_DELAY_FOR_TRACKING_STABILIZATION_MILLIS = 200;
    static final int MIN_VRCORE_API_VERSION = 5;
    private static final String TAG = "VrCoreSdkClient";
    static final int TARGET_VRCORE_API_VERSION = 10;
    private final Runnable closeVrRunnable;
    /* access modifiers changed from: private */
    public final ComponentName componentName;
    private final Context context;
    /* access modifiers changed from: private */
    public final DaydreamListenerImpl daydreamListener;
    /* access modifiers changed from: private */
    public IDaydreamManager daydreamManager;
    private final DaydreamUtilsWrapper daydreamUtils;
    private final FadeOverlayView fadeOverlayView;
    private final GvrApi gvrApi;
    private AlertDialog helpCenterDialog;
    private boolean isBound;
    private boolean isEnabled = true;
    private boolean isResumed;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            HeadTrackingState headTrackingState = null;
            IVrCoreSdkService asInterface = IVrCoreSdkService.Stub.asInterface(iBinder);
            try {
                if (asInterface.initialize(10)) {
                    IVrCoreSdkService unused = VrCoreSdkClient.this.vrCoreSdkService = asInterface;
                    try {
                        IDaydreamManager unused2 = VrCoreSdkClient.this.daydreamManager = VrCoreSdkClient.this.vrCoreSdkService.getDaydreamManager();
                        if (VrCoreSdkClient.this.daydreamManager != null) {
                            VrCoreSdkClient.this.daydreamManager.registerListener(VrCoreSdkClient.this.componentName, VrCoreSdkClient.this.daydreamListener);
                            try {
                                HeadTrackingState headTrackingState2 = VrCoreSdkClient.this.getHeadTrackingState();
                                int prepareVr = VrCoreSdkClient.this.daydreamManager.prepareVr(VrCoreSdkClient.this.componentName, headTrackingState2);
                                if (prepareVr != 2) {
                                    if (prepareVr == 0) {
                                        headTrackingState = headTrackingState2;
                                    }
                                    return;
                                }
                                Log.e(VrCoreSdkClient.TAG, "Daydream VR preparation failed, closing VR session.");
                                VrCoreSdkClient.this.handlePrepareVrFailed();
                                VrCoreSdkClient.this.resumeTracking(headTrackingState);
                            } catch (RemoteException e) {
                                String valueOf = String.valueOf(e);
                                Log.w(VrCoreSdkClient.TAG, new StringBuilder(String.valueOf(valueOf).length() + 61).append("Error while registering listener with the VrCore SDK Service:").append(valueOf).toString());
                            } finally {
                                VrCoreSdkClient.this.resumeTracking(headTrackingState);
                            }
                        } else {
                            Log.w(VrCoreSdkClient.TAG, "Failed to obtain DaydreamManager from VrCore SDK Service.");
                            VrCoreSdkClient.this.handleNoDaydreamManager();
                        }
                    } catch (RemoteException e2) {
                        String valueOf2 = String.valueOf(e2);
                        Log.w(VrCoreSdkClient.TAG, new StringBuilder(String.valueOf(valueOf2).length() + 57).append("Failed to obtain DaydreamManager from VrCore SDK Service:").append(valueOf2).toString());
                        VrCoreSdkClient.this.handleNoDaydreamManager();
                    }
                } else {
                    Log.e(VrCoreSdkClient.TAG, "Failed to initialize VrCore SDK Service.");
                    VrCoreSdkClient.this.handleBindFailed();
                }
            } catch (RemoteException e3) {
                String valueOf3 = String.valueOf(e3);
                Log.w(VrCoreSdkClient.TAG, new StringBuilder(String.valueOf(valueOf3).length() + 41).append("Failed to initialize VrCore SDK Service: ").append(valueOf3).toString());
                VrCoreSdkClient.this.handleBindFailed();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IVrCoreSdkService unused = VrCoreSdkClient.this.vrCoreSdkService = null;
            IDaydreamManager unused2 = VrCoreSdkClient.this.daydreamManager = null;
        }
    };
    private final boolean shouldBind;
    /* access modifiers changed from: private */
    public IVrCoreSdkService vrCoreSdkService;

    private static final class DaydreamListenerImpl extends IDaydreamListener.Stub {
        private static final long FADE_SAFEGUARD_DELAY_MILLIS = 3500;
        private static final int MSG_FADE_IN_SAFEGUARD = 2;
        private static final int MSG_TRACKING_RESUME_SAFEGUARD = 1;
        private static final long TRACKING_SAFEGUARD_DELAY_MILLIS = 3000;
        private final WeakReference<FadeOverlayView> fadeOverlayViewWeak;
        private final WeakReference<GvrApi> gvrApiWeak;
        private final Handler safeguardHandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        Log.w(VrCoreSdkClient.TAG, "Forcing tracking resume: VrCore unresponsive");
                        DaydreamListenerImpl.this.resumeHeadTrackingImpl((HeadTrackingState) null);
                        return;
                    case 2:
                        Log.w(VrCoreSdkClient.TAG, "Forcing fade in: VrCore unresponsive");
                        DaydreamListenerImpl.this.applyFadeImpl(1, 350);
                        return;
                    default:
                        super.handleMessage(message);
                        return;
                }
            }
        };

        DaydreamListenerImpl(GvrApi gvrApi, FadeOverlayView fadeOverlayView) {
            this.gvrApiWeak = new WeakReference<>(gvrApi);
            this.fadeOverlayViewWeak = new WeakReference<>(fadeOverlayView);
        }

        /* access modifiers changed from: private */
        public final void applyFadeImpl(int i, long j) {
            final FadeOverlayView fadeOverlayView = (FadeOverlayView) this.fadeOverlayViewWeak.get();
            if (fadeOverlayView != null) {
                cancelSafeguard(2);
                final int i2 = i;
                final long j2 = j;
                fadeOverlayView.post(new Runnable(this) {
                    public void run() {
                        fadeOverlayView.startFade(i2, j2);
                    }
                });
                if (i == 2) {
                    rescheduleSafeguard(2, FADE_SAFEGUARD_DELAY_MILLIS + j);
                }
            }
        }

        private final void cancelSafeguard(int i) {
            this.safeguardHandler.removeMessages(i);
        }

        private final void rescheduleSafeguard(int i, long j) {
            cancelSafeguard(i);
            this.safeguardHandler.sendEmptyMessageDelayed(i, j);
        }

        /* access modifiers changed from: private */
        public final void resumeHeadTrackingImpl(HeadTrackingState headTrackingState) {
            GvrApi gvrApi = (GvrApi) this.gvrApiWeak.get();
            if (gvrApi != null) {
                cancelSafeguard(1);
                VrCoreSdkClient.resumeTracking(gvrApi, headTrackingState);
                return;
            }
            Log.w(VrCoreSdkClient.TAG, "Invalid resumeHeadTracking() call: GvrApi no longer valid");
        }

        public final void applyFade(int i, long j) {
            applyFadeImpl(i, j);
        }

        public final void dumpDebugData() throws RemoteException {
            GvrApi gvrApi = (GvrApi) this.gvrApiWeak.get();
            if (gvrApi != null) {
                gvrApi.dumpDebugData();
            } else {
                Log.w(VrCoreSdkClient.TAG, "Invalid dumpDebugData() call: GvrApi no longer valid");
            }
        }

        public final int getTargetApiVersion() throws RemoteException {
            return 10;
        }

        public final void recenterHeadTracking() throws RemoteException {
            GvrApi gvrApi = (GvrApi) this.gvrApiWeak.get();
            if (gvrApi != null) {
                gvrApi.recenterTracking();
            } else {
                Log.w(VrCoreSdkClient.TAG, "Invalid recenterHeadTracking() call: GvrApi no longer valid");
            }
        }

        public final HeadTrackingState requestStopTracking() throws RemoteException {
            GvrApi gvrApi = (GvrApi) this.gvrApiWeak.get();
            if (gvrApi != null) {
                byte[] pauseTrackingGetState = gvrApi.pauseTrackingGetState();
                rescheduleSafeguard(1, TRACKING_SAFEGUARD_DELAY_MILLIS);
                if (pauseTrackingGetState == null) {
                    return null;
                }
                return new HeadTrackingState(pauseTrackingGetState);
            }
            Log.w(VrCoreSdkClient.TAG, "Invalid requestStopTracking() call: GvrApi no longer valid");
            return null;
        }

        /* access modifiers changed from: package-private */
        public final void resetSafeguards() {
            this.safeguardHandler.removeCallbacksAndMessages((Object) null);
        }

        public final void resumeHeadTracking(HeadTrackingState headTrackingState) {
            resumeHeadTrackingImpl(headTrackingState);
        }
    }

    public VrCoreSdkClient(Context context2, GvrApi gvrApi2, ComponentName componentName2, DaydreamUtilsWrapper daydreamUtilsWrapper, Runnable runnable, FadeOverlayView fadeOverlayView2) {
        this.context = context2;
        this.gvrApi = gvrApi2;
        this.componentName = componentName2;
        this.daydreamUtils = daydreamUtilsWrapper;
        this.closeVrRunnable = runnable;
        this.fadeOverlayView = fadeOverlayView2;
        this.daydreamListener = new DaydreamListenerImpl(gvrApi2, fadeOverlayView2);
        this.shouldBind = hasCompatibleSdkService(context2);
        gvrApi2.setIgnoreManualTrackerPauseResume(true);
    }

    private boolean doBind() {
        if (this.isBound) {
            return true;
        }
        if (this.shouldBind) {
            Intent intent = new Intent(SdkServiceConsts.BIND_INTENT_ACTION);
            intent.setPackage("com.google.vr.vrcore");
            this.isBound = this.context.bindService(intent, this.serviceConnection, 1);
        }
        if (!this.isBound) {
            handleBindFailed();
        }
        return this.isBound;
    }

    private void doUnbind() {
        if (!this.isResumed) {
            this.gvrApi.pauseTrackingGetState();
        } else {
            resumeTracking((HeadTrackingState) null);
        }
        if (this.isBound) {
            if (this.daydreamManager != null) {
                try {
                    this.daydreamManager.unregisterListener(this.componentName);
                } catch (RemoteException e) {
                    String valueOf = String.valueOf(e);
                    Log.w(TAG, new StringBuilder(String.valueOf(valueOf).length() + 40).append("Failed to unregister Daydream listener: ").append(valueOf).toString());
                }
                this.daydreamManager = null;
            }
            this.vrCoreSdkService = null;
            this.context.unbindService(this.serviceConnection);
            this.isBound = false;
        }
    }

    /* access modifiers changed from: private */
    public void handleBindFailed() {
        doUnbind();
        warnIfIncompatibleClient();
    }

    /* access modifiers changed from: private */
    public void handleNoDaydreamManager() {
        doUnbind();
        warnIfIncompatibleClient();
    }

    /* access modifiers changed from: private */
    public void handlePrepareVrFailed() {
        doUnbind();
        this.closeVrRunnable.run();
    }

    private static boolean hasCompatibleSdkService(Context context2) {
        try {
            int vrCoreClientApiVersion = VrCoreUtils.getVrCoreClientApiVersion(context2);
            if (vrCoreClientApiVersion >= 5) {
                return true;
            }
            Log.w(TAG, String.format("VrCore service obsolete, GVR SDK requires API %d but found API %d.", new Object[]{5, Integer.valueOf(vrCoreClientApiVersion)}));
            return false;
        } catch (VrCoreNotAvailableException e) {
        }
    }

    /* access modifiers changed from: private */
    public static void resumeTracking(GvrApi gvrApi2, HeadTrackingState headTrackingState) {
        byte[] bArr = null;
        if (headTrackingState != null && !headTrackingState.isEmpty()) {
            bArr = headTrackingState.getData();
        }
        gvrApi2.resumeTrackingSetState(bArr);
    }

    /* access modifiers changed from: private */
    public void resumeTracking(HeadTrackingState headTrackingState) {
        resumeTracking(this.gvrApi, headTrackingState);
        if (this.fadeOverlayView != null) {
            this.fadeOverlayView.flushAutoFade(200);
        }
    }

    private void warnIfIncompatibleClient() {
        if (this.daydreamUtils.isDaydreamPhone(this.context) || !this.daydreamUtils.isDaydreamRequiredActivity(ContextUtils.getActivity(this.context)) || ActivityManager.isRunningInTestHarness()) {
            return;
        }
        if (this.helpCenterDialog == null) {
            this.helpCenterDialog = UiUtils.showDaydreamHelpCenterDialog(this.context, R.string.dialog_title_incompatible_phone, R.string.dialog_message_incompatible_phone, this.closeVrRunnable);
        } else {
            this.helpCenterDialog.show();
        }
    }

    /* access modifiers changed from: package-private */
    public IDaydreamManager getDaydreamManager() {
        return this.daydreamManager;
    }

    /* access modifiers changed from: package-private */
    public HeadTrackingState getHeadTrackingState() {
        return new HeadTrackingState();
    }

    public boolean launchInVr(PendingIntent pendingIntent) {
        if (this.daydreamManager == null) {
            return false;
        }
        try {
            this.daydreamManager.deprecatedLaunchInVr(pendingIntent);
            return true;
        } catch (RemoteException e) {
            String valueOf = String.valueOf(e);
            Log.w(TAG, new StringBuilder(String.valueOf(valueOf).length() + 28).append("Failed to launch app in VR: ").append(valueOf).toString());
            return true;
        }
    }

    public void onPause() {
        this.isResumed = false;
        this.daydreamListener.resetSafeguards();
        if (this.isEnabled) {
            doUnbind();
        }
    }

    public boolean onResume() {
        this.isResumed = true;
        if (this.isEnabled) {
            return doBind();
        }
        return false;
    }

    public void setEnabled(boolean z) {
        if (this.isEnabled != z) {
            this.isEnabled = z;
            this.gvrApi.setIgnoreManualTrackerPauseResume(z);
            if (this.isResumed) {
                if (!this.isEnabled) {
                    doUnbind();
                } else {
                    doBind();
                }
            }
        }
    }
}
