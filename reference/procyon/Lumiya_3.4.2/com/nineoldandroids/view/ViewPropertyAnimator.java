// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.view;

import com.nineoldandroids.animation.Animator;
import android.view.animation.Interpolator;
import android.os.Build$VERSION;
import android.view.View;
import java.util.WeakHashMap;

public abstract class ViewPropertyAnimator
{
    private static final WeakHashMap<View, ViewPropertyAnimator> ANIMATORS;
    
    static {
        ANIMATORS = new WeakHashMap<View, ViewPropertyAnimator>(0);
    }
    
    public static ViewPropertyAnimator animate(final View view) {
        ViewPropertyAnimator value = ViewPropertyAnimator.ANIMATORS.get(view);
        if (value == null) {
            final int intValue = Integer.valueOf(Build$VERSION.SDK);
            if (intValue < 14) {
                if (intValue < 11) {
                    value = new ViewPropertyAnimatorPreHC(view);
                }
                else {
                    value = new ViewPropertyAnimatorHC(view);
                }
            }
            else {
                value = new ViewPropertyAnimatorICS(view);
            }
            ViewPropertyAnimator.ANIMATORS.put(view, value);
        }
        return value;
    }
    
    public abstract ViewPropertyAnimator alpha(final float p0);
    
    public abstract ViewPropertyAnimator alphaBy(final float p0);
    
    public abstract void cancel();
    
    public abstract long getDuration();
    
    public abstract long getStartDelay();
    
    public abstract ViewPropertyAnimator rotation(final float p0);
    
    public abstract ViewPropertyAnimator rotationBy(final float p0);
    
    public abstract ViewPropertyAnimator rotationX(final float p0);
    
    public abstract ViewPropertyAnimator rotationXBy(final float p0);
    
    public abstract ViewPropertyAnimator rotationY(final float p0);
    
    public abstract ViewPropertyAnimator rotationYBy(final float p0);
    
    public abstract ViewPropertyAnimator scaleX(final float p0);
    
    public abstract ViewPropertyAnimator scaleXBy(final float p0);
    
    public abstract ViewPropertyAnimator scaleY(final float p0);
    
    public abstract ViewPropertyAnimator scaleYBy(final float p0);
    
    public abstract ViewPropertyAnimator setDuration(final long p0);
    
    public abstract ViewPropertyAnimator setInterpolator(final Interpolator p0);
    
    public abstract ViewPropertyAnimator setListener(final Animator.AnimatorListener p0);
    
    public abstract ViewPropertyAnimator setStartDelay(final long p0);
    
    public abstract void start();
    
    public abstract ViewPropertyAnimator translationX(final float p0);
    
    public abstract ViewPropertyAnimator translationXBy(final float p0);
    
    public abstract ViewPropertyAnimator translationY(final float p0);
    
    public abstract ViewPropertyAnimator translationYBy(final float p0);
    
    public abstract ViewPropertyAnimator x(final float p0);
    
    public abstract ViewPropertyAnimator xBy(final float p0);
    
    public abstract ViewPropertyAnimator y(final float p0);
    
    public abstract ViewPropertyAnimator yBy(final float p0);
}
