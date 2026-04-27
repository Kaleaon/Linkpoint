package com.google.vr.ndk.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Presentation;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.cardboard.DisplaySynchronizer;
import com.google.vr.cardboard.DisplayUtils;
import com.google.vr.cardboard.EglFactory;
import com.google.vr.cardboard.MutableEglConfigChooser;
import com.google.vr.cardboard.ScanlineRacingRenderer;
import com.google.vr.ndk.base.DaydreamAlignment;
import java.util.ArrayList;
import java.util.List;

public class GvrLayout extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final int EXTERNAL_PRESENTATION_MIN_API = 16;
    private static final int SHOW_RENDERING_VIEWS_DELAY_FOR_FADE = 50;
    private static final String TAG = "GvrLayout";
    /* access modifiers changed from: private */
    public static PresentationFactory sOptionalPresentationFactory = null;
    private CardboardEmulator cardboardEmulator;
    private DaydreamAlignment daydreamAlignment;
    private DaydreamUtilsWrapper daydreamUtils;
    private DisplaySynchronizer displaySynchronizer;
    private EglFactory eglFactory;
    private FadeOverlayView fadeOverlayView;
    private GvrApi gvrApi;
    private boolean isAsyncReprojectionUsingProtectedBuffers;
    private boolean isAsyncReprojectionVideoEnabled;
    private boolean isResumed = false;
    private PresentationHelper presentationHelper;
    private FrameLayout presentationLayout;
    private View presentationView;
    /* access modifiers changed from: private */
    public ScanlineRacingRenderer scanlineRacingRenderer;
    private GvrSurfaceView scanlineRacingView;
    private final Runnable showRenderingViewsRunnable = new Runnable() {
        public void run() {
            GvrLayout.this.updateRenderingViewsVisibility(0);
        }
    };
    private boolean stereoModeEnabled = true;
    /* access modifiers changed from: private */
    public GvrUiLayout uiLayout;
    private int videoSurfaceId = -1;
    private VrCoreSdkClient vrCoreSdkClient;

    public interface ExternalSurfaceListener {
        void onFrameAvailable();

        void onSurfaceAvailable(Surface surface);
    }

    public interface ExternalSurfaceManager {
        int createExternalSurface();

        int createExternalSurface(ExternalSurfaceListener externalSurfaceListener, Handler handler);

        Surface getSurface(int i);

        int getSurfaceCount();

        void releaseExternalSurface(int i);
    }

    interface PresentationFactory {
        Presentation create(Context context, Display display);
    }

    private static class PresentationHelper implements DisplayManager.DisplayListener {
        private final Context context;
        private final DisplayManager displayManager;
        private final DisplaySynchronizer displaySynchronizer;
        private String externalDisplayName;
        private final RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(-1, -1);
        private final List<PresentationListener> listeners;
        private final FrameLayout originalParent;
        private Presentation presentation;
        private final View view;

        PresentationHelper(Context context2, FrameLayout frameLayout, View view2, DisplaySynchronizer displaySynchronizer2, String str) {
            this.context = context2;
            this.originalParent = frameLayout;
            this.view = view2;
            this.displaySynchronizer = displaySynchronizer2;
            this.externalDisplayName = str;
            this.displayManager = (DisplayManager) context2.getSystemService("display");
            this.listeners = new ArrayList();
        }

        private static void detachViewFromParent(View view2) {
            ViewGroup viewGroup = (ViewGroup) view2.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view2);
            }
        }

        private boolean hasCurrentPresentationExpired() {
            if (this.presentation != null) {
                return !this.presentation.isShowing() || !this.presentation.getDisplay().isValid();
            }
            return false;
        }

        private boolean isValidExternalDisplay(Display display) {
            return display.isValid() && display.getName().equals(this.externalDisplayName);
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0027  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00ad  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x00b5  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00cb  */
        /* JADX WARNING: Removed duplicated region for block: B:42:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void setDisplay(android.view.Display r7) {
            /*
                r6 = this;
                r5 = 0
                r1 = 0
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L_0x0037
                r0 = r1
            L_0x0007:
                boolean r2 = r6.hasCurrentPresentationExpired()
                if (r2 == 0) goto L_0x003e
            L_0x000d:
                android.app.Presentation r2 = r6.presentation
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L_0x0045
            L_0x0013:
                android.view.View r0 = r6.view
                detachViewFromParent(r0)
                if (r7 != 0) goto L_0x004d
            L_0x001a:
                android.widget.FrameLayout r0 = r6.originalParent
                android.view.View r1 = r6.view
                r0.addView(r1, r5)
            L_0x0021:
                com.google.vr.cardboard.DisplaySynchronizer r1 = r6.displaySynchronizer
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L_0x00ad
                android.content.Context r0 = r6.context
                android.view.Display r0 = com.google.vr.cardboard.DisplayUtils.getDefaultDisplay(r0)
            L_0x002d:
                r1.setDisplay(r0)
                if (r2 != 0) goto L_0x00b5
            L_0x0032:
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L_0x00cb
            L_0x0036:
                return
            L_0x0037:
                android.app.Presentation r0 = r6.presentation
                android.view.Display r0 = r0.getDisplay()
                goto L_0x0007
            L_0x003e:
                boolean r0 = com.google.vr.cardboard.DisplayUtils.isSameDisplay(r7, r0)
                if (r0 == 0) goto L_0x000d
                return
            L_0x0045:
                android.app.Presentation r0 = r6.presentation
                r0.dismiss()
                r6.presentation = r1
                goto L_0x0013
            L_0x004d:
                com.google.vr.ndk.base.GvrLayout$PresentationFactory r0 = com.google.vr.ndk.base.GvrLayout.sOptionalPresentationFactory
                if (r0 != 0) goto L_0x00a2
                android.app.Presentation r0 = new android.app.Presentation
                android.content.Context r3 = r6.context
                r0.<init>(r3, r7)
            L_0x005a:
                r6.presentation = r0
                android.app.Presentation r0 = r6.presentation
                android.view.View r3 = r6.view
                android.widget.RelativeLayout$LayoutParams r4 = r6.layout
                r0.addContentView(r3, r4)
                android.app.Presentation r0 = r6.presentation     // Catch:{ InvalidDisplayException -> 0x006b }
                r0.show()     // Catch:{ InvalidDisplayException -> 0x006b }
                goto L_0x0021
            L_0x006b:
                r0 = move-exception
                java.lang.String r0 = java.lang.String.valueOf(r0)
                java.lang.String r3 = java.lang.String.valueOf(r0)
                int r3 = r3.length()
                int r3 = r3 + 57
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>(r3)
                java.lang.String r3 = "Attaching Cardboard View to the external display failed: "
                java.lang.StringBuilder r3 = r4.append(r3)
                java.lang.StringBuilder r0 = r3.append(r0)
                java.lang.String r0 = r0.toString()
                java.lang.String r3 = "GvrLayout"
                android.util.Log.e(r3, r0)
                android.app.Presentation r0 = r6.presentation
                r0.cancel()
                r6.presentation = r1
                android.view.View r0 = r6.view
                detachViewFromParent(r0)
                goto L_0x001a
            L_0x00a2:
                com.google.vr.ndk.base.GvrLayout$PresentationFactory r0 = com.google.vr.ndk.base.GvrLayout.sOptionalPresentationFactory
                android.content.Context r3 = r6.context
                android.app.Presentation r0 = r0.create(r3, r7)
                goto L_0x005a
            L_0x00ad:
                android.app.Presentation r0 = r6.presentation
                android.view.Display r0 = r0.getDisplay()
                goto L_0x002d
            L_0x00b5:
                java.util.List<com.google.vr.ndk.base.GvrLayout$PresentationListener> r0 = r6.listeners
                java.util.Iterator r1 = r0.iterator()
            L_0x00bb:
                boolean r0 = r1.hasNext()
                if (r0 == 0) goto L_0x0032
                java.lang.Object r0 = r1.next()
                com.google.vr.ndk.base.GvrLayout$PresentationListener r0 = (com.google.vr.ndk.base.GvrLayout.PresentationListener) r0
                r0.onPresentationStopped()
                goto L_0x00bb
            L_0x00cb:
                java.util.List<com.google.vr.ndk.base.GvrLayout$PresentationListener> r0 = r6.listeners
                java.util.Iterator r1 = r0.iterator()
            L_0x00d1:
                boolean r0 = r1.hasNext()
                if (r0 == 0) goto L_0x0036
                java.lang.Object r0 = r1.next()
                com.google.vr.ndk.base.GvrLayout$PresentationListener r0 = (com.google.vr.ndk.base.GvrLayout.PresentationListener) r0
                android.app.Presentation r2 = r6.presentation
                android.view.Display r2 = r2.getDisplay()
                r0.onPresentationStarted(r2)
                goto L_0x00d1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.GvrLayout.PresentationHelper.setDisplay(android.view.Display):void");
        }

        public void addListener(PresentationListener presentationListener) {
            if (!this.listeners.contains(presentationListener)) {
                this.listeners.add(presentationListener);
                if (this.presentation != null) {
                    presentationListener.onPresentationStarted(this.presentation.getDisplay());
                }
            }
        }

        public boolean isPresenting() {
            return this.presentation != null && this.presentation.isShowing();
        }

        public void onDetachedFromWindow() {
            this.displayManager.unregisterDisplayListener(this);
            setDisplay((Display) null);
        }

        public void onDisplayAdded(int i) {
            Display display = this.displayManager.getDisplay(i);
            if (isValidExternalDisplay(display)) {
                setDisplay(display);
            }
        }

        public void onDisplayChanged(int i) {
        }

        public void onDisplayRemoved(int i) {
            if (this.presentation != null && this.presentation.getDisplay().getDisplayId() == i) {
                setDisplay((Display) null);
            }
        }

        public void onPause() {
            this.displayManager.unregisterDisplayListener(this);
        }

        public void onResume() {
            Display display = null;
            this.externalDisplayName = DisplayUtils.getExternalDisplayName(this.context);
            if (this.externalDisplayName != null) {
                this.displayManager.registerDisplayListener(this, (Handler) null);
                Display[] displays = this.displayManager.getDisplays();
                int length = displays.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        Display display2 = displays[i];
                        if (isValidExternalDisplay(display2)) {
                            display = display2;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                setDisplay(display);
                return;
            }
            setDisplay((Display) null);
        }

        public void shutdown() {
            this.displayManager.unregisterDisplayListener(this);
            if (this.presentation != null) {
                this.presentation.cancel();
                this.presentation = null;
                for (PresentationListener onPresentationStopped : this.listeners) {
                    onPresentationStopped.onPresentationStopped();
                }
            }
        }
    }

    public interface PresentationListener {
        void onPresentationStarted(Display display);

        void onPresentationStopped();
    }

    public GvrLayout(Context context) {
        super(context);
        init((GvrApi) null, (DisplaySynchronizer) null, (FadeOverlayView) null, (DaydreamUtilsWrapper) null);
    }

    public GvrLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init((GvrApi) null, (DisplaySynchronizer) null, (FadeOverlayView) null, (DaydreamUtilsWrapper) null);
    }

    GvrLayout(Context context, GvrApi gvrApi2, DisplaySynchronizer displaySynchronizer2, FadeOverlayView fadeOverlayView2, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        super(context);
        init(gvrApi2, displaySynchronizer2, fadeOverlayView2, daydreamUtilsWrapper);
    }

    private void addScanlineRacingView() {
        if (this.scanlineRacingView == null) {
            this.eglFactory = new EglFactory();
            this.eglFactory.setUsePriorityContext(true);
            this.eglFactory.setUseProtectedBuffers(this.isAsyncReprojectionUsingProtectedBuffers);
            this.eglFactory.setEGLContextClientVersion(2);
            this.scanlineRacingView = new GvrSurfaceView(getContext());
            this.scanlineRacingView.setEGLContextClientVersion(2);
            this.scanlineRacingView.setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new MutableEglConfigChooser());
            this.scanlineRacingView.setZOrderMediaOverlay(true);
            this.scanlineRacingView.setEGLContextFactory(this.eglFactory);
            this.scanlineRacingView.setEGLWindowSurfaceFactory(this.eglFactory);
            if (!this.stereoModeEnabled) {
                Log.w(TAG, "Disabling stereo mode with async reprojection enabled may not work properly.");
                this.scanlineRacingView.setVisibility(8);
            }
            if (this.scanlineRacingRenderer == null) {
                this.scanlineRacingRenderer = new ScanlineRacingRenderer(this.gvrApi);
            }
            this.scanlineRacingRenderer.setSurfaceView(this.scanlineRacingView);
            this.scanlineRacingView.setRenderer(this.scanlineRacingRenderer);
            this.scanlineRacingView.setSwapMode(1);
            this.presentationLayout.addView(this.scanlineRacingView, 0);
        }
    }

    private void init(GvrApi gvrApi2, DisplaySynchronizer displaySynchronizer2, FadeOverlayView fadeOverlayView2, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        boolean z = false;
        Activity activity = ContextUtils.getActivity(getContext());
        if (activity != null) {
            TraceCompat.beginSection("GvrLayout.init");
            if (displaySynchronizer2 == null) {
                try {
                    displaySynchronizer2 = GvrApi.createDefaultDisplaySynchronizer(getContext());
                } catch (Throwable th) {
                    TraceCompat.endSection();
                    throw th;
                }
            }
            if (gvrApi2 == null) {
                gvrApi2 = new GvrApi(getContext(), displaySynchronizer2);
            }
            if (daydreamUtilsWrapper == null) {
                daydreamUtilsWrapper = new DaydreamUtilsWrapper();
            }
            this.daydreamUtils = daydreamUtilsWrapper;
            this.presentationLayout = new FrameLayout(getContext());
            this.uiLayout = new GvrUiLayout(getContext());
            this.gvrApi = gvrApi2;
            this.displaySynchronizer = displaySynchronizer2;
            this.presentationHelper = tryCreatePresentationHelper();
            addView(this.presentationLayout, 0);
            addView(this.uiLayout, 1);
            updateUiLayout();
            boolean isDaydreamPhone = daydreamUtilsWrapper.isDaydreamPhone(getContext());
            if (isDaydreamPhone) {
                this.daydreamAlignment = createDaydreamAlignment();
                this.uiLayout.setOnTouchListener(new DaydreamAlignment.DefaultTouchListener(this.daydreamAlignment, gvrApi2));
            }
            int activityDaydreamCompatibility = daydreamUtilsWrapper.getActivityDaydreamCompatibility(activity);
            boolean z2 = activityDaydreamCompatibility != 0;
            boolean z3 = activityDaydreamCompatibility == 2;
            if (isDaydreamPhone || z3) {
                z = true;
            }
            if (z) {
                if (z2) {
                    if (fadeOverlayView2 == null) {
                        fadeOverlayView2 = new FadeOverlayView(getContext());
                    }
                    this.fadeOverlayView = fadeOverlayView2;
                    addView(this.fadeOverlayView, 2);
                }
                this.vrCoreSdkClient = createVrCoreSdkClient(getContext(), gvrApi2, daydreamUtilsWrapper, this.fadeOverlayView);
            }
            TraceCompat.endSection();
            return;
        }
        throw new IllegalArgumentException("An Activity Context is required for VR functionality.");
    }

    static void setPresentationFactory(PresentationFactory presentationFactory) {
        sOptionalPresentationFactory = presentationFactory;
    }

    private PresentationHelper tryCreatePresentationHelper() {
        if (Build.VERSION.SDK_INT <= 16) {
            return null;
        }
        String externalDisplayName = DisplayUtils.getExternalDisplayName(getContext());
        if (externalDisplayName != null) {
            return new PresentationHelper(getContext(), this, this.presentationLayout, this.displaySynchronizer, externalDisplayName);
        }
        Log.e(TAG, "HDMI display name could not be found, disabling external presentation support");
        return null;
    }

    private void updateFadeVisibility() {
        boolean z = false;
        if (getWindowVisibility() == 0) {
            z = true;
        }
        if (this.fadeOverlayView != null) {
            if (z && this.isResumed) {
                this.fadeOverlayView.onVisible();
                removeCallbacks(this.showRenderingViewsRunnable);
                postDelayed(this.showRenderingViewsRunnable, 50);
            } else if (!z && !this.isResumed) {
                this.fadeOverlayView.onInvisible();
                updateRenderingViewsVisibility(4);
                removeCallbacks(this.showRenderingViewsRunnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRenderingViewsVisibility(int i) {
        int i2 = 0;
        if (this.presentationView != null) {
            View view = this.presentationView;
            if (this.stereoModeEnabled) {
                i2 = i;
            }
            view.setVisibility(i2);
        }
        if (this.scanlineRacingView != null) {
            GvrSurfaceView gvrSurfaceView = this.scanlineRacingView;
            if (!this.stereoModeEnabled) {
                i = 8;
            }
            gvrSurfaceView.setVisibility(i);
        }
    }

    private void updateUiLayout() {
        boolean z = true;
        if (this.gvrApi.getViewerType() != 1) {
            z = false;
        }
        this.uiLayout.setDaydreamModeEnabled(z);
    }

    public void addPresentationListener(PresentationListener presentationListener) {
        if (this.presentationHelper != null) {
            this.presentationHelper.addListener(presentationListener);
        }
    }

    /* access modifiers changed from: package-private */
    public DaydreamAlignment createDaydreamAlignment() {
        return new DaydreamAlignment(getContext(), this.gvrApi);
    }

    /* access modifiers changed from: protected */
    public VrCoreSdkClient createVrCoreSdkClient(Context context, GvrApi gvrApi2, DaydreamUtilsWrapper daydreamUtilsWrapper, FadeOverlayView fadeOverlayView2) {
        return new VrCoreSdkClient(context, gvrApi2, ContextUtils.getActivity(context).getComponentName(), daydreamUtilsWrapper, new Runnable() {
            public void run() {
                GvrLayout.this.uiLayout.invokeCloseButtonListener();
            }
        }, fadeOverlayView2);
    }

    public boolean enableAsyncReprojectionVideoSurface(ExternalSurfaceListener externalSurfaceListener, Handler handler, boolean z) {
        if (!this.daydreamUtils.isDaydreamPhone(getContext())) {
            Log.e(TAG, "Only Daydream devices support async reprojection. Cannot enable video Surface.");
            return false;
        } else if (this.scanlineRacingView != null) {
            Log.e(TAG, "Async reprojection is already enabled. Cannot call enableAsyncReprojectionVideoSurface after calling setAsyncReprojectionEnabled.");
            return false;
        } else if (!this.gvrApi.usingVrDisplayService()) {
            this.isAsyncReprojectionVideoEnabled = true;
            this.isAsyncReprojectionUsingProtectedBuffers = z;
            this.scanlineRacingRenderer = new ScanlineRacingRenderer(this.gvrApi);
            this.videoSurfaceId = this.scanlineRacingRenderer.getExternalSurfaceManager().createExternalSurface(externalSurfaceListener, handler);
            return true;
        } else {
            Log.e(TAG, "Async reprojection video is not supported on this device.");
            return false;
        }
    }

    public boolean enableCardboardTriggerEmulation(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("The Cardboard trigger listener must not be null.");
        } else if (this.cardboardEmulator != null) {
            return true;
        } else {
            if (!this.daydreamUtils.isDaydreamPhone(getContext())) {
                return false;
            }
            this.cardboardEmulator = new CardboardEmulator(getContext(), runnable);
            return true;
        }
    }

    public Surface getAsyncReprojectionVideoSurface() {
        if (this.isAsyncReprojectionVideoEnabled) {
            if (this.scanlineRacingView == null) {
                Log.w(TAG, "No async reprojection view has been set. Cannot get async reprojection managed Surfaces. Have you called setAsyncReprojectionEnabled()?");
            }
            return this.scanlineRacingRenderer.getExternalSurfaceManager().getSurface(this.videoSurfaceId);
        }
        Log.w(TAG, "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
        return null;
    }

    public int getAsyncReprojectionVideoSurfaceId() {
        if (!this.isAsyncReprojectionVideoEnabled) {
            Log.w(TAG, "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
        }
        return this.videoSurfaceId;
    }

    /* access modifiers changed from: package-private */
    public FadeOverlayView getFadeOverlayView() {
        return this.fadeOverlayView;
    }

    public GvrApi getGvrApi() {
        return this.gvrApi;
    }

    public GvrUiLayout getUiLayout() {
        return this.uiLayout;
    }

    /* access modifiers changed from: package-private */
    public VrCoreSdkClient getVrCoreSdkClient() {
        return this.vrCoreSdkClient;
    }

    public boolean isPresenting() {
        return (this.presentationView == null || this.presentationHelper == null || !this.presentationHelper.isPresenting()) ? false : true;
    }

    public void launchInVr(PendingIntent pendingIntent) {
        if (this.vrCoreSdkClient == null || !this.vrCoreSdkClient.launchInVr(pendingIntent)) {
            try {
                pendingIntent.send();
            } catch (Exception e) {
                Log.e(TAG, "Error launching PendingIntent.", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.displaySynchronizer.onConfigurationChanged();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.presentationHelper != null) {
            this.presentationHelper.onDetachedFromWindow();
        }
    }

    public void onPause() {
        TraceCompat.beginSection("GvrLayout.onPause");
        try {
            this.gvrApi.pause();
            if (this.scanlineRacingView != null) {
                this.scanlineRacingView.queueEvent(new Runnable() {
                    public void run() {
                        GvrLayout.this.scanlineRacingRenderer.onPause();
                    }
                });
                this.scanlineRacingView.onPause();
            }
            if (this.presentationHelper != null) {
                this.presentationHelper.onPause();
            }
            this.displaySynchronizer.onPause();
            if (this.vrCoreSdkClient != null) {
                this.vrCoreSdkClient.onPause();
            }
            if (this.cardboardEmulator != null) {
                this.cardboardEmulator.onPause();
            }
            this.isResumed = false;
            updateFadeVisibility();
        } finally {
            TraceCompat.endSection();
        }
    }

    public void onResume() {
        TraceCompat.beginSection("GvrLayout.onResume");
        try {
            this.gvrApi.resume();
            if (this.daydreamAlignment != null) {
                this.daydreamAlignment.refreshViewerProfile();
            }
            this.displaySynchronizer.onResume();
            if (this.presentationHelper != null) {
                this.presentationHelper.onResume();
            }
            if (this.scanlineRacingView != null) {
                this.scanlineRacingView.onResume();
            }
            if (this.vrCoreSdkClient != null) {
                this.vrCoreSdkClient.onResume();
            }
            if (this.cardboardEmulator != null && this.gvrApi.getViewerType() == 1) {
                this.cardboardEmulator.onResume();
            }
            this.isResumed = true;
            updateFadeVisibility();
            updateUiLayout();
        } finally {
            TraceCompat.endSection();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.presentationView != null && isPresenting() && this.presentationView.dispatchTouchEvent(motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        updateFadeVisibility();
    }

    public boolean setAsyncReprojectionEnabled(boolean z) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("setAsyncReprojectionEnabled may only be called from the UI thread");
        } else if (this.scanlineRacingView != null && !z) {
            throw new UnsupportedOperationException("Async reprojection cannot be disabled once enabled");
        } else if (z && !this.daydreamUtils.isDaydreamPhone(getContext())) {
            return false;
        } else {
            boolean asyncReprojectionEnabled = this.gvrApi.setAsyncReprojectionEnabled(z);
            if (z) {
                if (!asyncReprojectionEnabled) {
                    Log.e(TAG, "Failed to initialize async reprojection, unsupported device.");
                    this.isAsyncReprojectionVideoEnabled = false;
                    this.scanlineRacingRenderer = null;
                } else if (!this.gvrApi.usingVrDisplayService()) {
                    addScanlineRacingView();
                }
            }
            return asyncReprojectionEnabled;
        }
    }

    public void setFixedPresentationSurfaceSize(int i, int i2) {
        this.gvrApi.setSurfaceSize(i, i2);
    }

    public void setPresentationView(View view) {
        if (this.presentationView != null) {
            this.presentationLayout.removeView(this.presentationView);
        }
        this.presentationLayout.addView(view, 0);
        this.presentationView = view;
    }

    public void setStereoModeEnabled(boolean z) {
        if (this.stereoModeEnabled != z) {
            this.stereoModeEnabled = z;
            this.uiLayout.setEnabled(z);
            if (this.vrCoreSdkClient != null) {
                this.vrCoreSdkClient.setEnabled(z);
            }
            if (this.fadeOverlayView != null) {
                this.fadeOverlayView.setEnabled(z);
            }
            if (this.daydreamAlignment != null) {
                this.daydreamAlignment.setEnabled(z);
            }
            updateRenderingViewsVisibility(0);
        }
    }

    public void shutdown() {
        TraceCompat.beginSection("GvrLayout.shutdown");
        try {
            this.displaySynchronizer.shutdown();
            if (this.daydreamAlignment != null) {
                this.daydreamAlignment.shutdown();
            }
            removeView(this.presentationLayout);
            removeView(this.uiLayout);
            if (this.scanlineRacingRenderer != null) {
                this.scanlineRacingRenderer.shutdown();
                this.scanlineRacingRenderer = null;
            }
            this.scanlineRacingView = null;
            this.presentationView = null;
            if (this.presentationHelper != null) {
                this.presentationHelper.shutdown();
                this.presentationHelper = null;
            }
            if (this.vrCoreSdkClient != null) {
                this.vrCoreSdkClient.onPause();
                this.vrCoreSdkClient = null;
            }
            if (this.cardboardEmulator != null) {
                this.cardboardEmulator.onPause();
                this.cardboardEmulator = null;
            }
            if (this.gvrApi != null) {
                this.gvrApi.shutdown();
                this.gvrApi = null;
            }
        } finally {
            TraceCompat.endSection();
        }
    }
}
