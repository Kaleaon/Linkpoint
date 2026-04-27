package com.lumiyaviewer.lumiya.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class LoadingLayout extends FrameLayout {
    private boolean butteryBarVisible;

    @Nullable
    private ButteryProgressBar butteryProgressBar;
    private final ProgressBar progressBar;

    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;
    private final TextView textView;
    private boolean withButteryProgressBar;

    public LoadingLayout(Context context) {
        super(context);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        this.progressBar = new ProgressBar(context);
        this.textView = new TextView(context);
        prepareViews(context);
    }

    public LoadingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        applyAttributes(context, attributeSet);
        this.progressBar = new ProgressBar(context, attributeSet);
        this.textView = new TextView(context, attributeSet);
        prepareViews(context);
    }

    public LoadingLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        applyAttributes(context, attributeSet);
        this.progressBar = new ProgressBar(context, attributeSet, i);
        this.textView = new TextView(context, attributeSet, i);
        prepareViews(context);
    }

    @TargetApi(21)
    public LoadingLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.withButteryProgressBar = false;
        this.swipeRefreshLayout = null;
        this.butteryProgressBar = null;
        this.butteryBarVisible = false;
        applyAttributes(context, attributeSet);
        this.progressBar = new ProgressBar(context, attributeSet, i, i2);
        this.textView = new TextView(context, attributeSet, i, i2);
        prepareViews(context);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.LoadingLayout, 0, 0);
        try {
            this.withButteryProgressBar = obtainStyledAttributes.getBoolean(0, this.withButteryProgressBar);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    private void prepareViews(Context context) {
        if (this.withButteryProgressBar && Build.VERSION.SDK_INT >= 14) {
            this.butteryProgressBar = new ButteryProgressBar(context);
            this.butteryProgressBar.setId(R.id.loading_layout_buttery_progress_bar_id);
            this.butteryProgressBar.setVisibility(8);
            addView(this.butteryProgressBar, new FrameLayout.LayoutParams(-1, -2, 48));
        }
        this.progressBar.setId(R.id.loading_layout_progress_bar_id);
        this.progressBar.setVisibility(8);
        this.progressBar.setIndeterminate(true);
        addView(this.progressBar, new FrameLayout.LayoutParams(-2, -2, 17));
        this.textView.setId(R.id.loading_layout_message_view_id);
        this.textView.setVisibility(8);
        addView(this.textView, new FrameLayout.LayoutParams(-2, -2, 17));
    }

    private void setMode(boolean z, boolean z2, boolean z3) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt == this.progressBar) {
                childAt.setVisibility(z ? 0 : 8);
            } else if (childAt == this.textView) {
                childAt.setVisibility(z2 ? 0 : 8);
            } else if (childAt != this.butteryProgressBar || this.butteryProgressBar == null) {
                childAt.setVisibility(z3 ? 0 : 8);
            } else {
                childAt.setVisibility(this.butteryBarVisible ? 0 : 8);
            }
        }
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setEnabled(z3);
            if (z3) {
                return;
            }
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setButteryProgressBar(boolean z) {
        this.butteryBarVisible = z;
        if (this.butteryProgressBar != null) {
            this.butteryProgressBar.setVisibility(z ? 0 : 8);
        }
    }

    public void setSwipeRefreshLayout(@Nullable SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void showContent(@Nullable String str) {
        setMode(false, str != null, true);
        this.textView.setText(str);
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void showLoading() {
        setMode(true, false, false);
    }

    public void showMessage(String str) {
        setMode(false, true, false);
        this.textView.setText(str);
    }
}
