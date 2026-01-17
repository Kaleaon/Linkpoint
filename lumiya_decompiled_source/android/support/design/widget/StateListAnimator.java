package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.StateSet;
import java.util.ArrayList;
/* loaded from: classes.dex */
final class StateListAnimator {
    private final ArrayList<Tuple> mTuples = new ArrayList<>();
    private Tuple mLastMatch = null;
    ValueAnimator mRunningAnimator = null;
    private final Animator.AnimatorListener mAnimationListener = new AnimatorListenerAdapter() { // from class: android.support.design.widget.StateListAnimator.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (StateListAnimator.this.mRunningAnimator != animator) {
                return;
            }
            StateListAnimator.this.mRunningAnimator = null;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Tuple {
        final ValueAnimator mAnimator;
        final int[] mSpecs;

        Tuple(int[] iArr, ValueAnimator valueAnimator) {
            this.mSpecs = iArr;
            this.mAnimator = valueAnimator;
        }
    }

    private void cancel() {
        if (this.mRunningAnimator == null) {
            return;
        }
        this.mRunningAnimator.cancel();
        this.mRunningAnimator = null;
    }

    private void start(Tuple tuple) {
        this.mRunningAnimator = tuple.mAnimator;
        this.mRunningAnimator.start();
    }

    public void addState(int[] iArr, ValueAnimator valueAnimator) {
        Tuple tuple = new Tuple(iArr, valueAnimator);
        valueAnimator.addListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    public void jumpToCurrentState() {
        if (this.mRunningAnimator == null) {
            return;
        }
        this.mRunningAnimator.end();
        this.mRunningAnimator = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setState(int[] iArr) {
        Tuple tuple;
        int size = this.mTuples.size();
        int i = 0;
        while (true) {
            if (i < size) {
                tuple = this.mTuples.get(i);
                if (StateSet.stateSetMatches(tuple.mSpecs, iArr)) {
                    break;
                }
                i++;
            } else {
                tuple = null;
                break;
            }
        }
        if (tuple != this.mLastMatch) {
            if (this.mLastMatch != null) {
                cancel();
            }
            this.mLastMatch = tuple;
            if (tuple == null) {
                return;
            }
            start(tuple);
        }
    }
}
