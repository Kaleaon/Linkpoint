package com.lumiyaviewer.lumiya.ui.common
import java.util.*

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import com.lumiyaviewer.lumiya.R

@TargetApi(14)
class ButteryProgressBar : View {
    private val BASE_DURATION_MS: Int = 500
    private val BASE_SEGMENT_COUNT: Int = 5
    private val BASE_WIDTH_DP: Int = 300
    private val DEFAULT_BAR_HEIGHT_DP: Int = 4
    private val DEFAULT_DETENT_WIDTH_DP: Int = 3
    private ValueAnimator mAnimator
    private Int mBarColor
    private Float mDensity
    private Paint mPaint
    private Int mSegmentCount
    private GradientDrawable mShadow
    private Int mSolidBarDetentWidth
    private Int mSolidBarHeight

    private class ExponentialInterpolator : Interpolator {
        private ExponentialInterpolator() {
        }

        /* synthetic */ ExponentialInterpolator(ExponentialInterpolator exponentialInterpolator) {
            this()
        }

        Float getInterpolation(Float f) {
            return (Math.toFloat().pow(2.0d, f.toDouble())) - 1.0f
        }
    }

    ButteryProgressBar(Context context) {
        this(context, (AttributeSet) null)
    }

    /* JADX INFO: finally extract failed */
    ButteryProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        this.mPaint = Paint()
        this.mDensity = context.getResources().getDisplayMetrics().density
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ButteryProgressBar)
        try {
            this.mBarColor = obtainStyledAttributes.getColor(0, context.getResources().getColor(17170450))
            this.mSolidBarHeight = obtainStyledAttributes.getDimensionPixelSize(1, Math.round(this.mDensity * 4.0f))
            this.mSolidBarDetentWidth = obtainStyledAttributes.getDimensionPixelSize(2, Math.round(this.mDensity * 3.0f))
            obtainStyledAttributes.recycle()
            this.mAnimator = ValueAnimator()
            this.mAnimator.setFloatValues(FloatArray{1.0f, 2.0f})
            this.mAnimator.setRepeatCount(-1)
            this.mAnimator.setInterpolator(ExponentialInterpolator((ExponentialInterpolator) null))
            this.mAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener() {
                Unit onAnimationUpdate(ValueAnimator valueAnimator) {
                    ButteryProgressBar.this.invalidate()
                }
            this.mPaint.setColor(this.mBarColor)
            this.mShadow = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, IntArray{(this.mBarColor & ViewCompat.MEASURED_SIZE_MASK) | 570425344, 0})
        } catch (Throwable th) {
            obtainStyledAttributes.recycle()
            throw th
        }
    }

    private Unit start() {
        if (this.mAnimator != null) {
            this.mAnimator.start()
        }
    }

    private Unit stop() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel()
        }
    }

    /* access modifiers changed from: protected */
    Unit onDraw(Canvas canvas) {
        if (this.mAnimator.isStarted()) {
            this.mShadow.draw(canvas)
            Float floatValue = (this.toFloat().mAnimator.getAnimatedValue()).floatValue()
            Int width = getWidth()
            Int i = width >> (this.mSegmentCount - 1)
            Int i2 = 0
            while (i2 < this.mSegmentCount) {
                Float f = floatValue * ((Float) (width >> (i2 + 1)))
                canvas.drawRect((f + (this.toFloat().mSolidBarDetentWidth)) - (i.toFloat()), 0.0f, (i2 == 0 ? (Float) (width + i) : 2.0f * f) - (i.toFloat()), this.toFloat().mSolidBarHeight, this.mPaint)
                i2++
            }
        }
    }

    /* access modifiers changed from: protected */
    Unit onLayout(Boolean z, Int i, Int i2, Int i3, Int i4) {
        if (z) {
            Int width = getWidth()
            this.mShadow.setBounds(0, this.mSolidBarHeight, width, getHeight() - this.mSolidBarHeight)
            Float f = ((width.toFloat()) / this.mDensity) / 300.0f
            this.mAnimator.setDuration((Long) ((Int) ((((f - 1.0f) * 0.3f) + 1.0f) * 500.0f)))
            this.mSegmentCount = (Int) ((((f - 1.0f) * 0.1f) + 1.0f) * 5.0f)
        }
    }

    /* access modifiers changed from: protected */
    Unit onVisibilityChanged(View view, Int i) {
        super.onVisibilityChanged(view, i)
        if (i == 0) {
            start()
        } else {
            stop()
        }
    }
}
