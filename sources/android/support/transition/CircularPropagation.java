package android.support.transition;

import android.graphics.Rect;
import android.view.ViewGroup;

public class CircularPropagation extends VisibilityPropagation {
    private float mPropagationSpeed = 3.0f;

    private static float distance(float f, float f2, float f3, float f4) {
        float f5 = f3 - f;
        float f6 = f4 - f2;
        return (float) Math.sqrt((double) ((f5 * f5) + (f6 * f6)));
    }

    public long getStartDelay(ViewGroup viewGroup, Transition transition, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int centerX;
        int centerY;
        if (transitionValues == null && transitionValues2 == null) {
            return 0;
        }
        int i = 1;
        if (transitionValues2 == null || getViewVisibility(transitionValues) == 0) {
            i = -1;
            transitionValues2 = transitionValues;
        }
        int viewX = getViewX(transitionValues2);
        int viewY = getViewY(transitionValues2);
        Rect epicenter = transition.getEpicenter();
        if (epicenter == null) {
            int[] iArr = new int[2];
            viewGroup.getLocationOnScreen(iArr);
            centerX = Math.round(((float) (iArr[0] + (viewGroup.getWidth() / 2))) + viewGroup.getTranslationX());
            centerY = Math.round(((float) (iArr[1] + (viewGroup.getHeight() / 2))) + viewGroup.getTranslationY());
        } else {
            centerX = epicenter.centerX();
            centerY = epicenter.centerY();
        }
        float distance = distance((float) viewX, (float) viewY, (float) centerX, (float) centerY) / distance(0.0f, 0.0f, (float) viewGroup.getWidth(), (float) viewGroup.getHeight());
        long duration = transition.getDuration();
        if (!(duration >= 0)) {
            duration = 300;
        }
        return (long) Math.round((((float) (((long) i) * duration)) / this.mPropagationSpeed) * distance);
    }

    public void setPropagationSpeed(float f) {
        if (f == 0.0f) {
            throw new IllegalArgumentException("propagationSpeed may not be 0");
        }
        this.mPropagationSpeed = f;
    }
}
