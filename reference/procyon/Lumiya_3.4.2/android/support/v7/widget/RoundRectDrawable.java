// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.graphics.Outline;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.PorterDuff$Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.content.res.ColorStateList;
import android.support.annotation.RequiresApi;
import android.graphics.drawable.Drawable;

@RequiresApi(21)
class RoundRectDrawable extends Drawable
{
    private ColorStateList mBackground;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private boolean mInsetForPadding;
    private boolean mInsetForRadius;
    private float mPadding;
    private final Paint mPaint;
    private float mRadius;
    private ColorStateList mTint;
    private PorterDuffColorFilter mTintFilter;
    private PorterDuff$Mode mTintMode;
    
    RoundRectDrawable(final ColorStateList background, final float mRadius) {
        this.mInsetForPadding = false;
        this.mInsetForRadius = true;
        this.mTintMode = PorterDuff$Mode.SRC_IN;
        this.mRadius = mRadius;
        this.mPaint = new Paint(5);
        this.setBackground(background);
        this.mBoundsF = new RectF();
        this.mBoundsI = new Rect();
    }
    
    private PorterDuffColorFilter createTintFilter(final ColorStateList list, final PorterDuff$Mode porterDuff$Mode) {
        if (list != null && porterDuff$Mode != null) {
            return new PorterDuffColorFilter(list.getColorForState(this.getState(), 0), porterDuff$Mode);
        }
        return null;
    }
    
    private void setBackground(ColorStateList value) {
        if (value == null) {
            value = ColorStateList.valueOf(0);
        }
        this.mBackground = value;
        this.mPaint.setColor(this.mBackground.getColorForState(this.getState(), this.mBackground.getDefaultColor()));
    }
    
    private void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = this.getBounds();
        }
        this.mBoundsF.set((float)bounds.left, (float)bounds.top, (float)bounds.right, (float)bounds.bottom);
        this.mBoundsI.set(bounds);
        if (this.mInsetForPadding) {
            this.mBoundsI.inset((int)Math.ceil(RoundRectDrawableWithShadow.calculateHorizontalPadding(this.mPadding, this.mRadius, this.mInsetForRadius)), (int)Math.ceil(RoundRectDrawableWithShadow.calculateVerticalPadding(this.mPadding, this.mRadius, this.mInsetForRadius)));
            this.mBoundsF.set(this.mBoundsI);
        }
    }
    
    public void draw(final Canvas canvas) {
        int n = 0;
        final Paint mPaint = this.mPaint;
        if (this.mTintFilter != null && mPaint.getColorFilter() == null) {
            mPaint.setColorFilter((ColorFilter)this.mTintFilter);
            n = 1;
        }
        canvas.drawRoundRect(this.mBoundsF, this.mRadius, this.mRadius, mPaint);
        if (n != 0) {
            mPaint.setColorFilter((ColorFilter)null);
        }
    }
    
    public ColorStateList getColor() {
        return this.mBackground;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public void getOutline(final Outline outline) {
        outline.setRoundRect(this.mBoundsI, this.mRadius);
    }
    
    float getPadding() {
        return this.mPadding;
    }
    
    public float getRadius() {
        return this.mRadius;
    }
    
    public boolean isStateful() {
        boolean b = false;
        if (this.mTint != null && this.mTint.isStateful()) {
            return true;
        }
        if (this.mBackground != null && this.mBackground.isStateful()) {
            return true;
        }
        if (super.isStateful()) {
            return true;
        }
        return b;
        b = true;
        return b;
    }
    
    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        this.updateBounds(rect);
    }
    
    protected boolean onStateChange(final int[] array) {
        boolean b = false;
        final int colorForState = this.mBackground.getColorForState(array, this.mBackground.getDefaultColor());
        if (colorForState != this.mPaint.getColor()) {
            b = true;
        }
        if (b) {
            this.mPaint.setColor(colorForState);
        }
        if (this.mTint != null && this.mTintMode != null) {
            this.mTintFilter = this.createTintFilter(this.mTint, this.mTintMode);
            return true;
        }
        return b;
    }
    
    public void setAlpha(final int alpha) {
        this.mPaint.setAlpha(alpha);
    }
    
    public void setColor(@Nullable final ColorStateList background) {
        this.setBackground(background);
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }
    
    void setPadding(final float mPadding, final boolean mInsetForPadding, final boolean mInsetForRadius) {
        if (mPadding == this.mPadding && this.mInsetForPadding == mInsetForPadding && this.mInsetForRadius == mInsetForRadius) {
            return;
        }
        this.mPadding = mPadding;
        this.mInsetForPadding = mInsetForPadding;
        this.mInsetForRadius = mInsetForRadius;
        this.updateBounds(null);
        this.invalidateSelf();
    }
    
    void setRadius(final float mRadius) {
        if (mRadius == this.mRadius) {
            return;
        }
        this.mRadius = mRadius;
        this.updateBounds(null);
        this.invalidateSelf();
    }
    
    public void setTintList(final ColorStateList mTint) {
        this.mTint = mTint;
        this.mTintFilter = this.createTintFilter(this.mTint, this.mTintMode);
        this.invalidateSelf();
    }
    
    public void setTintMode(final PorterDuff$Mode mTintMode) {
        this.mTintMode = mTintMode;
        this.mTintFilter = this.createTintFilter(this.mTint, this.mTintMode);
        this.invalidateSelf();
    }
}
