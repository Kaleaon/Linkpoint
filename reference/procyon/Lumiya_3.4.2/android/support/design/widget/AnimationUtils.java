// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.view.animation.DecelerateInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Interpolator;

class AnimationUtils
{
    static final Interpolator DECELERATE_INTERPOLATOR;
    static final Interpolator FAST_OUT_LINEAR_IN_INTERPOLATOR;
    static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR;
    static final Interpolator LINEAR_INTERPOLATOR;
    static final Interpolator LINEAR_OUT_SLOW_IN_INTERPOLATOR;
    
    static {
        LINEAR_INTERPOLATOR = (Interpolator)new LinearInterpolator();
        FAST_OUT_SLOW_IN_INTERPOLATOR = (Interpolator)new FastOutSlowInInterpolator();
        FAST_OUT_LINEAR_IN_INTERPOLATOR = (Interpolator)new FastOutLinearInInterpolator();
        LINEAR_OUT_SLOW_IN_INTERPOLATOR = (Interpolator)new LinearOutSlowInInterpolator();
        DECELERATE_INTERPOLATOR = (Interpolator)new DecelerateInterpolator();
    }
    
    static float lerp(final float n, final float n2, final float n3) {
        return (n2 - n) * n3 + n;
    }
    
    static int lerp(final int n, final int n2, final float n3) {
        return Math.round((n2 - n) * n3) + n;
    }
}
