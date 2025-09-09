package com.lumiyaviewer.lumiya.ui.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.lumiyaviewer.lumiya.R;

public class DashedSeparatorView extends View {
    private final Paint paint = new Paint();
    private final Path path = new Path();
    private final PathEffect pathEffect = new DashPathEffect(new float[]{1.0f, 10.0f}, 0.0f);
    private int separatorColor = -12303292;

    public DashedSeparatorView(Context context) {
        super(context);
    }

    public DashedSeparatorView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet, 0, 0);
    }

    public DashedSeparatorView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public DashedSeparatorView(Context context, @Nullable AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        applyAttributes(context, attributeSet, i, i2);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.DashedSeparatorView, i, i2);
        try {
            this.separatorColor = obtainStyledAttributes.getColor(0, this.separatorColor);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.paint.setColor(this.separatorColor);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics()));
        this.paint.setPathEffect(this.pathEffect);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.path.reset();
        this.path.moveTo(0.0f, (float) (getHeight() / 2));
        this.path.lineTo((float) getWidth(), (float) (getHeight() / 2));
        canvas.drawPath(this.path, this.paint);
    }
}
