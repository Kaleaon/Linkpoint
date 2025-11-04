// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.view;

import com.nineoldandroids.animation.ValueAnimator;
import java.util.Iterator;
import android.view.View;
import java.lang.ref.WeakReference;
import com.nineoldandroids.view.animation.AnimatorProxy;
import java.util.ArrayList;
import android.view.animation.Interpolator;
import com.nineoldandroids.animation.Animator;
import java.util.HashMap;

class ViewPropertyAnimatorPreHC extends ViewPropertyAnimator
{
    private static final int ALPHA = 512;
    private static final int NONE = 0;
    private static final int ROTATION = 16;
    private static final int ROTATION_X = 32;
    private static final int ROTATION_Y = 64;
    private static final int SCALE_X = 4;
    private static final int SCALE_Y = 8;
    private static final int TRANSFORM_MASK = 511;
    private static final int TRANSLATION_X = 1;
    private static final int TRANSLATION_Y = 2;
    private static final int X = 128;
    private static final int Y = 256;
    private Runnable mAnimationStarter;
    private AnimatorEventListener mAnimatorEventListener;
    private HashMap<Animator, PropertyBundle> mAnimatorMap;
    private long mDuration;
    private boolean mDurationSet;
    private Interpolator mInterpolator;
    private boolean mInterpolatorSet;
    private Animator.AnimatorListener mListener;
    ArrayList<NameValuesHolder> mPendingAnimations;
    private final AnimatorProxy mProxy;
    private long mStartDelay;
    private boolean mStartDelaySet;
    private final WeakReference<View> mView;
    
    ViewPropertyAnimatorPreHC(final View referent) {
        this.mDurationSet = false;
        this.mStartDelay = 0L;
        this.mStartDelaySet = false;
        this.mInterpolatorSet = false;
        this.mListener = null;
        this.mAnimatorEventListener = new AnimatorEventListener();
        this.mPendingAnimations = new ArrayList<NameValuesHolder>();
        this.mAnimationStarter = new Runnable() {
            @Override
            public void run() {
                ViewPropertyAnimatorPreHC.this.startAnimation();
            }
        };
        this.mAnimatorMap = new HashMap<Animator, PropertyBundle>();
        this.mView = new WeakReference<View>(referent);
        this.mProxy = AnimatorProxy.wrap(referent);
    }
    
    private void animateProperty(final int n, final float n2) {
        final float value = this.getValue(n);
        this.animatePropertyBy(n, value, n2 - value);
    }
    
    private void animatePropertyBy(final int n, final float n2) {
        this.animatePropertyBy(n, this.getValue(n), n2);
    }
    
    private void animatePropertyBy(final int n, final float n2, final float n3) {
        Label_0010: {
            if (this.mAnimatorMap.size() > 0) {
                while (true) {
                    for (final Object key : this.mAnimatorMap.keySet()) {
                        final PropertyBundle propertyBundle = this.mAnimatorMap.get(key);
                        if (propertyBundle.cancel(n)) {
                            if (propertyBundle.mPropertyMask != 0) {
                                continue;
                            }
                            if (key != null) {
                                ((Animator)key).cancel();
                            }
                            break Label_0010;
                        }
                    }
                    Object key = null;
                    continue;
                }
            }
        }
        this.mPendingAnimations.add(new NameValuesHolder(n, n2, n3));
        final View view = this.mView.get();
        if (view != null) {
            view.removeCallbacks(this.mAnimationStarter);
            view.post(this.mAnimationStarter);
        }
    }
    
    private float getValue(final int n) {
        switch (n) {
            default: {
                return 0.0f;
            }
            case 1: {
                return this.mProxy.getTranslationX();
            }
            case 2: {
                return this.mProxy.getTranslationY();
            }
            case 16: {
                return this.mProxy.getRotation();
            }
            case 32: {
                return this.mProxy.getRotationX();
            }
            case 64: {
                return this.mProxy.getRotationY();
            }
            case 4: {
                return this.mProxy.getScaleX();
            }
            case 8: {
                return this.mProxy.getScaleY();
            }
            case 128: {
                return this.mProxy.getX();
            }
            case 256: {
                return this.mProxy.getY();
            }
            case 512: {
                return this.mProxy.getAlpha();
            }
        }
    }
    
    private void setValue(final int n, final float n2) {
        switch (n) {
            case 1: {
                this.mProxy.setTranslationX(n2);
                break;
            }
            case 2: {
                this.mProxy.setTranslationY(n2);
                break;
            }
            case 16: {
                this.mProxy.setRotation(n2);
                break;
            }
            case 32: {
                this.mProxy.setRotationX(n2);
                break;
            }
            case 64: {
                this.mProxy.setRotationY(n2);
                break;
            }
            case 4: {
                this.mProxy.setScaleX(n2);
                break;
            }
            case 8: {
                this.mProxy.setScaleY(n2);
                break;
            }
            case 128: {
                this.mProxy.setX(n2);
                break;
            }
            case 256: {
                this.mProxy.setY(n2);
                break;
            }
            case 512: {
                this.mProxy.setAlpha(n2);
                break;
            }
        }
    }
    
    private void startAnimation() {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f);
        final ArrayList list = (ArrayList)this.mPendingAnimations.clone();
        this.mPendingAnimations.clear();
        final int size = list.size();
        int i = 0;
        int n = 0;
        while (i < size) {
            n |= list.get(i).mNameConstant;
            ++i;
        }
        this.mAnimatorMap.put(ofFloat, new PropertyBundle(n, list));
        ofFloat.addUpdateListener((ValueAnimator.AnimatorUpdateListener)this.mAnimatorEventListener);
        ofFloat.addListener((Animator.AnimatorListener)this.mAnimatorEventListener);
        if (this.mStartDelaySet) {
            ofFloat.setStartDelay(this.mStartDelay);
        }
        if (this.mDurationSet) {
            ofFloat.setDuration(this.mDuration);
        }
        if (this.mInterpolatorSet) {
            ofFloat.setInterpolator(this.mInterpolator);
        }
        ofFloat.start();
    }
    
    @Override
    public ViewPropertyAnimator alpha(final float n) {
        this.animateProperty(512, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator alphaBy(final float n) {
        this.animatePropertyBy(512, n);
        return this;
    }
    
    @Override
    public void cancel() {
        if (this.mAnimatorMap.size() > 0) {
            final Iterator iterator = ((HashMap)this.mAnimatorMap.clone()).keySet().iterator();
            while (iterator.hasNext()) {
                ((Animator)iterator.next()).cancel();
            }
        }
        this.mPendingAnimations.clear();
        final View view = this.mView.get();
        if (view != null) {
            view.removeCallbacks(this.mAnimationStarter);
        }
    }
    
    @Override
    public long getDuration() {
        if (!this.mDurationSet) {
            return new ValueAnimator().getDuration();
        }
        return this.mDuration;
    }
    
    @Override
    public long getStartDelay() {
        if (!this.mStartDelaySet) {
            return 0L;
        }
        return this.mStartDelay;
    }
    
    @Override
    public ViewPropertyAnimator rotation(final float n) {
        this.animateProperty(16, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationBy(final float n) {
        this.animatePropertyBy(16, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationX(final float n) {
        this.animateProperty(32, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationXBy(final float n) {
        this.animatePropertyBy(32, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationY(final float n) {
        this.animateProperty(64, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator rotationYBy(final float n) {
        this.animatePropertyBy(64, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleX(final float n) {
        this.animateProperty(4, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleXBy(final float n) {
        this.animatePropertyBy(4, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleY(final float n) {
        this.animateProperty(8, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator scaleYBy(final float n) {
        this.animatePropertyBy(8, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setDuration(final long n) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + n);
        }
        this.mDurationSet = true;
        this.mDuration = n;
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setInterpolator(final Interpolator mInterpolator) {
        this.mInterpolatorSet = true;
        this.mInterpolator = mInterpolator;
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setListener(final Animator.AnimatorListener mListener) {
        this.mListener = mListener;
        return this;
    }
    
    @Override
    public ViewPropertyAnimator setStartDelay(final long n) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + n);
        }
        this.mStartDelaySet = true;
        this.mStartDelay = n;
        return this;
    }
    
    @Override
    public void start() {
        this.startAnimation();
    }
    
    @Override
    public ViewPropertyAnimator translationX(final float n) {
        this.animateProperty(1, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationXBy(final float n) {
        this.animatePropertyBy(1, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationY(final float n) {
        this.animateProperty(2, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator translationYBy(final float n) {
        this.animatePropertyBy(2, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator x(final float n) {
        this.animateProperty(128, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator xBy(final float n) {
        this.animatePropertyBy(128, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator y(final float n) {
        this.animateProperty(256, n);
        return this;
    }
    
    @Override
    public ViewPropertyAnimator yBy(final float n) {
        this.animatePropertyBy(256, n);
        return this;
    }
    
    private class AnimatorEventListener implements AnimatorListener, AnimatorUpdateListener
    {
        @Override
        public void onAnimationCancel(final Animator animator) {
            if (ViewPropertyAnimatorPreHC.this.mListener != null) {
                ViewPropertyAnimatorPreHC.this.mListener.onAnimationCancel(animator);
            }
        }
        
        @Override
        public void onAnimationEnd(final Animator key) {
            if (ViewPropertyAnimatorPreHC.this.mListener != null) {
                ViewPropertyAnimatorPreHC.this.mListener.onAnimationEnd(key);
            }
            ViewPropertyAnimatorPreHC.this.mAnimatorMap.remove(key);
            if (ViewPropertyAnimatorPreHC.this.mAnimatorMap.isEmpty()) {
                ViewPropertyAnimatorPreHC.this.mListener = null;
            }
        }
        
        @Override
        public void onAnimationRepeat(final Animator animator) {
            if (ViewPropertyAnimatorPreHC.this.mListener != null) {
                ViewPropertyAnimatorPreHC.this.mListener.onAnimationRepeat(animator);
            }
        }
        
        @Override
        public void onAnimationStart(final Animator animator) {
            if (ViewPropertyAnimatorPreHC.this.mListener != null) {
                ViewPropertyAnimatorPreHC.this.mListener.onAnimationStart(animator);
            }
        }
        
        @Override
        public void onAnimationUpdate(final ValueAnimator key) {
            final float animatedFraction = key.getAnimatedFraction();
            final PropertyBundle propertyBundle = ViewPropertyAnimatorPreHC.this.mAnimatorMap.get(key);
            if ((propertyBundle.mPropertyMask & 0x1FF) != 0x0) {
                final View view = (View)ViewPropertyAnimatorPreHC.this.mView.get();
                if (view != null) {
                    view.invalidate();
                }
            }
            final ArrayList<NameValuesHolder> mNameValuesHolder = propertyBundle.mNameValuesHolder;
            if (mNameValuesHolder != null) {
                for (int size = mNameValuesHolder.size(), i = 0; i < size; ++i) {
                    final NameValuesHolder nameValuesHolder = mNameValuesHolder.get(i);
                    ViewPropertyAnimatorPreHC.this.setValue(nameValuesHolder.mNameConstant, nameValuesHolder.mFromValue + nameValuesHolder.mDeltaValue * animatedFraction);
                }
            }
            final View view2 = (View)ViewPropertyAnimatorPreHC.this.mView.get();
            if (view2 != null) {
                view2.invalidate();
            }
        }
    }
    
    private static class NameValuesHolder
    {
        float mDeltaValue;
        float mFromValue;
        int mNameConstant;
        
        NameValuesHolder(final int mNameConstant, final float mFromValue, final float mDeltaValue) {
            this.mNameConstant = mNameConstant;
            this.mFromValue = mFromValue;
            this.mDeltaValue = mDeltaValue;
        }
    }
    
    private static class PropertyBundle
    {
        ArrayList<NameValuesHolder> mNameValuesHolder;
        int mPropertyMask;
        
        PropertyBundle(final int mPropertyMask, final ArrayList<NameValuesHolder> mNameValuesHolder) {
            this.mPropertyMask = mPropertyMask;
            this.mNameValuesHolder = mNameValuesHolder;
        }
        
        boolean cancel(final int n) {
            if ((this.mPropertyMask & n) != 0x0 && this.mNameValuesHolder != null) {
                for (int size = this.mNameValuesHolder.size(), i = 0; i < size; ++i) {
                    if (this.mNameValuesHolder.get(i).mNameConstant == n) {
                        this.mNameValuesHolder.remove(i);
                        this.mPropertyMask &= ~n;
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
