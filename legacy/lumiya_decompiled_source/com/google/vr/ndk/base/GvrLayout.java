package com.google.vr.ndk.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Presentation;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
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
/* loaded from: classes.dex */
public class GvrLayout extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final int EXTERNAL_PRESENTATION_MIN_API = 16;
    private static final int SHOW_RENDERING_VIEWS_DELAY_FOR_FADE = 50;
    private static final String TAG = "GvrLayout";
    private static PresentationFactory sOptionalPresentationFactory = null;
    private CardboardEmulator cardboardEmulator;
    private DaydreamAlignment daydreamAlignment;
    private DaydreamUtilsWrapper daydreamUtils;
    private DisplaySynchronizer displaySynchronizer;
    private EglFactory eglFactory;
    private FadeOverlayView fadeOverlayView;
    private GvrApi gvrApi;
    private boolean isAsyncReprojectionUsingProtectedBuffers;
    private boolean isAsyncReprojectionVideoEnabled;
    private boolean isResumed;
    private PresentationHelper presentationHelper;
    private FrameLayout presentationLayout;
    private View presentationView;
    private ScanlineRacingRenderer scanlineRacingRenderer;
    private GvrSurfaceView scanlineRacingView;
    private final Runnable showRenderingViewsRunnable;
    private boolean stereoModeEnabled;
    private GvrUiLayout uiLayout;
    private int videoSurfaceId;
    private VrCoreSdkClient vrCoreSdkClient;

    /* loaded from: classes.dex */
    public interface ExternalSurfaceListener {
        void onFrameAvailable();

        void onSurfaceAvailable(Surface surface);
    }

    /* loaded from: classes.dex */
    public interface ExternalSurfaceManager {
        int createExternalSurface();

        int createExternalSurface(ExternalSurfaceListener externalSurfaceListener, Handler handler);

        Surface getSurface(int i);

        int getSurfaceCount();

        void releaseExternalSurface(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface PresentationFactory {
        Presentation create(Context context, Display display);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PresentationHelper implements DisplayManager.DisplayListener {
        private final Context context;
        private final DisplayManager displayManager;
        private final DisplaySynchronizer displaySynchronizer;
        private String externalDisplayName;
        private final RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(-1, -1);
        private final List<PresentationListener> listeners = new ArrayList();
        private final FrameLayout originalParent;
        private Presentation presentation;
        private final View view;

        PresentationHelper(Context context, FrameLayout frameLayout, View view, DisplaySynchronizer displaySynchronizer, String str) {
            this.context = context;
            this.originalParent = frameLayout;
            this.view = view;
            this.displaySynchronizer = displaySynchronizer;
            this.externalDisplayName = str;
            this.displayManager = (DisplayManager) context.getSystemService("display");
        }

        private static void detachViewFromParent(View view) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup == null) {
                return;
            }
            viewGroup.removeView(view);
        }

        private boolean hasCurrentPresentationExpired() {
            if (this.presentation != null) {
                return (this.presentation.isShowing() && this.presentation.getDisplay().isValid()) ? false : true;
            }
            return false;
        }

        private boolean isValidExternalDisplay(Display display) {
            return display.isValid() && display.getName().equals(this.externalDisplayName);
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x0027  */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0036 A[ORIG_RETURN, RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00ad  */
        /* JADX WARN: Removed duplicated region for block: B:35:0x00b5  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x00cb  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void setDisplay(android.view.Display r7) {
            /*
                r6 = this;
                r5 = 0
                r1 = 0
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L37
                r0 = r1
            L7:
                boolean r2 = r6.hasCurrentPresentationExpired()
                if (r2 == 0) goto L3e
            Ld:
                android.app.Presentation r2 = r6.presentation
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto L45
            L13:
                android.view.View r0 = r6.view
                detachViewFromParent(r0)
                if (r7 != 0) goto L4d
            L1a:
                android.widget.FrameLayout r0 = r6.originalParent
                android.view.View r1 = r6.view
                r0.addView(r1, r5)
            L21:
                com.google.vr.cardboard.DisplaySynchronizer r1 = r6.displaySynchronizer
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto Lad
                android.content.Context r0 = r6.context
                android.view.Display r0 = com.google.vr.cardboard.DisplayUtils.getDefaultDisplay(r0)
            L2d:
                r1.setDisplay(r0)
                if (r2 != 0) goto Lb5
            L32:
                android.app.Presentation r0 = r6.presentation
                if (r0 != 0) goto Lcb
            L36:
                return
            L37:
                android.app.Presentation r0 = r6.presentation
                android.view.Display r0 = r0.getDisplay()
                goto L7
            L3e:
                boolean r0 = com.google.vr.cardboard.DisplayUtils.isSameDisplay(r7, r0)
                if (r0 == 0) goto Ld
                return
            L45:
                android.app.Presentation r0 = r6.presentation
                r0.dismiss()
                r6.presentation = r1
                goto L13
            L4d:
                com.google.vr.ndk.base.GvrLayout$PresentationFactory r0 = com.google.vr.ndk.base.GvrLayout.access$300()
                if (r0 != 0) goto La2
                android.app.Presentation r0 = new android.app.Presentation
                android.content.Context r3 = r6.context
                r0.<init>(r3, r7)
            L5a:
                r6.presentation = r0
                android.app.Presentation r0 = r6.presentation
                android.view.View r3 = r6.view
                android.widget.RelativeLayout$LayoutParams r4 = r6.layout
                r0.addContentView(r3, r4)
                android.app.Presentation r0 = r6.presentation     // Catch: android.view.WindowManager.InvalidDisplayException -> L6b
                r0.show()     // Catch: android.view.WindowManager.InvalidDisplayException -> L6b
                goto L21
            L6b:
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
                goto L1a
            La2:
                com.google.vr.ndk.base.GvrLayout$PresentationFactory r0 = com.google.vr.ndk.base.GvrLayout.access$300()
                android.content.Context r3 = r6.context
                android.app.Presentation r0 = r0.create(r3, r7)
                goto L5a
            Lad:
                android.app.Presentation r0 = r6.presentation
                android.view.Display r0 = r0.getDisplay()
                goto L2d
            Lb5:
                java.util.List<com.google.vr.ndk.base.GvrLayout$PresentationListener> r0 = r6.listeners
                java.util.Iterator r1 = r0.iterator()
            Lbb:
                boolean r0 = r1.hasNext()
                if (r0 == 0) goto L32
                java.lang.Object r0 = r1.next()
                com.google.vr.ndk.base.GvrLayout$PresentationListener r0 = (com.google.vr.ndk.base.GvrLayout.PresentationListener) r0
                r0.onPresentationStopped()
                goto Lbb
            Lcb:
                java.util.List<com.google.vr.ndk.base.GvrLayout$PresentationListener> r0 = r6.listeners
                java.util.Iterator r1 = r0.iterator()
            Ld1:
                boolean r0 = r1.hasNext()
                if (r0 == 0) goto L36
                java.lang.Object r0 = r1.next()
                com.google.vr.ndk.base.GvrLayout$PresentationListener r0 = (com.google.vr.ndk.base.GvrLayout.PresentationListener) r0
                android.app.Presentation r2 = r6.presentation
                android.view.Display r2 = r2.getDisplay()
                r0.onPresentationStarted(r2)
                goto Ld1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.ndk.base.GvrLayout.PresentationHelper.setDisplay(android.view.Display):void");
        }

        public void addListener(PresentationListener presentationListener) {
            if (this.listeners.contains(presentationListener)) {
                return;
            }
            this.listeners.add(presentationListener);
            if (this.presentation == null) {
                return;
            }
            presentationListener.onPresentationStarted(this.presentation.getDisplay());
        }

        public boolean isPresenting() {
            return this.presentation != null && this.presentation.isShowing();
        }

        public void onDetachedFromWindow() {
            this.displayManager.unregisterDisplayListener(this);
            setDisplay(null);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
            Display display = this.displayManager.getDisplay(i);
            if (isValidExternalDisplay(display)) {
                setDisplay(display);
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            if (this.presentation != null && this.presentation.getDisplay().getDisplayId() == i) {
                setDisplay(null);
            }
        }

        public void onPause() {
            this.displayManager.unregisterDisplayListener(this);
        }

        public void onResume() {
            Display display = null;
            this.externalDisplayName = DisplayUtils.getExternalDisplayName(this.context);
            if (this.externalDisplayName == null) {
                setDisplay(null);
                return;
            }
            this.displayManager.registerDisplayListener(this, null);
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
        }

        public void shutdown() {
            this.displayManager.unregisterDisplayListener(this);
            if (this.presentation == null) {
                return;
            }
            this.presentation.cancel();
            this.presentation = null;
            for (PresentationListener presentationListener : this.listeners) {
                presentationListener.onPresentationStopped();
            }
        }
    }

    /* loaded from: classes.dex */
    public interface PresentationListener {
        void onPresentationStarted(Display display);

        void onPresentationStopped();
    }

    public GvrLayout(Context context) {
        super(context);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() { // from class: com.google.vr.ndk.base.GvrLayout.1
            @Override // java.lang.Runnable
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        init(null, null, null, null);
    }

    public GvrLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() { // from class: com.google.vr.ndk.base.GvrLayout.1
            @Override // java.lang.Runnable
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        init(null, null, null, null);
    }

    GvrLayout(Context context, GvrApi gvrApi, DisplaySynchronizer displaySynchronizer, FadeOverlayView fadeOverlayView, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        super(context);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() { // from class: com.google.vr.ndk.base.GvrLayout.1
            @Override // java.lang.Runnable
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        init(gvrApi, displaySynchronizer, fadeOverlayView, daydreamUtilsWrapper);
    }

    private void addScanlineRacingView() {
        if (this.scanlineRacingView == null) {
            this.eglFactory = new EglFactory();
            this.eglFactory.setUsePriorityContext(true);
            this.eglFactory.setUseProtectedBuffers(this.isAsyncReprojectionUsingProtectedBuffers);
            this.eglFactory.setEGLContextClientVersion(2);
            this.scanlineRacingView = new GvrSurfaceView(getContext());
            this.scanlineRacingView.setEGLContextClientVersion(2);
            this.scanlineRacingView.setEGLConfigChooser(new MutableEglConfigChooser());
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

    private void init(GvrApi gvrApi, DisplaySynchronizer displaySynchronizer, FadeOverlayView fadeOverlayView, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        Activity activity = ContextUtils.getActivity(getContext());
        if (activity == null) {
            throw new IllegalArgumentException("An Activity Context is required for VR functionality.");
        }
        TraceCompat.beginSection("GvrLayout.init");
        if (displaySynchronizer == null) {
            try {
                displaySynchronizer = GvrApi.createDefaultDisplaySynchronizer(getContext());
            } finally {
                TraceCompat.endSection();
            }
        }
        if (gvrApi == null) {
            gvrApi = new GvrApi(getContext(), displaySynchronizer);
        }
        if (daydreamUtilsWrapper == null) {
            daydreamUtilsWrapper = new DaydreamUtilsWrapper();
        }
        this.daydreamUtils = daydreamUtilsWrapper;
        this.presentationLayout = new FrameLayout(getContext());
        this.uiLayout = new GvrUiLayout(getContext());
        this.gvrApi = gvrApi;
        this.displaySynchronizer = displaySynchronizer;
        this.presentationHelper = tryCreatePresentationHelper();
        addView(this.presentationLayout, 0);
        addView(this.uiLayout, 1);
        updateUiLayout();
        boolean isDaydreamPhone = daydreamUtilsWrapper.isDaydreamPhone(getContext());
        if (isDaydreamPhone) {
            this.daydreamAlignment = createDaydreamAlignment();
            this.uiLayout.setOnTouchListener(new DaydreamAlignment.DefaultTouchListener(this.daydreamAlignment, gvrApi));
        }
        int activityDaydreamCompatibility = daydreamUtilsWrapper.getActivityDaydreamCompatibility(activity);
        boolean z = activityDaydreamCompatibility != 0;
        if (isDaydreamPhone || (activityDaydreamCompatibility == 2)) {
            if (z) {
                if (fadeOverlayView == null) {
                    fadeOverlayView = new FadeOverlayView(getContext());
                }
                this.fadeOverlayView = fadeOverlayView;
                addView(this.fadeOverlayView, 2);
            }
            this.vrCoreSdkClient = createVrCoreSdkClient(getContext(), gvrApi, daydreamUtilsWrapper, this.fadeOverlayView);
        }
    }

    static void setPresentationFactory(PresentationFactory presentationFactory) {
        sOptionalPresentationFactory = presentationFactory;
    }

    private PresentationHelper tryCreatePresentationHelper() {
        if (Build.VERSION.SDK_INT > 16) {
            String externalDisplayName = DisplayUtils.getExternalDisplayName(getContext());
            if (externalDisplayName != null) {
                return new PresentationHelper(getContext(), this, this.presentationLayout, this.displaySynchronizer, externalDisplayName);
            }
            Log.e(TAG, "HDMI display name could not be found, disabling external presentation support");
            return null;
        }
        return null;
    }

    private void updateFadeVisibility() {
        boolean z = getWindowVisibility() == 0;
        if (this.fadeOverlayView == null) {
            return;
        }
        if (z && this.isResumed) {
            this.fadeOverlayView.onVisible();
            removeCallbacks(this.showRenderingViewsRunnable);
            postDelayed(this.showRenderingViewsRunnable, 50L);
        } else if (z || this.isResumed) {
        } else {
            this.fadeOverlayView.onInvisible();
            updateRenderingViewsVisibility(4);
            removeCallbacks(this.showRenderingViewsRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRenderingViewsVisibility(int i) {
        if (this.presentationView != null) {
            this.presentationView.setVisibility(this.stereoModeEnabled ? i : 0);
        }
        if (this.scanlineRacingView == null) {
            return;
        }
        GvrSurfaceView gvrSurfaceView = this.scanlineRacingView;
        if (!this.stereoModeEnabled) {
            i = 8;
        }
        gvrSurfaceView.setVisibility(i);
    }

    private void updateUiLayout() {
        this.uiLayout.setDaydreamModeEnabled(this.gvrApi.getViewerType() == 1);
    }

    public void addPresentationListener(PresentationListener presentationListener) {
        if (this.presentationHelper == null) {
            return;
        }
        this.presentationHelper.addListener(presentationListener);
    }

    DaydreamAlignment createDaydreamAlignment() {
        return new DaydreamAlignment(getContext(), this.gvrApi);
    }

    protected VrCoreSdkClient createVrCoreSdkClient(Context context, GvrApi gvrApi, DaydreamUtilsWrapper daydreamUtilsWrapper, FadeOverlayView fadeOverlayView) {
        return new VrCoreSdkClient(context, gvrApi, ContextUtils.getActivity(context).getComponentName(), daydreamUtilsWrapper, new Runnable() { // from class: com.google.vr.ndk.base.GvrLayout.3
            @Override // java.lang.Runnable
            public void run() {
                GvrLayout.this.uiLayout.invokeCloseButtonListener();
            }
        }, fadeOverlayView);
    }

    public boolean enableAsyncReprojectionVideoSurface(ExternalSurfaceListener externalSurfaceListener, Handler handler, boolean z) {
        if (!this.daydreamUtils.isDaydreamPhone(getContext())) {
            Log.e(TAG, "Only Daydream devices support async reprojection. Cannot enable video Surface.");
            return false;
        } else if (this.scanlineRacingView != null) {
            Log.e(TAG, "Async reprojection is already enabled. Cannot call enableAsyncReprojectionVideoSurface after calling setAsyncReprojectionEnabled.");
            return false;
        } else if (this.gvrApi.usingVrDisplayService()) {
            Log.e(TAG, "Async reprojection video is not supported on this device.");
            return false;
        } else {
            this.isAsyncReprojectionVideoEnabled = true;
            this.isAsyncReprojectionUsingProtectedBuffers = z;
            this.scanlineRacingRenderer = new ScanlineRacingRenderer(this.gvrApi);
            this.videoSurfaceId = this.scanlineRacingRenderer.getExternalSurfaceManager().createExternalSurface(externalSurfaceListener, handler);
            return true;
        }
    }

    public boolean enableCardboardTriggerEmulation(Runnable runnable) {
        if (runnable != null) {
            if (this.cardboardEmulator == null) {
                if (this.daydreamUtils.isDaydreamPhone(getContext())) {
                    this.cardboardEmulator = new CardboardEmulator(getContext(), runnable);
                    return true;
                }
                return false;
            }
            return true;
        }
        throw new IllegalArgumentException("The Cardboard trigger listener must not be null.");
    }

    public Surface getAsyncReprojectionVideoSurface() {
        if (!this.isAsyncReprojectionVideoEnabled) {
            Log.w(TAG, "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
            return null;
        }
        if (this.scanlineRacingView == null) {
            Log.w(TAG, "No async reprojection view has been set. Cannot get async reprojection managed Surfaces. Have you called setAsyncReprojectionEnabled()?");
        }
        return this.scanlineRacingRenderer.getExternalSurfaceManager().getSurface(this.videoSurfaceId);
    }

    public int getAsyncReprojectionVideoSurfaceId() {
        if (!this.isAsyncReprojectionVideoEnabled) {
            Log.w(TAG, "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
        }
        return this.videoSurfaceId;
    }

    FadeOverlayView getFadeOverlayView() {
        return this.fadeOverlayView;
    }

    public GvrApi getGvrApi() {
        return this.gvrApi;
    }

    public GvrUiLayout getUiLayout() {
        return this.uiLayout;
    }

    VrCoreSdkClient getVrCoreSdkClient() {
        return this.vrCoreSdkClient;
    }

    public boolean isPresenting() {
        return (this.presentationView == null || this.presentationHelper == null || !this.presentationHelper.isPresenting()) ? false : true;
    }

    public void launchInVr(PendingIntent pendingIntent) {
        if (this.vrCoreSdkClient != null && this.vrCoreSdkClient.launchInVr(pendingIntent)) {
            return;
        }
        try {
            pendingIntent.send();
        } catch (Exception e) {
            Log.e(TAG, "Error launching PendingIntent.", e);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.displaySynchronizer.onConfigurationChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.presentationHelper == null) {
            return;
        }
        this.presentationHelper.onDetachedFromWindow();
    }

    public void onPause() {
        TraceCompat.beginSection("GvrLayout.onPause");
        try {
            this.gvrApi.pause();
            if (this.scanlineRacingView != null) {
                this.scanlineRacingView.queueEvent(new Runnable() { // from class: com.google.vr.ndk.base.GvrLayout.2
                    @Override // java.lang.Runnable
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

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.presentationView != null && isPresenting() && this.presentationView.dispatchTouchEvent(motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        updateFadeVisibility();
    }

    public boolean setAsyncReprojectionEnabled(boolean z) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            if (this.scanlineRacingView == null || z) {
                if (z && !this.daydreamUtils.isDaydreamPhone(getContext())) {
                    return false;
                }
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
            throw new UnsupportedOperationException("Async reprojection cannot be disabled once enabled");
        }
        throw new IllegalStateException("setAsyncReprojectionEnabled may only be called from the UI thread");
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
