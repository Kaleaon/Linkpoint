// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class CardboardControlsPlaceholder extends ViewGroup
{
    public static interface OnViewInvalidateListener
    {

        public abstract void onViewInvalidated();
    }


    private int fixedHeight;
    private int fixedWidth;
    private OnViewInvalidateListener onViewInvalidateListener;

    public CardboardControlsPlaceholder(Context context)
    {
        super(context);
        fixedWidth = 0;
        fixedHeight = 0;
        onViewInvalidateListener = null;
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        fixedWidth = 0;
        fixedHeight = 0;
        onViewInvalidateListener = null;
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        fixedWidth = 0;
        fixedHeight = 0;
        onViewInvalidateListener = null;
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        fixedWidth = 0;
        fixedHeight = 0;
        onViewInvalidateListener = null;
    }

    public ViewParent invalidateChildInParent(int ai[], Rect rect)
    {
        ai = super.invalidateChildInParent(ai, rect);
        if (onViewInvalidateListener != null)
        {
            onViewInvalidateListener.onViewInvalidated();
        }
        return ai;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        j = getChildCount();
        for (i = 0; i < j; i++)
        {
            getChildAt(i).layout(0, 0, fixedWidth, fixedHeight);
        }

    }

    protected void onMeasure(int i, int j)
    {
        j = getChildCount();
        for (i = 0; i < j; i++)
        {
            View view = getChildAt(i);
            if (view.getVisibility() != 8)
            {
                measureChild(view, android.view.View.MeasureSpec.makeMeasureSpec(fixedWidth, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(fixedHeight, 0x40000000));
            }
        }

        setMeasuredDimension(0, 0);
    }

    public void setFixedSize(int i, int j)
    {
        fixedWidth = i;
        fixedHeight = j;
        requestLayout();
    }

    public void setOnViewInvalidateListener(OnViewInvalidateListener onviewinvalidatelistener)
    {
        onViewInvalidateListener = onviewinvalidatelistener;
    }
}
