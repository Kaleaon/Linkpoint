package com.linkpoint.ui.render
import java.util.*

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

class CardboardControlsPlaceholder : ViewGroup {
    private Int fixedHeight = 0
    private Int fixedWidth = 0
    private OnViewInvalidateListener onViewInvalidateListener = null

    interface OnViewInvalidateListener {
        fun onViewInvalidated()
    }

    CardboardControlsPlaceholder(Context context) {
        super(context)
    }

    CardboardControlsPlaceholder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    @TargetApi(21)
    CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
    }

    fun invalidateChildInParent(IntArray iArr, Rect rect): ViewParent {
        ViewParent invalidateChildInParent = super.invalidateChildInParent(iArr, rect)
        if (this.onViewInvalidateListener != null) {
            this.onViewInvalidateListener.onViewInvalidated()
        }
        return invalidateChildInParent
    }

    /* access modifiers changed from: protected */
    fun onLayout(Boolean z, Int i, Int i2, Int i3, Int i4)  {
        var childCount: Int = getChildCount()
        for (i5 in 0 until childCount) {
            getChildAt(i5).layout(0, 0, this.fixedWidth, this.fixedHeight)
        }
    }

    /* access modifiers changed from: protected */
    fun onMeasure(Int i, Int i2)  {
        var childCount: Int = getChildCount()
        for (i3 in 0 until childCount) {
            View childAt = getChildAt(i3)
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, View.MeasureSpec.makeMeasureSpec(this.fixedWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.fixedHeight, 1073741824))
            }
        }
        setMeasuredDimension(0, 0)
    }

    fun setFixedSize(Int i, Int i2)  {
        this.fixedWidth = i
        this.fixedHeight = i2
        requestLayout()
    }

    fun setOnViewInvalidateListener(OnViewInvalidateListener onViewInvalidateListener2)  {
        this.onViewInvalidateListener = onViewInvalidateListener2
    }
}
