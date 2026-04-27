// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Rect;
import android.view.ViewGroup;

public class CircularPropagation extends VisibilityPropagation
{
    private float mPropagationSpeed;
    
    public CircularPropagation() {
        this.mPropagationSpeed = 3.0f;
    }
    
    private static float distance(float n, float n2, final float n3, final float n4) {
        n = n3 - n;
        n2 = n4 - n2;
        return (float)Math.sqrt(n * n + n2 * n2);
    }
    
    @Override
    public long getStartDelay(final ViewGroup viewGroup, final Transition transition, final TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null && transitionValues2 == null) {
            return 0L;
        }
        int n = 1;
        if (transitionValues2 == null || this.getViewVisibility(transitionValues) == 0) {
            n = -1;
            transitionValues2 = transitionValues;
        }
        final int viewX = this.getViewX(transitionValues2);
        final int viewY = this.getViewY(transitionValues2);
        final Rect epicenter = transition.getEpicenter();
        int n2;
        int n3;
        if (epicenter == null) {
            final int[] array = new int[2];
            viewGroup.getLocationOnScreen(array);
            n2 = Math.round(array[0] + viewGroup.getWidth() / 2 + viewGroup.getTranslationX());
            n3 = Math.round(array[1] + viewGroup.getHeight() / 2 + viewGroup.getTranslationY());
        }
        else {
            n2 = epicenter.centerX();
            n3 = epicenter.centerY();
        }
        final float n4 = distance((float)viewX, (float)viewY, (float)n2, (float)n3) / distance(0.0f, 0.0f, (float)viewGroup.getWidth(), (float)viewGroup.getHeight());
        long duration = transition.getDuration();
        int n5;
        if (duration >= 0L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (n5 == 0) {
            duration = 300L;
        }
        return Math.round(n * duration / this.mPropagationSpeed * n4);
    }
    
    public void setPropagationSpeed(final float mPropagationSpeed) {
        if (mPropagationSpeed == 0.0f) {
            throw new IllegalArgumentException("propagationSpeed may not be 0");
        }
        this.mPropagationSpeed = mPropagationSpeed;
    }
}
