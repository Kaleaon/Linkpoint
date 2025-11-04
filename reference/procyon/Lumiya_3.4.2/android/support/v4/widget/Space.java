// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

public class Space extends View
{
    public Space(final Context context) {
        this(context, null);
    }
    
    public Space(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public Space(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        if (this.getVisibility() == 0) {
            this.setVisibility(4);
        }
    }
    
    private static int getDefaultSize2(final int a, int min) {
        final int mode = View$MeasureSpec.getMode(min);
        final int size = View$MeasureSpec.getSize(min);
        min = a;
        switch (mode) {
            default: {
                min = a;
                return min;
            }
            case 1073741824: {
                min = size;
                return min;
            }
            case Integer.MIN_VALUE: {
                min = Math.min(a, size);
            }
            case 0: {
                return min;
            }
        }
    }
    
    @SuppressLint({ "MissingSuperCall" })
    public void draw(final Canvas canvas) {
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.setMeasuredDimension(getDefaultSize2(this.getSuggestedMinimumWidth(), n), getDefaultSize2(this.getSuggestedMinimumHeight(), n2));
    }
}
