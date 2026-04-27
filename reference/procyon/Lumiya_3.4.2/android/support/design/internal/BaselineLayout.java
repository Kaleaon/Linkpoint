// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;

public class BaselineLayout extends ViewGroup
{
    private int mBaseline;
    
    public BaselineLayout(final Context context) {
        super(context, (AttributeSet)null, 0);
        this.mBaseline = -1;
    }
    
    public BaselineLayout(final Context context, final AttributeSet set) {
        super(context, set, 0);
        this.mBaseline = -1;
    }
    
    public BaselineLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mBaseline = -1;
    }
    
    public int getBaseline() {
        return this.mBaseline;
    }
    
    protected void onLayout(final boolean b, final int n, int i, final int n2, int n3) {
        final int childCount = this.getChildCount();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int paddingTop = this.getPaddingTop();
        View child;
        int measuredWidth;
        int measuredHeight;
        int n4;
        for (i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                measuredWidth = child.getMeasuredWidth();
                measuredHeight = child.getMeasuredHeight();
                n4 = paddingLeft + (n2 - n - paddingRight - paddingLeft - measuredWidth) / 2;
                if (this.mBaseline != -1 && child.getBaseline() != -1) {
                    n3 = this.mBaseline + paddingTop - child.getBaseline();
                }
                else {
                    n3 = paddingTop;
                }
                child.layout(n4, n3, measuredWidth + n4, measuredHeight + n3);
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int childCount = this.getChildCount();
        int i = 0;
        int n3 = 0;
        int max = -1;
        int a = 0;
        int max2 = 0;
        int max3 = -1;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n4;
            int n5;
            if (child.getVisibility() != 8) {
                this.measureChild(child, n, n2);
                final int baseline = child.getBaseline();
                if (baseline != -1) {
                    max = Math.max(max, baseline);
                    max3 = Math.max(max3, child.getMeasuredHeight() - baseline);
                }
                max2 = Math.max(max2, child.getMeasuredWidth());
                a = Math.max(a, child.getMeasuredHeight());
                final int combineMeasuredStates = View.combineMeasuredStates(n3, child.getMeasuredState());
                n4 = max3;
                n5 = combineMeasuredStates;
            }
            else {
                final int n6 = max3;
                n5 = n3;
                n4 = n6;
            }
            final int n7 = i + 1;
            final int n8 = n5;
            max3 = n4;
            n3 = n8;
            i = n7;
        }
        if (max != -1) {
            a = Math.max(a, Math.max(max3, this.getPaddingBottom()) + max);
            this.mBaseline = max;
        }
        this.setMeasuredDimension(View.resolveSizeAndState(Math.max(max2, this.getSuggestedMinimumWidth()), n, n3), View.resolveSizeAndState(Math.max(a, this.getSuggestedMinimumHeight()), n2, n3 << 16));
    }
}
