package com.google.vr.sdk.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.cardboard.UsedByNative;
import com.google.vr.ndk.base.GvrSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;

public class GvrView extends FrameLayout {
    private CardboardViewApi cardboardViewApi;

    public interface Renderer {
        @UsedByNative
        void onDrawFrame(HeadTransform headTransform, Eye eye, Eye eye2);

        @UsedByNative
        void onFinishFrame(Viewport viewport);

        void onRendererShutdown();

        void onSurfaceChanged(int i, int i2);

        void onSurfaceCreated(EGLConfig eGLConfig);
    }

    public interface StereoRenderer {
        @UsedByNative
        void onDrawEye(Eye eye);

        @UsedByNative
        void onFinishFrame(Viewport viewport);

        @UsedByNative
        void onNewFrame(HeadTransform headTransform);

        void onRendererShutdown();

        void onSurfaceChanged(int i, int i2);

        void onSurfaceCreated(EGLConfig eGLConfig);
    }

    public GvrView(Context context) {
        super(context);
        init(context);
    }

    public GvrView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        if (ContextUtils.canGetActivity(context)) {
            this.cardboardViewApi = ImplementationSelector.createCardboardViewApi(context);
            addView(this.cardboardViewApi.getRootView(), 0);
            GvrSurfaceView gvrSurfaceView = this.cardboardViewApi.getGvrSurfaceView();
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

    public void getCurrentEyeParams(HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5) {
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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.cardboardViewApi.onTouchEvent(motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    public void queueEvent(Runnable runnable) {
        this.cardboardViewApi.getGvrSurfaceView().queueEvent(runnable);
    }

    public void recenterHeadTracker() {
        this.cardboardViewApi.recenterHeadTracker();
    }

    @Deprecated
    public void resetHeadTracker() {
        this.cardboardViewApi.resetHeadTracker();
    }

    public boolean setAsyncReprojectionEnabled(boolean z) {
        return this.cardboardViewApi.setAsyncReprojectionEnabled(z);
    }

    public void setDepthStencilFormat(int i) {
        this.cardboardViewApi.setDepthStencilFormat(i);
    }

    public void setDistortionCorrectionEnabled(boolean z) {
        this.cardboardViewApi.setDistortionCorrectionEnabled(z);
    }

    public void setDistortionCorrectionScale(float f) {
        this.cardboardViewApi.setDistortionCorrectionScale(f);
    }

    public void setEGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
        this.cardboardViewApi.getGvrSurfaceView().setEGLConfigChooser(i, i2, i3, i4, i5, i6);
    }

    public void setEGLContextClientVersion(int i) {
        this.cardboardViewApi.getGvrSurfaceView().setEGLContextClientVersion(i);
    }

    public void setMultisampling(int i) {
        this.cardboardViewApi.setMultisampling(i);
    }

    public void setNeckModelEnabled(boolean z) {
        this.cardboardViewApi.setNeckModelEnabled(z);
    }

    public void setNeckModelFactor(float f) {
        this.cardboardViewApi.setNeckModelFactor(f);
    }

    public void setOnCardboardBackListener(Runnable runnable) {
        this.cardboardViewApi.setOnCardboardBackListener(runnable);
    }

    public void setOnCardboardTriggerListener(Runnable runnable) {
        this.cardboardViewApi.setOnCardboardTriggerListener(runnable);
    }

    public void setOnCloseButtonListener(Runnable runnable) {
        this.cardboardViewApi.setOnCloseButtonListener(runnable);
    }

    public void setOnTransitionViewDoneListener(Runnable runnable) {
        this.cardboardViewApi.setOnTransitionViewDoneListener(runnable);
    }

    public void setRenderer(Renderer renderer) {
        if (renderer != null) {
            this.cardboardViewApi.setRenderer(renderer);
            return;
        }
        throw new IllegalArgumentException("Renderer must not be null");
    }

    public void setRenderer(StereoRenderer stereoRenderer) {
        if (stereoRenderer != null) {
            this.cardboardViewApi.setRenderer(stereoRenderer);
            return;
        }
        throw new IllegalArgumentException("StereoRenderer must not be null");
    }

    public void setStereoModeEnabled(boolean z) {
        this.cardboardViewApi.setStereoModeEnabled(z);
    }

    public void setTransitionViewEnabled(boolean z) {
        this.cardboardViewApi.setTransitionViewEnabled(z);
    }

    public void shutdown() {
        this.cardboardViewApi.shutdown();
    }

    @Deprecated
    public void undistortTexture(int i) {
        this.cardboardViewApi.undistortTexture(i);
    }

    public void updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        this.cardboardViewApi.updateGvrViewerParams(gvrViewerParams);
    }

    public void updateScreenParams(ScreenParams screenParams) {
        this.cardboardViewApi.updateScreenParams(screenParams);
    }
}
