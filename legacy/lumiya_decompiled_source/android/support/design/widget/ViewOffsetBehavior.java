package android.support.design.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
/* loaded from: classes.dex */
class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private int mTempLeftRightOffset;
    private int mTempTopBottomOffset;
    private ViewOffsetHelper mViewOffsetHelper;

    public ViewOffsetBehavior() {
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }

    public ViewOffsetBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }

    public int getLeftAndRightOffset() {
        if (this.mViewOffsetHelper == null) {
            return 0;
        }
        return this.mViewOffsetHelper.getLeftAndRightOffset();
    }

    public int getTopAndBottomOffset() {
        if (this.mViewOffsetHelper == null) {
            return 0;
        }
        return this.mViewOffsetHelper.getTopAndBottomOffset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void layoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        coordinatorLayout.onLayoutChild(v, i);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        layoutChild(coordinatorLayout, v, i);
        if (this.mViewOffsetHelper == null) {
            this.mViewOffsetHelper = new ViewOffsetHelper(v);
        }
        this.mViewOffsetHelper.onViewLayout();
        if (this.mTempTopBottomOffset != 0) {
            this.mViewOffsetHelper.setTopAndBottomOffset(this.mTempTopBottomOffset);
            this.mTempTopBottomOffset = 0;
        }
        if (this.mTempLeftRightOffset == 0) {
            return true;
        }
        this.mViewOffsetHelper.setLeftAndRightOffset(this.mTempLeftRightOffset);
        this.mTempLeftRightOffset = 0;
        return true;
    }

    public boolean setLeftAndRightOffset(int i) {
        if (this.mViewOffsetHelper == null) {
            this.mTempLeftRightOffset = i;
            return false;
        }
        return this.mViewOffsetHelper.setLeftAndRightOffset(i);
    }

    public boolean setTopAndBottomOffset(int i) {
        if (this.mViewOffsetHelper == null) {
            this.mTempTopBottomOffset = i;
            return false;
        }
        return this.mViewOffsetHelper.setTopAndBottomOffset(i);
    }
}
