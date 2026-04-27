// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import android.os.Build$VERSION;

class ObjectAnimatorUtils
{
    private static final ObjectAnimatorUtilsImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 21) {
            IMPL = new ObjectAnimatorUtilsApi14();
        }
        else {
            IMPL = new ObjectAnimatorUtilsApi21();
        }
    }
    
    static <T> ObjectAnimator ofPointF(final T t, final Property<T, PointF> property, final Path path) {
        return ObjectAnimatorUtils.IMPL.ofPointF(t, property, path);
    }
}
