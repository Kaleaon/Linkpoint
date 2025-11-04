// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.animation.Interpolator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.View;
import android.content.Context;
import android.graphics.PointF;
import android.view.animation.LinearInterpolator;
import android.view.animation.DecelerateInterpolator;

public class LinearSmoothScroller extends SmoothScroller
{
    private static final boolean DEBUG = false;
    private static final float MILLISECONDS_PER_INCH = 25.0f;
    public static final int SNAP_TO_ANY = 0;
    public static final int SNAP_TO_END = 1;
    public static final int SNAP_TO_START = -1;
    private static final String TAG = "LinearSmoothScroller";
    private static final float TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f;
    private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
    private final float MILLISECONDS_PER_PX;
    protected final DecelerateInterpolator mDecelerateInterpolator;
    protected int mInterimTargetDx;
    protected int mInterimTargetDy;
    protected final LinearInterpolator mLinearInterpolator;
    protected PointF mTargetVector;
    
    public LinearSmoothScroller(final Context context) {
        this.mLinearInterpolator = new LinearInterpolator();
        this.mDecelerateInterpolator = new DecelerateInterpolator();
        this.mInterimTargetDx = 0;
        this.mInterimTargetDy = 0;
        this.MILLISECONDS_PER_PX = this.calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }
    
    private int clampApplyScroll(final int n, int n2) {
        n2 = n - n2;
        if (n * n2 > 0) {
            return n2;
        }
        return 0;
    }
    
    public int calculateDtToFit(int n, final int n2, final int n3, final int n4, final int n5) {
        switch (n5) {
            default: {
                throw new IllegalArgumentException("snap preference should be one of the constants defined in SmoothScroller, starting with SNAP_");
            }
            case -1: {
                return n3 - n;
            }
            case 1: {
                return n4 - n2;
            }
            case 0: {
                n = n3 - n;
                if (n > 0) {
                    return n;
                }
                n = n4 - n2;
                if (n >= 0) {
                    return 0;
                }
                return n;
            }
        }
    }
    
    public int calculateDxToMakeVisible(final View view, final int n) {
        final LayoutManager layoutManager = ((RecyclerView.SmoothScroller)this).getLayoutManager();
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            return this.calculateDtToFit(layoutManager.getDecoratedLeft(view) - layoutParams.leftMargin, layoutManager.getDecoratedRight(view) + layoutParams.rightMargin, layoutManager.getPaddingLeft(), layoutManager.getWidth() - layoutManager.getPaddingRight(), n);
        }
        return 0;
    }
    
    public int calculateDyToMakeVisible(final View view, final int n) {
        final LayoutManager layoutManager = ((RecyclerView.SmoothScroller)this).getLayoutManager();
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            return this.calculateDtToFit(layoutManager.getDecoratedTop(view) - layoutParams.topMargin, layoutManager.getDecoratedBottom(view) + layoutParams.bottomMargin, layoutManager.getPaddingTop(), layoutManager.getHeight() - layoutManager.getPaddingBottom(), n);
        }
        return 0;
    }
    
    protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
        return 25.0f / displayMetrics.densityDpi;
    }
    
    protected int calculateTimeForDeceleration(final int n) {
        return (int)Math.ceil(this.calculateTimeForScrolling(n) / 0.3356);
    }
    
    protected int calculateTimeForScrolling(final int a) {
        return (int)Math.ceil(Math.abs(a) * this.MILLISECONDS_PER_PX);
    }
    
    @Nullable
    public PointF computeScrollVectorForPosition(final int n) {
        final LayoutManager layoutManager = ((RecyclerView.SmoothScroller)this).getLayoutManager();
        if (!(layoutManager instanceof ScrollVectorProvider)) {
            Log.w("LinearSmoothScroller", "You should override computeScrollVectorForPosition when the LayoutManager does not implement " + ScrollVectorProvider.class.getCanonicalName());
            return null;
        }
        return ((ScrollVectorProvider)layoutManager).computeScrollVectorForPosition(n);
    }
    
    protected int getHorizontalSnapPreference() {
        int n;
        if (this.mTargetVector != null && this.mTargetVector.x != 0.0f) {
            if (this.mTargetVector.x > 0.0f) {
                n = 1;
            }
            else {
                n = -1;
            }
        }
        else {
            n = 0;
        }
        return n;
    }
    
    protected int getVerticalSnapPreference() {
        int n;
        if (this.mTargetVector != null && this.mTargetVector.y != 0.0f) {
            if (this.mTargetVector.y > 0.0f) {
                n = 1;
            }
            else {
                n = -1;
            }
        }
        else {
            n = 0;
        }
        return n;
    }
    
    @Override
    protected void onSeekTargetStep(final int n, final int n2, final State state, final Action action) {
        if (((RecyclerView.SmoothScroller)this).getChildCount() != 0) {
            this.mInterimTargetDx = this.clampApplyScroll(this.mInterimTargetDx, n);
            this.mInterimTargetDy = this.clampApplyScroll(this.mInterimTargetDy, n2);
            if (this.mInterimTargetDx == 0 && this.mInterimTargetDy == 0) {
                this.updateActionForInterimTarget(action);
            }
            return;
        }
        ((RecyclerView.SmoothScroller)this).stop();
    }
    
    @Override
    protected void onStart() {
    }
    
    @Override
    protected void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }
    
    @Override
    protected void onTargetFound(final View view, final State state, final Action action) {
        final int calculateDxToMakeVisible = this.calculateDxToMakeVisible(view, this.getHorizontalSnapPreference());
        final int calculateDyToMakeVisible = this.calculateDyToMakeVisible(view, this.getVerticalSnapPreference());
        final int calculateTimeForDeceleration = this.calculateTimeForDeceleration((int)Math.sqrt(calculateDxToMakeVisible * calculateDxToMakeVisible + calculateDyToMakeVisible * calculateDyToMakeVisible));
        if (calculateTimeForDeceleration > 0) {
            action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, calculateTimeForDeceleration, (Interpolator)this.mDecelerateInterpolator);
        }
    }
    
    protected void updateActionForInterimTarget(final Action action) {
        final PointF computeScrollVectorForPosition = this.computeScrollVectorForPosition(((RecyclerView.SmoothScroller)this).getTargetPosition());
        if (computeScrollVectorForPosition != null && (computeScrollVectorForPosition.x != 0.0f || computeScrollVectorForPosition.y != 0.0f)) {
            ((RecyclerView.SmoothScroller)this).normalize(computeScrollVectorForPosition);
            this.mTargetVector = computeScrollVectorForPosition;
            this.mInterimTargetDx = (int)(computeScrollVectorForPosition.x * 10000.0f);
            this.mInterimTargetDy = (int)(computeScrollVectorForPosition.y * 10000.0f);
            action.update((int)(this.mInterimTargetDx * 1.2f), (int)(this.mInterimTargetDy * 1.2f), (int)(this.calculateTimeForScrolling(10000) * 1.2f), (Interpolator)this.mLinearInterpolator);
            return;
        }
        action.jumpTo(((RecyclerView.SmoothScroller)this).getTargetPosition());
        ((RecyclerView.SmoothScroller)this).stop();
    }
}
