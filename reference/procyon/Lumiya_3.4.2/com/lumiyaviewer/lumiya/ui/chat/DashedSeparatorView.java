// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.graphics.Paint$Style;
import com.lumiyaviewer.lumiya.R;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.graphics.DashPathEffect;
import android.content.Context;
import android.graphics.PathEffect;
import android.graphics.Path;
import android.graphics.Paint;
import android.view.View;

public class DashedSeparatorView extends View
{
    private final Paint paint;
    private final Path path;
    private final PathEffect pathEffect;
    private int separatorColor;
    
    public DashedSeparatorView(final Context context) {
        super(context);
        this.separatorColor = -12303292;
        this.paint = new Paint();
        this.path = new Path();
        this.pathEffect = (PathEffect)new DashPathEffect(new float[] { 1.0f, 10.0f }, 0.0f);
    }
    
    public DashedSeparatorView(final Context context, @Nullable final AttributeSet set) {
        super(context, set);
        this.separatorColor = -12303292;
        this.paint = new Paint();
        this.path = new Path();
        this.pathEffect = (PathEffect)new DashPathEffect(new float[] { 1.0f, 10.0f }, 0.0f);
        this.applyAttributes(context, set, 0, 0);
    }
    
    public DashedSeparatorView(final Context context, @Nullable final AttributeSet set, final int n) {
        super(context, set, n);
        this.separatorColor = -12303292;
        this.paint = new Paint();
        this.path = new Path();
        this.pathEffect = (PathEffect)new DashPathEffect(new float[] { 1.0f, 10.0f }, 0.0f);
        this.applyAttributes(context, set, n, 0);
    }
    
    @TargetApi(21)
    public DashedSeparatorView(final Context context, @Nullable final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.separatorColor = -12303292;
        this.paint = new Paint();
        this.path = new Path();
        this.pathEffect = (PathEffect)new DashPathEffect(new float[] { 1.0f, 10.0f }, 0.0f);
        this.applyAttributes(context, set, n, n2);
    }
    
    private void applyAttributes(Context obtainStyledAttributes, final AttributeSet set, final int n, final int n2) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R.styleable.DashedSeparatorView, n, n2);
        try {
            this.separatorColor = ((TypedArray)obtainStyledAttributes).getColor(0, this.separatorColor);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.paint.setColor(this.separatorColor);
        this.paint.setStyle(Paint$Style.STROKE);
        this.paint.setStrokeWidth(TypedValue.applyDimension(1, 1.0f, this.getResources().getDisplayMetrics()));
        this.paint.setPathEffect(this.pathEffect);
    }
    
    protected void onDraw(final Canvas canvas) {
        this.path.reset();
        this.path.moveTo(0.0f, (float)(this.getHeight() / 2));
        this.path.lineTo((float)this.getWidth(), (float)(this.getHeight() / 2));
        canvas.drawPath(this.path, this.paint);
    }
}
