// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Path$FillType;
import android.graphics.Rect;
import android.graphics.Paint$Style;
import android.support.v4.content.ContextCompat;
import android.support.design.R;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.graphics.drawable.DrawableWrapper;

class ShadowDrawableWrapper extends DrawableWrapper
{
    static final double COS_45;
    static final float SHADOW_BOTTOM_SCALE = 1.0f;
    static final float SHADOW_HORIZ_SCALE = 0.5f;
    static final float SHADOW_MULTIPLIER = 1.5f;
    static final float SHADOW_TOP_SCALE = 0.25f;
    private boolean mAddPaddingForCorners;
    final RectF mContentBounds;
    float mCornerRadius;
    final Paint mCornerShadowPaint;
    Path mCornerShadowPath;
    private boolean mDirty;
    final Paint mEdgeShadowPaint;
    float mMaxShadowSize;
    private boolean mPrintedShadowClipWarning;
    float mRawMaxShadowSize;
    float mRawShadowSize;
    private float mRotation;
    private final int mShadowEndColor;
    private final int mShadowMiddleColor;
    float mShadowSize;
    private final int mShadowStartColor;
    
    static {
        COS_45 = Math.cos(Math.toRadians(45.0));
    }
    
    public ShadowDrawableWrapper(final Context context, final Drawable drawable, final float a, final float n, final float n2) {
        super(drawable);
        this.mDirty = true;
        this.mAddPaddingForCorners = true;
        this.mPrintedShadowClipWarning = false;
        this.mShadowStartColor = ContextCompat.getColor(context, R.color.design_fab_shadow_start_color);
        this.mShadowMiddleColor = ContextCompat.getColor(context, R.color.design_fab_shadow_mid_color);
        this.mShadowEndColor = ContextCompat.getColor(context, R.color.design_fab_shadow_end_color);
        (this.mCornerShadowPaint = new Paint(5)).setStyle(Paint$Style.FILL);
        this.mCornerRadius = (float)Math.round(a);
        this.mContentBounds = new RectF();
        (this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint)).setAntiAlias(false);
        this.setShadowSize(n, n2);
    }
    
    private void buildComponents(final Rect rect) {
        final float n = this.mRawMaxShadowSize * 1.5f;
        this.mContentBounds.set(rect.left + this.mRawMaxShadowSize, rect.top + n, rect.right - this.mRawMaxShadowSize, rect.bottom - n);
        this.getWrappedDrawable().setBounds((int)this.mContentBounds.left, (int)this.mContentBounds.top, (int)this.mContentBounds.right, (int)this.mContentBounds.bottom);
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
        final float n = -rectF2.top;
        if (n > 0.0f) {
            final float n2 = this.mCornerRadius / n;
            this.mCornerShadowPaint.setShader((Shader)new RadialGradient(0.0f, 0.0f, n, new int[] { 0, this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor }, new float[] { 0.0f, n2, n2 + (1.0f - n2) / 2.0f, 1.0f }, Shader$TileMode.CLAMP));
        }
        this.mEdgeShadowPaint.setShader((Shader)new LinearGradient(0.0f, rectF.top, 0.0f, rectF2.top, new int[] { this.mShadowStartColor, this.mShadowMiddleColor, this.mShadowEndColor }, new float[] { 0.0f, 0.5f, 1.0f }, Shader$TileMode.CLAMP));
        this.mEdgeShadowPaint.setAntiAlias(false);
    }
    
    public static float calculateHorizontalPadding(final float n, final float n2, final boolean b) {
        if (!b) {
            return n;
        }
        return (float)(n + (1.0 - ShadowDrawableWrapper.COS_45) * n2);
    }
    
    public static float calculateVerticalPadding(final float n, final float n2, final boolean b) {
        if (!b) {
            return 1.5f * n;
        }
        return (float)(1.5f * n + (1.0 - ShadowDrawableWrapper.COS_45) * n2);
    }
    
    private void drawShadow(final Canvas canvas) {
        final int save = canvas.save();
        canvas.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
        final float n = -this.mCornerRadius - this.mShadowSize;
        final float mCornerRadius = this.mCornerRadius;
        boolean b;
        if (this.mContentBounds.width() - 2.0f * mCornerRadius > 0.0f) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (this.mContentBounds.height() - 2.0f * mCornerRadius > 0.0f) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        final float mRawShadowSize = this.mRawShadowSize;
        final float mRawShadowSize2 = this.mRawShadowSize;
        final float mRawShadowSize3 = this.mRawShadowSize;
        final float mRawShadowSize4 = this.mRawShadowSize;
        final float mRawShadowSize5 = this.mRawShadowSize;
        final float mRawShadowSize6 = this.mRawShadowSize;
        final float n2 = mCornerRadius / (mRawShadowSize3 - mRawShadowSize4 * 0.5f + mCornerRadius);
        final float n3 = mCornerRadius / (mRawShadowSize - mRawShadowSize2 * 0.25f + mCornerRadius);
        final float n4 = mCornerRadius / (mCornerRadius + (mRawShadowSize5 - mRawShadowSize6 * 1.0f));
        final int save2 = canvas.save();
        canvas.translate(this.mContentBounds.left + mCornerRadius, this.mContentBounds.top + mCornerRadius);
        canvas.scale(n2, n3);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b) {
            canvas.scale(1.0f / n2, 1.0f);
            canvas.drawRect(0.0f, n, this.mContentBounds.width() - 2.0f * mCornerRadius, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save2);
        final int save3 = canvas.save();
        canvas.translate(this.mContentBounds.right - mCornerRadius, this.mContentBounds.bottom - mCornerRadius);
        canvas.scale(n2, n4);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b) {
            canvas.scale(1.0f / n2, 1.0f);
            canvas.drawRect(0.0f, n, this.mContentBounds.width() - 2.0f * mCornerRadius, this.mShadowSize + -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save3);
        final int save4 = canvas.save();
        canvas.translate(this.mContentBounds.left + mCornerRadius, this.mContentBounds.bottom - mCornerRadius);
        canvas.scale(n2, n4);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b2) {
            canvas.scale(1.0f / n4, 1.0f);
            canvas.drawRect(0.0f, n, this.mContentBounds.height() - 2.0f * mCornerRadius, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save4);
        final int save5 = canvas.save();
        canvas.translate(this.mContentBounds.right - mCornerRadius, this.mContentBounds.top + mCornerRadius);
        canvas.scale(n2, n3);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (b2) {
            canvas.scale(1.0f / n3, 1.0f);
            canvas.drawRect(0.0f, n, this.mContentBounds.height() - 2.0f * mCornerRadius, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save5);
        canvas.restoreToCount(save);
    }
    
    private static int toEven(final float a) {
        int round = Math.round(a);
        if (round % 2 == 1) {
            --round;
        }
        return round;
    }
    
    @Override
    public void draw(final Canvas canvas) {
        if (this.mDirty) {
            this.buildComponents(this.getBounds());
            this.mDirty = false;
        }
        this.drawShadow(canvas);
        super.draw(canvas);
    }
    
    public float getCornerRadius() {
        return this.mCornerRadius;
    }
    
    public float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }
    
    public float getMinHeight() {
        return Math.max(this.mRawMaxShadowSize, this.mCornerRadius + this.mRawMaxShadowSize * 1.5f / 2.0f) * 2.0f + this.mRawMaxShadowSize * 1.5f * 2.0f;
    }
    
    public float getMinWidth() {
        return Math.max(this.mRawMaxShadowSize, this.mCornerRadius + this.mRawMaxShadowSize / 2.0f) * 2.0f + this.mRawMaxShadowSize * 2.0f;
    }
    
    @Override
    public int getOpacity() {
        return -3;
    }
    
    @Override
    public boolean getPadding(final Rect rect) {
        final int n = (int)Math.ceil(calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        final int n2 = (int)Math.ceil(calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        rect.set(n2, n, n2, n);
        return true;
    }
    
    public float getShadowSize() {
        return this.mRawShadowSize;
    }
    
    @Override
    protected void onBoundsChange(final Rect rect) {
        this.mDirty = true;
    }
    
    public void setAddPaddingForCorners(final boolean mAddPaddingForCorners) {
        this.mAddPaddingForCorners = mAddPaddingForCorners;
        this.invalidateSelf();
    }
    
    @Override
    public void setAlpha(final int alpha) {
        super.setAlpha(alpha);
        this.mCornerShadowPaint.setAlpha(alpha);
        this.mEdgeShadowPaint.setAlpha(alpha);
    }
    
    public void setCornerRadius(float n) {
        n = (float)Math.round(n);
        if (this.mCornerRadius == n) {
            return;
        }
        this.mCornerRadius = n;
        this.mDirty = true;
        this.invalidateSelf();
    }
    
    public void setMaxShadowSize(final float n) {
        this.setShadowSize(this.mRawShadowSize, n);
    }
    
    final void setRotation(final float mRotation) {
        if (this.mRotation != mRotation) {
            this.mRotation = mRotation;
            this.invalidateSelf();
        }
    }
    
    public void setShadowSize(final float n) {
        this.setShadowSize(n, this.mRawMaxShadowSize);
    }
    
    void setShadowSize(float mRawShadowSize, float n) {
        boolean b = false;
        if (mRawShadowSize < 0.0f) {
            b = true;
        }
        if (b || n < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        final float n2 = (float)toEven(mRawShadowSize);
        n = (float)toEven(n);
        mRawShadowSize = n2;
        if (n2 > n) {
            if (this.mPrintedShadowClipWarning) {
                mRawShadowSize = n;
            }
            else {
                this.mPrintedShadowClipWarning = true;
                mRawShadowSize = n;
            }
        }
        if (this.mRawShadowSize == mRawShadowSize && this.mRawMaxShadowSize == n) {
            return;
        }
        this.mRawShadowSize = mRawShadowSize;
        this.mRawMaxShadowSize = n;
        this.mShadowSize = (float)Math.round(mRawShadowSize * 1.5f);
        this.mMaxShadowSize = n;
        this.mDirty = true;
        this.invalidateSelf();
    }
}
