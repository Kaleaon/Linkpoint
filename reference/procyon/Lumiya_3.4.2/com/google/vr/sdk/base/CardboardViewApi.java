// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.view.MotionEvent;
import android.view.View;
import com.google.vr.ndk.base.GvrSurfaceView;

public interface CardboardViewApi
{
    void enableCardboardTriggerEmulation();
    
    boolean getAsyncReprojectionEnabled();
    
    void getCurrentEyeParams(final HeadTransform p0, final Eye p1, final Eye p2, final Eye p3, final Eye p4, final Eye p5);
    
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
    
    boolean onTouchEvent(final MotionEvent p0);
    
    void recenterHeadTracker();
    
    void resetHeadTracker();
    
    boolean setAsyncReprojectionEnabled(final boolean p0);
    
    void setDepthStencilFormat(final int p0);
    
    void setDistortionCorrectionEnabled(final boolean p0);
    
    void setDistortionCorrectionScale(final float p0);
    
    void setMultisampling(final int p0);
    
    void setNeckModelEnabled(final boolean p0);
    
    void setNeckModelFactor(final float p0);
    
    void setOnCardboardBackListener(final Runnable p0);
    
    void setOnCardboardTriggerListener(final Runnable p0);
    
    void setOnCloseButtonListener(final Runnable p0);
    
    void setOnTransitionViewDoneListener(final Runnable p0);
    
    void setRenderer(final GvrView.Renderer p0);
    
    void setRenderer(final GvrView.StereoRenderer p0);
    
    void setStereoModeEnabled(final boolean p0);
    
    void setTransitionViewEnabled(final boolean p0);
    
    void shutdown();
    
    void undistortTexture(final int p0);
    
    void updateGvrViewerParams(final GvrViewerParams p0);
    
    void updateScreenParams(final ScreenParams p0);
}
