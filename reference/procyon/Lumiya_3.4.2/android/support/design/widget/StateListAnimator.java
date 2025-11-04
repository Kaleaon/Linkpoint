// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.util.StateSet;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import java.util.ArrayList;
import android.animation.ValueAnimator;
import android.animation.Animator$AnimatorListener;

final class StateListAnimator
{
    private final Animator$AnimatorListener mAnimationListener;
    private Tuple mLastMatch;
    ValueAnimator mRunningAnimator;
    private final ArrayList<Tuple> mTuples;
    
    StateListAnimator() {
        this.mTuples = new ArrayList<Tuple>();
        this.mLastMatch = null;
        this.mRunningAnimator = null;
        this.mAnimationListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                if (StateListAnimator.this.mRunningAnimator == animator) {
                    StateListAnimator.this.mRunningAnimator = null;
                }
            }
        };
    }
    
    private void cancel() {
        if (this.mRunningAnimator != null) {
            this.mRunningAnimator.cancel();
            this.mRunningAnimator = null;
        }
    }
    
    private void start(final Tuple tuple) {
        (this.mRunningAnimator = tuple.mAnimator).start();
    }
    
    public void addState(final int[] array, final ValueAnimator valueAnimator) {
        final Tuple e = new Tuple(array, valueAnimator);
        valueAnimator.addListener(this.mAnimationListener);
        this.mTuples.add(e);
    }
    
    public void jumpToCurrentState() {
        if (this.mRunningAnimator != null) {
            this.mRunningAnimator.end();
            this.mRunningAnimator = null;
        }
    }
    
    void setState(final int[] array) {
        final int size = this.mTuples.size();
        int i = 0;
        while (true) {
            while (i < size) {
                final Tuple mLastMatch;
                if (!StateSet.stateSetMatches((mLastMatch = this.mTuples.get(i)).mSpecs, array)) {
                    ++i;
                }
                else {
                    if (mLastMatch != this.mLastMatch) {
                        if (this.mLastMatch != null) {
                            this.cancel();
                        }
                        if ((this.mLastMatch = mLastMatch) != null) {
                            this.start(mLastMatch);
                        }
                    }
                    return;
                }
            }
            Tuple mLastMatch = null;
            continue;
        }
    }
    
    static class Tuple
    {
        final ValueAnimator mAnimator;
        final int[] mSpecs;
        
        Tuple(final int[] mSpecs, final ValueAnimator mAnimator) {
            this.mSpecs = mSpecs;
            this.mAnimator = mAnimator;
        }
    }
}
