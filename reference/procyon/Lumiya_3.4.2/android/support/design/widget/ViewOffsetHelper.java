// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.v4.view.ViewCompat;
import android.view.View;

class ViewOffsetHelper
{
    private int mLayoutLeft;
    private int mLayoutTop;
    private int mOffsetLeft;
    private int mOffsetTop;
    private final View mView;
    
    public ViewOffsetHelper(final View mView) {
        this.mView = mView;
    }
    
    private void updateOffsets() {
        ViewCompat.offsetTopAndBottom(this.mView, this.mOffsetTop - (this.mView.getTop() - this.mLayoutTop));
        ViewCompat.offsetLeftAndRight(this.mView, this.mOffsetLeft - (this.mView.getLeft() - this.mLayoutLeft));
    }
    
    public int getLayoutLeft() {
        return this.mLayoutLeft;
    }
    
    public int getLayoutTop() {
        return this.mLayoutTop;
    }
    
    public int getLeftAndRightOffset() {
        return this.mOffsetLeft;
    }
    
    public int getTopAndBottomOffset() {
        return this.mOffsetTop;
    }
    
    public void onViewLayout() {
        this.mLayoutTop = this.mView.getTop();
        this.mLayoutLeft = this.mView.getLeft();
        this.updateOffsets();
    }
    
    public boolean setLeftAndRightOffset(final int mOffsetLeft) {
        if (this.mOffsetLeft == mOffsetLeft) {
            return false;
        }
        this.mOffsetLeft = mOffsetLeft;
        this.updateOffsets();
        return true;
    }
    
    public boolean setTopAndBottomOffset(final int mOffsetTop) {
        if (this.mOffsetTop == mOffsetTop) {
            return false;
        }
        this.mOffsetTop = mOffsetTop;
        this.updateOffsets();
        return true;
    }
}
