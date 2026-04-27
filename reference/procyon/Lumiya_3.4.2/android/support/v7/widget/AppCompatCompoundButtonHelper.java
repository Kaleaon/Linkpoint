// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.widget.CompoundButton;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;

class AppCompatCompoundButtonHelper
{
    private ColorStateList mButtonTintList;
    private PorterDuff$Mode mButtonTintMode;
    private boolean mHasButtonTint;
    private boolean mHasButtonTintMode;
    private boolean mSkipNextApply;
    private final CompoundButton mView;
    
    AppCompatCompoundButtonHelper(final CompoundButton mView) {
        this.mButtonTintList = null;
        this.mButtonTintMode = null;
        this.mHasButtonTint = false;
        this.mHasButtonTintMode = false;
        this.mView = mView;
    }
    
    void applyButtonTint() {
        final Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable != null) {
            if (this.mHasButtonTint || this.mHasButtonTintMode) {
                final Drawable mutate = DrawableCompat.wrap(buttonDrawable).mutate();
                if (this.mHasButtonTint) {
                    DrawableCompat.setTintList(mutate, this.mButtonTintList);
                }
                if (this.mHasButtonTintMode) {
                    DrawableCompat.setTintMode(mutate, this.mButtonTintMode);
                }
                if (mutate.isStateful()) {
                    mutate.setState(this.mView.getDrawableState());
                }
                this.mView.setButtonDrawable(mutate);
            }
        }
    }
    
    int getCompoundPaddingLeft(final int n) {
        int n2;
        if (Build$VERSION.SDK_INT >= 17) {
            n2 = n;
        }
        else {
            final Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
            n2 = n;
            if (buttonDrawable != null) {
                n2 = n + buttonDrawable.getIntrinsicWidth();
            }
        }
        return n2;
    }
    
    ColorStateList getSupportButtonTintList() {
        return this.mButtonTintList;
    }
    
    PorterDuff$Mode getSupportButtonTintMode() {
        return this.mButtonTintMode;
    }
    
    void loadFromAttributes(AttributeSet obtainStyledAttributes, int resourceId) {
        while (true) {
            obtainStyledAttributes = (AttributeSet)this.mView.getContext().obtainStyledAttributes(obtainStyledAttributes, R.styleable.CompoundButton, resourceId, 0);
        Label_0114:
            while (true) {
                try {
                    if (((TypedArray)obtainStyledAttributes).hasValue(R.styleable.CompoundButton_android_button)) {
                        resourceId = ((TypedArray)obtainStyledAttributes).getResourceId(R.styleable.CompoundButton_android_button, 0);
                        if (resourceId != 0) {
                            this.mView.setButtonDrawable(AppCompatResources.getDrawable(this.mView.getContext(), resourceId));
                        }
                    }
                    if (!((TypedArray)obtainStyledAttributes).hasValue(R.styleable.CompoundButton_buttonTint)) {
                        if (!((TypedArray)obtainStyledAttributes).hasValue(R.styleable.CompoundButton_buttonTintMode)) {
                            return;
                        }
                        break Label_0114;
                    }
                }
                finally {
                    ((TypedArray)obtainStyledAttributes).recycle();
                }
                CompoundButtonCompat.setButtonTintList(this.mView, ((TypedArray)obtainStyledAttributes).getColorStateList(R.styleable.CompoundButton_buttonTint));
                continue;
            }
            CompoundButtonCompat.setButtonTintMode(this.mView, DrawableUtils.parseTintMode(((TypedArray)obtainStyledAttributes).getInt(R.styleable.CompoundButton_buttonTintMode, -1), null));
        }
    }
    
    void onSetButtonDrawable() {
        if (!this.mSkipNextApply) {
            this.mSkipNextApply = true;
            this.applyButtonTint();
            return;
        }
        this.mSkipNextApply = false;
    }
    
    void setSupportButtonTintList(final ColorStateList mButtonTintList) {
        this.mButtonTintList = mButtonTintList;
        this.mHasButtonTint = true;
        this.applyButtonTint();
    }
    
    void setSupportButtonTintMode(@Nullable final PorterDuff$Mode mButtonTintMode) {
        this.mButtonTintMode = mButtonTintMode;
        this.mHasButtonTintMode = true;
        this.applyButtonTint();
    }
    
    interface DirectSetButtonDrawableInterface
    {
        void setButtonDrawable(final Drawable p0);
    }
}
