package android.support.graphics.drawable;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.TintAwareDrawable;
/* loaded from: classes.dex */
abstract class VectorDrawableCommon extends Drawable implements TintAwareDrawable {
    Drawable mDelegateDrawable;

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.applyTheme(this.mDelegateDrawable, theme);
    }

    @Override // android.graphics.drawable.Drawable
    public void clearColorFilter() {
        if (this.mDelegateDrawable == null) {
            super.clearColorFilter();
        } else {
            this.mDelegateDrawable.clearColorFilter();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        if (this.mDelegateDrawable == null) {
            return null;
        }
        return DrawableCompat.getColorFilter(this.mDelegateDrawable);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable getCurrent() {
        return this.mDelegateDrawable == null ? super.getCurrent() : this.mDelegateDrawable.getCurrent();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.mDelegateDrawable == null ? super.getMinimumHeight() : this.mDelegateDrawable.getMinimumHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return this.mDelegateDrawable == null ? super.getMinimumWidth() : this.mDelegateDrawable.getMinimumWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        return this.mDelegateDrawable == null ? super.getPadding(rect) : this.mDelegateDrawable.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public int[] getState() {
        return this.mDelegateDrawable == null ? super.getState() : this.mDelegateDrawable.getState();
    }

    @Override // android.graphics.drawable.Drawable
    public Region getTransparentRegion() {
        return this.mDelegateDrawable == null ? super.getTransparentRegion() : this.mDelegateDrawable.getTransparentRegion();
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.jumpToCurrentState(this.mDelegateDrawable);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        if (this.mDelegateDrawable == null) {
            super.onBoundsChange(rect);
        } else {
            this.mDelegateDrawable.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        return this.mDelegateDrawable == null ? super.onLevelChange(i) : this.mDelegateDrawable.setLevel(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setChangingConfigurations(int i) {
        if (this.mDelegateDrawable == null) {
            super.setChangingConfigurations(i);
        } else {
            this.mDelegateDrawable.setChangingConfigurations(i);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(int i, PorterDuff.Mode mode) {
        if (this.mDelegateDrawable == null) {
            super.setColorFilter(i, mode);
        } else {
            this.mDelegateDrawable.setColorFilter(i, mode);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean z) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        this.mDelegateDrawable.setFilterBitmap(z);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float f, float f2) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.setHotspot(this.mDelegateDrawable, f, f2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspotBounds(int i, int i2, int i3, int i4) {
        if (this.mDelegateDrawable == null) {
            return;
        }
        DrawableCompat.setHotspotBounds(this.mDelegateDrawable, i, i2, i3, i4);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setState(int[] iArr) {
        return this.mDelegateDrawable == null ? super.setState(iArr) : this.mDelegateDrawable.setState(iArr);
    }
}
