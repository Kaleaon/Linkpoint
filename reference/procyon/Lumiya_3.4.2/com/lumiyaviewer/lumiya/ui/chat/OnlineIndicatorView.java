// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint$Style;
import com.lumiyaviewer.lumiya.R;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;

public class OnlineIndicatorView extends View
{
    private final Paint innerRingPaint;
    private float innerRingThickness;
    private int onlineIndicatorColor;
    private float outerRingGap;
    private final Paint outerRingPaint;
    private float outerRingThickness;
    
    public OnlineIndicatorView(final Context context) {
        super(context);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
    }
    
    public OnlineIndicatorView(final Context context, @Nullable final AttributeSet set) {
        super(context, set);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
        this.applyAttributes(context, set, 0, 0);
    }
    
    public OnlineIndicatorView(final Context context, @Nullable final AttributeSet set, final int n) {
        super(context, set, n);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
        this.applyAttributes(context, set, n, 0);
    }
    
    @TargetApi(21)
    public OnlineIndicatorView(final Context context, @Nullable final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
        this.applyAttributes(context, set, n, n2);
    }
    
    private void applyAttributes(Context obtainStyledAttributes, final AttributeSet set, final int n, final int n2) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R.styleable.OnlineIndicatorView, n, n2);
        try {
            this.onlineIndicatorColor = ((TypedArray)obtainStyledAttributes).getColor(0, this.onlineIndicatorColor);
            this.innerRingThickness = ((TypedArray)obtainStyledAttributes).getDimension(1, this.innerRingThickness);
            this.outerRingGap = ((TypedArray)obtainStyledAttributes).getDimension(2, this.outerRingGap);
            this.outerRingThickness = ((TypedArray)obtainStyledAttributes).getDimension(3, this.outerRingThickness);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.innerRingPaint.setColor(this.onlineIndicatorColor);
        this.innerRingPaint.setAntiAlias(true);
        this.innerRingPaint.setStyle(Paint$Style.FILL);
        this.outerRingPaint.setColor(this.onlineIndicatorColor);
        this.outerRingPaint.setAntiAlias(true);
        this.outerRingPaint.setStyle(Paint$Style.STROKE);
        this.outerRingPaint.setStrokeWidth(this.outerRingThickness);
    }
    
    protected void onDraw(final Canvas canvas) {
        final float n = (float)this.getWidth();
        final float n2 = (float)this.getHeight();
        canvas.drawCircle(n / 2.0f, n2 / 2.0f, this.innerRingThickness / 2.0f, this.innerRingPaint);
        canvas.drawCircle(n / 2.0f, n2 / 2.0f, (this.innerRingThickness + this.outerRingGap + this.outerRingThickness / 2.0f) / 2.0f, this.outerRingPaint);
    }
}
