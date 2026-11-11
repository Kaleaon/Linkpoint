package com.lumiyaviewer.lumiya.ui.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
/* loaded from: classes.dex */
public class CardboardControlsPlaceholder extends ViewGroup {
    private int fixedHeight;
    private int fixedWidth;
    private OnViewInvalidateListener onViewInvalidateListener;

    /* loaded from: classes.dex */
    public interface OnViewInvalidateListener {
        void onViewInvalidated();
    }

    public CardboardControlsPlaceholder(Context context) {
        super(context);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }

    @TargetApi(21)
    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.fixedWidth = 0;
        this.fixedHeight = 0;
        this.onViewInvalidateListener = null;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        ViewParent invalidateChildInParent = super.invalidateChildInParent(iArr, rect);
        if (this.onViewInvalidateListener != null) {
            this.onViewInvalidateListener.onViewInvalidated();
        }
        return invalidateChildInParent;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            getChildAt(i5).layout(0, 0, this.fixedWidth, this.fixedHeight);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, View.MeasureSpec.makeMeasureSpec(this.fixedWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.fixedHeight, 1073741824));
            }
        }
        setMeasuredDimension(0, 0);
    }

    public void setFixedSize(int i, int i2) {
        this.fixedWidth = i;
        this.fixedHeight = i2;
        requestLayout();
    }

    public void setOnViewInvalidateListener(OnViewInvalidateListener onViewInvalidateListener) {
        this.onViewInvalidateListener = onViewInvalidateListener;
    }
}
