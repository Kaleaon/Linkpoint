package com.linkpoint.ui.common
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
import com.linkpoint.R

@TargetApi(14)
class ButteryProgressBar : View() {
    private const val BASE_DURATION_MS: Int = 500
    private const val BASE_SEGMENT_COUNT: Int = 5
    private const val BASE_WIDTH_DP: Int = 300
    private const val DEFAULT_BAR_HEIGHT_DP: Int = 4
    private const val DEFAULT_DETENT_WIDTH_DP: Int = 3
    private val ValueAnimator mAnimator
    private val Int mBarColor
    private val Float mDensity
    private val Paint mPaint
    private Int mSegmentCount
    private val GradientDrawable mShadow
    private val Int mSolidBarDetentWidth
    private val Int mSolidBarHeight

    @JvmStatic
private class ExponentialInterpolator : Interpolator {
        private ExponentialInterpolator() {
        }

        /* synthetic */ ExponentialInterpolator(ExponentialInterpolator exponentialInterpolator) {
            this()
        }

        public Float getInterpolation(Float f) {
            return ((Float) Math.pow(2.0d, (Double) f)) - 1.0f
        }
    }

    public ButteryProgressBar(Context context) {
        this(context, (AttributeSet) null)
    }

    /* JADX INFO: finally extract failed */
    public ButteryProgressBar(Context context, AttributeSet attributeSet) {
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
                fun onAnimationUpdate(ValueAnimator valueAnimator) {
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
    fun onDraw(Canvas canvas) {
        if (this.mAnimator.isStarted()) {
            this.mShadow.draw(canvas)
            Float floatValue = ((Float) this.mAnimator.getAnimatedValue()).floatValue()
            Int width = getWidth()
            Int i = width >> (this.mSegmentCount - 1)
            Int i2 = 0
            while (i2 < this.mSegmentCount) {
                Float f = floatValue * ((Float) (width >> (i2 + 1)))
                canvas.drawRect((f + ((Float) this.mSolidBarDetentWidth)) - ((Float) i), 0.0f, (i2 == 0 ? (Float) (width + i) : 2.0f * f) - ((Float) i), (Float) this.mSolidBarHeight, this.mPaint)
                i2++
            }
        }
    }

    /* access modifiers changed from: protected */
    fun onLayout(Boolean z, Int i, Int i2, Int i3, Int i4) {
        if (z) {
            Int width = getWidth()
            this.mShadow.setBounds(0, this.mSolidBarHeight, width, getHeight() - this.mSolidBarHeight)
            Float f = (((Float) width) / this.mDensity) / 300.0f
            this.mAnimator.setDuration((Long) ((Int) ((((f - 1.0f) * 0.3f) + 1.0f) * 500.0f)))
            this.mSegmentCount = (Int) ((((f - 1.0f) * 0.1f) + 1.0f) * 5.0f)
        }
    }

    /* access modifiers changed from: protected */
    fun onVisibilityChanged(View view, Int i) {
        super.onVisibilityChanged(view, i)
        if (i == 0) {
            start()
        } else {
            stop()
        }
    }
}
