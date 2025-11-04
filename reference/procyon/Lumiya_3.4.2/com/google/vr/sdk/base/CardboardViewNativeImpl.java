// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGL10;
import android.opengl.EGL14;
import android.os.Trace;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.EGLDisplay;
import android.opengl.GLSurfaceView$Renderer;
import com.google.vr.ndk.base.BufferSpec;
import android.view.MotionEvent;
import com.google.vr.ndk.base.GvrSurfaceView;
import com.google.vr.cardboard.TransitionView;
import com.google.vr.cardboard.ThreadUtils;
import android.view.WindowManager;
import android.view.Display;
import android.view.View;
import android.util.Log;
import com.google.vr.ndk.base.GvrUiLayout;
import java.util.concurrent.CountDownLatch;
import com.google.vr.ndk.base.GvrLayout;
import com.google.vr.ndk.base.GvrApi;
import android.content.Context;
import com.google.vr.cardboard.UsedByNative;
import com.google.vr.cardboard.CardboardGLSurfaceView;

@UsedByNative
public class CardboardViewNativeImpl implements DetachListener, CardboardViewApi
{
    private static final String TAG;
    private volatile Runnable cardboardBackListener;
    private int cardboardTriggerCount;
    private volatile Runnable cardboardTriggerListener;
    private final Context context;
    private volatile boolean distortionCorrectionEnabled;
    private final CardboardGLSurfaceView glSurfaceView;
    private final GvrApi gvrApi;
    private final GvrLayout gvrLayout;
    private final HeadMountedDisplayManager hmdManager;
    private long nativeCardboardView;
    private final RendererHelper rendererHelper;
    private CountDownLatch shutdownLatch;
    private boolean stereoMode;
    private Runnable transitionDoneListener;
    private final GvrUiLayout uiLayout;
    
    static {
        TAG = CardboardViewNativeImpl.class.getSimpleName();
    }
    
    public CardboardViewNativeImpl(final Context context) {
    Label_0099_Outer:
        while (true) {
            this.cardboardTriggerCount = 0;
            this.stereoMode = true;
            this.distortionCorrectionEnabled = true;
            this.context = context;
            this.hmdManager = new HeadMountedDisplayManager(context);
            while (true) {
                String value;
                while (true) {
                    try {
                        final String s = (String)Class.forName(String.valueOf(this.getClass().getPackage().getName()).concat(".NativeProxy")).getDeclaredField("PROXY_LIBRARY").get(null);
                        final String tag = CardboardViewNativeImpl.TAG;
                        value = String.valueOf(s);
                        if (value.length() == 0) {
                            final String concat = new String("Loading native library ");
                            Log.d(tag, concat);
                            System.loadLibrary(s);
                            Log.d(CardboardViewNativeImpl.TAG, "Native library loaded");
                            nativeSetApplicationState(this.getClass().getClassLoader(), context.getApplicationContext());
                            this.glSurfaceView = new CardboardGLSurfaceView(context, (CardboardGLSurfaceView.DetachListener)this);
                            (this.gvrLayout = new GvrLayout(context)).setPresentationView((View)this.glSurfaceView);
                            this.gvrLayout.addPresentationListener((GvrLayout.PresentationListener)new PresentationListener());
                            this.rendererHelper = new RendererHelper();
                            this.uiLayout = this.gvrLayout.getUiLayout();
                            this.gvrApi = this.gvrLayout.getGvrApi();
                            this.nativeCardboardView = this.nativeInit(this.gvrApi.getNativeGvrContext());
                            return;
                        }
                    }
                    catch (final Exception ex) {
                        Log.d(CardboardViewNativeImpl.TAG, "NativeProxy not found");
                        final String s = "gvr";
                        continue Label_0099_Outer;
                    }
                    break;
                }
                final String concat = "Loading native library ".concat(value);
                continue;
            }
        }
    }
    
    private void checkNativeCardboardView() {
        if (this.nativeCardboardView == 0L) {
            throw new IllegalStateException("GvrView has already been shut down.");
        }
    }
    
    private static Display getDefaultDisplay(final Context context) {
        return ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
    }
    
    private native void nativeDestroy(final long p0);
    
    private native void nativeGetCurrentEyeParams(final long p0, final HeadTransform p1, final Eye p2, final Eye p3, final Eye p4, final Eye p5, final Eye p6);
    
    private native float nativeGetNeckModelFactor(final long p0);
    
    private native long nativeInit(final long p0);
    
    private native void nativeLogEvent(final long p0, final int p1);
    
    private native void nativeOnDrawFrame(final long p0);
    
    private native void nativeOnSurfaceChanged(final long p0, final int p1, final int p2);
    
    private native void nativeOnSurfaceCreated(final long p0);
    
    private static native long nativeSetApplicationState(final ClassLoader p0, final Context p1);
    
    private native void nativeSetDepthStencilFormat(final long p0, final int p1);
    
    private native void nativeSetDistortionCorrectionEnabled(final long p0, final boolean p1);
    
    private native void nativeSetDistortionCorrectionScale(final long p0, final float p1);
    
    private native void nativeSetGvrViewerParams(final long p0, final byte[] p1);
    
    private native void nativeSetMultisampling(final long p0, final int p1);
    
    private native void nativeSetNeckModelEnabled(final long p0, final boolean p1);
    
    private native void nativeSetNeckModelFactor(final long p0, final float p1);
    
    private native void nativeSetRenderer(final long p0, final GvrView.Renderer p1);
    
    private native void nativeSetScreenParams(final long p0, final int p1, final int p2, final float p3, final float p4, final float p5);
    
    private native void nativeSetStereoModeEnabled(final long p0, final boolean p1);
    
    private native void nativeSetStereoRenderer(final long p0, final GvrView.StereoRenderer p1);
    
    private native void nativeUndistortTexture(final long p0, final int p1);
    
    @UsedByNative
    private void onCardboardBack() {
        this.runOnCardboardBackListener();
    }
    
    @UsedByNative
    private void onCardboardTrigger() {
        final Runnable cardboardTriggerListener = this.cardboardTriggerListener;
        if (cardboardTriggerListener != null) {
            ThreadUtils.runOnUiThread(cardboardTriggerListener);
        }
    }
    
    private void queueEvent(final Runnable runnable) {
        this.glSurfaceView.queueEvent(runnable);
    }
    
    private void reconnectSensors() {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.gvrApi.reconnectSensors();
            }
        });
    }
    
    private void runOnCardboardBackListener() {
        final Runnable cardboardBackListener = this.cardboardBackListener;
        if (cardboardBackListener != null) {
            ThreadUtils.runOnUiThread(cardboardBackListener);
        }
    }
    
    private void setGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        this.uiLayout.setViewerName(new GvrViewerParams(gvrViewerParams).getModel());
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.nativeSetGvrViewerParams(CardboardViewNativeImpl.this.nativeCardboardView, gvrViewerParams.toByteArray());
            }
        });
    }
    
    private void setScreenParams(final ScreenParams screenParams) {
        this.queueEvent(new Runnable() {
            final /* synthetic */ ScreenParams val$screenParams = new ScreenParams(screenParams);
            
            @Override
            public void run() {
                CardboardViewNativeImpl.this.rendererHelper.setScreenParams(this.val$screenParams);
                CardboardViewNativeImpl.this.nativeSetScreenParams(CardboardViewNativeImpl.this.nativeCardboardView, this.val$screenParams.getWidth(), this.val$screenParams.getHeight(), this.val$screenParams.getWidthMeters() / this.val$screenParams.getWidth(), this.val$screenParams.getHeightMeters() / this.val$screenParams.getHeight(), this.val$screenParams.getBorderSizeMeters());
            }
        });
    }
    
    private void updateTransitionListener() {
        if (!this.uiLayout.getUiLayer().getTransitionViewEnabled()) {
            this.uiLayout.getUiLayer().setTransitionViewListener(null);
        }
        else {
            this.uiLayout.getUiLayer().setTransitionViewListener(new TransitionView.TransitionListener() {
                @Override
                public void onSwitchViewer() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0L) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2003);
                    }
                }
                
                @Override
                public void onTransitionDone() {
                    if (CardboardViewNativeImpl.this.nativeCardboardView != 0L) {
                        CardboardViewNativeImpl.this.nativeLogEvent(CardboardViewNativeImpl.this.nativeCardboardView, 2002);
                    }
                    if (CardboardViewNativeImpl.this.transitionDoneListener != null) {
                        ThreadUtils.runOnUiThread(CardboardViewNativeImpl.this.transitionDoneListener);
                    }
                }
            });
        }
    }
    
    @Override
    public void enableCardboardTriggerEmulation() {
        this.gvrLayout.enableCardboardTriggerEmulation(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.onCardboardTrigger();
            }
        });
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeCardboardView != 0L) {
                Log.w(CardboardViewNativeImpl.TAG, "GvrView.shutdown() should be called to ensure resource cleanup");
                this.nativeDestroy(this.nativeCardboardView);
            }
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public boolean getAsyncReprojectionEnabled() {
        return this.gvrLayout.getGvrApi().getAsyncReprojectionEnabled();
    }
    
    @Override
    public void getCurrentEyeParams(final HeadTransform headTransform, final Eye eye, final Eye eye2, final Eye eye3, final Eye eye4, final Eye eye5) {
        this.rendererHelper.getCurrentEyeParams(headTransform, eye, eye2, eye3, eye4, eye5);
    }
    
    @Override
    public boolean getDistortionCorrectionEnabled() {
        return this.distortionCorrectionEnabled;
    }
    
    @Override
    public GvrSurfaceView getGvrSurfaceView() {
        return this.glSurfaceView;
    }
    
    @Override
    public GvrViewerParams getGvrViewerParams() {
        return this.hmdManager.getHeadMountedDisplay().getGvrViewerParams();
    }
    
    @Override
    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.hmdManager.getHeadMountedDisplay();
    }
    
    @Override
    public float getInterpupillaryDistance() {
        return this.getGvrViewerParams().getInterLensDistance();
    }
    
    @Override
    public float getNeckModelFactor() {
        return this.nativeGetNeckModelFactor(this.nativeCardboardView);
    }
    
    @Override
    public View getRootView() {
        return (View)this.gvrLayout;
    }
    
    @Override
    public ScreenParams getScreenParams() {
        return this.hmdManager.getHeadMountedDisplay().getScreenParams();
    }
    
    @Override
    public boolean getStereoModeEnabled() {
        return this.stereoMode;
    }
    
    @Override
    public void onPause() {
        this.checkNativeCardboardView();
        this.gvrApi.pauseTracking();
        this.hmdManager.onPause();
        this.glSurfaceView.onPause();
        this.gvrLayout.onPause();
    }
    
    @Override
    public void onResume() {
        this.checkNativeCardboardView();
        this.gvrLayout.onResume();
        this.glSurfaceView.onResume();
        this.hmdManager.onResume();
        this.setScreenParams(this.getScreenParams());
        this.setGvrViewerParams(this.getGvrViewerParams());
        this.gvrApi.resumeTracking();
    }
    
    @Override
    public void onSurfaceViewDetachedFromWindow() {
        if (this.shutdownLatch == null) {
            this.shutdownLatch = new CountDownLatch(1);
            this.rendererHelper.shutdown();
            while (true) {
                try {
                    this.shutdownLatch.await();
                    this.shutdownLatch = null;
                }
                catch (final InterruptedException ex) {
                    final String tag = CardboardViewNativeImpl.TAG;
                    final String value = String.valueOf(ex.toString());
                    String concat;
                    if (value.length() == 0) {
                        concat = new String("Interrupted during shutdown: ");
                    }
                    else {
                        concat = "Interrupted during shutdown: ".concat(value);
                    }
                    Log.e(tag, concat);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.cardboardTriggerListener != null) {
            this.onCardboardTrigger();
            return true;
        }
        return false;
    }
    
    @Override
    public void recenterHeadTracker() {
        this.gvrApi.recenterTracking();
    }
    
    @Override
    public void resetHeadTracker() {
        this.gvrApi.resetTracking();
    }
    
    @Override
    public boolean setAsyncReprojectionEnabled(final boolean asyncReprojectionEnabled) {
        return this.gvrLayout.setAsyncReprojectionEnabled(asyncReprojectionEnabled);
    }
    
    @Override
    public void setDepthStencilFormat(final int n) {
        this.checkNativeCardboardView();
        if (BufferSpec.isValidDepthStencilFormat(n)) {
            this.queueEvent(new Runnable() {
                @Override
                public void run() {
                    CardboardViewNativeImpl.this.nativeSetDepthStencilFormat(CardboardViewNativeImpl.this.nativeCardboardView, n);
                }
            });
            return;
        }
        throw new IllegalArgumentException("Invalid depth-stencil format.");
    }
    
    @Override
    public void setDistortionCorrectionEnabled(final boolean distortionCorrectionEnabled) {
        this.checkNativeCardboardView();
        this.distortionCorrectionEnabled = distortionCorrectionEnabled;
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionEnabled(CardboardViewNativeImpl.this.nativeCardboardView, distortionCorrectionEnabled);
            }
        });
    }
    
    @Override
    public void setDistortionCorrectionScale(final float n) {
        this.checkNativeCardboardView();
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.nativeSetDistortionCorrectionScale(CardboardViewNativeImpl.this.nativeCardboardView, n);
            }
        });
    }
    
    @Override
    public void setMultisampling(final int n) {
        this.checkNativeCardboardView();
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.nativeSetMultisampling(CardboardViewNativeImpl.this.nativeCardboardView, n);
            }
        });
    }
    
    @Override
    public void setNeckModelEnabled(final boolean b) {
        this.nativeSetNeckModelEnabled(this.nativeCardboardView, b);
    }
    
    @Override
    public void setNeckModelFactor(final float n) {
        this.nativeSetNeckModelFactor(this.nativeCardboardView, n);
    }
    
    @Override
    public void setOnCardboardBackListener(final Runnable cardboardBackListener) {
        this.cardboardBackListener = cardboardBackListener;
    }
    
    @Override
    public void setOnCardboardTriggerListener(final Runnable cardboardTriggerListener) {
        this.cardboardTriggerListener = cardboardTriggerListener;
    }
    
    @Override
    public void setOnCloseButtonListener(final Runnable closeButtonListener) {
        this.uiLayout.setCloseButtonListener(closeButtonListener);
    }
    
    @Override
    public void setOnTransitionViewDoneListener(final Runnable transitionDoneListener) {
        this.transitionDoneListener = transitionDoneListener;
        this.updateTransitionListener();
    }
    
    @Override
    public void setRenderer(final GvrView.Renderer renderer) {
        this.rendererHelper.setRenderer(renderer);
        this.glSurfaceView.setRenderer((GLSurfaceView$Renderer)this.rendererHelper);
    }
    
    @Override
    public void setRenderer(final GvrView.StereoRenderer renderer) {
        this.rendererHelper.setRenderer(renderer);
        this.glSurfaceView.setRenderer((GLSurfaceView$Renderer)this.rendererHelper);
    }
    
    @Override
    public void setStereoModeEnabled(final boolean b) {
        this.stereoMode = b;
        this.rendererHelper.setStereoModeEnabled(b);
    }
    
    @Override
    public void setTransitionViewEnabled(final boolean transitionViewEnabled) {
        this.uiLayout.setTransitionViewEnabled(transitionViewEnabled);
        this.updateTransitionListener();
    }
    
    @Override
    public void shutdown() {
        if (this.nativeCardboardView != 0L) {
            this.gvrLayout.shutdown();
            this.nativeDestroy(this.nativeCardboardView);
            this.nativeCardboardView = 0L;
        }
    }
    
    @Override
    public void undistortTexture(final int n) {
        this.checkNativeCardboardView();
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                CardboardViewNativeImpl.this.nativeUndistortTexture(CardboardViewNativeImpl.this.nativeCardboardView, n);
            }
        });
    }
    
    @Override
    public void updateGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        if (this.hmdManager.updateGvrViewerParams(gvrViewerParams)) {
            this.setGvrViewerParams(this.getGvrViewerParams());
        }
    }
    
    @Override
    public void updateScreenParams(final ScreenParams screenParams) {
        if (this.hmdManager.updateScreenParams(screenParams)) {
            this.setScreenParams(this.getScreenParams());
        }
    }
    
    private class PresentationListener implements GvrLayout.PresentationListener
    {
        ScreenParams originalParams;
        
        @Override
        public void onPresentationStarted(final Display display) {
            this.originalParams = CardboardViewNativeImpl.this.getScreenParams();
            CardboardViewNativeImpl.this.updateScreenParams(new ScreenParams(display));
            CardboardViewNativeImpl.this.reconnectSensors();
        }
        
        @Override
        public void onPresentationStopped() {
            if (this.originalParams != null) {
                CardboardViewNativeImpl.this.updateScreenParams(this.originalParams);
            }
        }
    }
    
    private class RendererHelper implements GLSurfaceView$Renderer
    {
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
        
        private void callOnRendererShutdown() {
            if (this.renderer == null) {
                if (this.stereoRenderer != null) {
                    this.stereoRenderer.onRendererShutdown();
                }
            }
            else {
                this.renderer.onRendererShutdown();
            }
        }
        
        private void callOnSurfaceChanged(final int n, final int n2) {
            if (this.renderer == null) {
                if (this.stereoRenderer != null) {
                    if (!this.stereoMode) {
                        this.stereoRenderer.onSurfaceChanged(n, n2);
                    }
                    else {
                        this.stereoRenderer.onSurfaceChanged(n / 2, n2);
                    }
                }
            }
            else {
                this.renderer.onSurfaceChanged(n, n2);
            }
        }
        
        private void callOnSurfaceCreated(final EGLConfig eglConfig) {
            CardboardViewNativeImpl.this.nativeOnSurfaceCreated(CardboardViewNativeImpl.this.nativeCardboardView);
            if (this.renderer == null) {
                if (this.stereoRenderer != null) {
                    this.stereoRenderer.onSurfaceCreated(eglConfig);
                }
            }
            else {
                this.renderer.onSurfaceCreated(eglConfig);
            }
        }
        
        public void getCurrentEyeParams(final HeadTransform headTransform, final Eye eye, final Eye eye2, final Eye eye3, final Eye eye4, final Eye eye5) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                @Override
                public void run() {
                    CardboardViewNativeImpl.this.nativeGetCurrentEyeParams(CardboardViewNativeImpl.this.nativeCardboardView, headTransform, eye, eye2, eye3, eye4, eye5);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            }
            catch (final InterruptedException ex) {
                final String access$3000 = CardboardViewNativeImpl.TAG;
                final String value = String.valueOf(ex.toString());
                String concat;
                if (value.length() == 0) {
                    concat = new String("Interrupted while reading frame params: ");
                }
                else {
                    concat = "Interrupted while reading frame params: ".concat(value);
                }
                Log.e(access$3000, concat);
            }
        }
        
        public void onDrawFrame(final GL10 gl10) {
            if ((this.renderer != null || this.stereoRenderer != null) && this.surfaceCreated) {
                Trace.beginSection("Render");
                CardboardViewNativeImpl.this.nativeOnDrawFrame(CardboardViewNativeImpl.this.nativeCardboardView);
                Trace.endSection();
                EGL14.eglSwapInterval(this.eglDisplay, 1);
            }
        }
        
        public void onSurfaceChanged(final GL10 gl10, final int i, final int j) {
            if ((this.renderer != null || this.stereoRenderer != null) && this.surfaceCreated) {
                if (this.stereoMode && (i != this.screenParams.getWidth() || j != this.screenParams.getHeight())) {
                    if (!this.invalidSurfaceSizeWarningShown) {
                        Log.e(CardboardViewNativeImpl.TAG, new StringBuilder(134).append("Surface size ").append(i).append("x").append(j).append(" does not match the expected screen size ").append(this.screenParams.getWidth()).append("x").append(this.screenParams.getHeight()).append(". Stereo rendering might feel off.").toString());
                    }
                    this.invalidSurfaceSizeWarningShown = true;
                }
                else {
                    this.invalidSurfaceSizeWarningShown = false;
                }
                CardboardViewNativeImpl.this.nativeOnSurfaceChanged(CardboardViewNativeImpl.this.nativeCardboardView, i, j);
                this.callOnSurfaceChanged(i, j);
            }
        }
        
        public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig) {
            if (this.renderer == null && this.stereoRenderer == null) {
                return;
            }
            this.surfaceCreated = true;
            this.eglDisplay = EGL14.eglGetCurrentDisplay();
            this.callOnSurfaceCreated(eglConfig);
        }
        
        public void setRenderer(final GvrView.Renderer renderer) {
            this.renderer = renderer;
            CardboardViewNativeImpl.this.nativeSetRenderer(CardboardViewNativeImpl.this.nativeCardboardView, renderer);
        }
        
        public void setRenderer(final GvrView.StereoRenderer stereoRenderer) {
            this.stereoRenderer = stereoRenderer;
            CardboardViewNativeImpl.this.nativeSetStereoRenderer(CardboardViewNativeImpl.this.nativeCardboardView, stereoRenderer);
        }
        
        public void setScreenParams(final ScreenParams screenParams) {
            this.screenParams = screenParams;
        }
        
        public void setStereoModeEnabled(final boolean stereoModeEnabled) {
            CardboardViewNativeImpl.this.checkNativeCardboardView();
            CardboardViewNativeImpl.this.gvrLayout.setStereoModeEnabled(stereoModeEnabled);
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (RendererHelper.this.stereoMode != stereoModeEnabled) {
                        RendererHelper.this.stereoMode = stereoModeEnabled;
                        CardboardViewNativeImpl.this.nativeSetStereoModeEnabled(CardboardViewNativeImpl.this.nativeCardboardView, stereoModeEnabled);
                        if (!EGL10.EGL_NO_SURFACE.equals(((EGL10)EGLContext.getEGL()).eglGetCurrentSurface(12377))) {
                            RendererHelper.this.onSurfaceChanged(null, RendererHelper.this.screenParams.getWidth(), RendererHelper.this.screenParams.getHeight());
                        }
                    }
                }
            });
        }
        
        public void shutdown() {
            CardboardViewNativeImpl.this.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (RendererHelper.this.renderer != null || RendererHelper.this.stereoRenderer != null) {
                        if (RendererHelper.this.surfaceCreated) {
                            RendererHelper.this.surfaceCreated = false;
                            RendererHelper.this.callOnRendererShutdown();
                        }
                    }
                    CardboardViewNativeImpl.this.shutdownLatch.countDown();
                }
            });
        }
    }
}
