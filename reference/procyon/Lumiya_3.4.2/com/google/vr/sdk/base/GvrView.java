// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import javax.microedition.khronos.egl.EGLConfig;
import com.google.vr.cardboard.UsedByNative;
import android.view.MotionEvent;
import com.google.vr.ndk.base.GvrSurfaceView;
import com.google.vr.cardboard.ContextUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class GvrView extends FrameLayout
{
    private CardboardViewApi cardboardViewApi;
    
    public GvrView(final Context context) {
        super(context);
        this.init(context);
    }
    
    public GvrView(final Context context, final AttributeSet set) {
        super(context, set);
        this.init(context);
    }
    
    private void init(final Context context) {
        if (this.isInEditMode()) {
            return;
        }
        if (ContextUtils.canGetActivity(context)) {
            this.cardboardViewApi = ImplementationSelector.createCardboardViewApi(context);
            this.addView(this.cardboardViewApi.getRootView(), 0);
            final GvrSurfaceView gvrSurfaceView = this.cardboardViewApi.getGvrSurfaceView();
            gvrSurfaceView.setEGLContextClientVersion(2);
            gvrSurfaceView.setPreserveEGLContextOnPause(true);
            return;
        }
        throw new IllegalArgumentException("An Activity Context is required for VR functionality.");
    }
    
    public void enableCardboardTriggerEmulation() {
        this.cardboardViewApi.enableCardboardTriggerEmulation();
    }
    
    public boolean getAsyncReprojectionEnabled() {
        return this.cardboardViewApi.getAsyncReprojectionEnabled();
    }
    
    public void getCurrentEyeParams(final HeadTransform headTransform, final Eye eye, final Eye eye2, final Eye eye3, final Eye eye4, final Eye eye5) {
        this.cardboardViewApi.getCurrentEyeParams(headTransform, eye, eye2, eye3, eye4, eye5);
    }
    
    public boolean getDistortionCorrectionEnabled() {
        return this.cardboardViewApi.getDistortionCorrectionEnabled();
    }
    
    public GvrViewerParams getGvrViewerParams() {
        return this.cardboardViewApi.getGvrViewerParams();
    }
    
    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.cardboardViewApi.getHeadMountedDisplay();
    }
    
    public float getInterpupillaryDistance() {
        return this.cardboardViewApi.getInterpupillaryDistance();
    }
    
    public float getNeckModelFactor() {
        return this.cardboardViewApi.getNeckModelFactor();
    }
    
    public ScreenParams getScreenParams() {
        return this.cardboardViewApi.getScreenParams();
    }
    
    public boolean getStereoModeEnabled() {
        return this.cardboardViewApi.getStereoModeEnabled();
    }
    
    public void onPause() {
        this.cardboardViewApi.onPause();
    }
    
    public void onResume() {
        this.cardboardViewApi.onResume();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.cardboardViewApi.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }
    
    public void queueEvent(final Runnable runnable) {
        this.cardboardViewApi.getGvrSurfaceView().queueEvent(runnable);
    }
    
    public void recenterHeadTracker() {
        this.cardboardViewApi.recenterHeadTracker();
    }
    
    @Deprecated
    public void resetHeadTracker() {
        this.cardboardViewApi.resetHeadTracker();
    }
    
    public boolean setAsyncReprojectionEnabled(final boolean asyncReprojectionEnabled) {
        return this.cardboardViewApi.setAsyncReprojectionEnabled(asyncReprojectionEnabled);
    }
    
    public void setDepthStencilFormat(final int depthStencilFormat) {
        this.cardboardViewApi.setDepthStencilFormat(depthStencilFormat);
    }
    
    public void setDistortionCorrectionEnabled(final boolean distortionCorrectionEnabled) {
        this.cardboardViewApi.setDistortionCorrectionEnabled(distortionCorrectionEnabled);
    }
    
    public void setDistortionCorrectionScale(final float distortionCorrectionScale) {
        this.cardboardViewApi.setDistortionCorrectionScale(distortionCorrectionScale);
    }
    
    public void setEGLConfigChooser(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.cardboardViewApi.getGvrSurfaceView().setEGLConfigChooser(n, n2, n3, n4, n5, n6);
    }
    
    public void setEGLContextClientVersion(final int eglContextClientVersion) {
        this.cardboardViewApi.getGvrSurfaceView().setEGLContextClientVersion(eglContextClientVersion);
    }
    
    public void setMultisampling(final int multisampling) {
        this.cardboardViewApi.setMultisampling(multisampling);
    }
    
    public void setNeckModelEnabled(final boolean neckModelEnabled) {
        this.cardboardViewApi.setNeckModelEnabled(neckModelEnabled);
    }
    
    public void setNeckModelFactor(final float neckModelFactor) {
        this.cardboardViewApi.setNeckModelFactor(neckModelFactor);
    }
    
    public void setOnCardboardBackListener(final Runnable onCardboardBackListener) {
        this.cardboardViewApi.setOnCardboardBackListener(onCardboardBackListener);
    }
    
    public void setOnCardboardTriggerListener(final Runnable onCardboardTriggerListener) {
        this.cardboardViewApi.setOnCardboardTriggerListener(onCardboardTriggerListener);
    }
    
    public void setOnCloseButtonListener(final Runnable onCloseButtonListener) {
        this.cardboardViewApi.setOnCloseButtonListener(onCloseButtonListener);
    }
    
    public void setOnTransitionViewDoneListener(final Runnable onTransitionViewDoneListener) {
        this.cardboardViewApi.setOnTransitionViewDoneListener(onTransitionViewDoneListener);
    }
    
    public void setRenderer(final Renderer renderer) {
        if (renderer != null) {
            this.cardboardViewApi.setRenderer(renderer);
            return;
        }
        throw new IllegalArgumentException("Renderer must not be null");
    }
    
    public void setRenderer(final StereoRenderer renderer) {
        if (renderer != null) {
            this.cardboardViewApi.setRenderer(renderer);
            return;
        }
        throw new IllegalArgumentException("StereoRenderer must not be null");
    }
    
    public void setStereoModeEnabled(final boolean stereoModeEnabled) {
        this.cardboardViewApi.setStereoModeEnabled(stereoModeEnabled);
    }
    
    public void setTransitionViewEnabled(final boolean transitionViewEnabled) {
        this.cardboardViewApi.setTransitionViewEnabled(transitionViewEnabled);
    }
    
    public void shutdown() {
        this.cardboardViewApi.shutdown();
    }
    
    @Deprecated
    public void undistortTexture(final int n) {
        this.cardboardViewApi.undistortTexture(n);
    }
    
    public void updateGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        this.cardboardViewApi.updateGvrViewerParams(gvrViewerParams);
    }
    
    public void updateScreenParams(final ScreenParams screenParams) {
        this.cardboardViewApi.updateScreenParams(screenParams);
    }
    
    public interface Renderer
    {
        @UsedByNative
        void onDrawFrame(final HeadTransform p0, final Eye p1, final Eye p2);
        
        @UsedByNative
        void onFinishFrame(final Viewport p0);
        
        void onRendererShutdown();
        
        void onSurfaceChanged(final int p0, final int p1);
        
        void onSurfaceCreated(final EGLConfig p0);
    }
    
    public interface StereoRenderer
    {
        @UsedByNative
        void onDrawEye(final Eye p0);
        
        @UsedByNative
        void onFinishFrame(final Viewport p0);
        
        @UsedByNative
        void onNewFrame(final HeadTransform p0);
        
        void onRendererShutdown();
        
        void onSurfaceChanged(final int p0, final int p1);
        
        void onSurfaceCreated(final EGLConfig p0);
    }
}
