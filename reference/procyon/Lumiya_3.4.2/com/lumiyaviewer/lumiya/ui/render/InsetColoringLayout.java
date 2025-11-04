// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.res.TypedArray;
import android.view.View;
import android.graphics.Canvas;
import com.lumiyaviewer.lumiya.R;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import android.widget.FrameLayout;

public class InsetColoringLayout extends FrameLayout
{
    private int backgroundColor;
    private final Paint backgroundPaint;
    private int childPaddingBottom;
    private int childPaddingLeft;
    private int childPaddingRight;
    private int childPaddingTop;
    
    public InsetColoringLayout(final Context context) {
        super(context);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        this.setWillNotDraw(false);
    }
    
    public InsetColoringLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        this.applyAttributes(context, set, 0, 0);
        this.setWillNotDraw(false);
    }
    
    public InsetColoringLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        this.applyAttributes(context, set, n, 0);
        this.setWillNotDraw(false);
    }
    
    @TargetApi(21)
    public InsetColoringLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.backgroundColor = 0;
        this.childPaddingLeft = 20;
        this.childPaddingTop = 20;
        this.childPaddingRight = 20;
        this.childPaddingBottom = 20;
        this.backgroundPaint = new Paint();
        this.applyAttributes(context, set, n, n2);
        this.setWillNotDraw(false);
    }
    
    private void applyAttributes(final Context context, AttributeSet obtainStyledAttributes, final int n, final int n2) {
        obtainStyledAttributes = (AttributeSet)context.getTheme().obtainStyledAttributes(obtainStyledAttributes, R.styleable.InsetColoringLayout, n, n2);
        try {
            this.backgroundColor = ((TypedArray)obtainStyledAttributes).getColor(0, this.backgroundColor);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        this.backgroundPaint.setColor(this.backgroundColor);
        if (this.childPaddingTop != 0) {
            canvas.drawRect(0.0f, 0.0f, (float)width, (float)this.childPaddingTop, this.backgroundPaint);
        }
        if (this.childPaddingBottom != 0) {
            canvas.drawRect(0.0f, (float)(height - this.childPaddingBottom), 0.0f, (float)height, this.backgroundPaint);
        }
        if (this.childPaddingLeft != 0) {
            canvas.drawRect(0.0f, 0.0f, (float)this.childPaddingLeft, (float)height, this.backgroundPaint);
        }
        if (this.childPaddingRight != 0) {
            canvas.drawRect((float)(width - this.childPaddingRight), 0.0f, (float)width, (float)height, this.backgroundPaint);
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (this.getChildCount() > 0) {
            final View child = this.getChildAt(0);
            if (child != null) {
                this.childPaddingLeft = child.getPaddingLeft();
                this.childPaddingRight = child.getPaddingRight();
                this.childPaddingTop = child.getPaddingTop();
                this.childPaddingBottom = child.getPaddingBottom();
            }
        }
    }
}
