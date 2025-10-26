package com.linkpoint.ui.render
import java.util.*

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

class CardboardControlsPlaceholder : ViewGroup() {
    private Int fixedHeight = 0
    private Int fixedWidth = 0
    private OnViewInvalidateListener onViewInvalidateListener = null

    interface OnViewInvalidateListener {
         fun onViewInvalidated()
    }

    public CardboardControlsPlaceholder(Context context) {
        super(context)
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    @TargetApi(21)
    public CardboardControlsPlaceholder(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
    }

     public fun invalidateChildInParent(iArr: IntArray, rect: Rect): ViewParent {
        val invalidateChildInParent: ViewParent = super.invalidateChildInParent(iArr, rect)
        if (this.onViewInvalidateListener != null) {
            this.onViewInvalidateListener.onViewInvalidated()
        }
        return invalidateChildInParent
    }

    /* access modifiers changed from: protected */
    override fun onLayout(z: Boolean, i: Int, i2: Int, i3: Int, i4: Int) {
        val childCount: Int = getChildCount()
        for (Int i5 = 0; i5 < childCount; i5++) {
            getChildAt(i5).layout(0, 0, this.fixedWidth, this.fixedHeight)
        }
    }

    /* access modifiers changed from: protected */
    override fun onMeasure(i: Int, i2: Int) {
        val childCount: Int = getChildCount()
        for (Int i3 = 0; i3 < childCount; i3++) {
            val childAt: View = getChildAt(i3)
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, View.MeasureSpec.makeMeasureSpec(this.fixedWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.fixedHeight, 1073741824))
            }
        }
        setMeasuredDimension(0, 0)
    }

    fun setFixedSize(i: Int, i2: Int) {
        this.fixedWidth = i
        this.fixedHeight = i2
        requestLayout()
    }

    fun setOnViewInvalidateListener(onViewInvalidateListener2: OnViewInvalidateListener) {
        this.onViewInvalidateListener = onViewInvalidateListener2
    }
}
