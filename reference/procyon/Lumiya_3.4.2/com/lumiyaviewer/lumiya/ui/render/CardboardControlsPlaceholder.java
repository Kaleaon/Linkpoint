// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import android.view.View$MeasureSpec;
import android.view.ViewParent;
import android.graphics.Rect;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;

public class CardboardControlsPlaceholder extends ViewGroup
{
    private int fixedHeight;
    private int fixedWidth;
    private OnViewInvalidateListener onViewInvalidateListener;
    
    public CardboardControlsPlaceholder(final Context context) {
        super(context);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }
    
    public CardboardControlsPlaceholder(final Context context, final AttributeSet set) {
        super(context, set);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }
    
    public CardboardControlsPlaceholder(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }
    
    @TargetApi(21)
    public CardboardControlsPlaceholder(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }
    
    public ViewParent invalidateChildInParent(final int[] array, final Rect rect) {
        final ViewParent invalidateChildInParent = super.invalidateChildInParent(array, rect);
        if (this.onViewInvalidateListener != null) {
            this.onViewInvalidateListener.onViewInvalidated();
        }
        return invalidateChildInParent;
    }
    
    protected void onLayout(final boolean b, int i, int childCount, final int n, final int n2) {
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            this.getChildAt(i).layout(0, 0, this.fixedWidth, this.fixedHeight);
        }
    }
    
    protected void onMeasure(int i, int childCount) {
        View child;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                this.measureChild(child, View$MeasureSpec.makeMeasureSpec(this.fixedWidth, 1073741824), View$MeasureSpec.makeMeasureSpec(this.fixedHeight, 1073741824));
            }
        }
        this.setMeasuredDimension(0, 0);
    }
    
    public void setFixedSize(final int fixedWidth, final int fixedHeight) {
        this.fixedWidth = fixedWidth;
        this.fixedHeight = fixedHeight;
        this.requestLayout();
    }
    
    public void setOnViewInvalidateListener(final OnViewInvalidateListener onViewInvalidateListener) {
        this.onViewInvalidateListener = onViewInvalidateListener;
    }
    
    public interface OnViewInvalidateListener
    {
        void onViewInvalidated();
    }
}
