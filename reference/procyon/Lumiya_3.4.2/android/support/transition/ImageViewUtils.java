// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.os.Build$VERSION;

class ImageViewUtils
{
    private static final ImageViewUtilsImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 21) {
            IMPL = new ImageViewUtilsApi14();
        }
        else {
            IMPL = new ImageViewUtilsApi21();
        }
    }
    
    static void animateTransform(final ImageView imageView, final Matrix matrix) {
        ImageViewUtils.IMPL.animateTransform(imageView, matrix);
    }
    
    static void reserveEndAnimateTransform(final ImageView imageView, final Animator animator) {
        ImageViewUtils.IMPL.reserveEndAnimateTransform(imageView, animator);
    }
    
    static void startAnimateTransform(final ImageView imageView) {
        ImageViewUtils.IMPL.startAnimateTransform(imageView);
    }
}
