package com.linkpoint.ui.common

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.linkpoint.R
import javax.annotation.Nullable

class LoadingLayout : FrameLayout() {
    private Boolean butteryBarVisible = false
    private ButteryProgressBar butteryProgressBar = null
    private val ProgressBar progressBar
    private SwipeRefreshLayout swipeRefreshLayout = null
    private val TextView textView
    private Boolean withButteryProgressBar = false

    public LoadingLayout(Context context) {
        super(context)
        this.progressBar = ProgressBar(context)
        this.textView = TextView(context)
        prepareViews(context)
    }

    public LoadingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        applyAttributes(context, attributeSet)
        this.progressBar = ProgressBar(context, attributeSet)
        this.textView = TextView(context, attributeSet)
        prepareViews(context)
    }

    public LoadingLayout(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        applyAttributes(context, attributeSet)
        this.progressBar = ProgressBar(context, attributeSet, i)
        this.textView = TextView(context, attributeSet, i)
        prepareViews(context)
    }

    @TargetApi(21)
    public LoadingLayout(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
        applyAttributes(context, attributeSet)
        this.progressBar = ProgressBar(context, attributeSet, i, i2)
        this.textView = TextView(context, attributeSet, i, i2)
        prepareViews(context)
    }

     private fun applyAttributes(context: Context, attributeSet: AttributeSet) {
        val obtainStyledAttributes: TypedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.LoadingLayout, 0, 0)
        try {
            this.withButteryProgressBar = obtainStyledAttributes.getBoolean(0, this.withButteryProgressBar)
        } finally {
            obtainStyledAttributes.recycle()
        }
    }

     private fun prepareViews(context: Context) {
        if (this.withButteryProgressBar && Build.VERSION.SDK_INT >= 14) {
            this.butteryProgressBar = ButteryProgressBar(context)
            this.butteryProgressBar.setId(R.id.loading_layout_buttery_progress_bar_id)
            this.butteryProgressBar.setVisibility(8)
            addView(this.butteryProgressBar, FrameLayout.LayoutParams(-1, -2, 48))
        }
        this.progressBar.setId(R.id.loading_layout_progress_bar_id)
        this.progressBar.setVisibility(8)
        this.progressBar.setIndeterminate(true)
        addView(this.progressBar, FrameLayout.LayoutParams(-2, -2, 17))
        this.textView.setId(R.id.loading_layout_message_view_id)
        this.textView.setVisibility(8)
        addView(this.textView, FrameLayout.LayoutParams(-2, -2, 17))
    }

     private fun setMode(z: Boolean, z2: Boolean, z3: Boolean) {
        val childCount: Int = getChildCount()
        for (Int i = 0; i < childCount; i++) {
            val childAt: View = getChildAt(i)
            if (childAt == this.progressBar) {
                childAt.setVisibility(z ? 0 : 8)
            } else if (childAt == this.textView) {
                childAt.setVisibility(z2 ? 0 : 8)
            } else if (childAt != this.butteryProgressBar || this.butteryProgressBar == null) {
                childAt.setVisibility(z3 ? 0 : 8)
            } else {
                childAt.setVisibility(this.butteryBarVisible ? 0 : 8)
            }
        }
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setEnabled(z3)
            if (!z3) {
                this.swipeRefreshLayout.setRefreshing(false)
            }
        }
    }

    fun setButteryProgressBar(z: Boolean) {
        this.butteryBarVisible = z
        if (this.butteryProgressBar != null) {
            this.butteryProgressBar.setVisibility(z ? 0 : 8)
        }
    }

    fun setSwipeRefreshLayout(swipeRefreshLayout2: SwipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout2
    }

    fun showContent(str: String) {
        setMode(false, str != null, true)
        this.textView.setText(str)
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false)
        }
    }

    fun showLoading() {
        setMode(true, false, false)
    }

    fun showMessage(str: String) {
        setMode(false, true, false)
        this.textView.setText(str)
    }
}
