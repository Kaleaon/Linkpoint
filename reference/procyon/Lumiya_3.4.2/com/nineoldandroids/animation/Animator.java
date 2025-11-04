// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;

public abstract class Animator implements Cloneable
{
    ArrayList<AnimatorListener> mListeners;
    
    public Animator() {
        this.mListeners = null;
    }
    
    public void addListener(final AnimatorListener e) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<AnimatorListener>();
        }
        this.mListeners.add(e);
    }
    
    public void cancel() {
    }
    
    public Animator clone() {
        try {
            final Animator animator = (Animator)super.clone();
            if (this.mListeners != null) {
                final ArrayList<AnimatorListener> mListeners = this.mListeners;
                animator.mListeners = new ArrayList<AnimatorListener>();
                for (int size = mListeners.size(), i = 0; i < size; ++i) {
                    animator.mListeners.add((AnimatorListener)mListeners.get(i));
                }
            }
            return animator;
        }
        catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
    
    public void end() {
    }
    
    public abstract long getDuration();
    
    public ArrayList<AnimatorListener> getListeners() {
        return this.mListeners;
    }
    
    public abstract long getStartDelay();
    
    public abstract boolean isRunning();
    
    public boolean isStarted() {
        return this.isRunning();
    }
    
    public void removeAllListeners() {
        if (this.mListeners != null) {
            this.mListeners.clear();
            this.mListeners = null;
        }
    }
    
    public void removeListener(final AnimatorListener o) {
        if (this.mListeners != null) {
            this.mListeners.remove(o);
            if (this.mListeners.size() == 0) {
                this.mListeners = null;
            }
        }
    }
    
    public abstract Animator setDuration(final long p0);
    
    public abstract void setInterpolator(final Interpolator p0);
    
    public abstract void setStartDelay(final long p0);
    
    public void setTarget(final Object o) {
    }
    
    public void setupEndValues() {
    }
    
    public void setupStartValues() {
    }
    
    public void start() {
    }
    
    public interface AnimatorListener
    {
        void onAnimationCancel(final Animator p0);
        
        void onAnimationEnd(final Animator p0);
        
        void onAnimationRepeat(final Animator p0);
        
        void onAnimationStart(final Animator p0);
    }
}
