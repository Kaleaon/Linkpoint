package com.lumiyaviewer.lumiya.ui.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.lumiyaviewer.lumiya.R;

public class OnlineIndicatorView extends View {
    private final Paint innerRingPaint = new Paint();
    private float innerRingThickness = 5.0f;
    private int onlineIndicatorColor = -16711936;
    private float outerRingGap = 1.0f;
    private final Paint outerRingPaint = new Paint();
    private float outerRingThickness = 1.0f;

    public OnlineIndicatorView(Context context) {
        super(context);
    }

    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet, 0, 0);
    }

    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        applyAttributes(context, attributeSet, i, i2);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.OnlineIndicatorView, i, i2);
        try {
            this.onlineIndicatorColor = obtainStyledAttributes.getColor(0, this.onlineIndicatorColor);
            this.innerRingThickness = obtainStyledAttributes.getDimension(1, this.innerRingThickness);
            this.outerRingGap = obtainStyledAttributes.getDimension(2, this.outerRingGap);
            this.outerRingThickness = obtainStyledAttributes.getDimension(3, this.outerRingThickness);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.innerRingPaint.setColor(this.onlineIndicatorColor);
        this.innerRingPaint.setAntiAlias(true);
        this.innerRingPaint.setStyle(Paint.Style.FILL);
        this.outerRingPaint.setColor(this.onlineIndicatorColor);
        this.outerRingPaint.setAntiAlias(true);
        this.outerRingPaint.setStyle(Paint.Style.STROKE);
        this.outerRingPaint.setStrokeWidth(this.outerRingThickness);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        canvas.drawCircle(width / 2.0f, height / 2.0f, this.innerRingThickness / 2.0f, this.innerRingPaint);
        canvas.drawCircle(width / 2.0f, height / 2.0f, ((this.innerRingThickness + this.outerRingGap) + (this.outerRingThickness / 2.0f)) / 2.0f, this.outerRingPaint);
    }
}
