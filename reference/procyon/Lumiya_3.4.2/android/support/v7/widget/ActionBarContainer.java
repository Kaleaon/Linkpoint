// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.ActionMode;
import android.view.ActionMode$Callback;
import android.view.ViewGroup$LayoutParams;
import android.graphics.drawable.Drawable$Callback;
import android.view.View$MeasureSpec;
import android.view.MotionEvent;
import android.widget.FrameLayout$LayoutParams;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R;
import android.support.v4.view.ViewCompat;
import android.os.Build$VERSION;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.support.annotation.RestrictTo;
import android.widget.FrameLayout;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ActionBarContainer extends FrameLayout
{
    private View mActionBarView;
    Drawable mBackground;
    private View mContextView;
    private int mHeight;
    boolean mIsSplit;
    boolean mIsStacked;
    private boolean mIsTransitioning;
    Drawable mSplitBackground;
    Drawable mStackedBackground;
    private View mTabContainer;
    
    public ActionBarContainer(final Context context) {
        this(context, null);
    }
    
    public ActionBarContainer(final Context context, final AttributeSet set) {
        super(context, set);
        ActionBarBackgroundDrawable actionBarBackgroundDrawable;
        if (Build$VERSION.SDK_INT < 21) {
            actionBarBackgroundDrawable = new ActionBarBackgroundDrawable(this);
        }
        else {
            actionBarBackgroundDrawable = new ActionBarBackgroundDrawableV21(this);
        }
        ViewCompat.setBackground((View)this, actionBarBackgroundDrawable);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ActionBar);
        this.mBackground = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_background);
        this.mStackedBackground = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_backgroundStacked);
        this.mHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionBar_height, -1);
        if (this.getId() == R.id.split_action_bar) {
            this.mIsSplit = true;
            this.mSplitBackground = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_backgroundSplit);
        }
        obtainStyledAttributes.recycle();
        while (true) {
            Label_0147: {
                if (!this.mIsSplit) {
                    if (this.mBackground == null) {
                        if (this.mStackedBackground == null) {
                            break Label_0147;
                        }
                    }
                }
                else if (this.mSplitBackground == null) {
                    break Label_0147;
                }
                final boolean willNotDraw = false;
                this.setWillNotDraw(willNotDraw);
                return;
            }
            final boolean willNotDraw = true;
            continue;
        }
    }
    
    private int getMeasuredHeightWithMargins(final View view) {
        final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        return frameLayout$LayoutParams.bottomMargin + (view.getMeasuredHeight() + frameLayout$LayoutParams.topMargin);
    }
    
    private boolean isCollapsed(final View view) {
        boolean b = false;
        if (view == null || view.getVisibility() == 8 || view.getMeasuredHeight() == 0) {
            b = true;
        }
        return b;
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackground != null && this.mBackground.isStateful()) {
            this.mBackground.setState(this.getDrawableState());
        }
        if (this.mStackedBackground != null && this.mStackedBackground.isStateful()) {
            this.mStackedBackground.setState(this.getDrawableState());
        }
        if (this.mSplitBackground != null && this.mSplitBackground.isStateful()) {
            this.mSplitBackground.setState(this.getDrawableState());
        }
    }
    
    public View getTabContainer() {
        return this.mTabContainer;
    }
    
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mBackground != null) {
            this.mBackground.jumpToCurrentState();
        }
        if (this.mStackedBackground != null) {
            this.mStackedBackground.jumpToCurrentState();
        }
        if (this.mSplitBackground != null) {
            this.mSplitBackground.jumpToCurrentState();
        }
    }
    
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = this.findViewById(R.id.action_bar);
        this.mContextView = this.findViewById(R.id.action_context_bar);
    }
    
    public boolean onHoverEvent(final MotionEvent motionEvent) {
        super.onHoverEvent(motionEvent);
        return true;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        boolean b = false;
        if (this.mIsTransitioning || super.onInterceptTouchEvent(motionEvent)) {
            b = true;
        }
        return b;
    }
    
    public void onLayout(final boolean b, int n, int measuredHeight, final int n2, final int n3) {
        final int n4 = 0;
        final int n5 = 0;
        super.onLayout(b, n, measuredHeight, n2, n3);
        final View mTabContainer = this.mTabContainer;
        final boolean mIsStacked = mTabContainer != null && mTabContainer.getVisibility() != 8;
        if (mTabContainer != null && mTabContainer.getVisibility() != 8) {
            measuredHeight = this.getMeasuredHeight();
            final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)mTabContainer.getLayoutParams();
            mTabContainer.layout(n, measuredHeight - mTabContainer.getMeasuredHeight() - frameLayout$LayoutParams.bottomMargin, n2, measuredHeight - frameLayout$LayoutParams.bottomMargin);
        }
        if (!this.mIsSplit) {
            if (this.mBackground == null) {
                n = n5;
            }
            else {
                if (this.mActionBarView.getVisibility() != 0) {
                    if (this.mContextView != null && this.mContextView.getVisibility() == 0) {
                        this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
                    }
                    else {
                        this.mBackground.setBounds(0, 0, 0, 0);
                    }
                }
                else {
                    this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                }
                n = 1;
            }
            this.mIsStacked = mIsStacked;
            if (mIsStacked && this.mStackedBackground != null) {
                this.mStackedBackground.setBounds(mTabContainer.getLeft(), mTabContainer.getTop(), mTabContainer.getRight(), mTabContainer.getBottom());
                n = 1;
            }
        }
        else {
            n = n4;
            if (this.mSplitBackground != null) {
                this.mSplitBackground.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
                n = 1;
            }
        }
        if (n != 0) {
            this.invalidate();
        }
    }
    
    public void onMeasure(int n, int b) {
        int measureSpec;
        if (this.mActionBarView != null) {
            measureSpec = b;
        }
        else {
            measureSpec = b;
            if (View$MeasureSpec.getMode(b) == Integer.MIN_VALUE) {
                measureSpec = b;
                if (this.mHeight >= 0) {
                    measureSpec = View$MeasureSpec.makeMeasureSpec(Math.min(this.mHeight, View$MeasureSpec.getSize(b)), Integer.MIN_VALUE);
                }
            }
        }
        super.onMeasure(n, measureSpec);
        if (this.mActionBarView != null) {
            b = View$MeasureSpec.getMode(measureSpec);
            if (this.mTabContainer != null && this.mTabContainer.getVisibility() != 8 && b != 1073741824) {
                if (this.isCollapsed(this.mActionBarView)) {
                    if (this.isCollapsed(this.mContextView)) {
                        n = 0;
                    }
                    else {
                        n = this.getMeasuredHeightWithMargins(this.mContextView);
                    }
                }
                else {
                    n = this.getMeasuredHeightWithMargins(this.mActionBarView);
                }
                if (b != Integer.MIN_VALUE) {
                    b = Integer.MAX_VALUE;
                }
                else {
                    b = View$MeasureSpec.getSize(measureSpec);
                }
                this.setMeasuredDimension(this.getMeasuredWidth(), Math.min(n + this.getMeasuredHeightWithMargins(this.mTabContainer), b));
            }
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }
    
    public void setPrimaryBackground(final Drawable mBackground) {
        boolean willNotDraw = true;
        if (this.mBackground != null) {
            this.mBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mBackground);
        }
        if ((this.mBackground = mBackground) != null) {
            mBackground.setCallback((Drawable$Callback)this);
            if (this.mActionBarView != null) {
                this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
            }
        }
        Label_0034: {
            if (!this.mIsSplit) {
                if (this.mBackground == null) {
                    if (this.mStackedBackground == null) {
                        break Label_0034;
                    }
                }
            }
            else if (this.mSplitBackground == null) {
                break Label_0034;
            }
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setSplitBackground(final Drawable mSplitBackground) {
        boolean willNotDraw = true;
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mSplitBackground);
        }
        if ((this.mSplitBackground = mSplitBackground) != null) {
            mSplitBackground.setCallback((Drawable$Callback)this);
            if (this.mIsSplit && this.mSplitBackground != null) {
                this.mSplitBackground.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
            }
        }
        Label_0034: {
            if (!this.mIsSplit) {
                if (this.mBackground == null) {
                    if (this.mStackedBackground == null) {
                        break Label_0034;
                    }
                }
            }
            else if (this.mSplitBackground == null) {
                break Label_0034;
            }
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setStackedBackground(final Drawable mStackedBackground) {
        boolean willNotDraw = true;
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mStackedBackground);
        }
        if ((this.mStackedBackground = mStackedBackground) != null) {
            mStackedBackground.setCallback((Drawable$Callback)this);
            if (this.mIsStacked && this.mStackedBackground != null) {
                this.mStackedBackground.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
            }
        }
        Label_0034: {
            if (!this.mIsSplit) {
                if (this.mBackground == null) {
                    if (this.mStackedBackground == null) {
                        break Label_0034;
                    }
                }
            }
            else if (this.mSplitBackground == null) {
                break Label_0034;
            }
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setTabContainer(final ScrollingTabContainerView mTabContainer) {
        if (this.mTabContainer != null) {
            this.removeView(this.mTabContainer);
        }
        if ((this.mTabContainer = (View)mTabContainer) != null) {
            this.addView((View)mTabContainer);
            final ViewGroup$LayoutParams layoutParams = mTabContainer.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            mTabContainer.setAllowCollapse(false);
        }
    }
    
    public void setTransitioning(final boolean mIsTransitioning) {
        int descendantFocusability;
        if (!(this.mIsTransitioning = mIsTransitioning)) {
            descendantFocusability = 262144;
        }
        else {
            descendantFocusability = 393216;
        }
        this.setDescendantFocusability(descendantFocusability);
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final boolean b = visibility == 0;
        if (this.mBackground != null) {
            this.mBackground.setVisible(b, false);
        }
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setVisible(b, false);
        }
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setVisible(b, false);
        }
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode$Callback actionMode$Callback) {
        return null;
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode$Callback actionMode$Callback, final int n) {
        if (n == 0) {
            return null;
        }
        return super.startActionModeForChild(view, actionMode$Callback, n);
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        boolean b = false;
        if (drawable == this.mBackground && !this.mIsSplit) {
            return true;
        }
        if (drawable == this.mStackedBackground && this.mIsStacked) {
            return true;
        }
        if (drawable == this.mSplitBackground && this.mIsSplit) {
            return true;
        }
        if (super.verifyDrawable(drawable)) {
            return true;
        }
        return b;
        b = true;
        return b;
    }
}
