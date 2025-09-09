package com.google.vr.ndk.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.cardboard.UiLayer;
import com.google.vr.cardboard.UiUtils;

public class GvrUiLayout extends FrameLayout {
    private static final float DAYDREAM_ALIGNMENT_MARKER_SCALE = 0.35f;
    private boolean daydreamModeEnabled;
    private final Runnable defaultCloseButtonRunnable;
    private final UiLayer uiLayer;

    GvrUiLayout(Context context) {
        this(context, new DaydreamUtilsWrapper());
    }

    GvrUiLayout(Context context, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        super(context);
        this.daydreamModeEnabled = false;
        if (ContextUtils.canGetActivity(context)) {
            this.defaultCloseButtonRunnable = createDefaultCloseButtonRunnable(context, daydreamUtilsWrapper);
            this.uiLayer = new UiLayer(context);
            this.uiLayer.setBackButtonListener(this.defaultCloseButtonRunnable);
            addView(this.uiLayer.getView());
            return;
        }
        throw new IllegalArgumentException("An Activity Context is required for VR functionality.");
    }

    private static Runnable createDefaultCloseButtonRunnable(Context context, DaydreamUtilsWrapper daydreamUtilsWrapper) {
        final Activity activity = ContextUtils.getActivity(context);
        return !daydreamUtilsWrapper.isDaydreamActivity(activity) ? new Runnable() {
            public final void run() {
                activity.onBackPressed();
            }
        } : new Runnable() {
            public final void run() {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(268435456);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }

    public static void launchOrInstallGvrApp(Activity activity) {
        UiUtils.launchOrInstallCardboard(activity);
    }

    public UiLayer getUiLayer() {
        return this.uiLayer;
    }

    /* access modifiers changed from: package-private */
    public void invokeCloseButtonListener() {
        Runnable backButtonRunnable = this.uiLayer.getBackButtonRunnable();
        if (backButtonRunnable != null) {
            backButtonRunnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDaydreamModeEnabled() {
        return this.daydreamModeEnabled;
    }

    public void setCloseButtonListener(Runnable runnable) {
        UiLayer uiLayer2 = this.uiLayer;
        if (runnable == null) {
            runnable = this.defaultCloseButtonRunnable;
        }
        uiLayer2.setBackButtonListener(runnable);
    }

    /* access modifiers changed from: package-private */
    public void setDaydreamModeEnabled(boolean z) {
        if (this.daydreamModeEnabled != z) {
            this.daydreamModeEnabled = z;
            if (!z) {
                this.uiLayer.setAlignmentMarkerScale(1.0f);
                return;
            }
            this.uiLayer.setAlignmentMarkerScale(DAYDREAM_ALIGNMENT_MARKER_SCALE);
            this.uiLayer.setTransitionViewEnabled(false);
        }
    }

    public void setEnabled(boolean z) {
        this.uiLayer.setEnabled(z);
    }

    public void setTransitionViewEnabled(boolean z) {
        boolean z2 = false;
        UiLayer uiLayer2 = this.uiLayer;
        if (z && !this.daydreamModeEnabled) {
            z2 = true;
        }
        uiLayer2.setTransitionViewEnabled(z2);
    }

    public void setViewerName(String str) {
        this.uiLayer.setViewerName(str);
    }
}
