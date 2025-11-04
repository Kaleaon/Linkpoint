// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.animation.Animator;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import org.xmlpull.v1.XmlPullParser;
import android.support.v4.content.res.TypedArrayUtils;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.content.Context;

public abstract class Visibility extends Transition
{
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String[] sTransitionProperties;
    private int mMode;
    
    static {
        sTransitionProperties = new String[] { "android:visibility:visibility", "android:visibility:parent" };
    }
    
    public Visibility() {
        this.mMode = 3;
    }
    
    public Visibility(final Context context, final AttributeSet set) {
        super(context, set);
        this.mMode = 3;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, Styleable.VISIBILITY_TRANSITION);
        final int namedInt = TypedArrayUtils.getNamedInt(obtainStyledAttributes, (XmlPullParser)set, "transitionVisibilityMode", 0, 0);
        obtainStyledAttributes.recycle();
        if (namedInt != 0) {
            this.setMode(namedInt);
        }
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        transitionValues.values.put("android:visibility:visibility", transitionValues.view.getVisibility());
        transitionValues.values.put("android:visibility:parent", transitionValues.view.getParent());
        final int[] array = new int[2];
        transitionValues.view.getLocationOnScreen(array);
        transitionValues.values.put("android:visibility:screenLocation", array);
    }
    
    private VisibilityInfo getVisibilityChangeInfo(final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        final VisibilityInfo visibilityInfo = new VisibilityInfo();
        visibilityInfo.mVisibilityChange = false;
        visibilityInfo.mFadeIn = false;
        if (transitionValues != null && transitionValues.values.containsKey("android:visibility:visibility")) {
            visibilityInfo.mStartVisibility = transitionValues.values.get("android:visibility:visibility");
            visibilityInfo.mStartParent = transitionValues.values.get("android:visibility:parent");
        }
        else {
            visibilityInfo.mStartVisibility = -1;
            visibilityInfo.mStartParent = null;
        }
        if (transitionValues2 != null && transitionValues2.values.containsKey("android:visibility:visibility")) {
            visibilityInfo.mEndVisibility = transitionValues2.values.get("android:visibility:visibility");
            visibilityInfo.mEndParent = transitionValues2.values.get("android:visibility:parent");
        }
        else {
            visibilityInfo.mEndVisibility = -1;
            visibilityInfo.mEndParent = null;
        }
        if (transitionValues != null && transitionValues2 != null) {
            if (visibilityInfo.mStartVisibility == visibilityInfo.mEndVisibility && visibilityInfo.mStartParent == visibilityInfo.mEndParent) {
                return visibilityInfo;
            }
            if (visibilityInfo.mStartVisibility == visibilityInfo.mEndVisibility) {
                if (visibilityInfo.mEndParent != null) {
                    if (visibilityInfo.mStartParent == null) {
                        visibilityInfo.mFadeIn = true;
                        visibilityInfo.mVisibilityChange = true;
                    }
                }
                else {
                    visibilityInfo.mFadeIn = false;
                    visibilityInfo.mVisibilityChange = true;
                }
            }
            else if (visibilityInfo.mStartVisibility != 0) {
                if (visibilityInfo.mEndVisibility == 0) {
                    visibilityInfo.mFadeIn = true;
                    visibilityInfo.mVisibilityChange = true;
                }
            }
            else {
                visibilityInfo.mFadeIn = false;
                visibilityInfo.mVisibilityChange = true;
            }
        }
        else if (transitionValues == null && visibilityInfo.mEndVisibility == 0) {
            visibilityInfo.mFadeIn = true;
            visibilityInfo.mVisibilityChange = true;
        }
        else if (transitionValues2 == null && visibilityInfo.mStartVisibility == 0) {
            visibilityInfo.mFadeIn = false;
            visibilityInfo.mVisibilityChange = true;
        }
        return visibilityInfo;
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
        final VisibilityInfo visibilityChangeInfo = this.getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (!visibilityChangeInfo.mVisibilityChange || (visibilityChangeInfo.mStartParent == null && visibilityChangeInfo.mEndParent == null)) {
            return null;
        }
        if (!visibilityChangeInfo.mFadeIn) {
            return this.onDisappear(viewGroup, transitionValues, visibilityChangeInfo.mStartVisibility, transitionValues2, visibilityChangeInfo.mEndVisibility);
        }
        return this.onAppear(viewGroup, transitionValues, visibilityChangeInfo.mStartVisibility, transitionValues2, visibilityChangeInfo.mEndVisibility);
    }
    
    public int getMode() {
        return this.mMode;
    }
    
    @Nullable
    @Override
    public String[] getTransitionProperties() {
        return Visibility.sTransitionProperties;
    }
    
    @Override
    public boolean isTransitionRequired(final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        boolean b = false;
        if (transitionValues == null && transitionValues2 == null) {
            return false;
        }
        if (transitionValues != null && transitionValues2 != null && transitionValues2.values.containsKey("android:visibility:visibility") != transitionValues.values.containsKey("android:visibility:visibility")) {
            return false;
        }
        final VisibilityInfo visibilityChangeInfo = this.getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (visibilityChangeInfo.mVisibilityChange) {
            if (visibilityChangeInfo.mStartVisibility == 0 || visibilityChangeInfo.mEndVisibility == 0) {
                b = true;
            }
        }
        return b;
    }
    
    public boolean isVisible(final TransitionValues transitionValues) {
        if (transitionValues != null) {
            final int intValue = transitionValues.values.get("android:visibility:visibility");
            final View view = transitionValues.values.get("android:visibility:parent");
            return intValue == 0 && view != null;
        }
        return false;
    }
    
    public Animator onAppear(final ViewGroup viewGroup, final TransitionValues transitionValues, final int n, final TransitionValues transitionValues2, final int n2) {
        if ((this.mMode & 0x1) == 0x1 && transitionValues2 != null) {
            if (transitionValues == null) {
                final View view = (View)transitionValues2.view.getParent();
                if (this.getVisibilityChangeInfo(this.getMatchedTransitionValues(view, false), this.getTransitionValues(view, false)).mVisibilityChange) {
                    return null;
                }
            }
            return this.onAppear(viewGroup, transitionValues2.view, transitionValues, transitionValues2);
        }
        return null;
    }
    
    public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        return null;
    }
    
    public Animator onDisappear(final ViewGroup viewGroup, final TransitionValues transitionValues, int n, final TransitionValues transitionValues2, int n2) {
        if ((this.mMode & 0x2) != 0x2) {
            return null;
        }
        View view;
        if (transitionValues == null) {
            view = null;
        }
        else {
            view = transitionValues.view;
        }
        View view2;
        if (transitionValues2 == null) {
            view2 = null;
        }
        else {
            view2 = transitionValues2.view;
        }
        if (view2 != null && view2.getParent() != null) {
            if (n2 != 4) {
                if (view != view2) {
                    view2 = null;
                }
                else {
                    view = null;
                }
            }
            else {
                view = null;
            }
        }
        else if (view2 == null) {
            if (view == null) {
                view2 = null;
                view = null;
            }
            else if (view.getParent() != null) {
                if (!(view.getParent() instanceof View)) {
                    view2 = null;
                    view = null;
                }
                else {
                    final View view3 = (View)view.getParent();
                    if (this.getVisibilityChangeInfo(this.getTransitionValues(view3, true), this.getMatchedTransitionValues(view3, true)).mVisibilityChange) {
                        if (view3.getParent() != null) {
                            view = null;
                        }
                        else {
                            n = view3.getId();
                            if (n == -1 || viewGroup.findViewById(n) == null || !this.mCanRemoveViews) {
                                view = null;
                            }
                        }
                    }
                    else {
                        view = TransitionUtils.copyViewImage(viewGroup, view, view3);
                    }
                    view2 = null;
                }
            }
            else {
                view2 = null;
            }
        }
        else {
            view = view2;
            view2 = null;
        }
        if (view != null && transitionValues != null) {
            final int[] array = transitionValues.values.get("android:visibility:screenLocation");
            n = array[0];
            n2 = array[1];
            final int[] array2 = new int[2];
            viewGroup.getLocationOnScreen(array2);
            view.offsetLeftAndRight(n - array2[0] - view.getLeft());
            view.offsetTopAndBottom(n2 - array2[1] - view.getTop());
            final ViewGroupOverlayImpl overlay = ViewGroupUtils.getOverlay(viewGroup);
            overlay.add(view);
            final Animator onDisappear = this.onDisappear(viewGroup, view, transitionValues, transitionValues2);
            if (onDisappear != null) {
                onDisappear.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        overlay.remove(view);
                    }
                });
            }
            else {
                overlay.remove(view);
            }
            return onDisappear;
        }
        if (view2 == null) {
            return null;
        }
        n = view2.getVisibility();
        ViewUtils.setTransitionVisibility(view2, 0);
        final Animator onDisappear2 = this.onDisappear(viewGroup, view2, transitionValues, transitionValues2);
        if (onDisappear2 == null) {
            ViewUtils.setTransitionVisibility(view2, n);
        }
        else {
            final DisappearListener disappearListener = new DisappearListener(view2, n2, true);
            onDisappear2.addListener((Animator$AnimatorListener)disappearListener);
            AnimatorUtils.addPauseListener(onDisappear2, disappearListener);
            this.addListener((TransitionListener)disappearListener);
        }
        return onDisappear2;
    }
    
    public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        return null;
    }
    
    public void setMode(final int mMode) {
        if ((mMode & 0xFFFFFFFC) == 0x0) {
            this.mMode = mMode;
            return;
        }
        throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
    }
    
    private static class DisappearListener extends AnimatorListenerAdapter implements TransitionListener, AnimatorPauseListenerCompat
    {
        boolean mCanceled;
        private final int mFinalVisibility;
        private boolean mLayoutSuppressed;
        private final ViewGroup mParent;
        private final boolean mSuppressLayout;
        private final View mView;
        
        DisappearListener(final View mView, final int mFinalVisibility, final boolean mSuppressLayout) {
            this.mCanceled = false;
            this.mView = mView;
            this.mFinalVisibility = mFinalVisibility;
            this.mParent = (ViewGroup)mView.getParent();
            this.mSuppressLayout = mSuppressLayout;
            this.suppressLayout(true);
        }
        
        private void hideViewWhenNotCanceled() {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
                if (this.mParent != null) {
                    this.mParent.invalidate();
                }
            }
            this.suppressLayout(false);
        }
        
        private void suppressLayout(final boolean mLayoutSuppressed) {
            if (this.mSuppressLayout && this.mLayoutSuppressed != mLayoutSuppressed && this.mParent != null) {
                this.mLayoutSuppressed = mLayoutSuppressed;
                ViewGroupUtils.suppressLayout(this.mParent, mLayoutSuppressed);
            }
        }
        
        public void onAnimationCancel(final Animator animator) {
            this.mCanceled = true;
        }
        
        public void onAnimationEnd(final Animator animator) {
            this.hideViewWhenNotCanceled();
        }
        
        public void onAnimationPause(final Animator animator) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
            }
        }
        
        public void onAnimationRepeat(final Animator animator) {
        }
        
        public void onAnimationResume(final Animator animator) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, 0);
            }
        }
        
        public void onAnimationStart(final Animator animator) {
        }
        
        public void onTransitionCancel(@NonNull final Transition transition) {
        }
        
        public void onTransitionEnd(@NonNull final Transition transition) {
            this.hideViewWhenNotCanceled();
            transition.removeListener((TransitionListener)this);
        }
        
        public void onTransitionPause(@NonNull final Transition transition) {
            this.suppressLayout(false);
        }
        
        public void onTransitionResume(@NonNull final Transition transition) {
            this.suppressLayout(true);
        }
        
        public void onTransitionStart(@NonNull final Transition transition) {
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface Mode {
    }
    
    private static class VisibilityInfo
    {
        ViewGroup mEndParent;
        int mEndVisibility;
        boolean mFadeIn;
        ViewGroup mStartParent;
        int mStartVisibility;
        boolean mVisibilityChange;
    }
}
