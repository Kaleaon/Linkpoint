// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ButteryProgressBar

public class LoadingLayout extends FrameLayout
{

    private boolean butteryBarVisible;
    private ButteryProgressBar butteryProgressBar;
    private final ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final TextView textView;
    private boolean withButteryProgressBar;

    public LoadingLayout(Context context)
    {
        super(context);
        withButteryProgressBar = false;
        swipeRefreshLayout = null;
        butteryProgressBar = null;
        butteryBarVisible = false;
        progressBar = new ProgressBar(context);
        textView = new TextView(context);
        prepareViews(context);
    }

    public LoadingLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        withButteryProgressBar = false;
        swipeRefreshLayout = null;
        butteryProgressBar = null;
        butteryBarVisible = false;
        applyAttributes(context, attributeset);
        progressBar = new ProgressBar(context, attributeset);
        textView = new TextView(context, attributeset);
        prepareViews(context);
    }

    public LoadingLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        withButteryProgressBar = false;
        swipeRefreshLayout = null;
        butteryProgressBar = null;
        butteryBarVisible = false;
        applyAttributes(context, attributeset);
        progressBar = new ProgressBar(context, attributeset, i);
        textView = new TextView(context, attributeset, i);
        prepareViews(context);
    }

    public LoadingLayout(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        withButteryProgressBar = false;
        swipeRefreshLayout = null;
        butteryProgressBar = null;
        butteryBarVisible = false;
        applyAttributes(context, attributeset);
        progressBar = new ProgressBar(context, attributeset, i, j);
        textView = new TextView(context, attributeset, i, j);
        prepareViews(context);
    }

    private void applyAttributes(Context context, AttributeSet attributeset)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.LoadingLayout, 0, 0);
        withButteryProgressBar = context.getBoolean(0, withButteryProgressBar);
        context.recycle();
        return;
        attributeset;
        context.recycle();
        throw attributeset;
    }

    private void prepareViews(Context context)
    {
        if (withButteryProgressBar && android.os.Build.VERSION.SDK_INT >= 14)
        {
            butteryProgressBar = new ButteryProgressBar(context);
            butteryProgressBar.setId(0x7f100013);
            butteryProgressBar.setVisibility(8);
            addView(butteryProgressBar, new android.widget.FrameLayout.LayoutParams(-1, -2, 48));
        }
        progressBar.setId(0x7f100015);
        progressBar.setVisibility(8);
        progressBar.setIndeterminate(true);
        addView(progressBar, new android.widget.FrameLayout.LayoutParams(-2, -2, 17));
        textView.setId(0x7f100014);
        textView.setVisibility(8);
        addView(textView, new android.widget.FrameLayout.LayoutParams(-2, -2, 17));
    }

    private void setMode(boolean flag, boolean flag1, boolean flag2)
    {
        int j1 = getChildCount();
        int i = 0;
        while (i < j1) 
        {
            View view = getChildAt(i);
            if (view == progressBar)
            {
                int j;
                if (flag)
                {
                    j = 0;
                } else
                {
                    j = 8;
                }
                view.setVisibility(j);
            } else
            if (view == textView)
            {
                int k;
                if (flag1)
                {
                    k = 0;
                } else
                {
                    k = 8;
                }
                view.setVisibility(k);
            } else
            if (view == butteryProgressBar && butteryProgressBar != null)
            {
                int l;
                if (butteryBarVisible)
                {
                    l = 0;
                } else
                {
                    l = 8;
                }
                view.setVisibility(l);
            } else
            {
                int i1;
                if (flag2)
                {
                    i1 = 0;
                } else
                {
                    i1 = 8;
                }
                view.setVisibility(i1);
            }
            i++;
        }
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setEnabled(flag2);
            if (!flag2)
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public void setButteryProgressBar(boolean flag)
    {
        butteryBarVisible = flag;
        if (butteryProgressBar != null)
        {
            ButteryProgressBar butteryprogressbar = butteryProgressBar;
            int i;
            if (flag)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            butteryprogressbar.setVisibility(i);
        }
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swiperefreshlayout)
    {
        swipeRefreshLayout = swiperefreshlayout;
    }

    public void showContent(String s)
    {
        boolean flag;
        if (s != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        setMode(false, flag, true);
        textView.setText(s);
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void showLoading()
    {
        setMode(true, false, false);
    }

    public void showMessage(String s)
    {
        setMode(false, true, false);
        textView.setText(s);
    }
}
