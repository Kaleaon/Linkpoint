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

public class InsetColoringLayout extends FrameLayout {
    private int backgroundColor = 0;
    private final Paint backgroundPaint = new Paint();
    private int childPaddingBottom = 20;
    private int childPaddingLeft = 20;
    private int childPaddingRight = 20;
    private int childPaddingTop = 20;

    public InsetColoringLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet, 0, 0);
        setWillNotDraw(false);
    }

    public InsetColoringLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet, i, 0);
        setWillNotDraw(false);
    }

    @TargetApi(21)
    public InsetColoringLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        this.backgroundPaint.setColor(this.backgroundColor);
        if (this.childPaddingTop != 0) {
            canvas.drawRect(0.0f, 0.0f, (float) width, (float) this.childPaddingTop, this.backgroundPaint);
        }
        if (this.childPaddingBottom != 0) {
            canvas.drawRect(0.0f, (float) (height - this.childPaddingBottom), 0.0f, (float) height, this.backgroundPaint);
        }
        if (this.childPaddingLeft != 0) {
            canvas.drawRect(0.0f, 0.0f, (float) this.childPaddingLeft, (float) height, this.backgroundPaint);
        }
        if (this.childPaddingRight != 0) {
            canvas.drawRect((float) (width - this.childPaddingRight), 0.0f, (float) width, (float) height, this.backgroundPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View childAt;
        super.onLayout(z, i, i2, i3, i4);
        if (getChildCount() > 0 && (childAt = getChildAt(0)) != null) {
            this.childPaddingLeft = childAt.getPaddingLeft();
            this.childPaddingRight = childAt.getPaddingRight();
            this.childPaddingTop = childAt.getPaddingTop();
            this.childPaddingBottom = childAt.getPaddingBottom();
        }
    }
}
