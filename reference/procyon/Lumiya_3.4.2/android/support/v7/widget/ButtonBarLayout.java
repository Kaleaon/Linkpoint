// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.v4.view.ViewCompat;
import android.widget.LinearLayout$LayoutParams;
import android.view.View$MeasureSpec;
import android.view.View;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.widget.LinearLayout;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ButtonBarLayout extends LinearLayout
{
    private static final int ALLOW_STACKING_MIN_HEIGHT_DP = 320;
    private static final int PEEK_BUTTON_DP = 16;
    private boolean mAllowStacking;
    private int mLastWidthSize;
    private int mMinimumHeight;
    
    public ButtonBarLayout(final Context context, final AttributeSet set) {
        boolean b = false;
        super(context, set);
        this.mLastWidthSize = -1;
        this.mMinimumHeight = 0;
        if (this.getResources().getConfiguration().screenHeightDp >= 320) {
            b = true;
        }
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ButtonBarLayout);
        this.mAllowStacking = obtainStyledAttributes.getBoolean(R.styleable.ButtonBarLayout_allowStacking, b);
        obtainStyledAttributes.recycle();
    }
    
    private int getNextVisibleChildIndex(int i) {
        while (i < this.getChildCount()) {
            if (this.getChildAt(i).getVisibility() == 0) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    private boolean isStacked() {
        boolean b = true;
        if (this.getOrientation() != 1) {
            b = false;
        }
        return b;
    }
    
    private void setStacked(final boolean b) {
        int orientation = 0;
        if (b) {
            orientation = 1;
        }
        this.setOrientation(orientation);
        int gravity;
        if (!b) {
            gravity = 80;
        }
        else {
            gravity = 5;
        }
        this.setGravity(gravity);
        final View viewById = this.findViewById(R.id.spacer);
        if (viewById != null) {
            int visibility;
            if (!b) {
                visibility = 4;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
        for (int i = this.getChildCount() - 2; i >= 0; --i) {
            this.bringChildToFront(this.getChildAt(i));
        }
    }
    
    public int getMinimumHeight() {
        return Math.max(this.mMinimumHeight, super.getMinimumHeight());
    }
    
    protected void onMeasure(int paddingTop, int n) {
        final int n2 = 0;
        final int size = View$MeasureSpec.getSize(paddingTop);
        if (this.mAllowStacking) {
            if (size > this.mLastWidthSize && this.isStacked()) {
                this.setStacked(false);
            }
            this.mLastWidthSize = size;
        }
        int measureSpec;
        boolean b;
        if (!this.isStacked() && View$MeasureSpec.getMode(paddingTop) == 1073741824) {
            measureSpec = View$MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            b = true;
        }
        else {
            measureSpec = paddingTop;
            b = false;
        }
        super.onMeasure(measureSpec, n);
        int n3;
        if (!this.mAllowStacking) {
            n3 = (b ? 1 : 0);
        }
        else {
            n3 = (b ? 1 : 0);
            if (!this.isStacked()) {
                int n4;
                if ((this.getMeasuredWidthAndState() & 0xFF000000) != 0x1000000) {
                    n4 = 0;
                }
                else {
                    n4 = 1;
                }
                n3 = (b ? 1 : 0);
                if (n4 != 0) {
                    this.setStacked(true);
                    n3 = 1;
                }
            }
        }
        if (n3 != 0) {
            super.onMeasure(paddingTop, n);
        }
        n = this.getNextVisibleChildIndex(0);
        if (n < 0) {
            paddingTop = n2;
        }
        else {
            final View child = this.getChildAt(n);
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)child.getLayoutParams();
            paddingTop = this.getPaddingTop();
            paddingTop = linearLayout$LayoutParams.bottomMargin + (child.getMeasuredHeight() + paddingTop + linearLayout$LayoutParams.topMargin) + 0;
            if (!this.isStacked()) {
                paddingTop += this.getPaddingBottom();
            }
            else {
                n = this.getNextVisibleChildIndex(n + 1);
                if (n >= 0) {
                    paddingTop += this.getChildAt(n).getPaddingTop() + (int)(this.getResources().getDisplayMetrics().density * 16.0f);
                }
            }
        }
        if (ViewCompat.getMinimumHeight((View)this) != paddingTop) {
            this.setMinimumHeight(paddingTop);
        }
    }
    
    public void setAllowStacking(final boolean mAllowStacking) {
        if (this.mAllowStacking != mAllowStacking) {
            this.mAllowStacking = mAllowStacking;
            if (!this.mAllowStacking && this.getOrientation() == 1) {
                this.setStacked(false);
            }
            this.requestLayout();
        }
    }
}
