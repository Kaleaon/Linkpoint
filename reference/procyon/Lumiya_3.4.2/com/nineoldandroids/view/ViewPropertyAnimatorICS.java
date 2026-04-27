// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.view;

import android.animation.Animator$AnimatorListener;
import com.nineoldandroids.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.animation.Interpolator;
import android.view.View;
import java.lang.ref.WeakReference;

class ViewPropertyAnimatorICS extends ViewPropertyAnimator
{
    private static final long RETURN_WHEN_NULL = -1L;
    private final WeakReference<android.view.ViewPropertyAnimator> mNative;
    
    ViewPropertyAnimatorICS(final View view) {
        this.mNative = new WeakReference<android.view.ViewPropertyAnimator>(view.animate());
    }
    
    @Override
    public ViewPropertyAnimator alpha(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.alpha(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator alphaBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.alphaBy(n);
        }
        return this;
    }
    
    @Override
    public void cancel() {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
    }
    
    @Override
    public long getDuration() {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator == null) {
            return -1L;
        }
        return viewPropertyAnimator.getDuration();
    }
    
    @Override
    public long getStartDelay() {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator == null) {
            return -1L;
        }
        return viewPropertyAnimator.getStartDelay();
    }
    
    @Override
    public ViewPropertyAnimator rotation(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotation(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotationBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationX(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotationX(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationXBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotationXBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationY(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotationY(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationYBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.rotationYBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleX(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.scaleX(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleXBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.scaleXBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleY(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.scaleY(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleYBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.scaleYBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setDuration(final long duration) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.setDuration(duration);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setInterpolator(final Interpolator interpolator) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.setInterpolator((TimeInterpolator)interpolator);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setListener(final Animator.AnimatorListener animatorListener) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            if (animatorListener != null) {
                viewPropertyAnimator.setListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
                    public void onAnimationCancel(final android.animation.Animator animator) {
                        animatorListener.onAnimationCancel(null);
                    }
                    
                    public void onAnimationEnd(final android.animation.Animator animator) {
                        animatorListener.onAnimationEnd(null);
                    }
                    
                    public void onAnimationRepeat(final android.animation.Animator animator) {
                        animatorListener.onAnimationRepeat(null);
                    }
                    
                    public void onAnimationStart(final android.animation.Animator animator) {
                        animatorListener.onAnimationStart(null);
                    }
                });
            }
            else {
                viewPropertyAnimator.setListener((Animator$AnimatorListener)null);
            }
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setStartDelay(final long startDelay) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.setStartDelay(startDelay);
        }
        return this;
    }
    
    @Override
    public void start() {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.start();
        }
    }
    
    @Override
    public ViewPropertyAnimator translationX(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.translationX(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationXBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.translationXBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationY(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.translationY(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationYBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.translationYBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator x(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.x(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator xBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.xBy(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator y(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.y(n);
        }
        return this;
    }
    
    @Override
    public ViewPropertyAnimator yBy(final float n) {
        final android.view.ViewPropertyAnimator viewPropertyAnimator = this.mNative.get();
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.yBy(n);
        }
        return this;
    }
}
