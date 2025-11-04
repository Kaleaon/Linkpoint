// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.AnimatorListenerAdapter;
import android.animation.Animator$AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.View;

class TranslationAnimationCreator
{
    static Animator createAnimation(final View view, final TransitionValues transitionValues, final int n, final int n2, float translationX, float translationY, final float n3, final float n4, final TimeInterpolator interpolator) {
        final float translationX2 = view.getTranslationX();
        final float translationY2 = view.getTranslationY();
        final int[] array = (int[])transitionValues.view.getTag(R.id.transition_position);
        if (array != null) {
            translationX = array[0] - n + translationX2;
            translationY = array[1] - n2 + translationY2;
        }
        final int round = Math.round(translationX - translationX2);
        final int round2 = Math.round(translationY - translationY2);
        view.setTranslationX(translationX);
        view.setTranslationY(translationY);
        if (translationX == n3 && translationY == n4) {
            return null;
        }
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[] { translationX, n3 }), PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[] { translationY, n4 }) });
        final TransitionPositionListener transitionPositionListener = new TransitionPositionListener(view, transitionValues.view, n + round, n2 + round2, translationX2, translationY2);
        ofPropertyValuesHolder.addListener((Animator$AnimatorListener)transitionPositionListener);
        AnimatorUtils.addPauseListener((Animator)ofPropertyValuesHolder, transitionPositionListener);
        ofPropertyValuesHolder.setInterpolator(interpolator);
        return (Animator)ofPropertyValuesHolder;
    }
    
    private static class TransitionPositionListener extends AnimatorListenerAdapter
    {
        private final View mMovingView;
        private float mPausedX;
        private float mPausedY;
        private final int mStartX;
        private final int mStartY;
        private final float mTerminalX;
        private final float mTerminalY;
        private int[] mTransitionPosition;
        private final View mViewInHierarchy;
        
        private TransitionPositionListener(final View mMovingView, final View mViewInHierarchy, final int n, final int n2, final float mTerminalX, final float mTerminalY) {
            this.mMovingView = mMovingView;
            this.mViewInHierarchy = mViewInHierarchy;
            this.mStartX = n - Math.round(this.mMovingView.getTranslationX());
            this.mStartY = n2 - Math.round(this.mMovingView.getTranslationY());
            this.mTerminalX = mTerminalX;
            this.mTerminalY = mTerminalY;
            this.mTransitionPosition = (int[])this.mViewInHierarchy.getTag(R.id.transition_position);
            if (this.mTransitionPosition != null) {
                this.mViewInHierarchy.setTag(R.id.transition_position, (Object)null);
            }
        }
        
        public void onAnimationCancel(final Animator animator) {
            if (this.mTransitionPosition == null) {
                this.mTransitionPosition = new int[2];
            }
            this.mTransitionPosition[0] = Math.round(this.mStartX + this.mMovingView.getTranslationX());
            this.mTransitionPosition[1] = Math.round(this.mStartY + this.mMovingView.getTranslationY());
            this.mViewInHierarchy.setTag(R.id.transition_position, (Object)this.mTransitionPosition);
        }
        
        public void onAnimationEnd(final Animator animator) {
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }
        
        public void onAnimationPause(final Animator animator) {
            this.mPausedX = this.mMovingView.getTranslationX();
            this.mPausedY = this.mMovingView.getTranslationY();
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }
        
        public void onAnimationResume(final Animator animator) {
            this.mMovingView.setTranslationX(this.mPausedX);
            this.mMovingView.setTranslationY(this.mPausedY);
        }
    }
}
