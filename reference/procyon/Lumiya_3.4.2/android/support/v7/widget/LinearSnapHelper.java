// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.Nullable;

public class LinearSnapHelper extends SnapHelper
{
    private static final float INVALID_DISTANCE = 1.0f;
    @Nullable
    private OrientationHelper mHorizontalHelper;
    @Nullable
    private OrientationHelper mVerticalHelper;
    
    private float computeDistancePerChild(final LayoutManager layoutManager, final OrientationHelper orientationHelper) {
        View view = null;
        int n = Integer.MAX_VALUE;
        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return 1.0f;
        }
        int i = 0;
        View view2 = null;
        int n2 = Integer.MIN_VALUE;
        while (i < childCount) {
            final View child = layoutManager.getChildAt(i);
            final int position = layoutManager.getPosition(child);
            if (position != -1) {
                if (position < n) {
                    n = position;
                    view2 = child;
                }
                if (position > n2) {
                    n2 = position;
                    view = child;
                }
            }
            ++i;
        }
        if (view2 == null || view == null) {
            return 1.0f;
        }
        final int n3 = Math.max(orientationHelper.getDecoratedEnd(view2), orientationHelper.getDecoratedEnd(view)) - Math.min(orientationHelper.getDecoratedStart(view2), orientationHelper.getDecoratedStart(view));
        if (n3 != 0) {
            return n3 * 1.0f / (n2 - n + 1);
        }
        return 1.0f;
    }
    
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
    
    private int estimateNextPositionDiffForFling(final LayoutManager layoutManager, final OrientationHelper orientationHelper, int n, final int n2) {
        final int[] calculateScrollDistance = this.calculateScrollDistance(n, n2);
        final float computeDistancePerChild = this.computeDistancePerChild(layoutManager, orientationHelper);
        if (computeDistancePerChild <= 0.0f) {
            return 0;
        }
        if (Math.abs(calculateScrollDistance[0]) <= Math.abs(calculateScrollDistance[1])) {
            n = calculateScrollDistance[1];
        }
        else {
            n = calculateScrollDistance[0];
        }
        return Math.round(n / computeDistancePerChild);
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
    public int findTargetSnapPosition(final LayoutManager layoutManager, int estimateNextPositionDiffForFling, int estimateNextPositionDiffForFling2) {
        if (!(layoutManager instanceof ScrollVectorProvider)) {
            return -1;
        }
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        final View snapView = this.findSnapView(layoutManager);
        if (snapView == null) {
            return -1;
        }
        final int position = layoutManager.getPosition(snapView);
        if (position == -1) {
            return -1;
        }
        final PointF computeScrollVectorForPosition = ((ScrollVectorProvider)layoutManager).computeScrollVectorForPosition(itemCount - 1);
        if (computeScrollVectorForPosition == null) {
            return -1;
        }
        if (!layoutManager.canScrollHorizontally()) {
            estimateNextPositionDiffForFling = 0;
        }
        else {
            final int n = estimateNextPositionDiffForFling = this.estimateNextPositionDiffForFling(layoutManager, this.getHorizontalHelper(layoutManager), estimateNextPositionDiffForFling, 0);
            if (computeScrollVectorForPosition.x < 0.0f) {
                estimateNextPositionDiffForFling = -n;
            }
        }
        if (!layoutManager.canScrollVertically()) {
            estimateNextPositionDiffForFling2 = 0;
        }
        else {
            final int n2 = estimateNextPositionDiffForFling2 = this.estimateNextPositionDiffForFling(layoutManager, this.getVerticalHelper(layoutManager), 0, estimateNextPositionDiffForFling2);
            if (computeScrollVectorForPosition.y < 0.0f) {
                estimateNextPositionDiffForFling2 = -n2;
            }
        }
        if (layoutManager.canScrollVertically()) {
            estimateNextPositionDiffForFling = estimateNextPositionDiffForFling2;
        }
        if (estimateNextPositionDiffForFling != 0) {
            estimateNextPositionDiffForFling += position;
            if (estimateNextPositionDiffForFling < 0) {
                estimateNextPositionDiffForFling = 0;
            }
            if (estimateNextPositionDiffForFling >= itemCount) {
                estimateNextPositionDiffForFling = itemCount - 1;
            }
            return estimateNextPositionDiffForFling;
        }
        return -1;
    }
}
