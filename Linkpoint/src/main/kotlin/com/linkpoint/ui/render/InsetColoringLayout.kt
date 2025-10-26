package com.linkpoint.ui.render

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.linkpoint.R

class InsetColoringLayout : FrameLayout() {
    private Int backgroundColor = 0
    private val Paint backgroundPaint = Paint()
    private Int childPaddingBottom = 20
    private Int childPaddingLeft = 20
    private Int childPaddingRight = 20
    private Int childPaddingTop = 20

    public InsetColoringLayout(Context context) {
        super(context)
        setWillNotDraw(false)
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        applyAttributes(context, attributeSet, 0, 0)
        setWillNotDraw(false)
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        applyAttributes(context, attributeSet, i, 0)
        setWillNotDraw(false)
    }

    @TargetApi(21)
    public InsetColoringLayout(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
        applyAttributes(context, attributeSet, i, i2)
        setWillNotDraw(false)
    }

     private fun applyAttributes(context: Context, attributeSet: AttributeSet, i: Int, i2: Int) {
        val obtainStyledAttributes: TypedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.InsetColoringLayout, i, i2)
        try {
            this.backgroundColor = obtainStyledAttributes.getColor(0, this.backgroundColor)
        } finally {
            obtainStyledAttributes.recycle()
        }
    }

    /* access modifiers changed from: protected */
    override fun onDraw(canvas: Canvas) {
        val width: Int = getWidth()
        val height: Int = getHeight()
        this.backgroundPaint.setColor(this.backgroundColor)
        if (this.childPaddingTop != 0) {
            canvas.drawRect(0.0f, 0.0f, (Float) width, (Float) this.childPaddingTop, this.backgroundPaint)
        }
        if (this.childPaddingBottom != 0) {
            canvas.drawRect(0.0f, (Float) (height - this.childPaddingBottom), 0.0f, (Float) height, this.backgroundPaint)
        }
        if (this.childPaddingLeft != 0) {
            canvas.drawRect(0.0f, 0.0f, (Float) this.childPaddingLeft, (Float) height, this.backgroundPaint)
        }
        if (this.childPaddingRight != 0) {
            canvas.drawRect((Float) (width - this.childPaddingRight), 0.0f, (Float) width, (Float) height, this.backgroundPaint)
        }
    }

    /* access modifiers changed from: protected */
    override fun onLayout(z: Boolean, i: Int, i2: Int, i3: Int, i4: Int) {
        View childAt
        super.onLayout(z, i, i2, i3, i4)
        if (getChildCount() > 0 && (childAt = getChildAt(0)) != null) {
            this.childPaddingLeft = childAt.getPaddingLeft()
            this.childPaddingRight = childAt.getPaddingRight()
            this.childPaddingTop = childAt.getPaddingTop()
            this.childPaddingBottom = childAt.getPaddingBottom()
        }
    }
}
