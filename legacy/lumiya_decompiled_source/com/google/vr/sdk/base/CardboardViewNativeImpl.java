package com.google.vr.sdk.base;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLDisplay;
import android.opengl.GLSurfaceView;
import android.os.Trace;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.google.vr.cardboard.CardboardGLSurfaceView;
import com.google.vr.cardboard.ThreadUtils;
import com.google.vr.cardboard.TransitionView;
import com.google.vr.cardboard.UsedByNative;
import com.google.vr.ndk.base.BufferSpec;
import com.google.vr.ndk.base.GvrApi;
import com.google.vr.ndk.base.GvrLayout;
import com.google.vr.ndk.base.GvrSurfaceView;
import com.google.vr.ndk.base.GvrUiLayout;
import com.google.vr.sdk.base.GvrView;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
@UsedByNative
/* loaded from: classes.dex */
public class CardboardViewNativeImpl implements CardboardGLSurfaceView.DetachListener, CardboardViewApi {
    private static final String TAG = CardboardViewNativeImpl.class.getSimpleName();
    private volatile Runnable cardboardBackListener;
    private volatile Runnable cardboardTriggerListener;
    private final Context context;
    private final CardboardGLSurfaceView glSurfaceView;
    private final GvrApi gvrApi;
    private final GvrLayout gvrLayout;
    private final HeadMountedDisplayManager hmdManager;
    private long nativeCardboardView;
    private final RendererHelper rendererHelper;
    private CountDownLatch shutdownLatch;
    private Runnable transitionDoneListener;
    private final GvrUiLayout uiLayout;
    private int cardboardTriggerCount = 0;
    private boolean stereoMode = true;
    private volatile boolean distortionCorrectionEnabled = true;

    /* loaded from: classes.dex */
    private class PresentationListener implements GvrLayout.PresentationListener {
        ScreenParams originalParams;

        private PresentationListener() {
        }

        @Override // com.google.vr.ndk.base.GvrLayout.PresentationListener
        public void onPresentationStarted(Display display) {
            this.originalParams = CardboardViewNativeImpl.this.getScreenParams();
            CardboardViewNativeImpl.this.updateScreenParams(new ScreenParams(display));
            CardboardViewNativeImpl.this.reconnectSensors();
        }

        @Override // com.google.vr.ndk.base.GvrLayout.PresentationListener
        public void onPresentationStopped() {
            if (this.originalParams == null) {
                return;
            }
            CardboardViewNativeImpl.this.updateScreenParams(this.originalParams);
        }
    }

    /* loaded from: classes.dex */
    private class RendererHelper implements GLSurfaceView.Renderer {
        private EGLDisplay eglDisplay;
        private boolean invalidSurfaceSizeWarningShown;
        private GvrView.Renderer renderer;
        private ScreenParams screenParams;
        private boolean stereoMode;
        private GvrView.StereoRenderer stereoRenderer;
        private boolean surfaceCreated;

        public RendererHelper() {
            this.screenParams = new ScreenParams(CardboardViewNativeImpl.this.getScreenParams());
            this.stereoMode = CardboardViewNativeImpl.this.stereoMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void callOnRendererShutdown() {
            if (this.renderer != null) {
                this.renderer.onRendererShutdown();
            } else if (this.stereoRenderer == null) {
            } else {
                this.stereoRenderer.onRendererShutdown();
            }
        }

        private void callOnSurfaceChanged(int i, int i2) {
            if (this.renderer != null) {
                this.renderer.onSurfaceChanged(i, i2);
            } else if (this.stereoRenderer == null) {
            } else {
                if (this.stereoMode) {
                    this.stereoRenderer.onSurfaceChanged(i / 2, i2);
                } else {
                    this.stereoRenderer.onSurfaceChanged(i, i2);
                }
            }
        }

        private void callOnSurfaceCreated(EGLConfig eGLConfig) {
            CardboardViewNativeImpl.this.nativeOnSurfaceCreated(CardboardViewNativeImpl.this.nativeCardboardView);
            if (this.renderer != null) {
                this.renderer.onSurfaceCreated(eGLConfig);
            } else if (this.stereoRenderer == null) {
            } else {
                this.stereoRenderer.onSurfaceCreated(eGLConfig);
            }
        }

        public void getCurrentEyeParams(final HeadTransform headTransform, final Eye eye, final Eye eye2, final Eye eye3, final Eye eye4, final Eye eye5) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            CardboardViewNativeImpl.this.queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.RendererHelper.3
                @Override // java.lang.Runnable
                public void run() {
                    CardboardViewNativeImpl.this.nativeGetCurrentEyeParams(CardboardViewNativeImpl.this.nativeCardboardView, headTransform, eye, eye2, eye3, eye4, eye5);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                String str = CardboardViewNativeImpl.TAG;
                String valueOf = String.valueOf(e.toString());
                Log.e(str, valueOf.length() == 0 ? new String("Interrupted while reading frame params: ") : "Interrupted while reading frame params: ".concat(valueOf));
            }
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onDrawFrame(GL10 gl10) {
            if ((this.renderer == null && this.stereoRenderer == null) || !this.surfaceCreated) {
                return;
            }
            Trace.beginSection("Render");
            CardboardViewNativeImpl.this.nativeOnDrawFrame(CardboardViewNativeImpl.this.nativeCardboardView);
            Trace.endSection();
            EGL14.eglSwapInterval(this.eglDisplay, 1);
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onSurfaceChanged(GL10 gl10, int i, int i2) {
            if ((this.renderer == null && this.stereoRenderer == null) || !this.surfaceCreated) {
                return;
            }
            if (this.stereoMode && !(i == this.screenParams.getWidth() && i2 == this.screenParams.getHeight())) {
                if (!this.invalidSurfaceSizeWarningShown) {
                    String str = CardboardViewNativeImpl.TAG;
                    int width = this.screenParams.getWidth();
                    Log.e(str, new StringBuilder(134).append("Surface size ").append(i).append("x").append(i2).append(" does not match the expected screen size ").append(width).append("x").append(this.screenParams.getHeight()).append(". Stereo rendering might feel off.").toString());
                }
                this.invalidSurfaceSizeWarningShown = true;
            } else {
                this.invalidSurfaceSizeWarningShown = false;
            }
            CardboardViewNativeImpl.this.nativeOnSurfaceChanged(CardboardViewNativeImpl.this.nativeCardboardView, i, i2);
            callOnSurfaceChanged(i, i2);
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
            if (this.renderer == null && this.stereoRenderer == null) {
                return;
            }
            this.surfaceCreated = true;
            this.eglDisplay = EGL14.eglGetCurrentDisplay();
            callOnSurfaceCreated(eGLConfig);
        }

        public void setRenderer(GvrView.Renderer renderer) {
            this.renderer = renderer;
            CardboardViewNativeImpl.this.nativeSetRenderer(CardboardViewNativeImpl.this.nativeCardboardView, renderer);
        }

        public void setRenderer(GvrView.StereoRenderer stereoRenderer) {
            this.stereoRenderer = stereoRenderer;
            CardboardViewNativeImpl.this.nativeSetStereoRenderer(CardboardViewNativeImpl.this.nativeCardboardView, stereoRenderer);
        }

        public void setScreenParams(ScreenParams screenParams) {
            this.screenParams = screenParams;
        }

        public void setStereoModeEnabled(final boolean z) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            CardboardViewNativeImpl.this.gvrLayout.setStereoModeEnabled(z);
            CardboardViewNativeImpl.this.queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.RendererHelper.2
                @Override // java.lang.Runnable
                public void run() {
                    if (RendererHelper.this.stereoMode != z) {
                        RendererHelper.this.stereoMode = z;
                        CardboardViewNativeImpl.this.nativeSetStereoModeEnabled(CardboardViewNativeImpl.this.nativeCardboardView, z);
                        if (EGL10.EGL_NO_SURFACE.equals(((EGL10) EGLContext.getEGL()).eglGetCurrentSurface(12377))) {
                            return;
                        }
                        RendererHelper.this.onSurfaceChanged(null, RendererHelper.this.screenParams.getWidth(), RendererHelper.this.screenParams.getHeight());
                    }
                }
            });
        }

        public void shutdown() {
            CardboardViewNativeImpl.this.queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.RendererHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    if ((RendererHelper.this.renderer != null || RendererHelper.this.stereoRenderer != null) && RendererHelper.this.surfaceCreated) {
                        RendererHelper.this.surfaceCreated = false;
                        RendererHelper.this.callOnRendererShutdown();
                    }
                    CardboardViewNativeImpl.this.shutdownLatch.countDown();
                }
            });
        }
    }

    public CardboardViewNativeImpl(Context context) {
        String str;
        this.context = context;
        this.hmdManager = new HeadMountedDisplayManager(context);
        try {
            str = (String) Class.forName(String.valueOf(getClass().getPackage().getName()).concat(".NativeProxy")).getDeclaredField("PROXY_LIBRARY").get(null);
        } catch (Exception e) {
            Log.d(TAG, "NativeProxy not found");
            str = "gvr";
        }
        String str2 = TAG;
        String valueOf = String.valueOf(str);
        Log.d(str2, valueOf.length() == 0 ? new String("Loading native library ") : "Loading native library ".concat(valueOf));
        System.loadLibrary(str);
        Log.d(TAG, "Native library loaded");
        nativeSetApplicationState(getClass().getClassLoader(), context.getApplicationContext());
        this.glSurfaceView = new CardboardGLSurfaceView(context, this);
        this.gvrLayout = new GvrLayout(context);
        this.gvrLayout.setPresentationView(this.glSurfaceView);
        this.gvrLayout.addPresentationListener(new PresentationListener());
        this.rendererHelper = new RendererHelper();
        this.uiLayout = this.gvrLayout.getUiLayout();
        this.gvrApi = this.gvrLayout.getGvrApi();
        this.nativeCardboardView = nativeInit(this.gvrApi.getNativeGvrContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkNativeCardboardView() {
        if (this.nativeCardboardView == 0) {
            throw new IllegalStateException("GvrView has already been shut down.");
        }
    }

    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
    }

    private native void nativeDestroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeGetCurrentEyeParams(long j, HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5);

    private native float nativeGetNeckModelFactor(long j);

    private native long nativeInit(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeLogEvent(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeOnDrawFrame(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeOnSurfaceChanged(long j, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeOnSurfaceCreated(long j);

    private static native long nativeSetApplicationState(ClassLoader classLoader, Context context);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetDepthStencilFormat(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetDistortionCorrectionEnabled(long j, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetDistortionCorrectionScale(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetGvrViewerParams(long j, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetMultisampling(long j, int i);

    private native void nativeSetNeckModelEnabled(long j, boolean z);

    private native void nativeSetNeckModelFactor(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetRenderer(long j, GvrView.Renderer renderer);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetScreenParams(long j, int i, int i2, float f, float f2, float f3);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetStereoModeEnabled(long j, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeSetStereoRenderer(long j, GvrView.StereoRenderer stereoRenderer);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeUndistortTexture(long j, int i);

    @UsedByNative
    private void onCardboardBack() {
        runOnCardboardBackListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UsedByNative
    public void onCardboardTrigger() {
        Runnable runnable = this.cardboardTriggerListener;
        if (runnable == null) {
            return;
        }
        ThreadUtils.runOnUiThread(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueEvent(Runnable runnable) {
        this.glSurfaceView.queueEvent(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconnectSensors() {
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.10
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.gvrApi.reconnectSensors();
            }
        });
    }

    private void runOnCardboardBackListener() {
        Runnable runnable = this.cardboardBackListener;
        if (runnable == null) {
            return;
        }
        ThreadUtils.runOnUiThread(runnable);
    }

    private void setGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        this.uiLayout.setViewerName(new GvrViewerParams(gvrViewerParams).getModel());
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.8
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeSetGvrViewerParams(CardboardViewNativeImpl.this.nativeCardboardView, gvrViewerParams.toByteArray());
            }
        });
    }

    private void setScreenParams(ScreenParams screenParams) {
        final ScreenParams screenParams2 = new ScreenParams(screenParams);
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.9
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.rendererHelper.setScreenParams(screenParams2);
                CardboardViewNativeImpl.this.nativeSetScreenParams(CardboardViewNativeImpl.this.nativeCardboardView, screenParams2.getWidth(), screenParams2.getHeight(), screenParams2.getWidthMeters() / screenParams2.getWidth(), screenParams2.getHeightMeters() / screenParams2.getHeight(), screenParams2.getBorderSizeMeters());
            }
        });
    }

    private void updateTransitionListener() {
        if (this.uiLayout.getUiLayer().getTransitionViewEnabled()) {
            this.uiLayout.getUiLayer().setTransitionViewListener(new TransitionView.TransitionListener() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.1
                @Override // com.google.vr.cardboard.TransitionView.TransitionListener
                public void onSwitchViewer() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2003);
                    }
                }

                @Override // com.google.vr.cardboard.TransitionView.TransitionListener
                public void onTransitionDone() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2002);
                    }
                    if (CardboardViewNativeImpl.this.transitionDoneListener == null) {
                        return;
                    }
                    ThreadUtils.runOnUiThread(CardboardViewNativeImpl.this.transitionDoneListener);
                }
            });
        } else {
            this.uiLayout.getUiLayer().setTransitionViewListener(null);
        }
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void enableCardboardTriggerEmulation() {
        this.gvrLayout.enableCardboardTriggerEmulation(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.7
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.onCardboardTrigger();
            }
        });
    }

    protected void finalize() throws Throwable {
        try {
            if (this.nativeCardboardView != 0) {
                Log.w(TAG, "GvrView.shutdown() should be called to ensure resource cleanup");
                nativeDestroy(this.nativeCardboardView);
            }
        } finally {
            super.finalize();
        }
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public boolean getAsyncReprojectionEnabled() {
        return this.gvrLayout.getGvrApi().getAsyncReprojectionEnabled();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void getCurrentEyeParams(HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5) {
        this.rendererHelper.getCurrentEyeParams(headTransform, eye, eye2, eye3, eye4, eye5);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public boolean getDistortionCorrectionEnabled() {
        return this.distortionCorrectionEnabled;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public GvrSurfaceView getGvrSurfaceView() {
        return this.glSurfaceView;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public GvrViewerParams getGvrViewerParams() {
        return this.hmdManager.getHeadMountedDisplay().getGvrViewerParams();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.hmdManager.getHeadMountedDisplay();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public float getInterpupillaryDistance() {
        return getGvrViewerParams().getInterLensDistance();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public float getNeckModelFactor() {
        return nativeGetNeckModelFactor(this.nativeCardboardView);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public View getRootView() {
        return this.gvrLayout;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public ScreenParams getScreenParams() {
        return this.hmdManager.getHeadMountedDisplay().getScreenParams();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public boolean getStereoModeEnabled() {
        return this.stereoMode;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void onPause() {
        checkNativeCardboardView();
        this.gvrApi.pauseTracking();
        this.hmdManager.onPause();
        this.glSurfaceView.onPause();
        this.gvrLayout.onPause();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void onResume() {
        checkNativeCardboardView();
        this.gvrLayout.onResume();
        this.glSurfaceView.onResume();
        this.hmdManager.onResume();
        setScreenParams(getScreenParams());
        setGvrViewerParams(getGvrViewerParams());
        this.gvrApi.resumeTracking();
    }

    @Override // com.google.vr.cardboard.CardboardGLSurfaceView.DetachListener
    public void onSurfaceViewDetachedFromWindow() {
        if (this.shutdownLatch != null) {
            return;
        }
        this.shutdownLatch = new CountDownLatch(1);
        this.rendererHelper.shutdown();
        try {
            this.shutdownLatch.await();
        } catch (InterruptedException e) {
            String str = TAG;
            String valueOf = String.valueOf(e.toString());
            Log.e(str, valueOf.length() == 0 ? new String("Interrupted during shutdown: ") : "Interrupted during shutdown: ".concat(valueOf));
        }
        this.shutdownLatch = null;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.cardboardTriggerListener != null) {
            onCardboardTrigger();
            return true;
        }
        return false;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void recenterHeadTracker() {
        this.gvrApi.recenterTracking();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void resetHeadTracker() {
        this.gvrApi.resetTracking();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public boolean setAsyncReprojectionEnabled(boolean z) {
        return this.gvrLayout.setAsyncReprojectionEnabled(z);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setDepthStencilFormat(final int i) {
        checkNativeCardboardView();
        if (!BufferSpec.isValidDepthStencilFormat(i)) {
            throw new IllegalArgumentException("Invalid depth-stencil format.");
        }
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.6
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDepthStencilFormat(CardboardViewNativeImpl.this.nativeCardboardView, i);
            }
        });
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setDistortionCorrectionEnabled(final boolean z) {
        checkNativeCardboardView();
        this.distortionCorrectionEnabled = z;
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.2
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionEnabled(CardboardViewNativeImpl.this.nativeCardboardView, z);
            }
        });
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setDistortionCorrectionScale(final float f) {
        checkNativeCardboardView();
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.4
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionScale(CardboardViewNativeImpl.this.nativeCardboardView, f);
            }
        });
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setMultisampling(final int i) {
        checkNativeCardboardView();
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.5
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeSetMultisampling(CardboardViewNativeImpl.this.nativeCardboardView, i);
            }
        });
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setNeckModelEnabled(boolean z) {
        nativeSetNeckModelEnabled(this.nativeCardboardView, z);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setNeckModelFactor(float f) {
        nativeSetNeckModelFactor(this.nativeCardboardView, f);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setOnCardboardBackListener(Runnable runnable) {
        this.cardboardBackListener = runnable;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setOnCardboardTriggerListener(Runnable runnable) {
        this.cardboardTriggerListener = runnable;
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setOnCloseButtonListener(Runnable runnable) {
        this.uiLayout.setCloseButtonListener(runnable);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setOnTransitionViewDoneListener(Runnable runnable) {
        this.transitionDoneListener = runnable;
        updateTransitionListener();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setRenderer(GvrView.Renderer renderer) {
        this.rendererHelper.setRenderer(renderer);
        this.glSurfaceView.setRenderer(this.rendererHelper);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setRenderer(GvrView.StereoRenderer stereoRenderer) {
        this.rendererHelper.setRenderer(stereoRenderer);
        this.glSurfaceView.setRenderer(this.rendererHelper);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setStereoModeEnabled(boolean z) {
        this.stereoMode = z;
        this.rendererHelper.setStereoModeEnabled(z);
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void setTransitionViewEnabled(boolean z) {
        this.uiLayout.setTransitionViewEnabled(z);
        updateTransitionListener();
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void shutdown() {
        if (this.nativeCardboardView != 0) {
            this.gvrLayout.shutdown();
            nativeDestroy(this.nativeCardboardView);
            this.nativeCardboardView = 0L;
        }
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void undistortTexture(final int i) {
        checkNativeCardboardView();
        queueEvent(new Runnable() { // from class: com.google.vr.sdk.base.CardboardViewNativeImpl.3
            @Override // java.lang.Runnable
            public void run() {
                CardboardViewNativeImpl.this.nativeUndistortTexture(CardboardViewNativeImpl.this.nativeCardboardView, i);
            }
        });
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        if (this.hmdManager.updateGvrViewerParams(gvrViewerParams)) {
            setGvrViewerParams(getGvrViewerParams());
        }
    }

    @Override // com.google.vr.sdk.base.CardboardViewApi
    public void updateScreenParams(ScreenParams screenParams) {
        if (this.hmdManager.updateScreenParams(screenParams)) {
            setScreenParams(getScreenParams());
        }
    }
}
