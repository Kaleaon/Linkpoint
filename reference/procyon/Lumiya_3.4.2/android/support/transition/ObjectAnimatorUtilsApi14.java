// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ObjectAnimatorUtilsApi14 implements ObjectAnimatorUtilsImpl
{
    @Override
    public <T> ObjectAnimator ofPointF(final T t, final Property<T, PointF> property, final Path path) {
        return ObjectAnimator.ofFloat((Object)t, (Property)new PathProperty<Object>(property, path), new float[] { 0.0f, 1.0f });
    }
}
