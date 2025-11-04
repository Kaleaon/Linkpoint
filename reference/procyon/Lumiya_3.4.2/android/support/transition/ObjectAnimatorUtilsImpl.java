// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;

interface ObjectAnimatorUtilsImpl
{
     <T> ObjectAnimator ofPointF(final T p0, final Property<T, PointF> p1, final Path p2);
}
