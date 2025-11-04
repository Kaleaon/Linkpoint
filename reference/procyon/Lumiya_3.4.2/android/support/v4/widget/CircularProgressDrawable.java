// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.graphics.Path$FillType;
import android.graphics.Paint$Style;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.graphics.ColorFilter;
import android.graphics.Paint$Cap;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.support.v4.util.Preconditions;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.LinearInterpolator;
import android.content.res.Resources;
import android.animation.Animator;
import android.view.animation.Interpolator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

public class CircularProgressDrawable extends Drawable implements Animatable
{
    private static final int ANIMATION_DURATION = 1332;
    private static final int ARROW_HEIGHT = 5;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final float CENTER_RADIUS = 7.5f;
    private static final float CENTER_RADIUS_LARGE = 11.0f;
    private static final int[] COLORS;
    private static final float COLOR_CHANGE_OFFSET = 0.75f;
    public static final int DEFAULT = 1;
    private static final float GROUP_FULL_ROTATION = 216.0f;
    public static final int LARGE = 0;
    private static final Interpolator LINEAR_INTERPOLATOR;
    private static final Interpolator MATERIAL_INTERPOLATOR;
    private static final float MAX_PROGRESS_ARC = 0.8f;
    private static final float MIN_PROGRESS_ARC = 0.01f;
    private static final float RING_ROTATION = 0.20999998f;
    private static final float SHRINK_OFFSET = 0.5f;
    private static final float STROKE_WIDTH = 2.5f;
    private static final float STROKE_WIDTH_LARGE = 3.0f;
    private Animator mAnimator;
    private boolean mFinishing;
    private Resources mResources;
    private final Ring mRing;
    private float mRotation;
    private float mRotationCount;
    
    static {
        LINEAR_INTERPOLATOR = (Interpolator)new LinearInterpolator();
        MATERIAL_INTERPOLATOR = (Interpolator)new FastOutSlowInInterpolator();
        COLORS = new int[] { -16777216 };
    }
    
    public CircularProgressDrawable(final Context context) {
        this.mResources = Preconditions.checkNotNull(context).getResources();
        (this.mRing = new Ring()).setColors(CircularProgressDrawable.COLORS);
        this.setStrokeWidth(2.5f);
        this.setupAnimators();
    }
    
    private void applyFinishTranslation(final float n, final Ring ring) {
        this.updateRingColor(n, ring);
        final float n2 = (float)(Math.floor(ring.getStartingRotation() / 0.8f) + 1.0);
        ring.setStartTrim(ring.getStartingStartTrim() + (ring.getStartingEndTrim() - 0.01f - ring.getStartingStartTrim()) * n);
        ring.setEndTrim(ring.getStartingEndTrim());
        ring.setRotation((n2 - ring.getStartingRotation()) * n + ring.getStartingRotation());
    }
    
    private void applyTransformation(final float n, final Ring ring, final boolean b) {
        if (!this.mFinishing) {
            if (n != 1.0f || b) {
                final float startingRotation = ring.getStartingRotation();
                float startingStartTrim;
                float endTrim;
                if (n < 0.5f) {
                    final float n2 = n / 0.5f;
                    startingStartTrim = ring.getStartingStartTrim();
                    endTrim = CircularProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(n2) * 0.79f + 0.01f + startingStartTrim;
                }
                else {
                    final float n3 = (n - 0.5f) / 0.5f;
                    endTrim = ring.getStartingStartTrim() + 0.79f;
                    startingStartTrim = endTrim - ((1.0f - CircularProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(n3)) * 0.79f + 0.01f);
                }
                final float mRotationCount = this.mRotationCount;
                ring.setStartTrim(startingStartTrim);
                ring.setEndTrim(endTrim);
                ring.setRotation(startingRotation + 0.20999998f * n);
                this.setRotation((mRotationCount + n) * 216.0f);
            }
        }
        else {
            this.applyFinishTranslation(n, ring);
        }
    }
    
    private int evaluateColorChange(final float n, int n2, final int n3) {
        final int n4 = n2 >> 24 & 0xFF;
        final int n5 = n2 >> 16 & 0xFF;
        final int n6 = n2 >> 8 & 0xFF;
        n2 &= 0xFF;
        return n4 + (int)(((n3 >> 24 & 0xFF) - n4) * n) << 24 | n5 + (int)(((n3 >> 16 & 0xFF) - n5) * n) << 16 | (int)(((n3 >> 8 & 0xFF) - n6) * n) + n6 << 8 | (int)(((n3 & 0xFF) - n2) * n) + n2;
    }
    
    private float getRotation() {
        return this.mRotation;
    }
    
    private void setRotation(final float mRotation) {
        this.mRotation = mRotation;
    }
    
    private void setSizeParameters(final float n, final float n2, final float n3, final float n4) {
        final Ring mRing = this.mRing;
        final float density = this.mResources.getDisplayMetrics().density;
        mRing.setStrokeWidth(n2 * density);
        mRing.setCenterRadius(n * density);
        mRing.setColorIndex(0);
        mRing.setArrowDimensions(n3 * density, density * n4);
    }
    
    private void setupAnimators() {
        final Ring mRing = this.mRing;
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final float floatValue = (float)valueAnimator.getAnimatedValue();
                CircularProgressDrawable.this.updateRingColor(floatValue, mRing);
                CircularProgressDrawable.this.applyTransformation(floatValue, mRing, false);
                CircularProgressDrawable.this.invalidateSelf();
            }
        });
        ofFloat.setRepeatCount(-1);
        ofFloat.setRepeatMode(1);
        ofFloat.setInterpolator((TimeInterpolator)CircularProgressDrawable.LINEAR_INTERPOLATOR);
        ofFloat.addListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
            public void onAnimationCancel(final Animator animator) {
            }
            
            public void onAnimationEnd(final Animator animator) {
            }
            
            public void onAnimationRepeat(final Animator animator) {
                CircularProgressDrawable.this.applyTransformation(1.0f, mRing, true);
                mRing.storeOriginals();
                mRing.goToNextColor();
                if (!CircularProgressDrawable.this.mFinishing) {
                    ++CircularProgressDrawable.this.mRotationCount;
                }
                else {
                    CircularProgressDrawable.this.mFinishing = false;
                    animator.cancel();
                    animator.setDuration(1332L);
                    animator.start();
                    mRing.setShowArrow(false);
                }
            }
            
            public void onAnimationStart(final Animator animator) {
                CircularProgressDrawable.this.mRotationCount = 0.0f;
            }
        });
        this.mAnimator = (Animator)ofFloat;
    }
    
    private void updateRingColor(final float n, final Ring ring) {
        if (n > 0.75f) {
            ring.setColor(this.evaluateColorChange((n - 0.75f) / 0.25f, ring.getStartingColor(), ring.getNextColor()));
        }
        else {
            ring.setColor(ring.getStartingColor());
        }
    }
    
    public void draw(final Canvas canvas) {
        final Rect bounds = this.getBounds();
        canvas.save();
        canvas.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(canvas, bounds);
        canvas.restore();
    }
    
    public int getAlpha() {
        return this.mRing.getAlpha();
    }
    
    public boolean getArrowEnabled() {
        return this.mRing.getShowArrow();
    }
    
    public float getArrowHeight() {
        return this.mRing.getArrowHeight();
    }
    
    public float getArrowScale() {
        return this.mRing.getArrowScale();
    }
    
    public float getArrowWidth() {
        return this.mRing.getArrowWidth();
    }
    
    public int getBackgroundColor() {
        return this.mRing.getBackgroundColor();
    }
    
    public float getCenterRadius() {
        return this.mRing.getCenterRadius();
    }
    
    public int[] getColorSchemeColors() {
        return this.mRing.getColors();
    }
    
    public float getEndTrim() {
        return this.mRing.getEndTrim();
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public float getProgressRotation() {
        return this.mRing.getRotation();
    }
    
    public float getStartTrim() {
        return this.mRing.getStartTrim();
    }
    
    public Paint$Cap getStrokeCap() {
        return this.mRing.getStrokeCap();
    }
    
    public float getStrokeWidth() {
        return this.mRing.getStrokeWidth();
    }
    
    public boolean isRunning() {
        return this.mAnimator.isRunning();
    }
    
    public void setAlpha(final int alpha) {
        this.mRing.setAlpha(alpha);
        this.invalidateSelf();
    }
    
    public void setArrowDimensions(final float n, final float n2) {
        this.mRing.setArrowDimensions(n, n2);
        this.invalidateSelf();
    }
    
    public void setArrowEnabled(final boolean showArrow) {
        this.mRing.setShowArrow(showArrow);
        this.invalidateSelf();
    }
    
    public void setArrowScale(final float arrowScale) {
        this.mRing.setArrowScale(arrowScale);
        this.invalidateSelf();
    }
    
    public void setBackgroundColor(final int backgroundColor) {
        this.mRing.setBackgroundColor(backgroundColor);
        this.invalidateSelf();
    }
    
    public void setCenterRadius(final float centerRadius) {
        this.mRing.setCenterRadius(centerRadius);
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
        this.invalidateSelf();
    }
    
    public void setColorSchemeColors(final int... colors) {
        this.mRing.setColors(colors);
        this.mRing.setColorIndex(0);
        this.invalidateSelf();
    }
    
    public void setProgressRotation(final float rotation) {
        this.mRing.setRotation(rotation);
        this.invalidateSelf();
    }
    
    public void setStartEndTrim(final float startTrim, final float endTrim) {
        this.mRing.setStartTrim(startTrim);
        this.mRing.setEndTrim(endTrim);
        this.invalidateSelf();
    }
    
    public void setStrokeCap(final Paint$Cap strokeCap) {
        this.mRing.setStrokeCap(strokeCap);
        this.invalidateSelf();
    }
    
    public void setStrokeWidth(final float strokeWidth) {
        this.mRing.setStrokeWidth(strokeWidth);
        this.invalidateSelf();
    }
    
    public void setStyle(final int n) {
        if (n != 0) {
            this.setSizeParameters(7.5f, 2.5f, 10.0f, 5.0f);
        }
        else {
            this.setSizeParameters(11.0f, 3.0f, 12.0f, 6.0f);
        }
        this.invalidateSelf();
    }
    
    public void start() {
        this.mAnimator.cancel();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mFinishing = true;
            this.mAnimator.setDuration(666L);
            this.mAnimator.start();
        }
        else {
            this.mRing.setColorIndex(0);
            this.mRing.resetOriginals();
            this.mAnimator.setDuration(1332L);
            this.mAnimator.start();
        }
    }
    
    public void stop() {
        this.mAnimator.cancel();
        this.setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
        this.invalidateSelf();
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface ProgressDrawableSize {
    }
    
    private static class Ring
    {
        int mAlpha;
        Path mArrow;
        int mArrowHeight;
        final Paint mArrowPaint;
        float mArrowScale;
        int mArrowWidth;
        final Paint mCirclePaint;
        int mColorIndex;
        int[] mColors;
        int mCurrentColor;
        float mEndTrim;
        final Paint mPaint;
        float mRingCenterRadius;
        float mRotation;
        boolean mShowArrow;
        float mStartTrim;
        float mStartingEndTrim;
        float mStartingRotation;
        float mStartingStartTrim;
        float mStrokeWidth;
        final RectF mTempBounds;
        
        Ring() {
            this.mTempBounds = new RectF();
            this.mPaint = new Paint();
            this.mArrowPaint = new Paint();
            this.mCirclePaint = new Paint();
            this.mStartTrim = 0.0f;
            this.mEndTrim = 0.0f;
            this.mRotation = 0.0f;
            this.mStrokeWidth = 5.0f;
            this.mArrowScale = 1.0f;
            this.mAlpha = 255;
            this.mPaint.setStrokeCap(Paint$Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint$Style.STROKE);
            this.mArrowPaint.setStyle(Paint$Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
            this.mCirclePaint.setColor(0);
        }
        
        void draw(final Canvas canvas, final Rect rect) {
            final RectF mTempBounds = this.mTempBounds;
            float n = this.mRingCenterRadius + this.mStrokeWidth / 2.0f;
            if (this.mRingCenterRadius <= 0.0f) {
                n = Math.min(rect.width(), rect.height()) / 2.0f - Math.max(this.mArrowWidth * this.mArrowScale / 2.0f, this.mStrokeWidth / 2.0f);
            }
            mTempBounds.set(rect.centerX() - n, rect.centerY() - n, rect.centerX() + n, n + rect.centerY());
            final float n2 = (this.mStartTrim + this.mRotation) * 360.0f;
            final float n3 = (this.mEndTrim + this.mRotation) * 360.0f - n2;
            this.mPaint.setColor(this.mCurrentColor);
            this.mPaint.setAlpha(this.mAlpha);
            final float n4 = this.mStrokeWidth / 2.0f;
            mTempBounds.inset(n4, n4);
            canvas.drawCircle(mTempBounds.centerX(), mTempBounds.centerY(), mTempBounds.width() / 2.0f, this.mCirclePaint);
            mTempBounds.inset(-n4, -n4);
            canvas.drawArc(mTempBounds, n2, n3, false, this.mPaint);
            this.drawTriangle(canvas, n2, n3, mTempBounds);
        }
        
        void drawTriangle(final Canvas canvas, final float n, final float n2, final RectF rectF) {
            if (this.mShowArrow) {
                if (this.mArrow != null) {
                    this.mArrow.reset();
                }
                else {
                    (this.mArrow = new Path()).setFillType(Path$FillType.EVEN_ODD);
                }
                final float n3 = Math.min(rectF.width(), rectF.height()) / 2.0f;
                final float n4 = this.mArrowWidth * this.mArrowScale / 2.0f;
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(this.mArrowWidth * this.mArrowScale, 0.0f);
                this.mArrow.lineTo(this.mArrowWidth * this.mArrowScale / 2.0f, this.mArrowHeight * this.mArrowScale);
                this.mArrow.offset(n3 + rectF.centerX() - n4, rectF.centerY() + this.mStrokeWidth / 2.0f);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mCurrentColor);
                this.mArrowPaint.setAlpha(this.mAlpha);
                canvas.save();
                canvas.rotate(n + n2, rectF.centerX(), rectF.centerY());
                canvas.drawPath(this.mArrow, this.mArrowPaint);
                canvas.restore();
            }
        }
        
        int getAlpha() {
            return this.mAlpha;
        }
        
        float getArrowHeight() {
            return (float)this.mArrowHeight;
        }
        
        float getArrowScale() {
            return this.mArrowScale;
        }
        
        float getArrowWidth() {
            return (float)this.mArrowWidth;
        }
        
        int getBackgroundColor() {
            return this.mCirclePaint.getColor();
        }
        
        float getCenterRadius() {
            return this.mRingCenterRadius;
        }
        
        int[] getColors() {
            return this.mColors;
        }
        
        float getEndTrim() {
            return this.mEndTrim;
        }
        
        int getNextColor() {
            return this.mColors[this.getNextColorIndex()];
        }
        
        int getNextColorIndex() {
            return (this.mColorIndex + 1) % this.mColors.length;
        }
        
        float getRotation() {
            return this.mRotation;
        }
        
        boolean getShowArrow() {
            return this.mShowArrow;
        }
        
        float getStartTrim() {
            return this.mStartTrim;
        }
        
        int getStartingColor() {
            return this.mColors[this.mColorIndex];
        }
        
        float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }
        
        float getStartingRotation() {
            return this.mStartingRotation;
        }
        
        float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }
        
        Paint$Cap getStrokeCap() {
            return this.mPaint.getStrokeCap();
        }
        
        float getStrokeWidth() {
            return this.mStrokeWidth;
        }
        
        void goToNextColor() {
            this.setColorIndex(this.getNextColorIndex());
        }
        
        void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.setStartTrim(this.mStartingRotation = 0.0f);
            this.setEndTrim(0.0f);
            this.setRotation(0.0f);
        }
        
        void setAlpha(final int mAlpha) {
            this.mAlpha = mAlpha;
        }
        
        void setArrowDimensions(final float n, final float n2) {
            this.mArrowWidth = (int)n;
            this.mArrowHeight = (int)n2;
        }
        
        void setArrowScale(final float mArrowScale) {
            if (mArrowScale != this.mArrowScale) {
                this.mArrowScale = mArrowScale;
            }
        }
        
        void setBackgroundColor(final int color) {
            this.mCirclePaint.setColor(color);
        }
        
        void setCenterRadius(final float mRingCenterRadius) {
            this.mRingCenterRadius = mRingCenterRadius;
        }
        
        void setColor(final int mCurrentColor) {
            this.mCurrentColor = mCurrentColor;
        }
        
        void setColorFilter(final ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }
        
        void setColorIndex(final int mColorIndex) {
            this.mColorIndex = mColorIndex;
            this.mCurrentColor = this.mColors[this.mColorIndex];
        }
        
        void setColors(@NonNull final int[] mColors) {
            this.mColors = mColors;
            this.setColorIndex(0);
        }
        
        void setEndTrim(final float mEndTrim) {
            this.mEndTrim = mEndTrim;
        }
        
        void setRotation(final float mRotation) {
            this.mRotation = mRotation;
        }
        
        void setShowArrow(final boolean mShowArrow) {
            if (this.mShowArrow != mShowArrow) {
                this.mShowArrow = mShowArrow;
            }
        }
        
        void setStartTrim(final float mStartTrim) {
            this.mStartTrim = mStartTrim;
        }
        
        void setStrokeCap(final Paint$Cap strokeCap) {
            this.mPaint.setStrokeCap(strokeCap);
        }
        
        void setStrokeWidth(final float n) {
            this.mStrokeWidth = n;
            this.mPaint.setStrokeWidth(n);
        }
        
        void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }
    }
}
