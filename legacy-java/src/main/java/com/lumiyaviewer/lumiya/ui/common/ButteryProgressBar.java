package com.lumiyaviewer.lumiya.ui.common;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import com.lumiyaviewer.lumiya.R;

@TargetApi(14)
/* loaded from: classes.dex */
public class ButteryProgressBar extends View {
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

    /* loaded from: classes.dex */
    private static class ExponentialInterpolator implements Interpolator {
        private ExponentialInterpolator() {
        }

        /* synthetic */ ExponentialInterpolator(ExponentialInterpolator exponentialInterpolator) {
            this();
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            return ((float) Math.pow(2.0d, f)) - 1.0f;
        }
    }

    public ButteryProgressBar(Context context) {
        this(context, null);
    }

    public ButteryProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPaint = new Paint();
        this.mDensity = context.getResources().getDisplayMetrics().density;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ButteryProgressBar);
        try {
            this.mBarColor = obtainStyledAttributes.getColor(0, context.getResources().getColor(android.R.color.holo_blue_light));
            this.mSolidBarHeight = obtainStyledAttributes.getDimensionPixelSize(1, Math.round(this.mDensity * 4.0f));
            this.mSolidBarDetentWidth = obtainStyledAttributes.getDimensionPixelSize(2, Math.round(this.mDensity * 3.0f));
            obtainStyledAttributes.recycle();
            this.mAnimator = new ValueAnimator();
            this.mAnimator.setFloatValues(1.0f, 2.0f);
            this.mAnimator.setRepeatCount(-1);
            this.mAnimator.setInterpolator(new ExponentialInterpolator(null));
            this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lumiyaviewer.lumiya.ui.common.ButteryProgressBar.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ButteryProgressBar.this.invalidate();
                }
            });
            this.mPaint.setColor(this.mBarColor);
            this.mShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{(this.mBarColor & ViewCompat.MEASURED_SIZE_MASK) | 570425344, 0});
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
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

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mAnimator.isStarted()) {
            this.mShadow.draw(canvas);
            float floatValue = ((Float) this.mAnimator.getAnimatedValue()).floatValue();
            int width = getWidth() >> (this.mSegmentCount - 1);
            int i = 0;
            while (i < this.mSegmentCount) {
                float f = floatValue * (r8 >> (i + 1));
                canvas.drawRect((f + this.mSolidBarDetentWidth) - width, 0.0f, (i == 0 ? r8 + width : 2.0f * f) - width, this.mSolidBarHeight, this.mPaint);
                i++;
            }
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            int width = getWidth();
            this.mShadow.setBounds(0, this.mSolidBarHeight, width, getHeight() - this.mSolidBarHeight);
            float f = (width / this.mDensity) / 300.0f;
            this.mAnimator.setDuration((int) ((((f - 1.0f) * 0.3f) + 1.0f) * 500.0f));
            this.mSegmentCount = (int) ((((f - 1.0f) * 0.1f) + 1.0f) * 5.0f);
        }
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (i == 0) {
            start();
        } else {
            stop();
        }
    }
}
