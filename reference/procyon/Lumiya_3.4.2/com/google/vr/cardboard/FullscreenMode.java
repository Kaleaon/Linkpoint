// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.view.View$OnSystemUiVisibilityChangeListener;
import android.os.Handler;
import android.os.Build$VERSION;
import android.view.Window;

public class FullscreenMode
{
    private static final int NAVIGATION_BAR_TIMEOUT_MS = 2000;
    private final Window window;
    
    public FullscreenMode(final Window window) {
        this.window = window;
    }
    
    private void setFullscreenModeFlags() {
        this.window.getDecorView().setSystemUiVisibility(5894);
    }
    
    private void setImmersiveStickyModeCompat() {
        if (Build$VERSION.SDK_INT < 19) {
            this.window.getDecorView().setOnSystemUiVisibilityChangeListener((View$OnSystemUiVisibilityChangeListener)new View$OnSystemUiVisibilityChangeListener() {
                final /* synthetic */ Handler val$handler = new Handler();
                
                public void onSystemUiVisibilityChange(final int n) {
                    if ((n & 0x2) == 0x0) {
                        this.val$handler.postDelayed((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                FullscreenMode.this.setFullscreenModeFlags();
                            }
                        }, 2000L);
                    }
                }
            });
        }
    }
    
    public void goFullscreen() {
        this.setFullscreenModeFlags();
        this.setImmersiveStickyModeCompat();
    }
    
    public void onWindowFocusChanged(final boolean b) {
        if (b) {
            this.setFullscreenModeFlags();
        }
    }
}
