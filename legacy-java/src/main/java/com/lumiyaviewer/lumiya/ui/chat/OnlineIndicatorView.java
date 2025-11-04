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

/* loaded from: classes.dex */
public class OnlineIndicatorView extends View {
    private final Paint innerRingPaint;
    private float innerRingThickness;
    private int onlineIndicatorColor;
    private float outerRingGap;
    private final Paint outerRingPaint;
    private float outerRingThickness;

    public OnlineIndicatorView(Context context) {
        super(context);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
    }

    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
        applyAttributes(context, attributeSet, 0, 0);
    }

    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
        applyAttributes(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public OnlineIndicatorView(Context context, @Nullable AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.onlineIndicatorColor = -16711936;
        this.innerRingThickness = 5.0f;
        this.outerRingGap = 1.0f;
        this.outerRingThickness = 1.0f;
        this.innerRingPaint = new Paint();
        this.outerRingPaint = new Paint();
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

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.innerRingPaint.setColor(this.onlineIndicatorColor);
        this.innerRingPaint.setAntiAlias(true);
        this.innerRingPaint.setStyle(Paint.Style.FILL);
        this.outerRingPaint.setColor(this.onlineIndicatorColor);
        this.outerRingPaint.setAntiAlias(true);
        this.outerRingPaint.setStyle(Paint.Style.STROKE);
        this.outerRingPaint.setStrokeWidth(this.outerRingThickness);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        canvas.drawCircle(width / 2.0f, height / 2.0f, this.innerRingThickness / 2.0f, this.innerRingPaint);
        canvas.drawCircle(width / 2.0f, height / 2.0f, ((this.innerRingThickness + this.outerRingGap) + (this.outerRingThickness / 2.0f)) / 2.0f, this.outerRingPaint);
    }
}
