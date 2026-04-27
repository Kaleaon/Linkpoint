// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.v7.content.res.AppCompatResources;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.graphics.drawable.RippleDrawable;
import android.os.Build$VERSION;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.support.v4.widget.ImageViewCompat;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class AppCompatImageHelper
{
    private TintInfo mImageTint;
    private TintInfo mInternalImageTint;
    private TintInfo mTmpInfo;
    private final ImageView mView;
    
    public AppCompatImageHelper(final ImageView mView) {
        this.mView = mView;
    }
    
    private boolean applyFrameworkTintUsingColorFilter(@NonNull final Drawable drawable) {
        if (this.mTmpInfo == null) {
            this.mTmpInfo = new TintInfo();
        }
        final TintInfo mTmpInfo = this.mTmpInfo;
        mTmpInfo.clear();
        final ColorStateList imageTintList = ImageViewCompat.getImageTintList(this.mView);
        if (imageTintList != null) {
            mTmpInfo.mHasTintList = true;
            mTmpInfo.mTintList = imageTintList;
        }
        final PorterDuff$Mode imageTintMode = ImageViewCompat.getImageTintMode(this.mView);
        if (imageTintMode != null) {
            mTmpInfo.mHasTintMode = true;
            mTmpInfo.mTintMode = imageTintMode;
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
        if (this.mInternalImageTint != null) {
            b = true;
        }
        return b;
    }
    
    void applySupportImageTint() {
        final Drawable drawable = this.mView.getDrawable();
        if (drawable != null) {
            DrawableUtils.fixDrawable(drawable);
        }
        if (drawable != null) {
            if (this.shouldApplyFrameworkTintUsingColorFilter() && this.applyFrameworkTintUsingColorFilter(drawable)) {
                return;
            }
            if (this.mImageTint == null) {
                if (this.mInternalImageTint != null) {
                    AppCompatDrawableManager.tintDrawable(drawable, this.mInternalImageTint, this.mView.getDrawableState());
                }
            }
            else {
                AppCompatDrawableManager.tintDrawable(drawable, this.mImageTint, this.mView.getDrawableState());
            }
        }
    }
    
    ColorStateList getSupportImageTintList() {
        ColorStateList mTintList = null;
        if (this.mImageTint != null) {
            mTintList = this.mImageTint.mTintList;
        }
        return mTintList;
    }
    
    PorterDuff$Mode getSupportImageTintMode() {
        PorterDuff$Mode mTintMode = null;
        if (this.mImageTint != null) {
            mTintMode = this.mImageTint.mTintMode;
        }
        return mTintMode;
    }
    
    boolean hasOverlappingRendering() {
        final Drawable background = this.mView.getBackground();
        return Build$VERSION.SDK_INT < 21 || !(background instanceof RippleDrawable);
    }
    
    public void loadFromAttributes(final AttributeSet set, int resourceId) {
    Label_0043_Outer:
        while (true) {
            final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), set, R.styleable.AppCompatImageView, resourceId, 0);
        Label_0143:
            while (true) {
            Label_0126:
                while (true) {
                    try {
                        Drawable drawable = this.mView.getDrawable();
                        if (drawable == null) {
                            resourceId = obtainStyledAttributes.getResourceId(R.styleable.AppCompatImageView_srcCompat, -1);
                            if (resourceId != -1) {
                                final Drawable drawable2 = AppCompatResources.getDrawable(this.mView.getContext(), resourceId);
                                if ((drawable = drawable2) != null) {
                                    this.mView.setImageDrawable(drawable2);
                                    drawable = drawable2;
                                }
                            }
                        }
                        if (drawable == null) {
                            if (obtainStyledAttributes.hasValue(R.styleable.AppCompatImageView_tint)) {
                                break Label_0126;
                            }
                            if (!obtainStyledAttributes.hasValue(R.styleable.AppCompatImageView_tintMode)) {
                                return;
                            }
                            break Label_0143;
                        }
                    }
                    finally {
                        obtainStyledAttributes.recycle();
                    }
                    final Drawable drawable3;
                    DrawableUtils.fixDrawable(drawable3);
                    continue Label_0043_Outer;
                }
                ImageViewCompat.setImageTintList(this.mView, obtainStyledAttributes.getColorStateList(R.styleable.AppCompatImageView_tint));
                continue;
            }
            ImageViewCompat.setImageTintMode(this.mView, DrawableUtils.parseTintMode(obtainStyledAttributes.getInt(R.styleable.AppCompatImageView_tintMode, -1), null));
        }
    }
    
    public void setImageResource(final int n) {
        if (n == 0) {
            this.mView.setImageDrawable((Drawable)null);
        }
        else {
            final Drawable drawable = AppCompatResources.getDrawable(this.mView.getContext(), n);
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
            this.mView.setImageDrawable(drawable);
        }
        this.applySupportImageTint();
    }
    
    void setInternalImageTint(final ColorStateList mTintList) {
        if (mTintList == null) {
            this.mInternalImageTint = null;
        }
        else {
            if (this.mInternalImageTint == null) {
                this.mInternalImageTint = new TintInfo();
            }
            this.mInternalImageTint.mTintList = mTintList;
            this.mInternalImageTint.mHasTintList = true;
        }
        this.applySupportImageTint();
    }
    
    void setSupportImageTintList(final ColorStateList mTintList) {
        if (this.mImageTint == null) {
            this.mImageTint = new TintInfo();
        }
        this.mImageTint.mTintList = mTintList;
        this.mImageTint.mHasTintList = true;
        this.applySupportImageTint();
    }
    
    void setSupportImageTintMode(final PorterDuff$Mode mTintMode) {
        if (this.mImageTint == null) {
            this.mImageTint = new TintInfo();
        }
        this.mImageTint.mTintMode = mTintMode;
        this.mImageTint.mHasTintMode = true;
        this.applySupportImageTint();
    }
}
