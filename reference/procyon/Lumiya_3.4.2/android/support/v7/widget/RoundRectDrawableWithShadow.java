// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.ColorFilter;
import android.support.annotation.Nullable;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Path$FillType;
import android.graphics.Rect;
import android.graphics.Paint$Style;
import android.support.v7.cardview.R;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

class RoundRectDrawableWithShadow extends Drawable
{
    private static final double COS_45;
    private static final float SHADOW_MULTIPLIER = 1.5f;
    static RoundRectHelper sRoundRectHelper;
    private boolean mAddPaddingForCorners;
    private ColorStateList mBackground;
    private final RectF mCardBounds;
    private float mCornerRadius;
    private Paint mCornerShadowPaint;
    private Path mCornerShadowPath;
    private boolean mDirty;
    private Paint mEdgeShadowPaint;
    private final int mInsetShadow;
    private Paint mPaint;
    private boolean mPrintedShadowClipWarning;
    private float mRawMaxShadowSize;
    private float mRawShadowSize;
    private final int mShadowEndColor;
    private float mShadowSize;
    private final int mShadowStartColor;
    
    static {
        COS_45 = Math.cos(Math.toRadians(45.0));
    }
    
    RoundRectDrawableWithShadow(final Resources resources, final ColorStateList background, final float n, final float n2, final float n3) {
        this.mDirty = true;
        this.mAddPaddingForCorners = true;
        this.mPrintedShadowClipWarning = false;
        this.mShadowStartColor = resources.getColor(R.color.cardview_shadow_start_color);
        this.mShadowEndColor = resources.getColor(R.color.cardview_shadow_end_color);
        this.mInsetShadow = resources.getDimensionPixelSize(R.dimen.cardview_compat_inset_shadow);
        this.mPaint = new Paint(5);
        this.setBackground(background);
        (this.mCornerShadowPaint = new Paint(5)).setStyle(Paint$Style.FILL);
        this.mCornerRadius = (float)(int)(0.5f + n);
        this.mCardBounds = new RectF();
        (this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint)).setAntiAlias(false);
        this.setShadowSize(n2, n3);
    }
    
    private void buildComponents(final Rect rect) {
        final float n = this.mRawMaxShadowSize * 1.5f;
        this.mCardBounds.set(rect.left + this.mRawMaxShadowSize, rect.top + n, rect.right - this.mRawMaxShadowSize, rect.bottom - n);
        this.buildShadowCorners();
    }
    
    private void buildShadowCorners() {
        final RectF rectF = new RectF(-this.mCornerRadius, -this.mCornerRadius, this.mCornerRadius, this.mCornerRadius);
        final RectF rectF2 = new RectF(rectF);
        rectF2.inset(-this.mShadowSize, -this.mShadowSize);
        if (this.mCornerShadowPath != null) {
            this.mCornerShadowPath.reset();
        }
        else {
            this.mCornerShadowPath = new Path();
        }
        this.mCornerShadowPath.setFillType(Path$FillType.EVEN_ODD);
        this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0f);
        this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
        this.mCornerShadowPath.arcTo(rectF2, 180.0f, 90.0f, false);
        this.mCornerShadowPath.arcTo(rectF, 270.0f, -90.0f, false);
        this.mCornerShadowPath.close();
        this.mCornerShadowPaint.setShader((Shader)new RadialGradient(0.0f, 0.0f, this.mCornerRadius + this.mShadowSize, new int[] { this.mShadowStartColor, this.mShadowStartColor, this.mShadowEndColor }, new float[] { 0.0f, this.mCornerRadius / (this.mCornerRadius + this.mShadowSize), 1.0f }, Shader$TileMode.CLAMP));
        this.mEdgeShadowPaint.setShader((Shader)new LinearGradient(0.0f, -this.mCornerRadius + this.mShadowSize, 0.0f, -this.mCornerRadius - this.mShadowSize, new int[] { this.mShadowStartColor, this.mShadowStartColor, this.mShadowEndColor }, new float[] { 0.0f, 0.5f, 1.0f }, Shader$TileMode.CLAMP));
        this.mEdgeShadowPaint.setAntiAlias(false);
    }
    
    static float calculateHorizontalPadding(final float n, final float n2, final boolean b) {
        if (!b) {
            return n;
        }
        return (float)(n + (1.0 - RoundRectDrawableWithShadow.COS_45) * n2);
    }
    
    static float calculateVerticalPadding(final float n, final float n2, final boolean b) {
        if (!b) {
            return 1.5f * n;
        }
        return (float)(1.5f * n + (1.0 - RoundRectDrawableWithShadow.COS_45) * n2);
    }
    
    private void drawShadow(final Canvas canvas) {
        final float n = -this.mCornerRadius - this.mShadowSize;
        final float n2 = this.mCornerRadius + this.mInsetShadow + this.mRawShadowSize / 2.0f;
        boolean b;
        if (this.mCardBounds.width() - 2.0f * n2 > 0.0f) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (this.mCardBounds.height() - 2.0f * n2 > 0.0f) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        final int save = canvas.save();
        canvas.translate(this.mCardBounds.left + n2, this.mCardBounds.top + n2);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b) {
            canvas.drawRect(0.0f, n, this.mCardBounds.width() - 2.0f * n2, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save);
        final int save2 = canvas.save();
        canvas.translate(this.mCardBounds.right - n2, this.mCardBounds.bottom - n2);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b) {
            canvas.drawRect(0.0f, n, this.mCardBounds.width() - 2.0f * n2, this.mShadowSize + -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save2);
        final int save3 = canvas.save();
        canvas.translate(this.mCardBounds.left + n2, this.mCardBounds.bottom - n2);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b2) {
            canvas.drawRect(0.0f, n, this.mCardBounds.height() - 2.0f * n2, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save3);
        final int save4 = canvas.save();
        canvas.translate(this.mCardBounds.right - n2, this.mCardBounds.top + n2);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b2) {
            canvas.drawRect(0.0f, n, this.mCardBounds.height() - 2.0f * n2, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save4);
    }
    
    private void setBackground(ColorStateList value) {
        if (value == null) {
            value = ColorStateList.valueOf(0);
        }
        this.mBackground = value;
        this.mPaint.setColor(this.mBackground.getColorForState(this.getState(), this.mBackground.getDefaultColor()));
    }
    
    private void setShadowSize(float n, float n2) {
        if (n < 0.0f) {
            throw new IllegalArgumentException("Invalid shadow size " + n + ". Must be >= 0");
        }
        if (n2 < 0.0f) {
            throw new IllegalArgumentException("Invalid max shadow size " + n2 + ". Must be >= 0");
        }
        final float n3 = (float)this.toEven(n);
        n2 = (float)this.toEven(n2);
        n = n3;
        if (n3 > n2) {
            if (this.mPrintedShadowClipWarning) {
                n = n2;
            }
            else {
                this.mPrintedShadowClipWarning = true;
                n = n2;
            }
        }
        if (this.mRawShadowSize == n && this.mRawMaxShadowSize == n2) {
            return;
        }
        this.mRawShadowSize = n;
        this.mRawMaxShadowSize = n2;
        this.mShadowSize = (float)(int)(n * 1.5f + this.mInsetShadow + 0.5f);
        this.mDirty = true;
        this.invalidateSelf();
    }
    
    private int toEven(final float n) {
        final int n2 = (int)(0.5f + n);
        if (n2 % 2 != 1) {
            return n2;
        }
        return n2 - 1;
    }
    
    public void draw(final Canvas canvas) {
        if (this.mDirty) {
            this.buildComponents(this.getBounds());
            this.mDirty = false;
        }
        canvas.translate(0.0f, this.mRawShadowSize / 2.0f);
        this.drawShadow(canvas);
        canvas.translate(0.0f, -this.mRawShadowSize / 2.0f);
        RoundRectDrawableWithShadow.sRoundRectHelper.drawRoundRect(canvas, this.mCardBounds, this.mCornerRadius, this.mPaint);
    }
    
    ColorStateList getColor() {
        return this.mBackground;
    }
    
    float getCornerRadius() {
        return this.mCornerRadius;
    }
    
    void getMaxShadowAndCornerPadding(final Rect rect) {
        this.getPadding(rect);
    }
    
    float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }
    
    float getMinHeight() {
        return Math.max(this.mRawMaxShadowSize, this.mCornerRadius + this.mInsetShadow + this.mRawMaxShadowSize * 1.5f / 2.0f) * 2.0f + (this.mRawMaxShadowSize * 1.5f + this.mInsetShadow) * 2.0f;
    }
    
    float getMinWidth() {
        return Math.max(this.mRawMaxShadowSize, this.mCornerRadius + this.mInsetShadow + this.mRawMaxShadowSize / 2.0f) * 2.0f + (this.mRawMaxShadowSize + this.mInsetShadow) * 2.0f;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public boolean getPadding(final Rect rect) {
        final int n = (int)Math.ceil(calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        final int n2 = (int)Math.ceil(calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        rect.set(n2, n, n2, n);
        return true;
    }
    
    float getShadowSize() {
        return this.mRawShadowSize;
    }
    
    public boolean isStateful() {
        boolean b = false;
        if ((this.mBackground != null && this.mBackground.isStateful()) || super.isStateful()) {
            b = true;
        }
        return b;
    }
    
    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        this.mDirty = true;
    }
    
    protected boolean onStateChange(final int[] array) {
        final int colorForState = this.mBackground.getColorForState(array, this.mBackground.getDefaultColor());
        if (this.mPaint.getColor() != colorForState) {
            this.mPaint.setColor(colorForState);
            this.mDirty = true;
            this.invalidateSelf();
            return true;
        }
        return false;
    }
    
    void setAddPaddingForCorners(final boolean mAddPaddingForCorners) {
        this.mAddPaddingForCorners = mAddPaddingForCorners;
        this.invalidateSelf();
    }
    
    public void setAlpha(final int alpha) {
        this.mPaint.setAlpha(alpha);
        this.mCornerShadowPaint.setAlpha(alpha);
        this.mEdgeShadowPaint.setAlpha(alpha);
    }
    
    void setColor(@Nullable final ColorStateList background) {
        this.setBackground(background);
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }
    
    void setCornerRadius(float n) {
        if (n < 0.0f) {
            throw new IllegalArgumentException("Invalid radius " + n + ". Must be >= 0");
        }
        n = (float)(int)(0.5f + n);
        if (this.mCornerRadius == n) {
            return;
        }
        this.mCornerRadius = n;
        this.mDirty = true;
        this.invalidateSelf();
    }
    
    void setMaxShadowSize(final float n) {
        this.setShadowSize(this.mRawShadowSize, n);
    }
    
    void setShadowSize(final float n) {
        this.setShadowSize(n, this.mRawMaxShadowSize);
    }
    
    interface RoundRectHelper
    {
        void drawRoundRect(final Canvas p0, final RectF p1, final float p2, final Paint p3);
    }
}
