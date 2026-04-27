// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OnlineIndicatorView extends View
{

    private final Paint innerRingPaint;
    private float innerRingThickness;
    private int onlineIndicatorColor;
    private float outerRingGap;
    private final Paint outerRingPaint;
    private float outerRingThickness;

    public OnlineIndicatorView(Context context)
    {
        super(context);
        onlineIndicatorColor = 0xff00ff00;
        innerRingThickness = 5F;
        outerRingGap = 1.0F;
        outerRingThickness = 1.0F;
        innerRingPaint = new Paint();
        outerRingPaint = new Paint();
    }

    public OnlineIndicatorView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        onlineIndicatorColor = 0xff00ff00;
        innerRingThickness = 5F;
        outerRingGap = 1.0F;
        outerRingThickness = 1.0F;
        innerRingPaint = new Paint();
        outerRingPaint = new Paint();
        applyAttributes(context, attributeset, 0, 0);
    }

    public OnlineIndicatorView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        onlineIndicatorColor = 0xff00ff00;
        innerRingThickness = 5F;
        outerRingGap = 1.0F;
        outerRingThickness = 1.0F;
        innerRingPaint = new Paint();
        outerRingPaint = new Paint();
        applyAttributes(context, attributeset, i, 0);
    }

    public OnlineIndicatorView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        onlineIndicatorColor = 0xff00ff00;
        innerRingThickness = 5F;
        outerRingGap = 1.0F;
        outerRingThickness = 1.0F;
        innerRingPaint = new Paint();
        outerRingPaint = new Paint();
        applyAttributes(context, attributeset, i, j);
    }

    private void applyAttributes(Context context, AttributeSet attributeset, int i, int j)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.OnlineIndicatorView, i, j);
        onlineIndicatorColor = context.getColor(0, onlineIndicatorColor);
        innerRingThickness = context.getDimension(1, innerRingThickness);
        outerRingGap = context.getDimension(2, outerRingGap);
        outerRingThickness = context.getDimension(3, outerRingThickness);
        context.recycle();
        return;
        attributeset;
        context.recycle();
        throw attributeset;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        innerRingPaint.setColor(onlineIndicatorColor);
        innerRingPaint.setAntiAlias(true);
        innerRingPaint.setStyle(android.graphics.Paint.Style.FILL);
        outerRingPaint.setColor(onlineIndicatorColor);
        outerRingPaint.setAntiAlias(true);
        outerRingPaint.setStyle(android.graphics.Paint.Style.STROKE);
        outerRingPaint.setStrokeWidth(outerRingThickness);
    }

    protected void onDraw(Canvas canvas)
    {
        float f = getWidth();
        float f1 = getHeight();
        canvas.drawCircle(f / 2.0F, f1 / 2.0F, innerRingThickness / 2.0F, innerRingPaint);
        canvas.drawCircle(f / 2.0F, f1 / 2.0F, (innerRingThickness + outerRingGap + outerRingThickness / 2.0F) / 2.0F, outerRingPaint);
    }
}
