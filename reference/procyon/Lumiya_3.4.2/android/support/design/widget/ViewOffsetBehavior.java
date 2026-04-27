// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

class ViewOffsetBehavior<V extends View> extends Behavior<V>
{
    private int mTempLeftRightOffset;
    private int mTempTopBottomOffset;
    private ViewOffsetHelper mViewOffsetHelper;
    
    public ViewOffsetBehavior() {
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }
    
    public ViewOffsetBehavior(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }
    
    public int getLeftAndRightOffset() {
        int leftAndRightOffset;
        if (this.mViewOffsetHelper == null) {
            leftAndRightOffset = 0;
        }
        else {
            leftAndRightOffset = this.mViewOffsetHelper.getLeftAndRightOffset();
        }
        return leftAndRightOffset;
    }
    
    public int getTopAndBottomOffset() {
        int topAndBottomOffset;
        if (this.mViewOffsetHelper == null) {
            topAndBottomOffset = 0;
        }
        else {
            topAndBottomOffset = this.mViewOffsetHelper.getTopAndBottomOffset();
        }
        return topAndBottomOffset;
    }
    
    protected void layoutChild(final CoordinatorLayout coordinatorLayout, final V v, final int n) {
        coordinatorLayout.onLayoutChild(v, n);
    }
    
    @Override
    public boolean onLayoutChild(final CoordinatorLayout coordinatorLayout, final V v, final int n) {
        this.layoutChild(coordinatorLayout, v, n);
        if (this.mViewOffsetHelper == null) {
            this.mViewOffsetHelper = new ViewOffsetHelper(v);
        }
        this.mViewOffsetHelper.onViewLayout();
        if (this.mTempTopBottomOffset != 0) {
            this.mViewOffsetHelper.setTopAndBottomOffset(this.mTempTopBottomOffset);
            this.mTempTopBottomOffset = 0;
        }
        if (this.mTempLeftRightOffset != 0) {
            this.mViewOffsetHelper.setLeftAndRightOffset(this.mTempLeftRightOffset);
            this.mTempLeftRightOffset = 0;
        }
        return true;
    }
    
    public boolean setLeftAndRightOffset(final int n) {
        if (this.mViewOffsetHelper == null) {
            this.mTempLeftRightOffset = n;
            return false;
        }
        return this.mViewOffsetHelper.setLeftAndRightOffset(n);
    }
    
    public boolean setTopAndBottomOffset(final int n) {
        if (this.mViewOffsetHelper == null) {
            this.mTempTopBottomOffset = n;
            return false;
        }
        return this.mViewOffsetHelper.setTopAndBottomOffset(n);
    }
}
