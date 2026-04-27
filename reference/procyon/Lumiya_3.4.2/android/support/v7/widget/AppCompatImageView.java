// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.net.Uri;
import android.graphics.drawable.Icon;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff$Mode;
import android.support.annotation.RestrictTo;
import android.support.annotation.Nullable;
import android.content.res.ColorStateList;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v4.widget.TintableImageSourceView;
import android.support.v4.view.TintableBackgroundView;
import android.widget.ImageView;

public class AppCompatImageView extends ImageView implements TintableBackgroundView, TintableImageSourceView
{
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private final AppCompatImageHelper mImageHelper;
    
    public AppCompatImageView(final Context context) {
        this(context, null);
    }
    
    public AppCompatImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AppCompatImageView(final Context context, final AttributeSet set, final int n) {
        super(TintContextWrapper.wrap(context), set, n);
        (this.mBackgroundTintHelper = new AppCompatBackgroundHelper((View)this)).loadFromAttributes(set, n);
        (this.mImageHelper = new AppCompatImageHelper(this)).loadFromAttributes(set, n);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
        if (this.mImageHelper != null) {
            this.mImageHelper.applySupportImageTint();
        }
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public ColorStateList getSupportBackgroundTintList() {
        ColorStateList supportBackgroundTintList = null;
        if (this.mBackgroundTintHelper != null) {
            supportBackgroundTintList = this.mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        return supportBackgroundTintList;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public PorterDuff$Mode getSupportBackgroundTintMode() {
        PorterDuff$Mode supportBackgroundTintMode = null;
        if (this.mBackgroundTintHelper != null) {
            supportBackgroundTintMode = this.mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        return supportBackgroundTintMode;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public ColorStateList getSupportImageTintList() {
        ColorStateList supportImageTintList = null;
        if (this.mImageHelper != null) {
            supportImageTintList = this.mImageHelper.getSupportImageTintList();
        }
        return supportImageTintList;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public PorterDuff$Mode getSupportImageTintMode() {
        PorterDuff$Mode supportImageTintMode = null;
        if (this.mImageHelper != null) {
            supportImageTintMode = this.mImageHelper.getSupportImageTintMode();
        }
        return supportImageTintMode;
    }
    
    public boolean hasOverlappingRendering() {
        boolean b = false;
        if (this.mImageHelper.hasOverlappingRendering() && super.hasOverlappingRendering()) {
            b = true;
        }
        return b;
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        super.setBackgroundDrawable(backgroundDrawable);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundDrawable(backgroundDrawable);
        }
    }
    
    public void setBackgroundResource(@DrawableRes final int backgroundResource) {
        super.setBackgroundResource(backgroundResource);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(backgroundResource);
        }
    }
    
    public void setImageBitmap(final Bitmap imageBitmap) {
        super.setImageBitmap(imageBitmap);
        if (this.mImageHelper != null) {
            this.mImageHelper.applySupportImageTint();
        }
    }
    
    public void setImageDrawable(@Nullable final Drawable imageDrawable) {
        super.setImageDrawable(imageDrawable);
        if (this.mImageHelper != null) {
            this.mImageHelper.applySupportImageTint();
        }
    }
    
    public void setImageIcon(@Nullable final Icon imageIcon) {
        super.setImageIcon(imageIcon);
        if (this.mImageHelper != null) {
            this.mImageHelper.applySupportImageTint();
        }
    }
    
    public void setImageResource(@DrawableRes final int imageResource) {
        if (this.mImageHelper != null) {
            this.mImageHelper.setImageResource(imageResource);
        }
    }
    
    public void setImageURI(@Nullable final Uri imageURI) {
        super.setImageURI(imageURI);
        if (this.mImageHelper != null) {
            this.mImageHelper.applySupportImageTint();
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportBackgroundTintList(@Nullable final ColorStateList supportBackgroundTintList) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintList(supportBackgroundTintList);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportBackgroundTintMode(@Nullable final PorterDuff$Mode supportBackgroundTintMode) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintMode(supportBackgroundTintMode);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportImageTintList(@Nullable final ColorStateList supportImageTintList) {
        if (this.mImageHelper != null) {
            this.mImageHelper.setSupportImageTintList(supportImageTintList);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportImageTintMode(@Nullable final PorterDuff$Mode supportImageTintMode) {
        if (this.mImageHelper != null) {
            this.mImageHelper.setSupportImageTintMode(supportImageTintMode);
        }
    }
}
