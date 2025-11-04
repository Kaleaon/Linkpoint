// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.res.TypedArray;
import android.view.animation.Interpolator;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable$Orientation;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.lumiyaviewer.lumiya.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Paint;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.view.View;

@TargetApi(14)
public class ButteryProgressBar extends View
{
    private static final int BASE_DURATION_MS = 500;
    private static final int BASE_SEGMENT_COUNT = 5;
    private static final int BASE_WIDTH_DP = 300;
    private static final int DEFAULT_BAR_HEIGHT_DP = 4;
    private static final int DEFAULT_DETENT_WIDTH_DP = 3;
    private final ValueAnimator mAnimator;
    private final int mBarColor;
    private final float mDensity;
    private final Paint mPaint;
    private int mSegmentCount;
    private final GradientDrawable mShadow;
    private final int mSolidBarDetentWidth;
    private final int mSolidBarHeight;
    
    public ButteryProgressBar(final Context context) {
        this(context, null);
    }
    
    public ButteryProgressBar(final Context context, AttributeSet obtainStyledAttributes) {
        super(context, obtainStyledAttributes);
        this.mPaint = new Paint();
        this.mDensity = context.getResources().getDisplayMetrics().density;
        obtainStyledAttributes = (AttributeSet)context.obtainStyledAttributes(obtainStyledAttributes, R.styleable.ButteryProgressBar);
        try {
            this.mBarColor = ((TypedArray)obtainStyledAttributes).getColor(0, context.getResources().getColor(17170450));
            this.mSolidBarHeight = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(1, Math.round(this.mDensity * 4.0f));
            this.mSolidBarDetentWidth = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(2, Math.round(this.mDensity * 3.0f));
            ((TypedArray)obtainStyledAttributes).recycle();
            (this.mAnimator = new ValueAnimator()).setFloatValues(new float[] { 1.0f, 2.0f });
            this.mAnimator.setRepeatCount(-1);
            this.mAnimator.setInterpolator((TimeInterpolator)new ExponentialInterpolator(null));
            this.mAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    ButteryProgressBar.this.invalidate();
                }
            });
            this.mPaint.setColor(this.mBarColor);
            this.mShadow = new GradientDrawable(GradientDrawable$Orientation.TOP_BOTTOM, new int[] { (this.mBarColor & 0xFFFFFF) | 0x22000000, 0 });
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private void start() {
        if (this.mAnimator == null) {
            return;
        }
        this.mAnimator.start();
    }
    
    private void stop() {
        if (this.mAnimator == null) {
            return;
        }
        this.mAnimator.cancel();
    }
    
    protected void onDraw(final Canvas canvas) {
        if (!this.mAnimator.isStarted()) {
            return;
        }
        this.mShadow.draw(canvas);
        final float floatValue = (float)this.mAnimator.getAnimatedValue();
        final int width = this.getWidth();
        final int n = width >> this.mSegmentCount - 1;
        for (int i = 0; i < this.mSegmentCount; ++i) {
            final float n2 = floatValue * (width >> i + 1);
            float n3;
            if (i == 0) {
                n3 = (float)(width + n);
            }
            else {
                n3 = 2.0f * n2;
            }
            canvas.drawRect(n2 + this.mSolidBarDetentWidth - n, 0.0f, n3 - n, (float)this.mSolidBarHeight, this.mPaint);
        }
    }
    
    protected void onLayout(final boolean b, int width, final int n, final int n2, final int n3) {
        if (b) {
            width = this.getWidth();
            this.mShadow.setBounds(0, this.mSolidBarHeight, width, this.getHeight() - this.mSolidBarHeight);
            final float n4 = width / this.mDensity / 300.0f;
            this.mAnimator.setDuration((long)(int)(((n4 - 1.0f) * 0.3f + 1.0f) * 500.0f));
            this.mSegmentCount = (int)(((n4 - 1.0f) * 0.1f + 1.0f) * 5.0f);
        }
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (n == 0) {
            this.start();
        }
        else {
            this.stop();
        }
    }
    
    private static class ExponentialInterpolator implements Interpolator
    {
        public float getInterpolation(final float n) {
            return (float)Math.pow(2.0, n) - 1.0f;
        }
    }
}
