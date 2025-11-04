// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.view.View$MeasureSpec;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.math.MathUtils;
import java.util.List;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View>
{
    private int mOverlayTop;
    final Rect mTempRect1;
    final Rect mTempRect2;
    private int mVerticalLayoutGap;
    
    public HeaderScrollingViewBehavior() {
        this.mTempRect1 = new Rect();
        this.mTempRect2 = new Rect();
        this.mVerticalLayoutGap = 0;
    }
    
    public HeaderScrollingViewBehavior(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTempRect1 = new Rect();
        this.mTempRect2 = new Rect();
        this.mVerticalLayoutGap = 0;
    }
    
    private static int resolveGravity(int n) {
        if (n == 0) {
            n = 8388659;
        }
        return n;
    }
    
    abstract View findFirstDependency(final List<View> p0);
    
    final int getOverlapPixelsForOffset(final View view) {
        int clamp = 0;
        if (this.mOverlayTop != 0) {
            clamp = MathUtils.clamp((int)(this.getOverlapRatioForOffset(view) * this.mOverlayTop), 0, this.mOverlayTop);
        }
        return clamp;
    }
    
    float getOverlapRatioForOffset(final View view) {
        return 1.0f;
    }
    
    public final int getOverlayTop() {
        return this.mOverlayTop;
    }
    
    int getScrollRange(final View view) {
        return view.getMeasuredHeight();
    }
    
    final int getVerticalLayoutGap() {
        return this.mVerticalLayoutGap;
    }
    
    @Override
    protected void layoutChild(final CoordinatorLayout coordinatorLayout, final View view, int overlapPixelsForOffset) {
        final View firstDependency = this.findFirstDependency(coordinatorLayout.getDependencies(view));
        if (firstDependency == null) {
            super.layoutChild(coordinatorLayout, view, overlapPixelsForOffset);
            this.mVerticalLayoutGap = 0;
        }
        else {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect mTempRect1 = this.mTempRect1;
            mTempRect1.set(coordinatorLayout.getPaddingLeft() + layoutParams.leftMargin, firstDependency.getBottom() + layoutParams.topMargin, coordinatorLayout.getWidth() - coordinatorLayout.getPaddingRight() - layoutParams.rightMargin, coordinatorLayout.getHeight() + firstDependency.getBottom() - coordinatorLayout.getPaddingBottom() - layoutParams.bottomMargin);
            final WindowInsetsCompat lastWindowInsets = coordinatorLayout.getLastWindowInsets();
            if (lastWindowInsets != null && ViewCompat.getFitsSystemWindows((View)coordinatorLayout) && !ViewCompat.getFitsSystemWindows(view)) {
                mTempRect1.left += lastWindowInsets.getSystemWindowInsetLeft();
                mTempRect1.right -= lastWindowInsets.getSystemWindowInsetRight();
            }
            final Rect mTempRect2 = this.mTempRect2;
            GravityCompat.apply(resolveGravity(layoutParams.gravity), view.getMeasuredWidth(), view.getMeasuredHeight(), mTempRect1, mTempRect2, overlapPixelsForOffset);
            overlapPixelsForOffset = this.getOverlapPixelsForOffset(firstDependency);
            view.layout(mTempRect2.left, mTempRect2.top - overlapPixelsForOffset, mTempRect2.right, mTempRect2.bottom - overlapPixelsForOffset);
            this.mVerticalLayoutGap = mTempRect2.top - firstDependency.getBottom();
        }
    }
    
    @Override
    public boolean onMeasureChild(final CoordinatorLayout coordinatorLayout, final View view, final int n, final int n2, int n3, final int n4) {
        final int height = view.getLayoutParams().height;
        if (height == -1 || height == -2) {
            final View firstDependency = this.findFirstDependency(coordinatorLayout.getDependencies(view));
            if (firstDependency != null) {
                if (ViewCompat.getFitsSystemWindows(firstDependency) && !ViewCompat.getFitsSystemWindows(view)) {
                    ViewCompat.setFitsSystemWindows(view, true);
                    if (ViewCompat.getFitsSystemWindows(view)) {
                        view.requestLayout();
                        return true;
                    }
                }
                n3 = View$MeasureSpec.getSize(n3);
                if (n3 == 0) {
                    n3 = coordinatorLayout.getHeight();
                }
                final int measuredHeight = firstDependency.getMeasuredHeight();
                final int scrollRange = this.getScrollRange(firstDependency);
                int n5;
                if (height != -1) {
                    n5 = Integer.MIN_VALUE;
                }
                else {
                    n5 = 1073741824;
                }
                coordinatorLayout.onMeasureChild(view, n, n2, View$MeasureSpec.makeMeasureSpec(scrollRange + (n3 - measuredHeight), n5), n4);
                return true;
            }
        }
        return false;
    }
    
    public final void setOverlayTop(final int mOverlayTop) {
        this.mOverlayTop = mOverlayTop;
    }
}
