// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.util.Property;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.view.View;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;

public class ChangeClipBounds extends Transition
{
    private static final String PROPNAME_BOUNDS = "android:clipBounds:bounds";
    private static final String PROPNAME_CLIP = "android:clipBounds:clip";
    private static final String[] sTransitionProperties;
    
    static {
        sTransitionProperties = new String[] { "android:clipBounds:clip" };
    }
    
    public ChangeClipBounds() {
    }
    
    public ChangeClipBounds(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getVisibility() != 8) {
            final Rect clipBounds = ViewCompat.getClipBounds(view);
            transitionValues.values.put("android:clipBounds:clip", clipBounds);
            if (clipBounds == null) {
                transitionValues.values.put("android:clipBounds:bounds", new Rect(0, 0, view.getWidth(), view.getHeight()));
            }
        }
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public Animator createAnimator(@NonNull final ViewGroup viewGroup, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null || !transitionValues.values.containsKey("android:clipBounds:clip") || !transitionValues2.values.containsKey("android:clipBounds:clip")) {
            return null;
        }
        Rect rect = transitionValues.values.get("android:clipBounds:clip");
        final Rect rect2 = transitionValues2.values.get("android:clipBounds:clip");
        int n;
        if (rect2 != null) {
            n = 0;
        }
        else {
            n = 1;
        }
        if (rect == null && rect2 == null) {
            return null;
        }
        Rect rect3;
        if (rect != null) {
            if (rect2 != null) {
                rect3 = rect2;
            }
            else {
                rect3 = transitionValues2.values.get("android:clipBounds:bounds");
            }
        }
        else {
            rect = transitionValues.values.get("android:clipBounds:bounds");
            rect3 = rect2;
        }
        if (!rect.equals((Object)rect3)) {
            ViewCompat.setClipBounds(transitionValues2.view, rect);
            final ObjectAnimator ofObject = ObjectAnimator.ofObject((Object)transitionValues2.view, (Property)ViewUtils.CLIP_BOUNDS, (TypeEvaluator)new RectEvaluator(new Rect()), (Object[])new Rect[] { rect, rect3 });
            if (n != 0) {
                ofObject.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    final /* synthetic */ View val$endView = transitionValues2.view;
                    
                    public void onAnimationEnd(final Animator animator) {
                        ViewCompat.setClipBounds(this.val$endView, null);
                    }
                });
            }
            return (Animator)ofObject;
        }
        return null;
    }
    
    @Override
    public String[] getTransitionProperties() {
        return ChangeClipBounds.sTransitionProperties;
    }
}
