// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;

public class ButteryProgressBar extends View
{
    private static class ExponentialInterpolator
        implements Interpolator
    {

        public float getInterpolation(float f)
        {
            return (float)Math.pow(2D, f) - 1.0F;
        }

        private ExponentialInterpolator()
        {
        }

        ExponentialInterpolator(ExponentialInterpolator exponentialinterpolator)
        {
            this();
        }
    }


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

    public ButteryProgressBar(Context context)
    {
        this(context, null);
    }

    public ButteryProgressBar(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mPaint = new Paint();
        mDensity = context.getResources().getDisplayMetrics().density;
        attributeset = context.obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.ButteryProgressBar);
        mBarColor = attributeset.getColor(0, context.getResources().getColor(0x1060012));
        mSolidBarHeight = attributeset.getDimensionPixelSize(1, Math.round(mDensity * 4F));
        mSolidBarDetentWidth = attributeset.getDimensionPixelSize(2, Math.round(mDensity * 3F));
        attributeset.recycle();
        mAnimator = new ValueAnimator();
        mAnimator.setFloatValues(new float[] {
            1.0F, 2.0F
        });
        mAnimator.setRepeatCount(-1);
        mAnimator.setInterpolator(new ExponentialInterpolator(null));
        mAnimator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {

            final ButteryProgressBar this$0;

            public void onAnimationUpdate(ValueAnimator valueanimator)
            {
                invalidate();
            }

            
            {
                this$0 = ButteryProgressBar.this;
                super();
            }
        });
        mPaint.setColor(mBarColor);
        mShadow = new GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
            mBarColor & 0xffffff | 0x22000000, 0
        });
        return;
        context;
        attributeset.recycle();
        throw context;
    }

    private void start()
    {
        if (mAnimator == null)
        {
            return;
        } else
        {
            mAnimator.start();
            return;
        }
    }

    private void stop()
    {
        if (mAnimator == null)
        {
            return;
        } else
        {
            mAnimator.cancel();
            return;
        }
    }

    protected void onDraw(Canvas canvas)
    {
        if (!mAnimator.isStarted())
        {
            return;
        }
        mShadow.draw(canvas);
        float f1 = ((Float)mAnimator.getAnimatedValue()).floatValue();
        int j = getWidth();
        int k = j >> mSegmentCount - 1;
        int i = 0;
        while (i < mSegmentCount) 
        {
            float f2 = f1 * (float)(j >> i + 1);
            float f;
            if (i == 0)
            {
                f = j + k;
            } else
            {
                f = 2.0F * f2;
            }
            canvas.drawRect((f2 + (float)mSolidBarDetentWidth) - (float)k, 0.0F, f - (float)k, mSolidBarHeight, mPaint);
            i++;
        }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        if (flag)
        {
            i = getWidth();
            mShadow.setBounds(0, mSolidBarHeight, i, getHeight() - mSolidBarHeight);
            float f = (float)i / mDensity / 300F;
            mAnimator.setDuration((int)(((f - 1.0F) * 0.3F + 1.0F) * 500F));
            mSegmentCount = (int)(((f - 1.0F) * 0.1F + 1.0F) * 5F);
        }
    }

    protected void onVisibilityChanged(View view, int i)
    {
        super.onVisibilityChanged(view, i);
        if (i == 0)
        {
            start();
            return;
        } else
        {
            stop();
            return;
        }
    }
}
