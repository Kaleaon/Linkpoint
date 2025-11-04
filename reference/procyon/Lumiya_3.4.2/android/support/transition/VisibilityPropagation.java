// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.view.View;

public abstract class VisibilityPropagation extends TransitionPropagation
{
    private static final String PROPNAME_VIEW_CENTER = "android:visibilityPropagation:center";
    private static final String PROPNAME_VISIBILITY = "android:visibilityPropagation:visibility";
    private static final String[] VISIBILITY_PROPAGATION_VALUES;
    
    static {
        VISIBILITY_PROPAGATION_VALUES = new String[] { "android:visibilityPropagation:visibility", "android:visibilityPropagation:center" };
    }
    
    private static int getViewCoordinate(final TransitionValues transitionValues, final int n) {
        if (transitionValues == null) {
            return -1;
        }
        final int[] array = transitionValues.values.get("android:visibilityPropagation:center");
        if (array != null) {
            return array[n];
        }
        return -1;
    }
    
    @Override
    public void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        Integer value = transitionValues.values.get("android:visibility:visibility");
        if (value == null) {
            value = view.getVisibility();
        }
        transitionValues.values.put("android:visibilityPropagation:visibility", value);
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        array[0] += Math.round(view.getTranslationX());
        array[0] += view.getWidth() / 2;
        array[1] += Math.round(view.getTranslationY());
        array[1] += view.getHeight() / 2;
        transitionValues.values.put("android:visibilityPropagation:center", array);
    }
    
    @Override
    public String[] getPropagationProperties() {
        return VisibilityPropagation.VISIBILITY_PROPAGATION_VALUES;
    }
    
    public int getViewVisibility(final TransitionValues transitionValues) {
        if (transitionValues == null) {
            return 8;
        }
        final Integer n = transitionValues.values.get("android:visibilityPropagation:visibility");
        if (n != null) {
            return n;
        }
        return 8;
    }
    
    public int getViewX(final TransitionValues transitionValues) {
        return getViewCoordinate(transitionValues, 0);
    }
    
    public int getViewY(final TransitionValues transitionValues) {
        return getViewCoordinate(transitionValues, 1);
    }
}
