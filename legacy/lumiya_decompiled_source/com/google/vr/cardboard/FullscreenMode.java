package com.google.vr.cardboard;

import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
/* loaded from: classes.dex */
public class FullscreenMode {
    private static final int NAVIGATION_BAR_TIMEOUT_MS = 2000;
    private final Window window;

    public FullscreenMode(Window window) {
        this.window = window;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFullscreenModeFlags() {
        this.window.getDecorView().setSystemUiVisibility(5894);
    }

    private void setImmersiveStickyModeCompat() {
        if (Build.VERSION.SDK_INT >= 19) {
            return;
        }
        final Handler handler = new Handler();
        this.window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.google.vr.cardboard.FullscreenMode.1
            @Override // android.view.View.OnSystemUiVisibilityChangeListener
            public void onSystemUiVisibilityChange(int i) {
                if ((i & 2) != 0) {
                    return;
                }
                handler.postDelayed(new Runnable() { // from class: com.google.vr.cardboard.FullscreenMode.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        FullscreenMode.this.setFullscreenModeFlags();
                    }
                }, 2000L);
            }
        });
    }

    public void goFullscreen() {
        setFullscreenModeFlags();
        setImmersiveStickyModeCompat();
    }

    public void onWindowFocusChanged(boolean z) {
        if (z) {
            setFullscreenModeFlags();
        }
    }
}
