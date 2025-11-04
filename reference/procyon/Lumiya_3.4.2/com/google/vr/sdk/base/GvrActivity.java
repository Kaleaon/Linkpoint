// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import com.google.vr.cardboard.AndroidNCompat;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper;
import com.google.vr.cardboard.FullscreenMode;
import android.app.Activity;

public class GvrActivity extends Activity
{
    private boolean androidVrModeEnabled;
    private GvrView cardboardView;
    private FullscreenMode fullscreenMode;
    private final ScreenOnFlagHelper screenOnFlagHelper;
    
    public GvrActivity() {
        this.screenOnFlagHelper = new ScreenOnFlagHelper(this);
    }
    
    private boolean shouldSuppressKey(final int n) {
        boolean b = false;
        if (!this.androidVrModeEnabled) {
            return false;
        }
        if (n == 24 || n == 25) {
            b = true;
        }
        return b;
    }
    
    public GvrView getGvrView() {
        return this.cardboardView;
    }
    
    public void onCardboardTrigger() {
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(1);
        this.fullscreenMode = new FullscreenMode(this.getWindow());
    }
    
    protected void onDestroy() {
        if (this.cardboardView != null) {
            this.cardboardView.setOnCardboardTriggerListener(null);
            this.cardboardView.shutdown();
            this.cardboardView = null;
        }
        super.onDestroy();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        boolean b = false;
        if (this.shouldSuppressKey(n) || super.onKeyDown(n, keyEvent)) {
            b = true;
        }
        return b;
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        boolean b = false;
        if (this.shouldSuppressKey(n) || super.onKeyUp(n, keyEvent)) {
            b = true;
        }
        return b;
    }
    
    protected void onPause() {
        super.onPause();
        if (this.cardboardView != null) {
            this.cardboardView.onPause();
        }
        this.screenOnFlagHelper.stop();
    }
    
    protected void onResume() {
        super.onResume();
        if (this.cardboardView != null) {
            this.cardboardView.onResume();
        }
        this.fullscreenMode.goFullscreen();
        this.screenOnFlagHelper.start();
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        this.fullscreenMode.onWindowFocusChanged(b);
    }
    
    public void setContentView(final View contentView) {
        if (contentView instanceof GvrView) {
            this.setGvrView((GvrView)contentView);
        }
        super.setContentView(contentView);
    }
    
    public void setContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (view instanceof GvrView) {
            this.setGvrView((GvrView)view);
        }
        super.setContentView(view, viewGroup$LayoutParams);
    }
    
    public void setGvrView(final GvrView gvrView) {
        this.setGvrView(gvrView, true);
    }
    
    public void setGvrView(final GvrView cardboardView, final boolean b) {
        final boolean b2 = true;
        if (this.cardboardView == cardboardView) {
            return;
        }
        if (this.cardboardView != null) {
            this.cardboardView.setOnCardboardTriggerListener(null);
        }
        this.cardboardView = cardboardView;
        final boolean b3 = cardboardView != null;
        int n;
        if (!b) {
            n = 0;
        }
        else {
            n = 1;
        }
        boolean androidVrModeEnabled = false;
        Label_0049: {
            if (AndroidNCompat.setVrModeEnabled(this, b3, n)) {
                androidVrModeEnabled = b2;
                if (b3) {
                    break Label_0049;
                }
            }
            androidVrModeEnabled = false;
        }
        this.androidVrModeEnabled = androidVrModeEnabled;
        if (cardboardView != null) {
            cardboardView.setOnCardboardTriggerListener(new Runnable() {
                @Override
                public void run() {
                    GvrActivity.this.onCardboardTrigger();
                }
            });
        }
    }
    
    public void setScreenAlwaysOn(final boolean screenAlwaysOn) {
        this.screenOnFlagHelper.setScreenAlwaysOn(screenAlwaysOn);
    }
    
    protected void updateGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        if (this.cardboardView != null) {
            this.cardboardView.updateGvrViewerParams(gvrViewerParams);
        }
    }
}
