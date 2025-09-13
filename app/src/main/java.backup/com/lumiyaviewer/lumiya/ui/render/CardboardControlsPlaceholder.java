package com.lumiyaviewer.lumiya.ui.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class CardboardControlsPlaceholder extends ViewGroup {
    private int fixedHeight = 0;
    private int fixedWidth = 0;
    private OnViewInvalidateListener onViewInvalidateListener = null;

    public interface OnViewInvalidateListener {
        void onViewInvalidated();
    }

    public CardboardControlsPlaceholder(Context context) {
        super(context);
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @TargetApi(21)
    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        ViewParent invalidateChildInParent = super.invalidateChildInParent(iArr, rect);
        if (this.onViewInvalidateListener != null) {
            this.onViewInvalidateListener.onViewInvalidated();
        }
        return invalidateChildInParent;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            getChildAt(i5).layout(0, 0, this.fixedWidth, this.fixedHeight);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
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

    public void setOnViewInvalidateListener(OnViewInvalidateListener onViewInvalidateListener2) {
        this.onViewInvalidateListener = onViewInvalidateListener2;
    }
}
