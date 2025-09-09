package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import java.util.ArrayList;

@RequiresApi(21)
class FloatingActionButtonLollipop extends FloatingActionButtonImpl {
    private InsetDrawable mInsetDrawable;

    static class AlwaysStatefulGradientDrawable extends GradientDrawable {
        AlwaysStatefulGradientDrawable() {
        }

        public boolean isStateful() {
            return true;
        }
    }

    FloatingActionButtonLollipop(VisibilityAwareImageButton visibilityAwareImageButton, ShadowViewDelegate shadowViewDelegate) {
        super(visibilityAwareImageButton, shadowViewDelegate);
    }

    public float getElevation() {
        return this.mView.getElevation();
    }

    /* access modifiers changed from: package-private */
    public void getPadding(Rect rect) {
        if (!this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            rect.set(0, 0, 0, 0);
            return;
        }
        float radius = this.mShadowViewDelegate.getRadius();
        float elevation = getElevation() + this.mPressedTranslationZ;
        int ceil = (int) Math.ceil((double) ShadowDrawableWrapper.calculateHorizontalPadding(elevation, radius, false));
        int ceil2 = (int) Math.ceil((double) ShadowDrawableWrapper.calculateVerticalPadding(elevation, radius, false));
        rect.set(ceil, ceil2, ceil, ceil2);
    }

    /* access modifiers changed from: package-private */
    public void jumpDrawableToCurrentState() {
    }

    /* access modifiers changed from: package-private */
    public CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }

    /* access modifiers changed from: package-private */
    public GradientDrawable newGradientDrawableForShape() {
        return new AlwaysStatefulGradientDrawable();
    }

    /* access modifiers changed from: package-private */
    public void onCompatShadowChanged() {
        updatePadding();
    }

    /* access modifiers changed from: package-private */
    public void onDrawableStateChanged(int[] iArr) {
    }

    /* access modifiers changed from: package-private */
    public void onElevationsChanged(float f, float f2) {
        if (Build.VERSION.SDK_INT != 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ObjectAnimator.ofFloat(this.mView, "elevation", new float[]{f}).setDuration(0)).with(ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{f2}).setDuration(100));
            animatorSet.setInterpolator(ANIM_INTERPOLATOR);
            stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, animatorSet);
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.play(ObjectAnimator.ofFloat(this.mView, "elevation", new float[]{f}).setDuration(0)).with(ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{f2}).setDuration(100));
            animatorSet2.setInterpolator(ANIM_INTERPOLATOR);
            stateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, animatorSet2);
            AnimatorSet animatorSet3 = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.mView, "elevation", new float[]{f}).setDuration(0));
            if (Build.VERSION.SDK_INT >= 22 && Build.VERSION.SDK_INT <= 24) {
                arrayList.add(ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{this.mView.getTranslationZ()}).setDuration(100));
            }
            arrayList.add(ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{0.0f}).setDuration(100));
            animatorSet3.playSequentially((Animator[]) arrayList.toArray(new ObjectAnimator[0]));
            animatorSet3.setInterpolator(ANIM_INTERPOLATOR);
            stateListAnimator.addState(ENABLED_STATE_SET, animatorSet3);
            AnimatorSet animatorSet4 = new AnimatorSet();
            animatorSet4.play(ObjectAnimator.ofFloat(this.mView, "elevation", new float[]{0.0f}).setDuration(0)).with(ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{0.0f}).setDuration(0));
            animatorSet4.setInterpolator(ANIM_INTERPOLATOR);
            stateListAnimator.addState(EMPTY_STATE_SET, animatorSet4);
            this.mView.setStateListAnimator(stateListAnimator);
        } else if (!this.mView.isEnabled()) {
            this.mView.setElevation(0.0f);
            this.mView.setTranslationZ(0.0f);
        } else {
            this.mView.setElevation(f);
            if (!this.mView.isFocused() && !this.mView.isPressed()) {
                this.mView.setTranslationZ(0.0f);
            } else {
                this.mView.setTranslationZ(f2);
            }
        }
        if (this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            updatePadding();
        }
    }

    /* access modifiers changed from: package-private */
    public void onPaddingUpdated(Rect rect) {
        if (!this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
            return;
        }
        this.mInsetDrawable = new InsetDrawable(this.mRippleDrawable, rect.left, rect.top, rect.right, rect.bottom);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mInsetDrawable);
    }

    /* access modifiers changed from: package-private */
    public boolean requirePreDrawListener() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundDrawable(ColorStateList colorStateList, PorterDuff.Mode mode, int i, int i2) {
        Drawable layerDrawable;
        this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        if (mode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, mode);
        }
        if (i2 <= 0) {
            this.mBorderDrawable = null;
            layerDrawable = this.mShapeDrawable;
        } else {
            this.mBorderDrawable = createBorderDrawable(i2, colorStateList);
            layerDrawable = new LayerDrawable(new Drawable[]{this.mBorderDrawable, this.mShapeDrawable});
        }
        this.mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(i), layerDrawable, (Drawable) null);
        this.mContentBackground = this.mRippleDrawable;
        this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
    }

    /* access modifiers changed from: package-private */
    public void setRippleColor(int i) {
        if (!(this.mRippleDrawable instanceof RippleDrawable)) {
            super.setRippleColor(i);
        } else {
            ((RippleDrawable) this.mRippleDrawable).setColor(ColorStateList.valueOf(i));
        }
    }
}
