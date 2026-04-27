// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.view.animation.AnimationUtils;
import android.util.Log;
import android.os.Message;
import android.os.Looper;
import android.content.Context;
import android.os.Handler;
import android.view.View;

class FadeOverlayView extends View
{
    static final long AUTO_FADE_DURATION_MILLIS = 350L;
    static final long AUTO_FADE_START_DELAY_MILLIS = 1000L;
    private static final int BACKGROUND_COLOR = -16777216;
    private static final boolean DEBUG = true;
    private static final int MSG_AUTO_FADE = 77337733;
    private static final String TAG = "FadeOverlayView";
    private final Handler autoFadeHandler;
    private long fadeDurationMillis;
    private long fadeStartTimeMillis;
    private int fadeType;
    private final Runnable fadeUpdateRunnable;
    private boolean visible;
    
    public FadeOverlayView(final Context context) {
        super(context);
        this.fadeType = 0;
        this.fadeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                FadeOverlayView.this.updateFade();
            }
        };
        this.autoFadeHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                if (message.what != 77337733) {
                    super.handleMessage(message);
                    return;
                }
                FadeOverlayView.this.startFade(1, 350L);
            }
        };
        this.setBackgroundColor(-16777216);
    }
    
    private void endFade() {
        if (this.fadeType != 0) {
            int visibility;
            if (this.fadeType != 2) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            this.setVisibility(visibility);
            float alpha;
            if (this.fadeType != 2) {
                alpha = 0.0f;
            }
            else {
                alpha = 1.0f;
            }
            this.setAlpha(alpha);
            this.removeCallbacks(this.fadeUpdateRunnable);
            this.fadeType = 0;
            Log.d("FadeOverlayView", ".endFade");
        }
    }
    
    private void removeFadeCallbacks() {
        this.autoFadeHandler.removeMessages(77337733);
        this.removeCallbacks(this.fadeUpdateRunnable);
    }
    
    private void updateFade() {
        final long n = AnimationUtils.currentAnimationTimeMillis() - this.fadeStartTimeMillis;
        float a = n / (float)this.fadeDurationMillis;
        if (this.fadeType != 2) {
            a = 1.0f - a;
        }
        this.setAlpha(Math.min(Math.max(a, 0.0f), 1.0f));
        boolean b;
        if (n >= this.fadeDurationMillis) {
            b = true;
        }
        else {
            b = false;
        }
        if (!b && this.getVisibility() != 0) {
            this.setVisibility(0);
        }
        int n2;
        if (n >= this.fadeDurationMillis) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            this.postOnAnimation(this.fadeUpdateRunnable);
            return;
        }
        this.endFade();
    }
    
    public void flushAutoFade(final long n) {
        Log.d("FadeOverlayView", ".flushAutoFade");
        if (this.autoFadeHandler.hasMessages(77337733)) {
            this.autoFadeHandler.removeMessages(77337733);
            this.autoFadeHandler.sendEmptyMessageDelayed(77337733, n);
        }
    }
    
    int getFadeType() {
        return this.fadeType;
    }
    
    boolean isVisible() {
        return this.visible;
    }
    
    public void onInvisible() {
        if (this.visible) {
            this.visible = false;
            if (this.isEnabled()) {
                this.removeFadeCallbacks();
                this.fadeType = 2;
                this.endFade();
            }
        }
    }
    
    public void onVisible() {
        if (this.visible && this.getAlpha() == 0.0f) {
            return;
        }
        this.visible = true;
        if (this.isEnabled()) {
            this.autoFadeHandler.removeMessages(77337733);
            this.autoFadeHandler.sendEmptyMessageDelayed(77337733, 1000L);
        }
    }
    
    public void setEnabled(final boolean enabled) {
        if (this.isEnabled() != enabled) {
            super.setEnabled(enabled);
            if (!enabled) {
                this.removeFadeCallbacks();
                this.fadeType = 1;
                this.endFade();
            }
        }
    }
    
    public void startFade(final int n, final long fadeDurationMillis) {
        Log.d("FadeOverlayView", new StringBuilder(23).append(".startFade: ").append(n).toString());
        if (!this.isEnabled()) {
            Log.w("FadeOverlayView", "Ignoring fade request while disabled.");
            return;
        }
        if (this.visible) {
            this.removeFadeCallbacks();
            this.fadeType = n;
            this.fadeDurationMillis = fadeDurationMillis;
            this.fadeStartTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.updateFade();
            return;
        }
        Log.w("FadeOverlayView", "Ignoring fade request while invisible.");
    }
}
