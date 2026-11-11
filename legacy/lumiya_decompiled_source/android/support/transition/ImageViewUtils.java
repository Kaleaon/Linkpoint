package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.os.Build;
import android.widget.ImageView;
/* loaded from: classes.dex */
class ImageViewUtils {
    private static final ImageViewUtilsImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT < 21) {
            IMPL = new ImageViewUtilsApi14();
        } else {
            IMPL = new ImageViewUtilsApi21();
        }
    }

    ImageViewUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void animateTransform(ImageView imageView, Matrix matrix) {
        IMPL.animateTransform(imageView, matrix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reserveEndAnimateTransform(ImageView imageView, Animator animator) {
        IMPL.reserveEndAnimateTransform(imageView, animator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startAnimateTransform(ImageView imageView) {
        IMPL.startAnimateTransform(imageView);
    }
}
