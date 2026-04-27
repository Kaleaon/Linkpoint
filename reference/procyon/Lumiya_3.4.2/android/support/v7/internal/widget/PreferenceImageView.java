// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.internal.widget;

import android.view.View$MeasureSpec;
import android.content.res.TypedArray;
import android.support.v7.preference.R;
import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.widget.ImageView;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class PreferenceImageView extends ImageView
{
    private int mMaxHeight;
    private int mMaxWidth;
    
    public PreferenceImageView(final Context context) {
        this(context, null);
    }
    
    public PreferenceImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PreferenceImageView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.PreferenceImageView, n, 0);
        this.setMaxWidth(obtainStyledAttributes.getDimensionPixelSize(R.styleable.PreferenceImageView_maxWidth, Integer.MAX_VALUE));
        this.setMaxHeight(obtainStyledAttributes.getDimensionPixelSize(R.styleable.PreferenceImageView_maxHeight, Integer.MAX_VALUE));
        obtainStyledAttributes.recycle();
    }
    
    public int getMaxHeight() {
        return this.mMaxHeight;
    }
    
    public int getMaxWidth() {
        return this.mMaxWidth;
    }
    
    protected void onMeasure(int measureSpec, int measureSpec2) {
        final int mode = View$MeasureSpec.getMode(measureSpec);
        if (mode == Integer.MIN_VALUE || mode == 0) {
            final int size = View$MeasureSpec.getSize(measureSpec);
            final int maxWidth = this.getMaxWidth();
            if (maxWidth != Integer.MAX_VALUE) {
                if (maxWidth < size || mode == 0) {
                    measureSpec = View$MeasureSpec.makeMeasureSpec(maxWidth, Integer.MIN_VALUE);
                }
            }
        }
        final int mode2 = View$MeasureSpec.getMode(measureSpec2);
        if (mode2 == Integer.MIN_VALUE || mode2 == 0) {
            final int size2 = View$MeasureSpec.getSize(measureSpec2);
            final int maxHeight = this.getMaxHeight();
            if (maxHeight != Integer.MAX_VALUE) {
                if (maxHeight < size2 || mode2 == 0) {
                    measureSpec2 = View$MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE);
                }
            }
        }
        super.onMeasure(measureSpec, measureSpec2);
    }
    
    public void setMaxHeight(final int mMaxHeight) {
        super.setMaxHeight(this.mMaxHeight = mMaxHeight);
    }
    
    public void setMaxWidth(final int mMaxWidth) {
        super.setMaxWidth(this.mMaxWidth = mMaxWidth);
    }
}
