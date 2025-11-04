// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview;

import android.view.View;
import android.annotation.TargetApi;
import android.os.Build$VERSION;

public class Compat
{
    private static final int SIXTY_FPS_INTERVAL = 16;
    
    public static int getPointerIndex(final int n) {
        if (Build$VERSION.SDK_INT < 11) {
            return getPointerIndexEclair(n);
        }
        return getPointerIndexHoneyComb(n);
    }
    
    @TargetApi(5)
    private static int getPointerIndexEclair(final int n) {
        return (0xFF00 & n) >> 8;
    }
    
    @TargetApi(11)
    private static int getPointerIndexHoneyComb(final int n) {
        return (0xFF00 & n) >> 8;
    }
    
    public static void postOnAnimation(final View view, final Runnable runnable) {
        if (Build$VERSION.SDK_INT < 16) {
            view.postDelayed(runnable, 16L);
        }
        else {
            postOnAnimationJellyBean(view, runnable);
        }
    }
    
    @TargetApi(16)
    private static void postOnAnimationJellyBean(final View view, final Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}
