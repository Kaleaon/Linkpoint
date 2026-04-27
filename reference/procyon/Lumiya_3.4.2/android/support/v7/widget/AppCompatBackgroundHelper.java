// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.os.Build$VERSION;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.view.View;

class AppCompatBackgroundHelper
{
    private int mBackgroundResId;
    private TintInfo mBackgroundTint;
    private final AppCompatDrawableManager mDrawableManager;
    private TintInfo mInternalBackgroundTint;
    private TintInfo mTmpInfo;
    private final View mView;
    
    AppCompatBackgroundHelper(final View mView) {
        this.mBackgroundResId = -1;
        this.mView = mView;
        this.mDrawableManager = AppCompatDrawableManager.get();
    }
    
    private boolean applyFrameworkTintUsingColorFilter(@NonNull final Drawable drawable) {
        if (this.mTmpInfo == null) {
            this.mTmpInfo = new TintInfo();
        }
        final TintInfo mTmpInfo = this.mTmpInfo;
        mTmpInfo.clear();
        final ColorStateList backgroundTintList = ViewCompat.getBackgroundTintList(this.mView);
        if (backgroundTintList != null) {
            mTmpInfo.mHasTintList = true;
            mTmpInfo.mTintList = backgroundTintList;
        }
        final PorterDuff$Mode backgroundTintMode = ViewCompat.getBackgroundTintMode(this.mView);
        if (backgroundTintMode != null) {
            mTmpInfo.mHasTintMode = true;
            mTmpInfo.mTintMode = backgroundTintMode;
        }
        if (!mTmpInfo.mHasTintList && !mTmpInfo.mHasTintMode) {
            return false;
        }
        AppCompatDrawableManager.tintDrawable(drawable, mTmpInfo, this.mView.getDrawableState());
        return true;
    }
    
    private boolean shouldApplyFrameworkTintUsingColorFilter() {
        boolean b = false;
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT <= 21) {
            return sdk_INT == 21;
        }
        if (this.mInternalBackgroundTint != null) {
            b = true;
        }
        return b;
    }
    
    void applySupportBackgroundTint() {
        final Drawable background = this.mView.getBackground();
        if (background != null) {
            if (this.shouldApplyFrameworkTintUsingColorFilter() && this.applyFrameworkTintUsingColorFilter(background)) {
                return;
            }
            if (this.mBackgroundTint == null) {
                if (this.mInternalBackgroundTint != null) {
                    AppCompatDrawableManager.tintDrawable(background, this.mInternalBackgroundTint, this.mView.getDrawableState());
                }
            }
            else {
                AppCompatDrawableManager.tintDrawable(background, this.mBackgroundTint, this.mView.getDrawableState());
            }
        }
    }
    
    ColorStateList getSupportBackgroundTintList() {
        ColorStateList mTintList = null;
        if (this.mBackgroundTint != null) {
            mTintList = this.mBackgroundTint.mTintList;
        }
        return mTintList;
    }
    
    PorterDuff$Mode getSupportBackgroundTintMode() {
        PorterDuff$Mode mTintMode = null;
        if (this.mBackgroundTint != null) {
            mTintMode = this.mBackgroundTint.mTintMode;
        }
        return mTintMode;
    }
    
    void loadFromAttributes(AttributeSet obtainStyledAttributes, final int n) {
        while (true) {
            obtainStyledAttributes = (AttributeSet)TintTypedArray.obtainStyledAttributes(this.mView.getContext(), obtainStyledAttributes, R.styleable.ViewBackgroundHelper, n, 0);
        Label_0126:
            while (true) {
                try {
                    if (((TintTypedArray)obtainStyledAttributes).hasValue(R.styleable.ViewBackgroundHelper_android_background)) {
                        this.mBackgroundResId = ((TintTypedArray)obtainStyledAttributes).getResourceId(R.styleable.ViewBackgroundHelper_android_background, -1);
                        final ColorStateList tintList = this.mDrawableManager.getTintList(this.mView.getContext(), this.mBackgroundResId);
                        if (tintList != null) {
                            this.setInternalBackgroundTint(tintList);
                        }
                    }
                    if (!((TintTypedArray)obtainStyledAttributes).hasValue(R.styleable.ViewBackgroundHelper_backgroundTint)) {
                        if (!((TintTypedArray)obtainStyledAttributes).hasValue(R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
                            return;
                        }
                        break Label_0126;
                    }
                }
                finally {
                    ((TintTypedArray)obtainStyledAttributes).recycle();
                }
                ViewCompat.setBackgroundTintList(this.mView, ((TintTypedArray)obtainStyledAttributes).getColorStateList(R.styleable.ViewBackgroundHelper_backgroundTint));
                continue;
            }
            ViewCompat.setBackgroundTintMode(this.mView, DrawableUtils.parseTintMode(((TintTypedArray)obtainStyledAttributes).getInt(R.styleable.ViewBackgroundHelper_backgroundTintMode, -1), null));
        }
    }
    
    void onSetBackgroundDrawable(final Drawable drawable) {
        this.mBackgroundResId = -1;
        this.setInternalBackgroundTint(null);
        this.applySupportBackgroundTint();
    }
    
    void onSetBackgroundResource(final int mBackgroundResId) {
        ColorStateList tintList = null;
        this.mBackgroundResId = mBackgroundResId;
        if (this.mDrawableManager != null) {
            tintList = this.mDrawableManager.getTintList(this.mView.getContext(), mBackgroundResId);
        }
        this.setInternalBackgroundTint(tintList);
        this.applySupportBackgroundTint();
    }
    
    void setInternalBackgroundTint(final ColorStateList mTintList) {
        if (mTintList == null) {
            this.mInternalBackgroundTint = null;
        }
        else {
            if (this.mInternalBackgroundTint == null) {
                this.mInternalBackgroundTint = new TintInfo();
            }
            this.mInternalBackgroundTint.mTintList = mTintList;
            this.mInternalBackgroundTint.mHasTintList = true;
        }
        this.applySupportBackgroundTint();
    }
    
    void setSupportBackgroundTintList(final ColorStateList mTintList) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = mTintList;
        this.mBackgroundTint.mHasTintList = true;
        this.applySupportBackgroundTint();
    }
    
    void setSupportBackgroundTintMode(final PorterDuff$Mode mTintMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = mTintMode;
        this.mBackgroundTint.mHasTintMode = true;
        this.applySupportBackgroundTint();
    }
}
