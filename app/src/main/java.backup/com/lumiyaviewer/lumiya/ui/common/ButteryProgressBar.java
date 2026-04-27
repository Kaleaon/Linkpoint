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

    private static class ExponentialInterpolator implements Interpolator {
        private ExponentialInterpolator() {
        }

        /* synthetic */ ExponentialInterpolator(ExponentialInterpolator exponentialInterpolator) {
            this();
        }

        public float getInterpolation(float f) {
            return ((float) Math.pow(2.0d, (double) f)) - 1.0f;
        }
    }

    public ButteryProgressBar(Context context) {
        this(context, (AttributeSet) null);
    }

    /* JADX INFO: finally extract failed */
    public ButteryProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPaint = new Paint();
        this.mDensity = context.getResources().getDisplayMetrics().density;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ButteryProgressBar);
        try {
            this.mBarColor = obtainStyledAttributes.getColor(0, context.getResources().getColor(17170450));
            this.mSolidBarHeight = obtainStyledAttributes.getDimensionPixelSize(1, Math.round(this.mDensity * 4.0f));
            this.mSolidBarDetentWidth = obtainStyledAttributes.getDimensionPixelSize(2, Math.round(this.mDensity * 3.0f));
            obtainStyledAttributes.recycle();
            this.mAnimator = new ValueAnimator();
            this.mAnimator.setFloatValues(new float[]{1.0f, 2.0f});
            this.mAnimator.setRepeatCount(-1);
            this.mAnimator.setInterpolator(new ExponentialInterpolator((ExponentialInterpolator) null));
            this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
        if (this.mAnimator != null) {
            this.mAnimator.start();
        }
    }

    private void stop() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mAnimator.isStarted()) {
            this.mShadow.draw(canvas);
            float floatValue = ((Float) this.mAnimator.getAnimatedValue()).floatValue();
            int width = getWidth();
            int i = width >> (this.mSegmentCount - 1);
            int i2 = 0;
            while (i2 < this.mSegmentCount) {
                float f = floatValue * ((float) (width >> (i2 + 1)));
                canvas.drawRect((f + ((float) this.mSolidBarDetentWidth)) - ((float) i), 0.0f, (i2 == 0 ? (float) (width + i) : 2.0f * f) - ((float) i), (float) this.mSolidBarHeight, this.mPaint);
                i2++;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            int width = getWidth();
            this.mShadow.setBounds(0, this.mSolidBarHeight, width, getHeight() - this.mSolidBarHeight);
            float f = (((float) width) / this.mDensity) / 300.0f;
            this.mAnimator.setDuration((long) ((int) ((((f - 1.0f) * 0.3f) + 1.0f) * 500.0f)));
            this.mSegmentCount = (int) ((((f - 1.0f) * 0.1f) + 1.0f) * 5.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (i == 0) {
            start();
        } else {
            stop();
        }
    }
}
