// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class SidePropagation extends VisibilityPropagation
{
    private float mPropagationSpeed;
    private int mSide;
    
    public SidePropagation() {
        this.mPropagationSpeed = 3.0f;
        this.mSide = 80;
    }
    
    private int distance(final View view, int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        int mSide = 5;
        final int n9 = 1;
        final int n10 = 1;
        final int n11 = 0;
        if (this.mSide != 8388611) {
            if (this.mSide != 8388613) {
                mSide = this.mSide;
            }
            else {
                int n12 = n9;
                if (ViewCompat.getLayoutDirection(view) != 1) {
                    n12 = 0;
                }
                if (n12 != 0) {
                    mSide = 3;
                }
            }
        }
        else {
            int n13 = n10;
            if (ViewCompat.getLayoutDirection(view) != 1) {
                n13 = 0;
            }
            if (n13 == 0) {
                mSide = 3;
            }
            else {
                mSide = 5;
            }
        }
        switch (mSide) {
            default: {
                n = n11;
                break;
            }
            case 3: {
                n = Math.abs(n4 - n2) + (n7 - n);
                break;
            }
            case 48: {
                n = Math.abs(n3 - n) + (n8 - n2);
                break;
            }
            case 5: {
                n = Math.abs(n4 - n2) + (n - n5);
                break;
            }
            case 80: {
                n = Math.abs(n3 - n) + (n2 - n6);
                break;
            }
        }
        return n;
    }
    
    private int getMaxDistance(final ViewGroup viewGroup) {
        switch (this.mSide) {
            default: {
                return viewGroup.getHeight();
            }
            case 3:
            case 5:
            case 8388611:
            case 8388613: {
                return viewGroup.getWidth();
            }
        }
    }
    
    @Override
    public long getStartDelay(final ViewGroup viewGroup, final Transition transition, TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues == null && transitionValues2 == null) {
            return 0L;
        }
        final Rect epicenter = transition.getEpicenter();
        int n;
        if (transitionValues2 != null && this.getViewVisibility(transitionValues) != 0) {
            n = 1;
            transitionValues = transitionValues2;
        }
        else {
            n = -1;
        }
        final int viewX = this.getViewX(transitionValues);
        final int viewY = this.getViewY(transitionValues);
        final int[] array = new int[2];
        viewGroup.getLocationOnScreen(array);
        final int n2 = array[0] + Math.round(viewGroup.getTranslationX());
        final int n3 = array[1] + Math.round(viewGroup.getTranslationY());
        final int n4 = n2 + viewGroup.getWidth();
        final int n5 = n3 + viewGroup.getHeight();
        int centerX;
        int centerY;
        if (epicenter == null) {
            centerX = (n2 + n4) / 2;
            centerY = (n3 + n5) / 2;
        }
        else {
            centerX = epicenter.centerX();
            centerY = epicenter.centerY();
        }
        final float n6 = this.distance((View)viewGroup, viewX, viewY, centerX, centerY, n2, n3, n4, n5) / (float)this.getMaxDistance(viewGroup);
        long duration = transition.getDuration();
        int n7;
        if (duration >= 0L) {
            n7 = 1;
        }
        else {
            n7 = 0;
        }
        if (n7 == 0) {
            duration = 300L;
        }
        return Math.round(duration * n / this.mPropagationSpeed * n6);
    }
    
    public void setPropagationSpeed(final float mPropagationSpeed) {
        if (mPropagationSpeed == 0.0f) {
            throw new IllegalArgumentException("propagationSpeed may not be 0");
        }
        this.mPropagationSpeed = mPropagationSpeed;
    }
    
    public void setSide(final int mSide) {
        this.mSide = mSide;
    }
}
