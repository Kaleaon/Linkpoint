// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.res.TypedArray;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.widget.FrameLayout$LayoutParams;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.R;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ProgressBar;
import javax.annotation.Nullable;
import android.widget.FrameLayout;

public class LoadingLayout extends FrameLayout
{
    private boolean butteryBarVisible;
    @Nullable
    private ButteryProgressBar butteryProgressBar;
    private final ProgressBar progressBar;
    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;
    private final TextView textView;
    private boolean withButteryProgressBar;
    
    public LoadingLayout(final Context context) {
        super(context);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        this.progressBar = new ProgressBar(context);
        this.textView = new TextView(context);
        this.prepareViews(context);
    }
    
    public LoadingLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        this.applyAttributes(context, set);
        this.progressBar = new ProgressBar(context, set);
        this.textView = new TextView(context, set);
        this.prepareViews(context);
    }
    
    public LoadingLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        this.applyAttributes(context, set);
        this.progressBar = new ProgressBar(context, set, n);
        this.textView = new TextView(context, set, n);
        this.prepareViews(context);
    }
    
    @TargetApi(21)
    public LoadingLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        this.applyAttributes(context, set);
        this.progressBar = new ProgressBar(context, set, n, n2);
        this.textView = new TextView(context, set, n, n2);
        this.prepareViews(context);
    }
    
    private void applyAttributes(Context obtainStyledAttributes, final AttributeSet set) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R.styleable.LoadingLayout, 0, 0);
        try {
            this.withButteryProgressBar = ((TypedArray)obtainStyledAttributes).getBoolean(0, this.withButteryProgressBar);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private void prepareViews(final Context context) {
        if (this.withButteryProgressBar && Build$VERSION.SDK_INT >= 14) {
            (this.butteryProgressBar = new ButteryProgressBar(context)).setId(2131755027);
            this.butteryProgressBar.setVisibility(8);
            this.addView((View)this.butteryProgressBar, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -2, 48));
        }
        this.progressBar.setId(2131755029);
        this.progressBar.setVisibility(8);
        this.progressBar.setIndeterminate(true);
        this.addView((View)this.progressBar, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2, 17));
        this.textView.setId(2131755028);
        this.textView.setVisibility(8);
        this.addView((View)this.textView, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2, 17));
    }
    
    private void setMode(final boolean b, final boolean b2, final boolean enabled) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child == this.progressBar) {
                int visibility;
                if (b) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                child.setVisibility(visibility);
            }
            else if (child == this.textView) {
                int visibility2;
                if (b2) {
                    visibility2 = 0;
                }
                else {
                    visibility2 = 8;
                }
                child.setVisibility(visibility2);
            }
            else if (child == this.butteryProgressBar && this.butteryProgressBar != null) {
                int visibility3;
                if (this.butteryBarVisible) {
                    visibility3 = 0;
                }
                else {
                    visibility3 = 8;
                }
                child.setVisibility(visibility3);
            }
            else {
                int visibility4;
                if (enabled) {
                    visibility4 = 0;
                }
                else {
                    visibility4 = 8;
                }
                child.setVisibility(visibility4);
            }
        }
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setEnabled(enabled);
            if (!enabled) {
                this.swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    
    public void setButteryProgressBar(final boolean butteryBarVisible) {
        this.butteryBarVisible = butteryBarVisible;
        if (this.butteryProgressBar != null) {
            final ButteryProgressBar butteryProgressBar = this.butteryProgressBar;
            int visibility;
            if (butteryBarVisible) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            butteryProgressBar.setVisibility(visibility);
        }
    }
    
    public void setSwipeRefreshLayout(@Nullable final SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }
    
    public void showContent(@Nullable final String text) {
        this.setMode(false, text != null, true);
        this.textView.setText((CharSequence)text);
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }
    
    public void showLoading() {
        this.setMode(true, false, false);
    }
    
    public void showMessage(final String text) {
        this.setMode(false, true, false);
        this.textView.setText((CharSequence)text);
    }
}
