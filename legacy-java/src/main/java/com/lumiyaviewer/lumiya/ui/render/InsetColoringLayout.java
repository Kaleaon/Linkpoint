package com.lumiyaviewer.lumiya.ui.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.lumiyaviewer.lumiya.R;

/* loaded from: classes.dex */
public class InsetColoringLayout extends FrameLayout {
    private int backgroundColor;
    private final Paint backgroundPaint;
    private int childPaddingBottom;
    private int childPaddingLeft;
    private int childPaddingRight;
    private int childPaddingTop;

    public InsetColoringLayout(Context context) {
        super(context);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        applyAttributes(context, attributeSet, 0, 0);
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        applyAttributes(context, attributeSet, i, 0);
        setWillNotDraw(false);
    }

    @TargetApi(21)
    public InsetColoringLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        applyAttributes(context, attributeSet, i, i2);
        setWillNotDraw(false);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.InsetColoringLayout, i, i2);
        try {
            this.backgroundColor = obtainStyledAttributes.getColor(0, this.backgroundColor);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        this.backgroundPaint.setColor(this.backgroundColor);
        if (this.childPaddingTop != 0) {
            canvas.drawRect(0.0f, 0.0f, width, this.childPaddingTop, this.backgroundPaint);
        }
        if (this.childPaddingBottom != 0) {
            canvas.drawRect(0.0f, height - this.childPaddingBottom, 0.0f, height, this.backgroundPaint);
        }
        if (this.childPaddingLeft != 0) {
            canvas.drawRect(0.0f, 0.0f, this.childPaddingLeft, height, this.backgroundPaint);
        }
        if (this.childPaddingRight != 0) {
            canvas.drawRect(width - this.childPaddingRight, 0.0f, width, height, this.backgroundPaint);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View childAt;
        super.onLayout(z, i, i2, i3, i4);
        if (getChildCount() <= 0 || (childAt = getChildAt(0)) == null) {
            return;
        }
        this.childPaddingLeft = childAt.getPaddingLeft();
        this.childPaddingRight = childAt.getPaddingRight();
        this.childPaddingTop = childAt.getPaddingTop();
        this.childPaddingBottom = childAt.getPaddingBottom();
    }
}
