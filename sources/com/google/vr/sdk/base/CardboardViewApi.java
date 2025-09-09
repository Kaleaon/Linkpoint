package com.google.vr.sdk.base;

import android.view.MotionEvent;
import android.view.View;
import com.google.vr.ndk.base.GvrSurfaceView;
import com.google.vr.sdk.base.GvrView;

public interface CardboardViewApi {
    void enableCardboardTriggerEmulation();

    boolean getAsyncReprojectionEnabled();

    void getCurrentEyeParams(HeadTransform headTransform, Eye eye, Eye eye2, Eye eye3, Eye eye4, Eye eye5);

    boolean getDistortionCorrectionEnabled();

    GvrSurfaceView getGvrSurfaceView();

    GvrViewerParams getGvrViewerParams();

    HeadMountedDisplay getHeadMountedDisplay();

    float getInterpupillaryDistance();

    float getNeckModelFactor();

    View getRootView();

    ScreenParams getScreenParams();

    boolean getStereoModeEnabled();

    void onPause();

    void onResume();

    boolean onTouchEvent(MotionEvent motionEvent);

    void recenterHeadTracker();

    void resetHeadTracker();

    boolean setAsyncReprojectionEnabled(boolean z);

    void setDepthStencilFormat(int i);

    void setDistortionCorrectionEnabled(boolean z);

    void setDistortionCorrectionScale(float f);

    void setMultisampling(int i);

    void setNeckModelEnabled(boolean z);

    void setNeckModelFactor(float f);

    void setOnCardboardBackListener(Runnable runnable);

    void setOnCardboardTriggerListener(Runnable runnable);

    void setOnCloseButtonListener(Runnable runnable);

    void setOnTransitionViewDoneListener(Runnable runnable);

    void setRenderer(GvrView.Renderer renderer);

    void setRenderer(GvrView.StereoRenderer stereoRenderer);

    void setStereoModeEnabled(boolean z);

    void setTransitionViewEnabled(boolean z);

    void shutdown();

    void undistortTexture(int i);

    void updateGvrViewerParams(GvrViewerParams gvrViewerParams);

    void updateScreenParams(ScreenParams screenParams);
}
