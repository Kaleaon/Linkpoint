// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.PointF;
import android.view.animation.Interpolator;
import android.util.DisplayMetrics;
import android.content.Context;
import android.view.View;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PagerSnapHelper extends SnapHelper
{
    private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
    @Nullable
    private OrientationHelper mHorizontalHelper;
    @Nullable
    private OrientationHelper mVerticalHelper;
    
    private int distanceToCenter(@NonNull final LayoutManager layoutManager, @NonNull final View view, final OrientationHelper orientationHelper) {
        final int decoratedStart = orientationHelper.getDecoratedStart(view);
        final int n = orientationHelper.getDecoratedMeasurement(view) / 2;
        int n2;
        if (!layoutManager.getClipToPadding()) {
            n2 = orientationHelper.getEnd() / 2;
        }
        else {
            n2 = orientationHelper.getStartAfterPadding() + orientationHelper.getTotalSpace() / 2;
        }
        return n + decoratedStart - n2;
    }
    
    @Nullable
    private View findCenterView(final LayoutManager layoutManager, final OrientationHelper orientationHelper) {
        View view = null;
        final int childCount = layoutManager.getChildCount();
        if (childCount != 0) {
            int n;
            if (!layoutManager.getClipToPadding()) {
                n = orientationHelper.getEnd() / 2;
            }
            else {
                n = orientationHelper.getStartAfterPadding() + orientationHelper.getTotalSpace() / 2;
            }
            int i = 0;
            int n2 = Integer.MAX_VALUE;
            while (i < childCount) {
                final View child = layoutManager.getChildAt(i);
                final int abs = Math.abs(orientationHelper.getDecoratedStart(child) + orientationHelper.getDecoratedMeasurement(child) / 2 - n);
                if (abs < n2) {
                    n2 = abs;
                    view = child;
                }
                ++i;
            }
            return view;
        }
        return null;
    }
    
    @Nullable
    private View findStartView(final LayoutManager layoutManager, final OrientationHelper orientationHelper) {
        View view = null;
        final int childCount = layoutManager.getChildCount();
        if (childCount != 0) {
            int i = 0;
            int n = Integer.MAX_VALUE;
            while (i < childCount) {
                final View child = layoutManager.getChildAt(i);
                final int decoratedStart = orientationHelper.getDecoratedStart(child);
                if (decoratedStart < n) {
                    n = decoratedStart;
                    view = child;
                }
                ++i;
            }
            return view;
        }
        return null;
    }
    
    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull final LayoutManager layoutManager) {
        if (this.mHorizontalHelper == null || this.mHorizontalHelper.mLayoutManager != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }
    
    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull final LayoutManager layoutManager) {
        if (this.mVerticalHelper == null || this.mVerticalHelper.mLayoutManager != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }
    
    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull final LayoutManager layoutManager, @NonNull final View view) {
        final int[] array = new int[2];
        if (!layoutManager.canScrollHorizontally()) {
            array[0] = 0;
        }
        else {
            array[0] = this.distanceToCenter(layoutManager, view, this.getHorizontalHelper(layoutManager));
        }
        if (!layoutManager.canScrollVertically()) {
            array[1] = 0;
        }
        else {
            array[1] = this.distanceToCenter(layoutManager, view, this.getVerticalHelper(layoutManager));
        }
        return array;
    }
    
    @Override
    protected LinearSmoothScroller createSnapScroller(final LayoutManager layoutManager) {
        if (layoutManager instanceof ScrollVectorProvider) {
            return new LinearSmoothScroller(this.mRecyclerView.getContext()) {
                @Override
                protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
                    return 100.0f / displayMetrics.densityDpi;
                }
                
                @Override
                protected int calculateTimeForScrolling(final int n) {
                    return Math.min(100, super.calculateTimeForScrolling(n));
                }
                
                @Override
                protected void onTargetFound(final View view, final State state, final Action action) {
                    final int[] calculateDistanceToFinalSnap = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), view);
                    final int a = calculateDistanceToFinalSnap[0];
                    final int a2 = calculateDistanceToFinalSnap[1];
                    final int calculateTimeForDeceleration = this.calculateTimeForDeceleration(Math.max(Math.abs(a), Math.abs(a2)));
                    if (calculateTimeForDeceleration > 0) {
                        action.update(a, a2, calculateTimeForDeceleration, (Interpolator)this.mDecelerateInterpolator);
                    }
                }
            };
        }
        return null;
    }
    
    @Nullable
    @Override
    public View findSnapView(final LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return this.findCenterView(layoutManager, this.getVerticalHelper(layoutManager));
        }
        if (!layoutManager.canScrollHorizontally()) {
            return null;
        }
        return this.findCenterView(layoutManager, this.getHorizontalHelper(layoutManager));
    }
    
    @Override
    public int findTargetSnapPosition(final LayoutManager layoutManager, int n, int n2) {
        View view = null;
        final int n3 = 0;
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        if (!layoutManager.canScrollVertically()) {
            if (layoutManager.canScrollHorizontally()) {
                view = this.findStartView(layoutManager, this.getHorizontalHelper(layoutManager));
            }
        }
        else {
            view = this.findStartView(layoutManager, this.getVerticalHelper(layoutManager));
        }
        if (view == null) {
            return -1;
        }
        final int position = layoutManager.getPosition(view);
        if (position != -1) {
            if (!layoutManager.canScrollHorizontally()) {
                if (n2 <= 0) {
                    n = 0;
                }
                else {
                    n = 1;
                }
            }
            else if (n <= 0) {
                n = 0;
            }
            else {
                n = 1;
            }
            Label_0073: {
                if (!(layoutManager instanceof ScrollVectorProvider)) {
                    n2 = n3;
                }
                else {
                    final PointF computeScrollVectorForPosition = ((ScrollVectorProvider)layoutManager).computeScrollVectorForPosition(itemCount - 1);
                    n2 = n3;
                    if (computeScrollVectorForPosition != null) {
                        if (computeScrollVectorForPosition.x < 0.0f) {
                            n2 = 1;
                        }
                        else {
                            n2 = 0;
                        }
                        if (n2 == 0) {
                            n2 = n3;
                            if (computeScrollVectorForPosition.y >= 0.0f) {
                                break Label_0073;
                            }
                        }
                        n2 = 1;
                    }
                }
            }
            if (n2 == 0) {
                if (n != 0) {
                    n = position + 1;
                    return n;
                }
            }
            else if (n != 0) {
                n = position - 1;
                return n;
            }
            n = position;
            return n;
        }
        return -1;
    }
}
