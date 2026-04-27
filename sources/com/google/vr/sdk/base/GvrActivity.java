package com.google.vr.sdk.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.vr.cardboard.AndroidNCompat;
import com.google.vr.cardboard.FullscreenMode;
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper;

public class GvrActivity extends Activity {
    private boolean androidVrModeEnabled;
    private GvrView cardboardView;
    private FullscreenMode fullscreenMode;
    private final ScreenOnFlagHelper screenOnFlagHelper = new ScreenOnFlagHelper(this);

    private boolean shouldSuppressKey(int i) {
        if (!this.androidVrModeEnabled) {
            return false;
        }
        return i == 24 || i == 25;
    }

    public GvrView getGvrView() {
        return this.cardboardView;
    }

    public void onCardboardTrigger() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.fullscreenMode = new FullscreenMode(getWindow());
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.cardboardView != null) {
            this.cardboardView.setOnCardboardTriggerListener((Runnable) null);
            this.cardboardView.shutdown();
            this.cardboardView = null;
        }
        super.onDestroy();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return shouldSuppressKey(i) || super.onKeyDown(i, keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return shouldSuppressKey(i) || super.onKeyUp(i, keyEvent);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.cardboardView != null) {
            this.cardboardView.onPause();
        }
        this.screenOnFlagHelper.stop();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.cardboardView != null) {
            this.cardboardView.onResume();
        }
        this.fullscreenMode.goFullscreen();
        this.screenOnFlagHelper.start();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.fullscreenMode.onWindowFocusChanged(z);
    }

    public void setContentView(View view) {
        if (view instanceof GvrView) {
            setGvrView((GvrView) view);
        }
        super.setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        if (view instanceof GvrView) {
            setGvrView((GvrView) view);
        }
        super.setContentView(view, layoutParams);
    }

    public void setGvrView(GvrView gvrView) {
        setGvrView(gvrView, true);
    }

    public void setGvrView(GvrView gvrView, boolean z) {
        boolean z2 = true;
        if (this.cardboardView != gvrView) {
            if (this.cardboardView != null) {
                this.cardboardView.setOnCardboardTriggerListener((Runnable) null);
            }
            this.cardboardView = gvrView;
            boolean z3 = gvrView != null;
            if (!AndroidNCompat.setVrModeEnabled(this, z3, !z ? 0 : 1) || !z3) {
                z2 = false;
            }
            this.androidVrModeEnabled = z2;
            if (gvrView != null) {
                gvrView.setOnCardboardTriggerListener(new Runnable() {
                    public void run() {
                        GvrActivity.this.onCardboardTrigger();
                    }
                });
            }
        }
    }

    public void setScreenAlwaysOn(boolean z) {
        this.screenOnFlagHelper.setScreenAlwaysOn(z);
    }

    /* access modifiers changed from: protected */
    public void updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        if (this.cardboardView != null) {
            this.cardboardView.updateGvrViewerParams(gvrViewerParams);
        }
    }
}
