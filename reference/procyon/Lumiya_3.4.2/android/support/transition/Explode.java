// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.graphics.Rect;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.animation.TimeInterpolator;

public class Explode extends Visibility
{
    private static final String PROPNAME_SCREEN_BOUNDS = "android:explode:screenBounds";
    private static final TimeInterpolator sAccelerate;
    private static final TimeInterpolator sDecelerate;
    private int[] mTempLoc;
    
    static {
        sDecelerate = (TimeInterpolator)new DecelerateInterpolator();
        sAccelerate = (TimeInterpolator)new AccelerateInterpolator();
    }
    
    public Explode() {
        this.mTempLoc = new int[2];
        this.setPropagation(new CircularPropagation());
    }
    
    public Explode(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTempLoc = new int[2];
        this.setPropagation(new CircularPropagation());
    }
    
    private static float calculateDistance(final float n, final float n2) {
        return (float)Math.sqrt(n * n + n2 * n2);
    }
    
    private static float calculateMaxDistance(final View view, int max, int max2) {
        max = Math.max(max, view.getWidth() - max);
        max2 = Math.max(max2, view.getHeight() - max2);
        return calculateDistance((float)max, (float)max2);
    }
    
    private void calculateOut(final View view, final Rect rect, final int[] array) {
        view.getLocationOnScreen(this.mTempLoc);
        final int n = this.mTempLoc[0];
        final int n2 = this.mTempLoc[1];
        final Rect epicenter = this.getEpicenter();
        int centerX;
        int centerY;
        if (epicenter != null) {
            centerX = epicenter.centerX();
            centerY = epicenter.centerY();
        }
        else {
            centerX = Math.round(view.getTranslationX()) + (view.getWidth() / 2 + n);
            centerY = view.getHeight() / 2 + n2 + Math.round(view.getTranslationY());
        }
        final int centerX2 = rect.centerX();
        final int centerY2 = rect.centerY();
        final float n3 = (float)(centerX2 - centerX);
        float n5;
        final float n4 = n5 = (float)(centerY2 - centerY);
        float n6 = n3;
        if (n3 == 0.0f) {
            n5 = n4;
            n6 = n3;
            if (n4 == 0.0f) {
                n6 = (float)(Math.random() * 2.0) - 1.0f;
                n5 = (float)(Math.random() * 2.0) - 1.0f;
            }
        }
        final float calculateDistance = calculateDistance(n6, n5);
        final float n7 = n6 / calculateDistance;
        final float n8 = n5 / calculateDistance;
        final float calculateMaxDistance = calculateMaxDistance(view, centerX - n, centerY - n2);
        array[0] = Math.round(calculateMaxDistance * n7);
        array[1] = Math.round(calculateMaxDistance * n8);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        view.getLocationOnScreen(this.mTempLoc);
        final int n = this.mTempLoc[0];
        final int n2 = this.mTempLoc[1];
        transitionValues.values.put("android:explode:screenBounds", new Rect(n, n2, view.getWidth() + n, view.getHeight() + n2));
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    @Override
    public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues2 != null) {
            final Rect rect = transitionValues2.values.get("android:explode:screenBounds");
            final float translationX = view.getTranslationX();
            final float translationY = view.getTranslationY();
            this.calculateOut((View)viewGroup, rect, this.mTempLoc);
            return TranslationAnimationCreator.createAnimation(view, transitionValues2, rect.left, rect.top, translationX + this.mTempLoc[0], translationY + this.mTempLoc[1], translationX, translationY, Explode.sDecelerate);
        }
        return null;
    }
    
    @Override
    public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues != null) {
            final Rect rect = transitionValues.values.get("android:explode:screenBounds");
            final int left = rect.left;
            final int top = rect.top;
            final float translationX = view.getTranslationX();
            final float translationY = view.getTranslationY();
            final int[] array = (int[])transitionValues.view.getTag(R.id.transition_position);
            float n;
            float n2;
            if (array == null) {
                n = translationY;
                n2 = translationX;
            }
            else {
                final float n3 = (float)(array[0] - rect.left);
                final float n4 = (float)(array[1] - rect.top);
                rect.offsetTo(array[0], array[1]);
                n = n4 + translationY;
                n2 = translationX + n3;
            }
            this.calculateOut((View)viewGroup, rect, this.mTempLoc);
            return TranslationAnimationCreator.createAnimation(view, transitionValues, left, top, translationX, translationY, n2 + this.mTempLoc[0], n + this.mTempLoc[1], Explode.sAccelerate);
        }
        return null;
    }
}
