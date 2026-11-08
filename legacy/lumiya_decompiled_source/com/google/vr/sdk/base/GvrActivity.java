package com.google.vr.sdk.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.vr.cardboard.AndroidNCompat;
import com.google.vr.cardboard.FullscreenMode;
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper;
/* loaded from: classes.dex */
public class GvrActivity extends Activity {
    private boolean androidVrModeEnabled;
    private GvrView cardboardView;
    private FullscreenMode fullscreenMode;
    private final ScreenOnFlagHelper screenOnFlagHelper = new ScreenOnFlagHelper(this);

    private boolean shouldSuppressKey(int i) {
        if (this.androidVrModeEnabled) {
            return i == 24 || i == 25;
        }
        return false;
    }

    public GvrView getGvrView() {
        return this.cardboardView;
    }

    public void onCardboardTrigger() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.fullscreenMode = new FullscreenMode(getWindow());
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.cardboardView != null) {
            this.cardboardView.setOnCardboardTriggerListener(null);
            this.cardboardView.shutdown();
            this.cardboardView = null;
        }
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return shouldSuppressKey(i) || super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return shouldSuppressKey(i) || super.onKeyUp(i, keyEvent);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.cardboardView != null) {
            this.cardboardView.onPause();
        }
        this.screenOnFlagHelper.stop();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.cardboardView != null) {
            this.cardboardView.onResume();
        }
        this.fullscreenMode.goFullscreen();
        this.screenOnFlagHelper.start();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.fullscreenMode.onWindowFocusChanged(z);
    }

    @Override // android.app.Activity
    public void setContentView(View view) {
        if (view instanceof GvrView) {
            setGvrView((GvrView) view);
        }
        super.setContentView(view);
    }

    @Override // android.app.Activity
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
        if (this.cardboardView != gvrView) {
            if (this.cardboardView != null) {
                this.cardboardView.setOnCardboardTriggerListener(null);
            }
            this.cardboardView = gvrView;
            boolean z2 = gvrView != null;
            this.androidVrModeEnabled = AndroidNCompat.setVrModeEnabled(this, z2, !z ? 0 : 1) && z2;
            if (gvrView != null) {
                gvrView.setOnCardboardTriggerListener(new Runnable() { // from class: com.google.vr.sdk.base.GvrActivity.1
                    @Override // java.lang.Runnable
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

    protected void updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        if (this.cardboardView == null) {
            return;
        }
        this.cardboardView.updateGvrViewerParams(gvrViewerParams);
    }
}
