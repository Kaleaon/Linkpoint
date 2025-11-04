// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import android.animation.TimeInterpolator;
import android.view.View;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.StateListAnimator;
import android.os.Build$VERSION;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class FloatingActionButtonLollipop extends FloatingActionButtonImpl
{
    private InsetDrawable mInsetDrawable;
    
    FloatingActionButtonLollipop(final VisibilityAwareImageButton visibilityAwareImageButton, final ShadowViewDelegate shadowViewDelegate) {
        super(visibilityAwareImageButton, shadowViewDelegate);
    }
    
    public float getElevation() {
        return this.mView.getElevation();
    }
    
    @Override
    void getPadding(final Rect rect) {
        if (!this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            rect.set(0, 0, 0, 0);
        }
        else {
            final float radius = this.mShadowViewDelegate.getRadius();
            final float n = this.getElevation() + this.mPressedTranslationZ;
            final int n2 = (int)Math.ceil(ShadowDrawableWrapper.calculateHorizontalPadding(n, radius, false));
            final int n3 = (int)Math.ceil(ShadowDrawableWrapper.calculateVerticalPadding(n, radius, false));
            rect.set(n2, n3, n2, n3);
        }
    }
    
    @Override
    void jumpDrawableToCurrentState() {
    }
    
    @Override
    CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }
    
    @Override
    GradientDrawable newGradientDrawableForShape() {
        return new AlwaysStatefulGradientDrawable();
    }
    
    @Override
    void onCompatShadowChanged() {
        this.updatePadding();
    }
    
    @Override
    void onDrawableStateChanged(final int[] array) {
    }
    
    @Override
    void onElevationsChanged(final float elevation, final float translationZ) {
        if (Build$VERSION.SDK_INT != 21) {
            final StateListAnimator stateListAnimator = new StateListAnimator();
            final AnimatorSet set = new AnimatorSet();
            set.play((Animator)ObjectAnimator.ofFloat((Object)this.mView, "elevation", new float[] { elevation }).setDuration(0L)).with((Animator)ObjectAnimator.ofFloat((Object)this.mView, View.TRANSLATION_Z, new float[] { translationZ }).setDuration(100L));
            set.setInterpolator((TimeInterpolator)FloatingActionButtonLollipop.ANIM_INTERPOLATOR);
            stateListAnimator.addState(FloatingActionButtonLollipop.PRESSED_ENABLED_STATE_SET, (Animator)set);
            final AnimatorSet set2 = new AnimatorSet();
            set2.play((Animator)ObjectAnimator.ofFloat((Object)this.mView, "elevation", new float[] { elevation }).setDuration(0L)).with((Animator)ObjectAnimator.ofFloat((Object)this.mView, View.TRANSLATION_Z, new float[] { translationZ }).setDuration(100L));
            set2.setInterpolator((TimeInterpolator)FloatingActionButtonLollipop.ANIM_INTERPOLATOR);
            stateListAnimator.addState(FloatingActionButtonLollipop.FOCUSED_ENABLED_STATE_SET, (Animator)set2);
            final AnimatorSet set3 = new AnimatorSet();
            final ArrayList list = new ArrayList();
            list.add(ObjectAnimator.ofFloat((Object)this.mView, "elevation", new float[] { elevation }).setDuration(0L));
            if (Build$VERSION.SDK_INT >= 22 && Build$VERSION.SDK_INT <= 24) {
                list.add(ObjectAnimator.ofFloat((Object)this.mView, View.TRANSLATION_Z, new float[] { this.mView.getTranslationZ() }).setDuration(100L));
            }
            list.add(ObjectAnimator.ofFloat((Object)this.mView, View.TRANSLATION_Z, new float[] { 0.0f }).setDuration(100L));
            set3.playSequentially((Animator[])list.toArray(new ObjectAnimator[0]));
            set3.setInterpolator((TimeInterpolator)FloatingActionButtonLollipop.ANIM_INTERPOLATOR);
            stateListAnimator.addState(FloatingActionButtonLollipop.ENABLED_STATE_SET, (Animator)set3);
            final AnimatorSet set4 = new AnimatorSet();
            set4.play((Animator)ObjectAnimator.ofFloat((Object)this.mView, "elevation", new float[] { 0.0f }).setDuration(0L)).with((Animator)ObjectAnimator.ofFloat((Object)this.mView, View.TRANSLATION_Z, new float[] { 0.0f }).setDuration(0L));
            set4.setInterpolator((TimeInterpolator)FloatingActionButtonLollipop.ANIM_INTERPOLATOR);
            stateListAnimator.addState(FloatingActionButtonLollipop.EMPTY_STATE_SET, (Animator)set4);
            this.mView.setStateListAnimator(stateListAnimator);
        }
        else if (!this.mView.isEnabled()) {
            this.mView.setElevation(0.0f);
            this.mView.setTranslationZ(0.0f);
        }
        else {
            this.mView.setElevation(elevation);
            if (!this.mView.isFocused() && !this.mView.isPressed()) {
                this.mView.setTranslationZ(0.0f);
            }
            else {
                this.mView.setTranslationZ(translationZ);
            }
        }
        if (this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            this.updatePadding();
        }
    }
    
    @Override
    void onPaddingUpdated(final Rect rect) {
        if (!this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
        }
        else {
            this.mInsetDrawable = new InsetDrawable(this.mRippleDrawable, rect.left, rect.top, rect.right, rect.bottom);
            this.mShadowViewDelegate.setBackgroundDrawable((Drawable)this.mInsetDrawable);
        }
    }
    
    @Override
    boolean requirePreDrawListener() {
        return false;
    }
    
    @Override
    void setBackgroundDrawable(final ColorStateList list, final PorterDuff$Mode porterDuff$Mode, final int n, final int n2) {
        DrawableCompat.setTintList(this.mShapeDrawable = DrawableCompat.wrap((Drawable)this.createShapeDrawable()), list);
        if (porterDuff$Mode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, porterDuff$Mode);
        }
        Object mShapeDrawable;
        if (n2 <= 0) {
            this.mBorderDrawable = null;
            mShapeDrawable = this.mShapeDrawable;
        }
        else {
            this.mBorderDrawable = this.createBorderDrawable(n2, list);
            mShapeDrawable = new LayerDrawable(new Drawable[] { this.mBorderDrawable, this.mShapeDrawable });
        }
        this.mRippleDrawable = (Drawable)new RippleDrawable(ColorStateList.valueOf(n), (Drawable)mShapeDrawable, (Drawable)null);
        this.mContentBackground = this.mRippleDrawable;
        this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
    }
    
    @Override
    void setRippleColor(final int rippleColor) {
        if (!(this.mRippleDrawable instanceof RippleDrawable)) {
            super.setRippleColor(rippleColor);
        }
        else {
            ((RippleDrawable)this.mRippleDrawable).setColor(ColorStateList.valueOf(rippleColor));
        }
    }
    
    static class AlwaysStatefulGradientDrawable extends GradientDrawable
    {
        public boolean isStateful() {
            return true;
        }
    }
}
