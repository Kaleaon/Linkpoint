// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class DashedSeparatorView extends View
{

    private final Paint paint;
    private final Path path;
    private final PathEffect pathEffect;
    private int separatorColor;

    public DashedSeparatorView(Context context)
    {
        super(context);
        separatorColor = 0xff444444;
        paint = new Paint();
        path = new Path();
        pathEffect = new DashPathEffect(new float[] {
            1.0F, 10F
        }, 0.0F);
    }

    public DashedSeparatorView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        separatorColor = 0xff444444;
        paint = new Paint();
        path = new Path();
        pathEffect = new DashPathEffect(new float[] {
            1.0F, 10F
        }, 0.0F);
        applyAttributes(context, attributeset, 0, 0);
    }

    public DashedSeparatorView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        separatorColor = 0xff444444;
        paint = new Paint();
        path = new Path();
        pathEffect = new DashPathEffect(new float[] {
            1.0F, 10F
        }, 0.0F);
        applyAttributes(context, attributeset, i, 0);
    }

    public DashedSeparatorView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        separatorColor = 0xff444444;
        paint = new Paint();
        path = new Path();
        pathEffect = new DashPathEffect(new float[] {
            1.0F, 10F
        }, 0.0F);
        applyAttributes(context, attributeset, i, j);
    }

    private void applyAttributes(Context context, AttributeSet attributeset, int i, int j)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.DashedSeparatorView, i, j);
        separatorColor = context.getColor(0, separatorColor);
        context.recycle();
        return;
        attributeset;
        context.recycle();
        throw attributeset;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        paint.setColor(separatorColor);
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(TypedValue.applyDimension(1, 1.0F, getResources().getDisplayMetrics()));
        paint.setPathEffect(pathEffect);
    }

    protected void onDraw(Canvas canvas)
    {
        path.reset();
        path.moveTo(0.0F, getHeight() / 2);
        path.lineTo(getWidth(), getHeight() / 2);
        canvas.drawPath(path, paint);
    }
}
