package android.support.transition;

import android.graphics.Rect;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class SidePropagation extends VisibilityPropagation {
    private float mPropagationSpeed = 3.0f;
    private int mSide = 80;

    private int distance(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9 = 5;
        boolean z = true;
        if (this.mSide == 8388611) {
            if (ViewCompat.getLayoutDirection(view) != 1) {
                z = false;
            }
            i9 = !z ? 3 : 5;
        } else if (this.mSide != 8388613) {
            i9 = this.mSide;
        } else {
            if (ViewCompat.getLayoutDirection(view) != 1) {
                z = false;
            }
            if (z) {
                i9 = 3;
            }
        }
        switch (i9) {
            case 3:
                return Math.abs(i4 - i2) + (i7 - i);
            case 5:
                return Math.abs(i4 - i2) + (i - i5);
            case 48:
                return Math.abs(i3 - i) + (i8 - i2);
            case 80:
                return Math.abs(i3 - i) + (i2 - i6);
            default:
                return 0;
        }
    }

    private int getMaxDistance(ViewGroup viewGroup) {
        switch (this.mSide) {
            case 3:
            case 5:
            case GravityCompat.START /*8388611*/:
            case GravityCompat.END /*8388613*/:
                return viewGroup.getWidth();
            default:
                return viewGroup.getHeight();
        }
    }

    public long getStartDelay(ViewGroup viewGroup, Transition transition, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int i;
        int centerX;
        int centerY;
        if (transitionValues == null && transitionValues2 == null) {
            return 0;
        }
        Rect epicenter = transition.getEpicenter();
        if (transitionValues2 == null || getViewVisibility(transitionValues) == 0) {
            transitionValues2 = transitionValues;
            i = -1;
        } else {
            i = 1;
        }
        int viewX = getViewX(transitionValues2);
        int viewY = getViewY(transitionValues2);
        int[] iArr = new int[2];
        viewGroup.getLocationOnScreen(iArr);
        int round = iArr[0] + Math.round(viewGroup.getTranslationX());
        int round2 = iArr[1] + Math.round(viewGroup.getTranslationY());
        int width = round + viewGroup.getWidth();
        int height = round2 + viewGroup.getHeight();
        if (epicenter == null) {
            centerX = (round + width) / 2;
            centerY = (round2 + height) / 2;
        } else {
            centerX = epicenter.centerX();
            centerY = epicenter.centerY();
        }
        float distance = ((float) distance(viewGroup, viewX, viewY, centerX, centerY, round, round2, width, height)) / ((float) getMaxDistance(viewGroup));
        long duration = transition.getDuration();
        if (!(duration >= 0)) {
            duration = 300;
        }
        return (long) Math.round((((float) (duration * ((long) i))) / this.mPropagationSpeed) * distance);
    }

    public void setPropagationSpeed(float f) {
        if (f == 0.0f) {
            throw new IllegalArgumentException("propagationSpeed may not be 0");
        }
        this.mPropagationSpeed = f;
    }

    public void setSide(int i) {
        this.mSide = i;
    }
}
