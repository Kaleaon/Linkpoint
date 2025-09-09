package android.support.graphics.drawable;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.TintAwareDrawable;

abstract class VectorDrawableCommon extends Drawable implements TintAwareDrawable {
    Drawable mDelegateDrawable;

    VectorDrawableCommon() {
    }

    public void applyTheme(Resources.Theme theme) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.applyTheme(this.mDelegateDrawable, theme);
        }
    }

    public void clearColorFilter() {
        if (this.mDelegateDrawable == null) {
            super.clearColorFilter();
        } else {
            this.mDelegateDrawable.clearColorFilter();
        }
    }

    public ColorFilter getColorFilter() {
        if (this.mDelegateDrawable == null) {
            return null;
        }
        return DrawableCompat.getColorFilter(this.mDelegateDrawable);
    }

    public Drawable getCurrent() {
        return this.mDelegateDrawable == null ? super.getCurrent() : this.mDelegateDrawable.getCurrent();
    }

    public int getMinimumHeight() {
        return this.mDelegateDrawable == null ? super.getMinimumHeight() : this.mDelegateDrawable.getMinimumHeight();
    }

    public int getMinimumWidth() {
        return this.mDelegateDrawable == null ? super.getMinimumWidth() : this.mDelegateDrawable.getMinimumWidth();
    }

    public boolean getPadding(Rect rect) {
        return this.mDelegateDrawable == null ? super.getPadding(rect) : this.mDelegateDrawable.getPadding(rect);
    }

    public int[] getState() {
        return this.mDelegateDrawable == null ? super.getState() : this.mDelegateDrawable.getState();
    }

    public Region getTransparentRegion() {
        return this.mDelegateDrawable == null ? super.getTransparentRegion() : this.mDelegateDrawable.getTransparentRegion();
    }

    public void jumpToCurrentState() {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.jumpToCurrentState(this.mDelegateDrawable);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        if (this.mDelegateDrawable == null) {
            super.onBoundsChange(rect);
        } else {
            this.mDelegateDrawable.setBounds(rect);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int i) {
        return this.mDelegateDrawable == null ? super.onLevelChange(i) : this.mDelegateDrawable.setLevel(i);
    }

    public void setChangingConfigurations(int i) {
        if (this.mDelegateDrawable == null) {
            super.setChangingConfigurations(i);
        } else {
            this.mDelegateDrawable.setChangingConfigurations(i);
        }
    }

    public void setColorFilter(int i, PorterDuff.Mode mode) {
        if (this.mDelegateDrawable == null) {
            super.setColorFilter(i, mode);
        } else {
            this.mDelegateDrawable.setColorFilter(i, mode);
        }
    }

    public void setFilterBitmap(boolean z) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setFilterBitmap(z);
        }
    }

    public void setHotspot(float f, float f2) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setHotspot(this.mDelegateDrawable, f, f2);
        }
    }

    public void setHotspotBounds(int i, int i2, int i3, int i4) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setHotspotBounds(this.mDelegateDrawable, i, i2, i3, i4);
        }
    }

    public boolean setState(int[] iArr) {
        return this.mDelegateDrawable == null ? super.setState(iArr) : this.mDelegateDrawable.setState(iArr);
    }
}
