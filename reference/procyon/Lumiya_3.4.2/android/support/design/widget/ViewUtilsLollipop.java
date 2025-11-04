// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.content.res.TypedArray;
import android.content.Context;
import android.animation.AnimatorInflater;
import android.util.AttributeSet;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.support.design.R;
import android.view.ViewOutlineProvider;
import android.view.View;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class ViewUtilsLollipop
{
    private static final int[] STATE_LIST_ANIM_ATTRS;
    
    static {
        STATE_LIST_ANIM_ATTRS = new int[] { 16843848 };
    }
    
    static void setBoundsViewOutlineProvider(final View view) {
        view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }
    
    static void setDefaultAppBarLayoutStateListAnimator(final View view, final float n) {
        final int integer = view.getResources().getInteger(R.integer.app_bar_elevation_anim_duration);
        final StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[] { 16842766, R.attr.state_collapsible, -R.attr.state_collapsed }, (Animator)ObjectAnimator.ofFloat((Object)view, "elevation", new float[] { 0.0f }).setDuration((long)integer));
        stateListAnimator.addState(new int[] { 16842766 }, (Animator)ObjectAnimator.ofFloat((Object)view, "elevation", new float[] { n }).setDuration((long)integer));
        stateListAnimator.addState(new int[0], (Animator)ObjectAnimator.ofFloat((Object)view, "elevation", new float[] { 0.0f }).setDuration(0L));
        view.setStateListAnimator(stateListAnimator);
    }
    
    static void setStateListAnimatorFromAttrs(final View view, AttributeSet obtainStyledAttributes, final int n, final int n2) {
        final Context context = view.getContext();
        obtainStyledAttributes = (AttributeSet)context.obtainStyledAttributes(obtainStyledAttributes, ViewUtilsLollipop.STATE_LIST_ANIM_ATTRS, n, n2);
        try {
            if (((TypedArray)obtainStyledAttributes).hasValue(0)) {
                view.setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, ((TypedArray)obtainStyledAttributes).getResourceId(0, 0)));
            }
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
}
