// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class InsetColoringLayout extends FrameLayout
{

    private int backgroundColor;
    private final Paint backgroundPaint;
    private int childPaddingBottom;
    private int childPaddingLeft;
    private int childPaddingRight;
    private int childPaddingTop;

    public InsetColoringLayout(Context context)
    {
        super(context);
        backgroundColor = 0;
        childPaddingLeft = 20;
        childPaddingTop = 20;
        childPaddingRight = 20;
        childPaddingBottom = 20;
        backgroundPaint = new Paint();
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        backgroundColor = 0;
        childPaddingLeft = 20;
        childPaddingTop = 20;
        childPaddingRight = 20;
        childPaddingBottom = 20;
        backgroundPaint = new Paint();
        applyAttributes(context, attributeset, 0, 0);
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        backgroundColor = 0;
        childPaddingLeft = 20;
        childPaddingTop = 20;
        childPaddingRight = 20;
        childPaddingBottom = 20;
        backgroundPaint = new Paint();
        applyAttributes(context, attributeset, i, 0);
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        backgroundColor = 0;
        childPaddingLeft = 20;
        childPaddingTop = 20;
        childPaddingRight = 20;
        childPaddingBottom = 20;
        backgroundPaint = new Paint();
        applyAttributes(context, attributeset, i, j);
        setWillNotDraw(false);
    }

    private void applyAttributes(Context context, AttributeSet attributeset, int i, int j)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.InsetColoringLayout, i, j);
        backgroundColor = context.getColor(0, backgroundColor);
        context.recycle();
        return;
        attributeset;
        context.recycle();
        throw attributeset;
    }

    protected void onDraw(Canvas canvas)
    {
        int i = getWidth();
        int j = getHeight();
        backgroundPaint.setColor(backgroundColor);
        if (childPaddingTop != 0)
        {
            canvas.drawRect(0.0F, 0.0F, i, childPaddingTop, backgroundPaint);
        }
        if (childPaddingBottom != 0)
        {
            canvas.drawRect(0.0F, j - childPaddingBottom, 0.0F, j, backgroundPaint);
        }
        if (childPaddingLeft != 0)
        {
            canvas.drawRect(0.0F, 0.0F, childPaddingLeft, j, backgroundPaint);
        }
        if (childPaddingRight != 0)
        {
            canvas.drawRect(i - childPaddingRight, 0.0F, i, j, backgroundPaint);
        }
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        if (getChildCount() > 0)
        {
            View view = getChildAt(0);
            if (view != null)
            {
                childPaddingLeft = view.getPaddingLeft();
                childPaddingRight = view.getPaddingRight();
                childPaddingTop = view.getPaddingTop();
                childPaddingBottom = view.getPaddingBottom();
            }
        }
    }
}
