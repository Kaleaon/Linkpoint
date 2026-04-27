package uk.co.senab.photoview;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

public class Compat {
    private static final int SIXTY_FPS_INTERVAL = 16;

    public static int getPointerIndex(int i) {
        return Build.VERSION.SDK_INT < 11 ? getPointerIndexEclair(i) : getPointerIndexHoneyComb(i);
    }

    @TargetApi(5)
    private static int getPointerIndexEclair(int i) {
        return (65280 & i) >> 8;
    }

    @TargetApi(11)
    private static int getPointerIndexHoneyComb(int i) {
        return (65280 & i) >> 8;
    }

    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT < 16) {
            view.postDelayed(runnable, 16);
        } else {
            postOnAnimationJellyBean(view, runnable);
        }
    }

    @TargetApi(16)
    private static void postOnAnimationJellyBean(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}
