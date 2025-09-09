package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;

public abstract class Animator implements Cloneable {
    ArrayList<AnimatorListener> mListeners = null;

    public interface AnimatorListener {
        void onAnimationCancel(Animator animator);

        void onAnimationEnd(Animator animator);

        void onAnimationRepeat(Animator animator);

        void onAnimationStart(Animator animator);
    }

    public void addListener(AnimatorListener animatorListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(animatorListener);
    }

    public void cancel() {
    }

    public Animator clone() {
        try {
            Animator animator = (Animator) super.clone();
            if (this.mListeners != null) {
                ArrayList<AnimatorListener> arrayList = this.mListeners;
                animator.mListeners = new ArrayList<>();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    animator.mListeners.add(arrayList.get(i));
                }
            }
            return animator;
        } catch (CloneNotSupportedException e) {
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
        return isRunning();
    }

    public void removeAllListeners() {
        if (this.mListeners != null) {
            this.mListeners.clear();
            this.mListeners = null;
        }
    }

    public void removeListener(AnimatorListener animatorListener) {
        if (this.mListeners != null) {
            this.mListeners.remove(animatorListener);
            if (this.mListeners.size() == 0) {
                this.mListeners = null;
            }
        }
    }

    public abstract Animator setDuration(long j);

    public abstract void setInterpolator(Interpolator interpolator);

    public abstract void setStartDelay(long j);

    public void setTarget(Object obj) {
    }

    public void setupEndValues() {
    }

    public void setupStartValues() {
    }

    public void start() {
    }
}
