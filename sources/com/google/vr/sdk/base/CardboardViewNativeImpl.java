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
public class CardboardViewNativeImpl implements CardboardGLSurfaceView.DetachListener, CardboardViewApi {
    /* access modifiers changed from: private */
    public static final String TAG = CardboardViewNativeImpl.class.getSimpleName();
    private volatile Runnable cardboardBackListener;
    private int cardboardTriggerCount = 0;
    private volatile Runnable cardboardTriggerListener;
    private final Context context;
    private volatile boolean distortionCorrectionEnabled = true;
    private final CardboardGLSurfaceView glSurfaceView;
    /* access modifiers changed from: private */
    public final GvrApi gvrApi;
    /* access modifiers changed from: private */
    public final GvrLayout gvrLayout;
    private final HeadMountedDisplayManager hmdManager;
    /* access modifiers changed from: private */
    public long nativeCardboardView;
    /* access modifiers changed from: private */
    public final RendererHelper rendererHelper;
    /* access modifiers changed from: private */
    public CountDownLatch shutdownLatch;
    /* access modifiers changed from: private */
    public boolean stereoMode = true;
    /* access modifiers changed from: private */
    public Runnable transitionDoneListener;
    private final GvrUiLayout uiLayout;

    private class PresentationListener implements GvrLayout.PresentationListener {
        ScreenParams originalParams;

        private PresentationListener() {
        }

        public void onPresentationStarted(Display display) {
            this.originalParams = CardboardViewNativeImpl.this.getScreenParams();
            CardboardViewNativeImpl.this.updateScreenParams(new ScreenParams(display));
            CardboardViewNativeImpl.this.reconnectSensors();
        }

        public void onPresentationStopped() {
            if (this.originalParams != null) {
                CardboardViewNativeImpl.this.updateScreenParams(this.originalParams);
            }
        }
    }

    private class RendererHelper implements GLSurfaceView.Renderer {
        private EGLDisplay eglDisplay;
        private boolean invalidSurfaceSizeWarningShown;
        /* access modifiers changed from: private */
        public GvrView.Renderer renderer;
        /* access modifiers changed from: private */
        public ScreenParams screenParams;
        /* access modifiers changed from: private */
        public boolean stereoMode;
        /* access modifiers changed from: private */
        public GvrView.StereoRenderer stereoRenderer;
        /* access modifiers changed from: private */
        public boolean surfaceCreated;

        public RendererHelper() {
            this.screenParams = new ScreenParams(CardboardViewNativeImpl.this.getScreenParams());
            this.stereoMode = CardboardViewNativeImpl.this.stereoMode;
        }

        /* access modifiers changed from: private */
        public void callOnRendererShutdown() {
            if (this.renderer != null) {
                this.renderer.onRendererShutdown();
            } else if (this.stereoRenderer != null) {
                this.stereoRenderer.onRendererShutdown();
            }
        }

        private void callOnSurfaceChanged(int i, int i2) {
            if (this.renderer != null) {
                this.renderer.onSurfaceChanged(i, i2);
            } else if (this.stereoRenderer != null) {
                if (!this.stereoMode) {
                    this.stereoRenderer.onSurfaceChanged(i, i2);
                } else {
                    this.stereoRenderer.onSurfaceChanged(i / 2, i2);
                }
            }
        }

        private void callOnSurfaceCreated(EGLConfig eGLConfig) {
            CardboardViewNativeImpl.this.nativeOnSurfaceCreated(CardboardViewNativeImpl.this.nativeCardboardView);
            if (this.renderer != null) {
                this.renderer.onSurfaceCreated(eGLConfig);
            } else if (this.stereoRenderer != null) {
                this.stereoRenderer.onSurfaceCreated(eGLConfig);
            }
        }

        public void getCurrentEyeParams(HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final HeadTransform headTransform2 = headTransform;
            final Eye eye6 = eye;
            final Eye eye7 = eye2;
            final Eye eye8 = eye3;
            final Eye eye9 = eye4;
            final Eye eye10 = eye5;
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                public void run() {
                    CardboardViewNativeImpl.this.nativeGetCurrentEyeParams(CardboardViewNativeImpl.this.nativeCardboardView, headTransform2, eye6, eye7, eye8, eye9, eye10);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                String access$3000 = CardboardViewNativeImpl.TAG;
                String valueOf = String.valueOf(e.toString());
                Log.e(access$3000, valueOf.length() == 0 ? new String("Interrupted while reading frame params: ") : "Interrupted while reading frame params: ".concat(valueOf));
            }
        }

        public void onDrawFrame(GL10 gl10) {
            if ((this.renderer != null || this.stereoRenderer != null) && this.surfaceCreated) {
                Trace.beginSection("Render");
                CardboardViewNativeImpl.this.nativeOnDrawFrame(CardboardViewNativeImpl.this.nativeCardboardView);
                Trace.endSection();
                EGL14.eglSwapInterval(this.eglDisplay, 1);
            }
        }

        public void onSurfaceChanged(GL10 gl10, int i, int i2) {
            if ((this.renderer != null || this.stereoRenderer != null) && this.surfaceCreated) {
                if (this.stereoMode && !(i == this.screenParams.getWidth() && i2 == this.screenParams.getHeight())) {
                    if (!this.invalidSurfaceSizeWarningShown) {
                        String access$3000 = CardboardViewNativeImpl.TAG;
                        int width = this.screenParams.getWidth();
                        Log.e(access$3000, new StringBuilder(134).append("Surface size ").append(i).append("x").append(i2).append(" does not match the expected screen size ").append(width).append("x").append(this.screenParams.getHeight()).append(". Stereo rendering might feel off.").toString());
                    }
                    this.invalidSurfaceSizeWarningShown = true;
                } else {
                    this.invalidSurfaceSizeWarningShown = false;
                }
                CardboardViewNativeImpl.this.nativeOnSurfaceChanged(CardboardViewNativeImpl.this.nativeCardboardView, i, i2);
                callOnSurfaceChanged(i, i2);
            }
        }

        public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
            if (this.renderer != null || this.stereoRenderer != null) {
                this.surfaceCreated = true;
                this.eglDisplay = EGL14.eglGetCurrentDisplay();
                callOnSurfaceCreated(eGLConfig);
            }
        }

        public void setRenderer(GvrView.Renderer renderer2) {
            this.renderer = renderer2;
            CardboardViewNativeImpl.this.nativeSetRenderer(CardboardViewNativeImpl.this.nativeCardboardView, renderer2);
        }

        public void setRenderer(GvrView.StereoRenderer stereoRenderer2) {
            this.stereoRenderer = stereoRenderer2;
            CardboardViewNativeImpl.this.nativeSetStereoRenderer(CardboardViewNativeImpl.this.nativeCardboardView, stereoRenderer2);
        }

        public void setScreenParams(ScreenParams screenParams2) {
            this.screenParams = screenParams2;
        }

        public void setStereoModeEnabled(final boolean z) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            CardboardViewNativeImpl.this.gvrLayout.setStereoModeEnabled(z);
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                public void run() {
                    if (RendererHelper.this.stereoMode != z) {
                        boolean unused = RendererHelper.this.stereoMode = z;
                        CardboardViewNativeImpl.this.nativeSetStereoModeEnabled(CardboardViewNativeImpl.this.nativeCardboardView, z);
                        if (!EGL10.EGL_NO_SURFACE.equals(((EGL10) EGLContext.getEGL()).eglGetCurrentSurface(12377))) {
                            RendererHelper.this.onSurfaceChanged((GL10) null, RendererHelper.this.screenParams.getWidth(), RendererHelper.this.screenParams.getHeight());
                        }
                    }
                }
            });
        }

        public void shutdown() {
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                public void run() {
                    if (!(RendererHelper.this.renderer == null && RendererHelper.this.stereoRenderer == null) && RendererHelper.this.surfaceCreated) {
                        boolean unused = RendererHelper.this.surfaceCreated = false;
                        RendererHelper.this.callOnRendererShutdown();
                    }
                    CardboardViewNativeImpl.this.shutdownLatch.countDown();
                }
            });
        }
    }

    public CardboardViewNativeImpl(Context context2) {
        String str;
        this.context = context2;
        this.hmdManager = new HeadMountedDisplayManager(context2);
        try {
            str = (String) Class.forName(String.valueOf(getClass().getPackage().getName()).concat(".NativeProxy")).getDeclaredField("PROXY_LIBRARY").get((Object) null);
        } catch (Exception e) {
            Log.d(TAG, "NativeProxy not found");
            str = "gvr";
        }
        String str2 = TAG;
        String valueOf = String.valueOf(str);
        Log.d(str2, valueOf.length() == 0 ? new String("Loading native library ") : "Loading native library ".concat(valueOf));
        System.loadLibrary(str);
        Log.d(TAG, "Native library loaded");
        nativeSetApplicationState(getClass().getClassLoader(), context2.getApplicationContext());
        this.glSurfaceView = new CardboardGLSurfaceView(context2, this);
        this.gvrLayout = new GvrLayout(context2);
        this.gvrLayout.setPresentationView(this.glSurfaceView);
        this.gvrLayout.addPresentationListener(new PresentationListener());
        this.rendererHelper = new RendererHelper();
        this.uiLayout = this.gvrLayout.getUiLayout();
        this.gvrApi = this.gvrLayout.getGvrApi();
        this.nativeCardboardView = nativeInit(this.gvrApi.getNativeGvrContext());
    }

    /* access modifiers changed from: private */
    public void checkNativeCardboardView() {
        if (this.nativeCardboardView == 0) {
            throw new IllegalStateException("GvrView has already been shut down.");
        }
    }

    private static Display getDefaultDisplay(Context context2) {
        return ((WindowManager) context2.getSystemService("window")).getDefaultDisplay();
    }

    private native void nativeDestroy(long j);

    /* access modifiers changed from: private */
    public native void nativeGetCurrentEyeParams(long j, HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5);

    private native float nativeGetNeckModelFactor(long j);

    private native long nativeInit(long j);

    /* access modifiers changed from: private */
    public native void nativeLogEvent(long j, int i);

    /* access modifiers changed from: private */
    public native void nativeOnDrawFrame(long j);

    /* access modifiers changed from: private */
    public native void nativeOnSurfaceChanged(long j, int i, int i2);

    /* access modifiers changed from: private */
    public native void nativeOnSurfaceCreated(long j);

    private static native long nativeSetApplicationState(ClassLoader classLoader, Context context2);

    /* access modifiers changed from: private */
    public native void nativeSetDepthStencilFormat(long j, int i);

    /* access modifiers changed from: private */
    public native void nativeSetDistortionCorrectionEnabled(long j, boolean z);

    /* access modifiers changed from: private */
    public native void nativeSetDistortionCorrectionScale(long j, float f);

    /* access modifiers changed from: private */
    public native void nativeSetGvrViewerParams(long j, byte[] bArr);

    /* access modifiers changed from: private */
    public native void nativeSetMultisampling(long j, int i);

    private native void nativeSetNeckModelEnabled(long j, boolean z);

    private native void nativeSetNeckModelFactor(long j, float f);

    /* access modifiers changed from: private */
    public native void nativeSetRenderer(long j, GvrView.Renderer renderer);

    /* access modifiers changed from: private */
    public native void nativeSetScreenParams(long j, int i, int i2, float f, float f2, float f3);

    /* access modifiers changed from: private */
    public native void nativeSetStereoModeEnabled(long j, boolean z);

    /* access modifiers changed from: private */
    public native void nativeSetStereoRenderer(long j, GvrView.StereoRenderer stereoRenderer);

    /* access modifiers changed from: private */
    public native void nativeUndistortTexture(long j, int i);

    @UsedByNative
    private void onCardboardBack() {
        runOnCardboardBackListener();
    }

    /* access modifiers changed from: private */
    @UsedByNative
    public void onCardboardTrigger() {
        Runnable runnable = this.cardboardTriggerListener;
        if (runnable != null) {
            ThreadUtils.runOnUiThread(runnable);
        }
    }

    /* access modifiers changed from: private */
    public void queueEvent(Runnable runnable) {
        this.glSurfaceView.queueEvent(runnable);
    }

    /* access modifiers changed from: private */
    public void reconnectSensors() {
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.gvrApi.reconnectSensors();
            }
        });
    }

    private void runOnCardboardBackListener() {
        Runnable runnable = this.cardboardBackListener;
        if (runnable != null) {
            ThreadUtils.runOnUiThread(runnable);
        }
    }

    private void setGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        this.uiLayout.setViewerName(new GvrViewerParams(gvrViewerParams).getModel());
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.nativeSetGvrViewerParams(CardboardViewNativeImpl.this.nativeCardboardView, gvrViewerParams.toByteArray());
            }
        });
    }

    private void setScreenParams(ScreenParams screenParams) {
        final ScreenParams screenParams2 = new ScreenParams(screenParams);
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.rendererHelper.setScreenParams(screenParams2);
                CardboardViewNativeImpl.this.nativeSetScreenParams(CardboardViewNativeImpl.this.nativeCardboardView, screenParams2.getWidth(), screenParams2.getHeight(), screenParams2.getWidthMeters() / ((float) screenParams2.getWidth()), screenParams2.getHeightMeters() / ((float) screenParams2.getHeight()), screenParams2.getBorderSizeMeters());
            }
        });
    }

    private void updateTransitionListener() {
        if (!this.uiLayout.getUiLayer().getTransitionViewEnabled()) {
            this.uiLayout.getUiLayer().setTransitionViewListener((TransitionView.TransitionListener) null);
        } else {
            this.uiLayout.getUiLayer().setTransitionViewListener(new TransitionView.TransitionListener() {
                public void onSwitchViewer() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2003);
                    }
                }

                public void onTransitionDone() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2002);
                    }
                    if (CardboardViewNativeImpl.this.transitionDoneListener != null) {
                        ThreadUtils.runOnUiThread(CardboardViewNativeImpl.this.transitionDoneListener);
                    }
                }
            });
        }
    }

    public void enableCardboardTriggerEmulation() {
        this.gvrLayout.enableCardboardTriggerEmulation(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.onCardboardTrigger();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.nativeCardboardView != 0) {
                Log.w(TAG, "GvrView.shutdown() should be called to ensure resource cleanup");
                nativeDestroy(this.nativeCardboardView);
            }
        } finally {
            super.finalize();
        }
    }

    public boolean getAsyncReprojectionEnabled() {
        return this.gvrLayout.getGvrApi().getAsyncReprojectionEnabled();
    }

    public void getCurrentEyeParams(HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5) {
        this.rendererHelper.getCurrentEyeParams(headTransform, eye, eye2, eye3, eye4, eye5);
    }

    public boolean getDistortionCorrectionEnabled() {
        return this.distortionCorrectionEnabled;
    }

    public GvrSurfaceView getGvrSurfaceView() {
        return this.glSurfaceView;
    }

    public GvrViewerParams getGvrViewerParams() {
        return this.hmdManager.getHeadMountedDisplay().getGvrViewerParams();
    }

    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.hmdManager.getHeadMountedDisplay();
    }

    public float getInterpupillaryDistance() {
        return getGvrViewerParams().getInterLensDistance();
    }

    public float getNeckModelFactor() {
        return nativeGetNeckModelFactor(this.nativeCardboardView);
    }

    public View getRootView() {
        return this.gvrLayout;
    }

    public ScreenParams getScreenParams() {
        return this.hmdManager.getHeadMountedDisplay().getScreenParams();
    }

    public boolean getStereoModeEnabled() {
        return this.stereoMode;
    }

    public void onPause() {
        checkNativeCardboardView();
        this.gvrApi.pauseTracking();
        this.hmdManager.onPause();
        this.glSurfaceView.onPause();
        this.gvrLayout.onPause();
    }

    public void onResume() {
        checkNativeCardboardView();
        this.gvrLayout.onResume();
        this.glSurfaceView.onResume();
        this.hmdManager.onResume();
        setScreenParams(getScreenParams());
        setGvrViewerParams(getGvrViewerParams());
        this.gvrApi.resumeTracking();
    }

    public void onSurfaceViewDetachedFromWindow() {
        if (this.shutdownLatch == null) {
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
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0 || this.cardboardTriggerListener == null) {
            return false;
        }
        onCardboardTrigger();
        return true;
    }

    public void recenterHeadTracker() {
        this.gvrApi.recenterTracking();
    }

    public void resetHeadTracker() {
        this.gvrApi.resetTracking();
    }

    public boolean setAsyncReprojectionEnabled(boolean z) {
        return this.gvrLayout.setAsyncReprojectionEnabled(z);
    }

    public void setDepthStencilFormat(final int i) {
        checkNativeCardboardView();
        if (BufferSpec.isValidDepthStencilFormat(i)) {
            queueEvent(new Runnable() {
                public void run() {
                    CardboardViewNativeImpl.this.nativeSetDepthStencilFormat(CardboardViewNativeImpl.this.nativeCardboardView, i);
                }
            });
            return;
        }
        throw new IllegalArgumentException("Invalid depth-stencil format.");
    }

    public void setDistortionCorrectionEnabled(final boolean z) {
        checkNativeCardboardView();
        this.distortionCorrectionEnabled = z;
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionEnabled(CardboardViewNativeImpl.this.nativeCardboardView, z);
            }
        });
    }

    public void setDistortionCorrectionScale(final float f) {
        checkNativeCardboardView();
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionScale(CardboardViewNativeImpl.this.nativeCardboardView, f);
            }
        });
    }

    public void setMultisampling(final int i) {
        checkNativeCardboardView();
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.nativeSetMultisampling(CardboardViewNativeImpl.this.nativeCardboardView, i);
            }
        });
    }

    public void setNeckModelEnabled(boolean z) {
        nativeSetNeckModelEnabled(this.nativeCardboardView, z);
    }

    public void setNeckModelFactor(float f) {
        nativeSetNeckModelFactor(this.nativeCardboardView, f);
    }

    public void setOnCardboardBackListener(Runnable runnable) {
        this.cardboardBackListener = runnable;
    }

    public void setOnCardboardTriggerListener(Runnable runnable) {
        this.cardboardTriggerListener = runnable;
    }

    public void setOnCloseButtonListener(Runnable runnable) {
        this.uiLayout.setCloseButtonListener(runnable);
    }

    public void setOnTransitionViewDoneListener(Runnable runnable) {
        this.transitionDoneListener = runnable;
        updateTransitionListener();
    }

    public void setRenderer(GvrView.Renderer renderer) {
        this.rendererHelper.setRenderer(renderer);
        this.glSurfaceView.setRenderer(this.rendererHelper);
    }

    public void setRenderer(GvrView.StereoRenderer stereoRenderer) {
        this.rendererHelper.setRenderer(stereoRenderer);
        this.glSurfaceView.setRenderer(this.rendererHelper);
    }

    public void setStereoModeEnabled(boolean z) {
        this.stereoMode = z;
        this.rendererHelper.setStereoModeEnabled(z);
    }

    public void setTransitionViewEnabled(boolean z) {
        this.uiLayout.setTransitionViewEnabled(z);
        updateTransitionListener();
    }

    public void shutdown() {
        if (this.nativeCardboardView != 0) {
            this.gvrLayout.shutdown();
            nativeDestroy(this.nativeCardboardView);
            this.nativeCardboardView = 0;
        }
    }

    public void undistortTexture(final int i) {
        checkNativeCardboardView();
        queueEvent(new Runnable() {
            public void run() {
                CardboardViewNativeImpl.this.nativeUndistortTexture(CardboardViewNativeImpl.this.nativeCardboardView, i);
            }
        });
    }

    public void updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        if (this.hmdManager.updateGvrViewerParams(gvrViewerParams)) {
            setGvrViewerParams(getGvrViewerParams());
        }
    }

    public void updateScreenParams(ScreenParams screenParams) {
        if (this.hmdManager.updateScreenParams(screenParams)) {
            setScreenParams(getScreenParams());
        }
    }
}
