// 
// Decompiled by Procyon v0.6.0
// 

package android.support.graphics.drawable;

import android.graphics.PorterDuff$Mode;
import android.graphics.Region;
import android.graphics.Rect;
import android.graphics.ColorFilter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.content.res.Resources$Theme;
import android.support.v4.graphics.drawable.TintAwareDrawable;
import android.graphics.drawable.Drawable;

abstract class VectorDrawableCommon extends Drawable implements TintAwareDrawable
{
    Drawable mDelegateDrawable;
    
    public void applyTheme(final Resources$Theme resources$Theme) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.applyTheme(this.mDelegateDrawable, resources$Theme);
    }
    
    public void clearColorFilter() {
        if (this.mDelegateDrawable == null) {
            super.clearColorFilter();
            return;
        }
        this.mDelegateDrawable.clearColorFilter();
    }
    
    public ColorFilter getColorFilter() {
        if (this.mDelegateDrawable == null) {
            return null;
        }
        return DrawableCompat.getColorFilter(this.mDelegateDrawable);
    }
    
    public Drawable getCurrent() {
        if (this.mDelegateDrawable == null) {
            return super.getCurrent();
        }
        return this.mDelegateDrawable.getCurrent();
    }
    
    public int getMinimumHeight() {
        if (this.mDelegateDrawable == null) {
            return super.getMinimumHeight();
        }
        return this.mDelegateDrawable.getMinimumHeight();
    }
    
    public int getMinimumWidth() {
        if (this.mDelegateDrawable == null) {
            return super.getMinimumWidth();
        }
        return this.mDelegateDrawable.getMinimumWidth();
    }
    
    public boolean getPadding(final Rect rect) {
        if (this.mDelegateDrawable == null) {
            return super.getPadding(rect);
        }
        return this.mDelegateDrawable.getPadding(rect);
    }
    
    public int[] getState() {
        if (this.mDelegateDrawable == null) {
            return super.getState();
        }
        return this.mDelegateDrawable.getState();
    }
    
    public Region getTransparentRegion() {
        if (this.mDelegateDrawable == null) {
            return super.getTransparentRegion();
        }
        return this.mDelegateDrawable.getTransparentRegion();
    }
    
    public void jumpToCurrentState() {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.jumpToCurrentState(this.mDelegateDrawable);
    }
    
    protected void onBoundsChange(final Rect bounds) {
        if (this.mDelegateDrawable == null) {
            super.onBoundsChange(bounds);
            return;
        }
        this.mDelegateDrawable.setBounds(bounds);
    }
    
    protected boolean onLevelChange(final int level) {
        if (this.mDelegateDrawable == null) {
            return super.onLevelChange(level);
        }
        return this.mDelegateDrawable.setLevel(level);
    }
    
    public void setChangingConfigurations(final int n) {
        if (this.mDelegateDrawable == null) {
            super.setChangingConfigurations(n);
            return;
        }
        this.mDelegateDrawable.setChangingConfigurations(n);
    }
    
    public void setColorFilter(final int n, final PorterDuff$Mode porterDuff$Mode) {
        if (this.mDelegateDrawable == null) {
            super.setColorFilter(n, porterDuff$Mode);
            return;
        }
        this.mDelegateDrawable.setColorFilter(n, porterDuff$Mode);
    }
    
    public void setFilterBitmap(final boolean filterBitmap) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        this.mDelegateDrawable.setFilterBitmap(filterBitmap);
    }
    
    public void setHotspot(final float n, final float n2) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setHotspot(this.mDelegateDrawable, n, n2);
        }
    }
    
    public void setHotspotBounds(final int n, final int n2, final int n3, final int n4) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.setHotspotBounds(this.mDelegateDrawable, n, n2, n3, n4);
    }
    
    public boolean setState(final int[] array) {
        if (this.mDelegateDrawable == null) {
            return super.setState(array);
        }
        return this.mDelegateDrawable.setState(array);
    }
}
