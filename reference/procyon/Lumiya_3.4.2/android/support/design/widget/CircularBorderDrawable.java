// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader$TileMode;
import android.support.v4.graphics.ColorUtils;
import android.graphics.Shader;
import android.graphics.Paint$Style;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Paint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

class CircularBorderDrawable extends Drawable
{
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;
    private ColorStateList mBorderTint;
    float mBorderWidth;
    private int mBottomInnerStrokeColor;
    private int mBottomOuterStrokeColor;
    private int mCurrentBorderTintColor;
    private boolean mInvalidateShader;
    final Paint mPaint;
    final Rect mRect;
    final RectF mRectF;
    private float mRotation;
    private int mTopInnerStrokeColor;
    private int mTopOuterStrokeColor;
    
    public CircularBorderDrawable() {
        this.mRect = new Rect();
        this.mRectF = new RectF();
        this.mInvalidateShader = true;
        (this.mPaint = new Paint(1)).setStyle(Paint$Style.STROKE);
    }
    
    private Shader createGradientShader() {
        final Rect mRect = this.mRect;
        this.copyBounds(mRect);
        final float n = this.mBorderWidth / mRect.height();
        return (Shader)new LinearGradient(0.0f, (float)mRect.top, 0.0f, (float)mRect.bottom, new int[] { ColorUtils.compositeColors(this.mTopOuterStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mTopInnerStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mTopInnerStrokeColor, 0), this.mCurrentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mBottomInnerStrokeColor, 0), this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mBottomInnerStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mBottomOuterStrokeColor, this.mCurrentBorderTintColor) }, new float[] { 0.0f, n, 0.5f, 0.5f, 1.0f - n, 1.0f }, Shader$TileMode.CLAMP);
    }
    
    public void draw(final Canvas canvas) {
        if (this.mInvalidateShader) {
            this.mPaint.setShader(this.createGradientShader());
            this.mInvalidateShader = false;
        }
        final float n = this.mPaint.getStrokeWidth() / 2.0f;
        final RectF mRectF = this.mRectF;
        this.copyBounds(this.mRect);
        mRectF.set(this.mRect);
        mRectF.left += n;
        mRectF.top += n;
        mRectF.right -= n;
        mRectF.bottom -= n;
        canvas.save();
        canvas.rotate(this.mRotation, mRectF.centerX(), mRectF.centerY());
        canvas.drawOval(mRectF, this.mPaint);
        canvas.restore();
    }
    
    public int getOpacity() {
        int n;
        if (this.mBorderWidth > 0.0f) {
            n = -3;
        }
        else {
            n = -2;
        }
        return n;
    }
    
    public boolean getPadding(final Rect rect) {
        final int round = Math.round(this.mBorderWidth);
        rect.set(round, round, round, round);
        return true;
    }
    
    public boolean isStateful() {
        boolean b = false;
        if ((this.mBorderTint != null && this.mBorderTint.isStateful()) || super.isStateful()) {
            b = true;
        }
        return b;
    }
    
    protected void onBoundsChange(final Rect rect) {
        this.mInvalidateShader = true;
    }
    
    protected boolean onStateChange(final int[] array) {
        if (this.mBorderTint != null) {
            final int colorForState = this.mBorderTint.getColorForState(array, this.mCurrentBorderTintColor);
            if (colorForState != this.mCurrentBorderTintColor) {
                this.mInvalidateShader = true;
                this.mCurrentBorderTintColor = colorForState;
            }
        }
        if (this.mInvalidateShader) {
            this.invalidateSelf();
        }
        return this.mInvalidateShader;
    }
    
    public void setAlpha(final int alpha) {
        this.mPaint.setAlpha(alpha);
        this.invalidateSelf();
    }
    
    void setBorderTint(final ColorStateList mBorderTint) {
        if (mBorderTint != null) {
            this.mCurrentBorderTintColor = mBorderTint.getColorForState(this.getState(), this.mCurrentBorderTintColor);
        }
        this.mBorderTint = mBorderTint;
        this.mInvalidateShader = true;
        this.invalidateSelf();
    }
    
    void setBorderWidth(final float mBorderWidth) {
        if (this.mBorderWidth != mBorderWidth) {
            this.mBorderWidth = mBorderWidth;
            this.mPaint.setStrokeWidth(1.3333f * mBorderWidth);
            this.mInvalidateShader = true;
            this.invalidateSelf();
        }
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        this.invalidateSelf();
    }
    
    void setGradientColors(final int mTopOuterStrokeColor, final int mTopInnerStrokeColor, final int mBottomOuterStrokeColor, final int mBottomInnerStrokeColor) {
        this.mTopOuterStrokeColor = mTopOuterStrokeColor;
        this.mTopInnerStrokeColor = mTopInnerStrokeColor;
        this.mBottomOuterStrokeColor = mBottomOuterStrokeColor;
        this.mBottomInnerStrokeColor = mBottomInnerStrokeColor;
    }
    
    final void setRotation(final float mRotation) {
        if (mRotation != this.mRotation) {
            this.mRotation = mRotation;
            this.invalidateSelf();
        }
    }
}
