// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.view.View;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.content.Context;

public class ChangeScroll extends Transition
{
    private static final String[] PROPERTIES;
    private static final String PROPNAME_SCROLL_X = "android:changeScroll:x";
    private static final String PROPNAME_SCROLL_Y = "android:changeScroll:y";
    
    static {
        PROPERTIES = new String[] { "android:changeScroll:x", "android:changeScroll:y" };
    }
    
    public ChangeScroll() {
    }
    
    public ChangeScroll(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        transitionValues.values.put("android:changeScroll:x", transitionValues.view.getScrollX());
        transitionValues.values.put("android:changeScroll:y", transitionValues.view.getScrollY());
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Nullable
    @Override
    public Animator createAnimator(@NonNull final ViewGroup viewGroup, @Nullable final TransitionValues transitionValues, @Nullable final TransitionValues transitionValues2) {
        if (transitionValues != null && transitionValues2 != null) {
            final View view = transitionValues2.view;
            final int intValue = transitionValues.values.get("android:changeScroll:x");
            final int intValue2 = transitionValues2.values.get("android:changeScroll:x");
            final int intValue3 = transitionValues.values.get("android:changeScroll:y");
            final int intValue4 = transitionValues2.values.get("android:changeScroll:y");
            Object ofInt;
            if (intValue == intValue2) {
                ofInt = null;
            }
            else {
                view.setScrollX(intValue);
                ofInt = ObjectAnimator.ofInt((Object)view, "scrollX", new int[] { intValue, intValue2 });
            }
            Object ofInt2;
            if (intValue3 == intValue4) {
                ofInt2 = null;
            }
            else {
                view.setScrollY(intValue3);
                ofInt2 = ObjectAnimator.ofInt((Object)view, "scrollY", new int[] { intValue3, intValue4 });
            }
            return TransitionUtils.mergeAnimators((Animator)ofInt, (Animator)ofInt2);
        }
        return null;
    }
    
    @Nullable
    @Override
    public String[] getTransitionProperties() {
        return ChangeScroll.PROPERTIES;
    }
}
