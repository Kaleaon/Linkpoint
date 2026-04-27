// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vr.cardboard.UiUtils;
import android.content.Intent;
import android.app.Activity;
import com.google.vr.cardboard.ContextUtils;
import android.content.Context;
import com.google.vr.cardboard.UiLayer;
import android.widget.FrameLayout;

public class GvrUiLayout extends FrameLayout
{
    private static final float DAYDREAM_ALIGNMENT_MARKER_SCALE = 0.35f;
    private boolean daydreamModeEnabled;
    private final Runnable defaultCloseButtonRunnable;
    private final UiLayer uiLayer;
    
    GvrUiLayout(final Context context) {
        this(context, new DaydreamUtilsWrapper());
    }
    
    GvrUiLayout(final Context context, final DaydreamUtilsWrapper daydreamUtilsWrapper) {
        super(context);
        this.daydreamModeEnabled = false;
        if (ContextUtils.canGetActivity(context)) {
            this.defaultCloseButtonRunnable = createDefaultCloseButtonRunnable(context, daydreamUtilsWrapper);
            (this.uiLayer = new UiLayer(context)).setBackButtonListener(this.defaultCloseButtonRunnable);
            this.addView(this.uiLayer.getView());
            return;
        }
        throw new IllegalArgumentException("An Activity Context is required for VR functionality.");
    }
    
    private static Runnable createDefaultCloseButtonRunnable(final Context context, final DaydreamUtilsWrapper daydreamUtilsWrapper) {
        final Activity activity = ContextUtils.getActivity(context);
        if (!daydreamUtilsWrapper.isDaydreamActivity(activity)) {
            return new Runnable() {
                @Override
                public final void run() {
                    activity.onBackPressed();
                }
            };
        }
        return new Runnable() {
            @Override
            public final void run() {
                final Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(268435456);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }
    
    public static void launchOrInstallGvrApp(final Activity activity) {
        UiUtils.launchOrInstallCardboard((Context)activity);
    }
    
    public UiLayer getUiLayer() {
        return this.uiLayer;
    }
    
    void invokeCloseButtonListener() {
        final Runnable backButtonRunnable = this.uiLayer.getBackButtonRunnable();
        if (backButtonRunnable != null) {
            backButtonRunnable.run();
        }
    }
    
    boolean isDaydreamModeEnabled() {
        return this.daydreamModeEnabled;
    }
    
    public void setCloseButtonListener(final Runnable runnable) {
        final UiLayer uiLayer = this.uiLayer;
        Runnable defaultCloseButtonRunnable = runnable;
        if (runnable == null) {
            defaultCloseButtonRunnable = this.defaultCloseButtonRunnable;
        }
        uiLayer.setBackButtonListener(defaultCloseButtonRunnable);
    }
    
    void setDaydreamModeEnabled(final boolean daydreamModeEnabled) {
        if (this.daydreamModeEnabled == daydreamModeEnabled) {
            return;
        }
        if (!(this.daydreamModeEnabled = daydreamModeEnabled)) {
            this.uiLayer.setAlignmentMarkerScale(1.0f);
            return;
        }
        this.uiLayer.setAlignmentMarkerScale(0.35f);
        this.uiLayer.setTransitionViewEnabled(false);
    }
    
    public void setEnabled(final boolean enabled) {
        this.uiLayer.setEnabled(enabled);
    }
    
    public void setTransitionViewEnabled(final boolean b) {
        final boolean b2 = false;
        final UiLayer uiLayer = this.uiLayer;
        boolean transitionViewEnabled;
        if (!b) {
            transitionViewEnabled = b2;
        }
        else {
            transitionViewEnabled = b2;
            if (!this.daydreamModeEnabled) {
                transitionViewEnabled = true;
            }
        }
        uiLayer.setTransitionViewEnabled(transitionViewEnabled);
    }
    
    public void setViewerName(final String viewerName) {
        this.uiLayer.setViewerName(viewerName);
    }
}
